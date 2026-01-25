package com.example.tbokhle.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tbokhle.LoginActivity;
import com.example.tbokhle.R;
import com.example.tbokhle.SessionManager;
import com.example.tbokhle.adapters.HouseholdsAdapter;
import com.example.tbokhle.adapters.MembersAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.HashMap;
import java.util.Map;

public class FragmentSix extends Fragment {

    // ==========================
    // UI
    // ==========================
    RecyclerView recViewMembers;
    RecyclerView recHouseholds;

    // ==========================
    // NETWORK
    // ==========================
    RequestQueue queue;

    // ==========================
    // SESSION
    // ==========================
    SessionManager session;
    int userId;

    public FragmentSix() {
        super(R.layout.fragment_six);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        session = new SessionManager(requireContext());

        // 🔒 Security check
        if (!session.isLoggedIn()) {
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
            return;
        }

        userId = Integer.parseInt(session.getUserId());
        queue = Volley.newRequestQueue(requireContext());

        // ==========================
        // FAMILY MEMBERS
        // ==========================
        recViewMembers = view.findViewById(R.id.recViewMembers);
        recViewMembers.setLayoutManager(new LinearLayoutManager(getContext()));
        recViewMembers.setNestedScrollingEnabled(false);

        // ==========================
        // HOUSEHOLDS
        // ==========================
        recHouseholds = view.findViewById(R.id.rvRecentActivity);
        recHouseholds.setLayoutManager(new LinearLayoutManager(getContext()));
        recHouseholds.setNestedScrollingEnabled(false);

        // ==========================
        // INVITE BUTTON
        // ==========================
        MaterialButton btnInvite = view.findViewById(R.id.btnInvite);
        btnInvite.setOnClickListener(v -> showInviteDialog());

        setupTopMenu();
        setupSwitches(view);

        // ==========================
        // LOAD DATA
        // ==========================
        loadHouseholds();          // sets default household if needed
        loadHouseholdMembers();    // loads members for active household
    }

    // =====================================================
    // FAMILY MEMBERS
    // =====================================================
    public void loadHouseholdMembers() {

        int householdId = session.getHouseholdId(); // ✅ SINGLE SOURCE

        if (householdId == -1) return;

        Uri uri = Uri.parse("http://10.0.2.2/tbokhle/get_household_members.php")
                .buildUpon()
                .appendQueryParameter("household_id", String.valueOf(householdId))
                .build();

        JsonArrayRequest req = new JsonArrayRequest(
                Request.Method.GET,
                uri.toString(),
                null,
                response -> recViewMembers.setAdapter(
                        new MembersAdapter(
                                requireContext(),
                                response,
                                this,
                                userId
                        )
                ),
                error -> Toast.makeText(
                        requireContext(),
                        "Failed to load family members",
                        Toast.LENGTH_SHORT
                ).show()
        );

        queue.add(req);
    }

    // =====================================================
    // INVITE MEMBER
    // =====================================================
    private void showInviteDialog() {
        EditText input = new EditText(requireContext());
        input.setHint("User email");

        new AlertDialog.Builder(requireContext())
                .setTitle("Invite member")
                .setView(input)
                .setPositiveButton("Invite", (d, w) -> {
                    String email = input.getText().toString().trim();
                    if (!email.isEmpty()) {
                        inviteMember(email);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void inviteMember(String email) {

        int householdId = session.getHouseholdId(); // ✅ ALWAYS FROM SESSION

        String url = "http://10.0.2.2/tbokhle/invite_household_member.php";

        StringRequest req = new StringRequest(
                Request.Method.POST,
                url,
                res -> {
                    Toast.makeText(requireContext(),
                            "Member invited",
                            Toast.LENGTH_SHORT).show();
                    loadHouseholdMembers();
                },
                err -> Toast.makeText(
                        requireContext(),
                        "Invite failed",
                        Toast.LENGTH_SHORT
                ).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("household_id", String.valueOf(householdId));
                p.put("email", email);
                return p;
            }
        };

        queue.add(req);
    }

    // =====================================================
    // HOUSEHOLDS
    // =====================================================
    private void loadHouseholds() {

        Uri uri = Uri.parse("http://10.0.2.2/tbokhle/get_user_households.php")
                .buildUpon()
                .appendQueryParameter("user_id", String.valueOf(userId))
                .build();

        JsonArrayRequest req = new JsonArrayRequest(
                Request.Method.GET,
                uri.toString(),
                null,
                response -> {

                    // ✅ Set default household ONLY if none exists
                    if (session.getHouseholdId() == -1 && response.length() > 0) {
                        try {
                            session.setHouseholdId(
                                    response.getJSONObject(0).getInt("id")
                            );
                        } catch (Exception ignored) {}
                    }

                    recHouseholds.setAdapter(
                            new HouseholdsAdapter(
                                    requireContext(),
                                    response,
                                    userId,
                                    () -> {
                                        loadHouseholds();
                                        loadHouseholdMembers(); // 🔄 refresh on switch
                                    }
                            )
                    );
                },
                error -> Toast.makeText(
                        requireContext(),
                        "Failed to load households",
                        Toast.LENGTH_SHORT
                ).show()
        );

        queue.add(req);
    }

    // =====================================================
    // TOP MENU
    // =====================================================
    private void setupTopMenu() {
        MenuHost host = requireActivity();
        host.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
                inflater.inflate(R.menu.menu_household_topbar, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_logout) {

                    session.logout();

                    Intent i = new Intent(requireContext(), LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    // =====================================================
    // SWITCHES
    // =====================================================
    private void setupSwitches(View view) {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("sync_prefs", Context.MODE_PRIVATE);

        SwitchMaterial swShopping = view.findViewById(R.id.swShopping);
        SwitchMaterial swPantry = view.findViewById(R.id.swPantry);
        SwitchMaterial swRecipes = view.findViewById(R.id.swRecipes);
        SwitchMaterial swNotifications = view.findViewById(R.id.swNotifications);

        swShopping.setChecked(prefs.getBoolean("sync_shopping", true));
        swPantry.setChecked(prefs.getBoolean("sync_pantry", true));
        swRecipes.setChecked(prefs.getBoolean("sync_recipes", false));
        swNotifications.setChecked(prefs.getBoolean("sync_notifications", true));

        swShopping.setOnCheckedChangeListener((b, v) ->
                prefs.edit().putBoolean("sync_shopping", v).apply());
        swPantry.setOnCheckedChangeListener((b, v) ->
                prefs.edit().putBoolean("sync_pantry", v).apply());
        swRecipes.setOnCheckedChangeListener((b, v) ->
                prefs.edit().putBoolean("sync_recipes", v).apply());
        swNotifications.setOnCheckedChangeListener((b, v) ->
                prefs.edit().putBoolean("sync_notifications", v).apply());
    }
}
