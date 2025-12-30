package com.example.tbokhle.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tbokhle.R;
import com.example.tbokhle.adapters.MembersAdapter;
import com.example.tbokhle.model.Member;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

public class FragmentSix extends Fragment {

    RecyclerView recViewMembers;

    public FragmentSix() {
        super(R.layout.fragment_six);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        RecyclerView member = view.findViewById(R.id.recViewMembers);
        RecyclerView recent = view.findViewById(R.id.rvRecentActivity);

        member.setNestedScrollingEnabled(false);
        recent.setNestedScrollingEnabled(false);

        SwitchMaterial swShopping = view.findViewById(R.id.swShopping);
        SwitchMaterial swPantry = view.findViewById(R.id.swPantry);
        SwitchMaterial swRecipes = view.findViewById(R.id.swRecipes);
        SwitchMaterial swNotifications = view.findViewById(R.id.swNotifications);

        // optional: set initial states like the screenshot
        SharedPreferences prefs = requireContext().getSharedPreferences("sync_prefs", Context.MODE_PRIVATE);

        swShopping.setChecked(prefs.getBoolean("sync_shopping", true));
        swPantry.setChecked(prefs.getBoolean("sync_pantry", true));
        swRecipes.setChecked(prefs.getBoolean("sync_recipes", false));
        swNotifications.setChecked(prefs.getBoolean("sync_notifications", true));

        swShopping.setOnCheckedChangeListener((b, v) -> prefs.edit().putBoolean("sync_shopping", v).apply());
        swPantry.setOnCheckedChangeListener((b, v) -> prefs.edit().putBoolean("sync_pantry", v).apply());
        swRecipes.setOnCheckedChangeListener((b, v) -> prefs.edit().putBoolean("sync_recipes", v).apply());
        swNotifications.setOnCheckedChangeListener((b, v) -> prefs.edit().putBoolean("sync_notifications", v).apply());


        recViewMembers = view.findViewById(R.id.recViewMembers);
        recViewMembers.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Member> members = new ArrayList<>();
        members.add(new Member("You", "you@email.com", true, R.drawable.outline_account_circle_24));
        members.add(new Member("Ali", "ali@email.com", false, R.drawable.outline_account_circle_24));

        MembersAdapter adapter = new MembersAdapter(requireContext(), members);
        recViewMembers.setAdapter(adapter);
        RecyclerView rv = view.findViewById(R.id.rvRecentActivity);
        rv.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(requireContext()));

        java.util.List<com.example.tbokhle.model.RecentActivity> data = new java.util.ArrayList<>();
        data.add(new com.example.tbokhle.model.RecentActivity(
                "Sarah added Milk to shopping list", "2h ago"));

        data.add(new com.example.tbokhle.model.RecentActivity(
                "John marked 3 items as purchased", "5h ago"));

        data.add(new com.example.tbokhle.model.RecentActivity(
                "Sarah added new recipe (Buddha Bowl)", "1d ago"));

        rv.setAdapter(new com.example.tbokhle.adapters.RecentActivityAdapter(requireContext(), data));

    }
}