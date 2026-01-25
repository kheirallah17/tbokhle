package com.example.tbokhle.fragments;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tbokhle.R;
import com.example.tbokhle.SessionManager;
import com.example.tbokhle.adapters.PantryAdapter;
import com.example.tbokhle.model.PantryItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FragmentTwo extends Fragment {

    // 🔗 API Base URL
    private static final String BASE_URL =
            "http://10.0.2.2/Tbokhle/";

    private PantryAdapter adapter;
    private final List<PantryItem> allItems = new ArrayList<>();

    private SessionManager session;
    private int householdId;

    private RequestQueue queue;

    public FragmentTwo() {}

    // =====================================================
    // 📌 CREATE VIEW
    // =====================================================
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_two, container, false);

        // -------------------------
        // Init Volley
        // -------------------------
        queue = Volley.newRequestQueue(requireContext());

        // -------------------------
        // Init Session
        // -------------------------
        session = new SessionManager(requireContext());

        if (!session.isLoggedIn()) {
            Toast.makeText(getContext(),
                    "Please login again",
                    Toast.LENGTH_LONG).show();
            return view;
        }

        householdId = session.getHouseholdId();

        if (householdId == -1) {
            Toast.makeText(getContext(),
                    "Household not found",
                    Toast.LENGTH_LONG).show();
            return view;
        }

        // -------------------------
        // RecyclerView Setup
        // -------------------------
        RecyclerView recycler = view.findViewById(R.id.recycler_pantry);

        recycler.setLayoutManager(
                new LinearLayoutManager(getContext())
        );

        adapter = new PantryAdapter();
        recycler.setAdapter(adapter);

        // -------------------------
        // Views
        // -------------------------
        EditText etSearch = view.findViewById(R.id.et_search);
        FloatingActionButton btnAdd = view.findViewById(R.id.btn_add);

        // -------------------------
        // Load Pantry
        // -------------------------
        loadPantryItems();

        // -------------------------
        // Search
        // -------------------------
        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start,
                                          int count,
                                          int after) {}

            @Override
            public void onTextChanged(CharSequence s,
                                      int start,
                                      int before,
                                      int count) {

                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // -------------------------
        // Add Button
        // -------------------------
        btnAdd.setOnClickListener(v -> showAddDialog());

        return view;
    }

    // =====================================================
    // 📥 LOAD PANTRY (GET)
    // =====================================================
    private void loadPantryItems() {

        Uri uri = Uri.parse(BASE_URL + "pantry/get_pantry.php")
                .buildUpon()
                .appendQueryParameter(
                        "household_id",
                        String.valueOf(householdId)
                )
                .build();

        JsonArrayRequest req = new JsonArrayRequest(
                Request.Method.GET,
                uri.toString(),
                null,

                response -> {

                    allItems.clear();

                    for (int i = 0; i < response.length(); i++) {

                        JSONObject o = response.optJSONObject(i);
                        if (o == null) continue;

                        allItems.add(new PantryItem(
                                o.optInt("id"),
                                o.optString("name"),
                                "", // no category in DB
                                o.optDouble("quantity"),
                                o.optString("unit"),
                                7 // default days
                        ));
                    }

                    adapter.setItems(allItems);
                },

                error -> Toast.makeText(
                        requireContext(),
                        "Failed to load pantry",
                        Toast.LENGTH_SHORT
                ).show()
        );

        queue.add(req);
    }

    // =====================================================
    // 🔍 SEARCH FILTER
    // =====================================================
    private void filter(String query) {

        String q = query.trim().toLowerCase(Locale.ROOT);

        if (q.isEmpty()) {
            adapter.setItems(allItems);
            return;
        }

        List<PantryItem> filtered = new ArrayList<>();

        for (PantryItem item : allItems) {

            if (item.name.toLowerCase(Locale.ROOT)
                    .contains(q)) {

                filtered.add(item);
            }
        }

        adapter.setItems(filtered);
    }

    // =====================================================
    // ➕ ADD ITEM DIALOG
    // =====================================================
    private void showAddDialog() {

        if (getContext() == null) return;

        View dialogView = LayoutInflater
                .from(getContext())
                .inflate(R.layout.dialog_add_item, null);

        EditText etName = dialogView.findViewById(R.id.et_name);
        EditText etQty = dialogView.findViewById(R.id.et_qty);
        EditText etUnit = dialogView.findViewById(R.id.et_unit);

        new AlertDialog.Builder(getContext())
                .setTitle("Add Pantry Item")
                .setView(dialogView)

                .setPositiveButton("Add", (d, which) -> {

                    String name =
                            etName.getText().toString().trim();

                    String unit =
                            etUnit.getText().toString().trim();

                    double qty =
                            safeDouble(
                                    etQty.getText().toString().trim()
                            );

                    if (name.isEmpty()) name = "Unnamed";
                    if (unit.isEmpty()) unit = "pcs";

                    addPantryItem(name, qty, unit);
                })

                .setNegativeButton("Cancel", null)
                .show();
    }

    // =====================================================
    // 📤 ADD ITEM (POST)
    // =====================================================
    private void addPantryItem(String name,
                               double qty,
                               String unit) {

        StringRequest req = new StringRequest(
                Request.Method.POST,
                BASE_URL + "pantry/add_pantry.php",

                response -> {
                    Toast.makeText(
                            requireContext(),
                            "Item added",
                            Toast.LENGTH_SHORT
                    ).show();

                    loadPantryItems();
                },

                error -> {
                    Toast.makeText(
                            requireContext(),
                            "Add failed",
                            Toast.LENGTH_LONG
                    ).show();

                    error.printStackTrace();
                }
        ) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> p = new HashMap<>();

                p.put("household_id",
                        String.valueOf(householdId));

                p.put("name", name);

                p.put("quantity",
                        String.valueOf(qty));

                p.put("unit", unit);

                return p;
            }
        };

        queue.add(req);
    }

    // =====================================================
    // 🛟 HELPER
    // =====================================================
    private double safeDouble(String s) {

        try {
            return Double.parseDouble(s);
        }
        catch (Exception e) {
            return 1;
        }
    }
}