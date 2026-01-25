package com.example.tbokhle.fragments;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.example.tbokhle.adapters.ShoppingAdapter;
import com.example.tbokhle.model.ShoppingItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentFive extends Fragment
        implements ShoppingAdapter.Listener {

    // 🔗 API base
    private static final String BASE_URL =
            "http://10.0.2.2/Tbokhle/";

    private ShoppingAdapter adapter;
    private final List<ShoppingItem> items = new ArrayList<>();

    private TextView tvProgress;
    private ProgressBar progressBar;
    private RecyclerView recycler;
    private TextView tvToggle;

    private boolean hidden = false;

    // Session
    private SessionManager session;
    private int householdId;

    // Volley
    private RequestQueue queue;

    public FragmentFive() {}

    // =====================================================
    // CREATE VIEW
    // =====================================================
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_five, container, false);

        // -------------------------
        // Volley
        // -------------------------
        queue = Volley.newRequestQueue(requireContext());

        // -------------------------
        // Session
        // -------------------------
        session = new SessionManager(requireContext());

        if (!session.isLoggedIn()) {
            Toast.makeText(
                    getContext(),
                    "Please login again",
                    Toast.LENGTH_LONG
            ).show();

            return view;
        }

        householdId = session.getHouseholdId();

        if (householdId == -1) {
            Toast.makeText(
                    getContext(),
                    "Household not found",
                    Toast.LENGTH_LONG
            ).show();

            return view;
        }

        // -------------------------
        // Views
        // -------------------------
        tvProgress = view.findViewById(R.id.tv_progress_text);
        progressBar = view.findViewById(R.id.progress_bar);
        recycler = view.findViewById(R.id.recycler_shopping);
        tvToggle = view.findViewById(R.id.tv_toggle);

        FloatingActionButton btnAdd =
                view.findViewById(R.id.btn_add_shopping);

        // -------------------------
        // Recycler
        // -------------------------
        recycler.setLayoutManager(
                new LinearLayoutManager(getContext())
        );

        adapter = new ShoppingAdapter(this);
        recycler.setAdapter(adapter);

        // -------------------------
        // Load Data
        // -------------------------
        loadShoppingItems();

        // -------------------------
        // Hide / Show
        // -------------------------
        tvToggle.setOnClickListener(v -> {

            hidden = !hidden;

            recycler.setVisibility(
                    hidden ? View.GONE : View.VISIBLE
            );

            tvToggle.setText(
                    hidden ? "Show" : "Hide"
            );
        });

        // -------------------------
        // Add Button
        // -------------------------
        btnAdd.setOnClickListener(v -> showAddDialog());

        return view;
    }

    // =====================================================
    // LOAD SHOPPING LIST (GET)
    // =====================================================
    private void loadShoppingItems() {

        Uri uri = Uri.parse(BASE_URL + "shopping/get_shopping.php")
                .buildUpon()
                .appendQueryParameter(
                        "household_id",
                        String.valueOf(householdId)
                )
                .build();

        JsonArrayRequest req =
                new JsonArrayRequest(
                        Request.Method.GET,
                        uri.toString(),
                        null,

                        response -> {

                            items.clear();

                            for (int i = 0;
                                 i < response.length();
                                 i++) {

                                JSONObject o =
                                        response.optJSONObject(i);

                                if (o == null) continue;

                                items.add(
                                        new ShoppingItem(
                                                o.optInt("id"),
                                                o.optString("name"),
                                                o.optDouble("quantity"),
                                                o.optString("unit"),
                                                o.optString("category"),
                                                o.optString("added_by"),
                                                o.optBoolean("is_done")
                                        )
                                );
                            }

                            adapter.setItems(items);
                            updateProgressUI();
                        },

                        error -> showVolleyError(error)
                );

        queue.add(req);
    }

    // =====================================================
    // UPDATE PROGRESS
    // =====================================================
    private void updateProgressUI() {

        int total = items.size();
        int done = 0;

        for (ShoppingItem i : items) {
            if (i.done) done++;
        }

        tvProgress.setText(
                done + " of " + total + " items completed"
        );

        int percent =
                (total == 0)
                        ? 0
                        : (done * 100 / total);

        progressBar.setProgress(percent);
    }

    // =====================================================
    // ADD DIALOG
    // =====================================================
    private void showAddDialog() {

        if (getContext() == null) return;

        View dialogView =
                LayoutInflater.from(getContext())
                        .inflate(
                                R.layout.dialog_add_shopping,
                                null
                        );

        EditText etName =
                dialogView.findViewById(R.id.et_name);

        EditText etCategory =
                dialogView.findViewById(R.id.et_category);

        EditText etQty =
                dialogView.findViewById(R.id.et_qty);

        EditText etUnit =
                dialogView.findViewById(R.id.et_unit);

        EditText etAddedBy =
                dialogView.findViewById(R.id.et_added_by);

        new AlertDialog.Builder(getContext())
                .setTitle("Add Shopping Item")
                .setView(dialogView)

                .setPositiveButton("Add",
                        (d, w) -> {

                            String name =
                                    etName.getText().toString().trim();

                            String category =
                                    etCategory.getText().toString().trim();

                            String unit =
                                    etUnit.getText().toString().trim();

                            String addedBy =
                                    etAddedBy.getText().toString().trim();

                            double qty =
                                    safeDouble(
                                            etQty.getText()
                                                    .toString()
                                                    .trim()
                                    );

                            if (name.isEmpty())
                                name = "Unnamed";

                            if (category.isEmpty())
                                category = "Other";

                            if (unit.isEmpty())
                                unit = "pcs";

                            if (addedBy.isEmpty())
                                addedBy = "You";

                            addShoppingItem(
                                    name,
                                    category,
                                    qty,
                                    unit,
                                    addedBy
                            );
                        })

                .setNegativeButton("Cancel", null)
                .show();
    }

    // =====================================================
    // ADD ITEM (POST)
    // =====================================================
    private void addShoppingItem(String name,
                                 String category,
                                 double qty,
                                 String unit,
                                 String addedBy) {

        StringRequest req =
                new StringRequest(
                        Request.Method.POST,
                        BASE_URL + "shopping/add_shopping.php",

                        response -> {
                            Toast.makeText(
                                    requireContext(),
                                    response,
                                    Toast.LENGTH_LONG
                            ).show();

                            loadShoppingItems();
                        },

                        this::showVolleyError
                ) {

                    @Override
                    protected Map<String, String> getParams() {

                        Map<String, String> p =
                                new HashMap<>();

                        p.put(
                                "household_id",
                                String.valueOf(householdId)
                        );

                        p.put("name", name);
                        p.put("category", category);
                        p.put("quantity", String.valueOf(qty));
                        p.put("unit", unit);
                        p.put("added_by", addedBy);
                        p.put("is_auto", "0");

                        return p;
                    }
                };

        queue.add(req);
    }

    // =====================================================
    // ERROR HANDLER
    // =====================================================
    private void showVolleyError(com.android.volley.VolleyError error) {

        String msg = error.getMessage();

        if (error.networkResponse != null &&
                error.networkResponse.data != null) {

            msg = new String(error.networkResponse.data);
        }

        Toast.makeText(
                requireContext(),
                msg,
                Toast.LENGTH_LONG
        ).show();

        error.printStackTrace();
    }

    // =====================================================
    // HELPERS
    // =====================================================
    private double safeDouble(String s) {

        try {
            return Double.parseDouble(s);
        }
        catch (Exception e) {
            return 1;
        }
    }

    // =====================================================
    // ADAPTER CALLBACKS
    // =====================================================
    @Override
    public void onToggleDone(int position, boolean done) {

        if (position < 0 ||
                position >= items.size()) return;

        items.get(position).done = done;

        updateProgressUI();
    }

    @Override
    public void onDelete(int position) {

        if (position < 0 ||
                position >= items.size()) return;

        items.remove(position);

        adapter.setItems(items);

        updateProgressUI();
    }
}