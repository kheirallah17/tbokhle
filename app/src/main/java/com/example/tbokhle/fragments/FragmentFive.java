package com.example.tbokhle.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tbokhle.R;
import com.example.tbokhle.adapters.ShoppingAdapter;
import com.example.tbokhle.model.ShoppingItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FragmentFive extends Fragment implements ShoppingAdapter.Listener {

    private ShoppingAdapter adapter;
    private final List<ShoppingItem> items = new ArrayList<>();

    private TextView tvProgress;
    private ProgressBar progressBar;
    private RecyclerView recycler;
    private TextView tvToggle;
    private boolean hidden = false;

    public FragmentFive() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_five, container, false);

        tvProgress = view.findViewById(R.id.tv_progress_text);
        progressBar = view.findViewById(R.id.progress_bar);
        recycler = view.findViewById(R.id.recycler_shopping);
        tvToggle = view.findViewById(R.id.tv_toggle);
        FloatingActionButton btnAdd = view.findViewById(R.id.btn_add_shopping);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ShoppingAdapter(this);
        recycler.setAdapter(adapter);

        seedDummyData();
        adapter.setItems(items);
        updateProgressUI();

        tvToggle.setOnClickListener(v -> {
            hidden = !hidden;
            recycler.setVisibility(hidden ? View.GONE : View.VISIBLE);
            tvToggle.setText(hidden ? "Show" : "Hide");
        });

        btnAdd.setOnClickListener(v -> showAddDialog());

        return view;
    }

    private void seedDummyData() {
        items.clear();
        items.add(new ShoppingItem("Milk", 2, "L", "Dairy", "You", false));
        items.add(new ShoppingItem("Tomatoes", 1, "kg", "Vegetables", "Sarah", true));
        items.add(new ShoppingItem("Chicken Breast", 600, "g", "Meat", "You", false));
        items.add(new ShoppingItem("Bread", 1, "loaf", "Bakery", "John", false));
        items.add(new ShoppingItem("Olive Oil", 500, "ml", "Oils", "You", false));
        items.add(new ShoppingItem("Pasta", 500, "g", "Grains", "Sarah", true));
        items.add(new ShoppingItem("Eggs", 12, "pieces", "Dairy", "You", false));
    }

    private void updateProgressUI() {
        int total = items.size();
        int done = 0;
        for (ShoppingItem i : items) if (i.done) done++;

        tvProgress.setText(done + " of " + total + " items completed");

        int percent = (total == 0) ? 0 : (int) ((done * 100.0f) / total);
        progressBar.setProgress(percent);
    }

    private void showAddDialog() {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_shopping, null);

        EditText etName = dialogView.findViewById(R.id.et_name);
        EditText etCategory = dialogView.findViewById(R.id.et_category);
        EditText etQty = dialogView.findViewById(R.id.et_qty);
        EditText etUnit = dialogView.findViewById(R.id.et_unit);
        EditText etAddedBy = dialogView.findViewById(R.id.et_added_by);

        new AlertDialog.Builder(getContext())
                .setTitle("Add Shopping Item")
                .setView(dialogView)
                .setPositiveButton("Add", (d, w) -> {
                    String name = etName.getText().toString().trim();
                    String category = etCategory.getText().toString().trim();
                    String unit = etUnit.getText().toString().trim();
                    String addedBy = etAddedBy.getText().toString().trim();

                    double qty = safeDouble(etQty.getText().toString().trim());

                    if (name.isEmpty()) name = "Unnamed";
                    if (category.isEmpty()) category = "Other";
                    if (unit.isEmpty()) unit = "pcs";
                    if (addedBy.isEmpty()) addedBy = "You";

                    items.add(0, new ShoppingItem(name, qty, unit, category, addedBy, false));
                    adapter.setItems(items);
                    updateProgressUI();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private double safeDouble(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return 1; }
    }

    @Override
    public void onToggleDone(int position, boolean done) {
        if (position < 0 || position >= items.size()) return;
        items.get(position).done = done;
        adapter.notifyItemChanged(position);
        updateProgressUI();
    }

    @Override
    public void onDelete(int position) {
        if (position < 0 || position >= items.size()) return;
        items.remove(position);
        adapter.setItems(items);
        updateProgressUI();
    }
}
