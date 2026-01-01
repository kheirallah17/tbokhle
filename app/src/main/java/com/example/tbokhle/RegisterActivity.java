package com.example.tbokhle;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLoginRedirect;
    private static final String REGISTER_URL =
            "http://10.0.2.2/tbokhle_api/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // UI Elements
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        tvLoginRedirect = findViewById(R.id.tvLoginRedirect);

        // Register button
        btnRegister.setOnClickListener(v -> registerUser());

        // Redirect to login
        tvLoginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {

        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirm = etConfirmPassword.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(fullName)
                || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(confirm)) {

            Toast.makeText(this,
                    "All fields are required",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirm)) {
            Toast.makeText(this,
                    "Passwords do not match",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Volley request
        StringRequest request = new StringRequest(
                Request.Method.POST,
                REGISTER_URL,
                response -> {

                    if (response.trim().equals("success")) {

                        Toast.makeText(RegisterActivity.this,
                                "Registration successful",
                                Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();

                    } else if (response.trim().equals("exists")) {

                        Toast.makeText(RegisterActivity.this,
                                "Email already exists",
                                Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(RegisterActivity.this,
                                "Registration failed",
                                Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(RegisterActivity.this,
                        "Network error",
                        Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("full_name", fullName);
                params.put("email", email);
                params.put("password", password); // hashed in PHP
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
