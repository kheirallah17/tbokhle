package com.example.tbokhle.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tbokhle.R;
import com.example.tbokhle.adapters.RecipesAdapter;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;

public class FragmentFour extends Fragment {

    RecyclerView recView;
    RecipesAdapter adapter;

    SearchView searchView;
    MaterialButton btnAll, btnQuick, btnVeg, btnProtein;

    String currentSearch = "";
    String currentFilter = "all";

    RequestQueue queue;

    public FragmentFour() {
        super(R.layout.fragment_four);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        queue = Volley.newRequestQueue(requireContext());

        recView = view.findViewById(R.id.recViewRecipes);
        recView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchView = view.findViewById(R.id.searchView);
        btnAll = view.findViewById(R.id.btnAll);
        btnQuick = view.findViewById(R.id.btnQuick);
        btnVeg = view.findViewById(R.id.btnVeg);
        btnProtein = view.findViewById(R.id.btnProtein);

        //  first load will set adapter
        btnAll.setOnClickListener(filterClickListener("all"));
        btnQuick.setOnClickListener(filterClickListener("quick"));
        btnVeg.setOnClickListener(filterClickListener("veg"));
        btnProtein.setOnClickListener(filterClickListener("protein"));

        searchView.setOnQueryTextListener(searchListener());

        getAllRecipesFromDb(); // initial load
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllRecipesFromDb();
    }

    // ==========================
    //  Filter Listener
    // ==========================
    private View.OnClickListener filterClickListener(final String filter) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentFilter = filter;
                getAllRecipesFromDb();
            }
        };
    }

    // ==========================
    // Search Listener
    // ==========================
    private SearchView.OnQueryTextListener searchListener() {
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearch = (query == null) ? "" : query.trim();
                getAllRecipesFromDb();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentSearch = (newText == null) ? "" : newText.trim();
                getAllRecipesFromDb();
                return true;
            }
        };
    }

    // ==========================
    // MAIN LOAD METHOD
    // ==========================
    public void getAllRecipesFromDb() {

        String url = buildUrl();

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        // create adapter again
                        adapter = new RecipesAdapter(requireContext(), response);
                        recView.setAdapter(adapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showVolleyError(error);
                    }
                }
        );

        queue.add(request);
    }

    private String buildUrl() {
        Uri uri = Uri.parse("http://10.0.2.2/tbokhle/getallrecipes.php")
                .buildUpon()
                .appendQueryParameter("search", currentSearch)
                .appendQueryParameter("filter", currentFilter)
                .build();

        return uri.toString();
    }

    private void showVolleyError(VolleyError error) {
        error.printStackTrace();
        Toast.makeText(requireContext(), "Failed to load recipes", Toast.LENGTH_SHORT).show();
    }

    // ==========================
    // CALLED FROM ADAPTER VIEW HOLDER CLICK
    // ==========================
    public void openRecipeDetails(int recipeId) {
        //  navigate to recipe details screen
        Toast.makeText(requireContext(), "Recipe ID: " + recipeId, Toast.LENGTH_SHORT).show();
    }
}

