package com.example.tbokhle.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.example.tbokhle.R;
import com.example.tbokhle.model.Recipe;
import com.example.tbokhle.adapters.RecipesAdapter;

import java.util.ArrayList;
import java.util.List;


public class FragmentFour extends Fragment {

    RecyclerView recView;

    public FragmentFour() {
        super(R.layout.fragment_four);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recView = view.findViewById(R.id.recViewRecipes);
        recView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Recipe> list = new ArrayList<>();
        list.add(new Recipe("Fresh Veggie Delight", "10/11 ingredients", "25 min", "Easy", "350 kcal", R.drawable.food));
        list.add(new Recipe("Pasta Primavera", "8/10 ingredients", "10 min", "Medium", "450 kcal", R.drawable.food));

        RecipesAdapter adapter = new RecipesAdapter(list,getContext());
        recView.setAdapter(adapter);
    }
}
