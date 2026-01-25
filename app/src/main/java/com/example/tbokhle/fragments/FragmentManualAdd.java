package com.example.tbokhle.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tbokhle.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FragmentManualAdd extends Fragment {

    private static final String ADD_PRODUCT_URL =
            "http://10.0.2.2/tbokhle_api/add_product.php";

    private EditText etName, etQty, etExpiry;
    private Spinner spCategory;
    private Button btnSave;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_manual_add, container, false);

        etName = view.findViewById(R.id.etName);
        etQty = view.findViewById(R.id.etQty);
        etExpiry = view.findViewById(R.id.etExpiry);
        spCategory = view.findViewById(R.id.spCategory);
        btnSave = view.findViewById(R.id.btnSaveProduct);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.categories,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);

        etExpiry.setOnClickListener(v -> openDatePicker());

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String qty = etQty.getText().toString().trim();
            String date = etExpiry.getText().toString().trim();
            String category = spCategory.getSelectedItem().toString();

            if (name.isEmpty() || qty.isEmpty() || date.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            saveProductToServer(name, qty, date, category);
        });

        return view;
    }

    private void openDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(
                requireContext(),
                (view, y, m, d) -> etExpiry.setText(y + "-" + (m + 1) + "-" + d),
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void saveProductToServer(String name, String quantity, String expiryDate, String category) {

        StringRequest request = new StringRequest(
                Request.Method.POST,
                ADD_PRODUCT_URL,
                response -> {
                    if (response.trim().equals("success")) {
                        Toast.makeText(requireContext(), "Product saved successfully", Toast.LENGTH_SHORT).show();
                        etName.setText("");
                        etQty.setText("");
                        etExpiry.setText("");
                        spCategory.setSelection(0);
                    } else {
                        Toast.makeText(requireContext(), "Server error: " + response, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(requireContext(), "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("quantity", quantity);
                params.put("expiry_date", expiryDate);
                params.put("category", category);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(request);
    }
}
