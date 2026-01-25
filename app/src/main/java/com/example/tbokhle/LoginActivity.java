package com.example.tbokhle;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
// 🔹 ADD at top
import com.android.volley.toolbox.JsonArrayRequest;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView loginToRegister;

    private SessionManager session;

    // Emulator -> your PC localhost is 10.0.2.2
    private static final String LOGIN_URL =
            "http://10.0.2.2/tbokhle_api/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        session = new SessionManager(this);

        // 1) Auto-login: if already saved before, go directly to MainActivity
        if (session.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        loginToRegister = findViewById(R.id.tvRegisterRedirect);

        btnLogin.setOnClickListener(v -> loginUser());

        loginToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }

    private void loginUser() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(
                Request.Method.POST,
                LOGIN_URL,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response.trim());

                        String status = json.optString("status", "");
                        if ("success".equalsIgnoreCase(status)) {

                            String userId = json.optString("id", "");
                            String userEmail = json.optString("email", email);

                            // 2) Save user data locally as key/value (SharedPreferences)
                            session.saveLogin(userId, userEmail);
                            // ADDED by maria
                            fetchAndSetDefaultHousehold(userId);

                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();

                        } else {
                            Toast.makeText(LoginActivity.this,
                                    json.optString("message", "Invalid email or password"),
                                    Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this,
                                "Bad server response (not JSON): " + response,
                                Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(LoginActivity.this,
                        "Network error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
    // ADDED by maria
    private void fetchAndSetDefaultHousehold(String userId) {

        String url = "http://10.0.2.2/tbokhle/get_user_households.php?user_id=" + userId;

        JsonArrayRequest req = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        int selectedHousehold = -1;

                        // 1️⃣ Prefer admin household
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject h = response.getJSONObject(i);
                            if ("admin".equalsIgnoreCase(h.optString("role"))) {
                                selectedHousehold = h.getInt("id");
                                break;
                            }
                        }

                        // 2️⃣ Fallback → first household
                        if (selectedHousehold == -1 && response.length() > 0) {
                            selectedHousehold = response.getJSONObject(0).getInt("id");
                        }

                        if (selectedHousehold != -1) {
                            session.setHouseholdId(selectedHousehold); // ✅ STORED
                        }

                    } catch (Exception ignored) {}
                },
                error -> {}
        );

        Volley.newRequestQueue(this).add(req);
    }

}
