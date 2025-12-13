package com.example.tbokhle.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tbokhle.R;
import com.example.tbokhle.adapters.PantryAdapter;
import com.example.tbokhle.model.PantryItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FragmentTwo extends Fragment {

    private PantryAdapter adapter;
    private final List<PantryItem> allItems = new ArrayList<>();

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

        seedDummyData();
        adapter.setItems(allItems);

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

    private void seedDummyData() {
        allItems.clear();
        allItems.add(new PantryItem("Milk", "Dairy", 1, "L", 2));
        allItems.add(new PantryItem("Tomatoes", "Vegetables", 5, "pieces", 5));
        allItems.add(new PantryItem("Rice", "Grains", 2, "kg", 180));
        allItems.add(new PantryItem("Chicken Breast", "Meat", 600, "g", 1));
        allItems.add(new PantryItem("Eggs", "Dairy", 10, "pieces", 7));
        allItems.add(new PantryItem("Bread", "Bakery", 1, "loaf", 3));
        allItems.add(new PantryItem("Olive Oil", "Oils", 500, "ml", 365));
        allItems.add(new PantryItem("Pasta", "Grains", 500, "g", 365));
    }

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

    private void showAddDialog() {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_item, null);

        EditText etName = dialogView.findViewById(R.id.et_name);
        EditText etCategory = dialogView.findViewById(R.id.et_category);
        EditText etQty = dialogView.findViewById(R.id.et_qty);
        EditText etUnit = dialogView.findViewById(R.id.et_unit);
        EditText etDays = dialogView.findViewById(R.id.et_days);

        new AlertDialog.Builder(getContext())
                .setTitle("Add Pantry Item")
                .setView(dialogView)
                .setPositiveButton("Add", (d, which) -> {
                    String name = etName.getText().toString().trim();
                    String category = etCategory.getText().toString().trim();
                    String unit = etUnit.getText().toString().trim();

                    double qty = safeDouble(etQty.getText().toString().trim());
                    int days = safeInt(etDays.getText().toString().trim());

                    if (name.isEmpty()) name = "Unnamed";
                    if (category.isEmpty()) category = "Other";
                    if (unit.isEmpty()) unit = "pcs";

                    allItems.add(0, new PantryItem(name, category, qty, unit, days));
                    adapter.setItems(allItems);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private double safeDouble(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return 1; }
    }

    private int safeInt(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 7; }
    }
}
