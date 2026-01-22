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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tbokhle.R;
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

    // 🔗 API base
    private static final String BASE_URL =
            "http://10.0.2.2/Tbokhle/";

    private PantryAdapter adapter;
    private final List<PantryItem> allItems = new ArrayList<>();

    // later this should come from login
    private int householdId = 1;

    public FragmentTwo() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_two, container, false);

        RecyclerView recycler = view.findViewById(R.id.recycler_pantry);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PantryAdapter();
        recycler.setAdapter(adapter);

        EditText etSearch = view.findViewById(R.id.et_search);
        FloatingActionButton btnAdd = view.findViewById(R.id.btn_add);

        loadPantryItems(); // ✅ FROM DB

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnAdd.setOnClickListener(v -> showAddDialog());

        return view;
    }

    // -----------------------------
    // 📥 LOAD PANTRY FROM DB
    // -----------------------------
    private void loadPantryItems() {

        Uri uri = Uri.parse(BASE_URL + "pantry/get_pantry.php")
                .buildUpon()
                .appendQueryParameter("household_id", String.valueOf(householdId))
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
                                o.optString("category"),
                                o.optDouble("quantity"),
                                o.optString("unit"),
                                o.optInt("days_left", 7) // backend can compute
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

        Volley.newRequestQueue(requireContext()).add(req);
    }

    // -----------------------------
    // 🔍 SEARCH FILTER
    // -----------------------------
    private void filter(String query) {
        String q = query.trim().toLowerCase(Locale.ROOT);

        if (q.isEmpty()) {
            adapter.setItems(allItems);
            return;
        }

        List<PantryItem> filtered = new ArrayList<>();
        for (PantryItem item : allItems) {
            if (item.name.toLowerCase(Locale.ROOT).contains(q) ||
                    item.category.toLowerCase(Locale.ROOT).contains(q)) {
                filtered.add(item);
            }
        }
        adapter.setItems(filtered);
    }

    // -----------------------------
    // ➕ ADD ITEM (POST → DB)
    // -----------------------------
    private void showAddDialog() {

        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_add_item, null);

        EditText etName = dialogView.findViewById(R.id.et_name);
        EditText etCategory = dialogView.findViewById(R.id.et_category);
        EditText etQty = dialogView.findViewById(R.id.et_qty);
        EditText etUnit = dialogView.findViewById(R.id.et_unit);

        new AlertDialog.Builder(getContext())
                .setTitle("Add Pantry Item")
                .setView(dialogView)
                .setPositiveButton("Add", (d, which) -> {

                    String name = etName.getText().toString().trim();
                    String category = etCategory.getText().toString().trim();
                    String unit = etUnit.getText().toString().trim();
                    double qty = safeDouble(etQty.getText().toString().trim());

                    if (name.isEmpty()) name = "Unnamed";
                    if (category.isEmpty()) category = "Other";
                    if (unit.isEmpty()) unit = "pcs";

                    addPantryItem(name, category, qty, unit);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addPantryItem(String name, String category, double qty, String unit) {

        StringRequest req = new StringRequest(
                Request.Method.POST,
                BASE_URL + "pantry/add_pantry.php",
                response -> loadPantryItems(), // refresh list
                error -> Toast.makeText(
                        requireContext(),
                        "Failed to add item",
                        Toast.LENGTH_SHORT
                ).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("household_id", String.valueOf(householdId));
                p.put("name", name);
                p.put("category", category);
                p.put("quantity", String.valueOf(qty));
                p.put("unit", unit);
                return p;
            }
        };

        Volley.newRequestQueue(requireContext()).add(req);
    }

    // -----------------------------
    // 🛟 HELPERS
    // -----------------------------
    private double safeDouble(String s) {
        try { return Double.parseDouble(s); }
        catch (Exception e) { return 1; }
    }
}
