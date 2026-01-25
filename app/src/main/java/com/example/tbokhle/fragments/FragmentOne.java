package com.example.tbokhle.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tbokhle.R;
import com.example.tbokhle.SessionManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FragmentOne extends Fragment {

    private static final String DASHBOARD_URL =
            "http://10.0.2.2/tbokhle_api/get_dashboard.php";

    private TextView tvTotal, tvLow, tvShopping;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_one, container, false);

        tvTotal = view.findViewById(R.id.tvTotalItems);
        tvLow = view.findViewById(R.id.tvLowStock);
        tvShopping = view.findViewById(R.id.tvShopping);

        loadDashboardStats();

        return view;
    }

    private void loadDashboardStats() {

        SessionManager session = new SessionManager(requireContext());
        int householdId = session.getHouseholdId();

        if (householdId == -1) {
            Toast.makeText(requireContext(),
                    "Household not found for this user",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(
                Request.Method.POST,
                DASHBOARD_URL,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);

                        if (!obj.getString("status").equals("success")) {
                            Toast.makeText(requireContext(),
                                    "Server error",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        tvTotal.setText(obj.getString("total"));
                        tvLow.setText(obj.getString("low_stock"));
                        tvShopping.setText(obj.getString("shopping"));

                    } catch (Exception e) {
                        Toast.makeText(requireContext(),
                                "Parse error: " + response,
                                Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(requireContext(),
                        "Network error",
                        Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("household_id", String.valueOf(householdId));
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }
}
