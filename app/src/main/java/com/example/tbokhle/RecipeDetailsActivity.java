package com.example.tbokhle;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.tbokhle.adapters.IngredientsStatusAdapter;
import com.example.tbokhle.adapters.InstructionsAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

public class RecipeDetailsActivity extends AppCompatActivity {

    ImageView imgRecipe;
    TextView tvName, tvSummary;

    RecyclerView recIngredients;
    RecyclerView recInstructions;

    IngredientsStatusAdapter ingredientsAdapter;
    InstructionsAdapter instructionsAdapter;

    RequestQueue queue;

    int recipeId;
    int householdId = 1; // TEMP: hardcode until you connect login/households

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        queue = Volley.newRequestQueue(this);

        imgRecipe = findViewById(R.id.imgRecipeDetails);
        tvName = findViewById(R.id.tvRecipeNameDetails);
        tvSummary = findViewById(R.id.tvIngredientsSummary);

        recIngredients = findViewById(R.id.recIngredientsStatus);
        recInstructions = findViewById(R.id.recInstructions);

        // Ingredients list
        recIngredients.setLayoutManager(new LinearLayoutManager(this));
        ingredientsAdapter = new IngredientsStatusAdapter(this, new JSONArray());
        recIngredients.setAdapter(ingredientsAdapter);

        // Instructions list
        recInstructions.setLayoutManager(new LinearLayoutManager(this));
        instructionsAdapter = new InstructionsAdapter(this, new JSONArray());
        recInstructions.setAdapter(instructionsAdapter);

        recipeId = getIntent().getIntExtra("recipe_id", -1);
        if (recipeId == -1) {
            Toast.makeText(this, "Missing recipe_id", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadRecipeDetails();        // your existing endpoint (recipe + ingredients status)
        loadRecipeInstructions();   // new endpoint (steps)
    }

    // ==========================
    // 1) LOAD RECIPE + INGREDIENTS STATUS (EXISTING)
    // ==========================
    private void loadRecipeDetails() {
        Uri uri = Uri.parse("http://10.0.2.2/tbokhle/get_recipe_details.php")
                .buildUpon()
                .appendQueryParameter("recipe_id", String.valueOf(recipeId))
                .appendQueryParameter("household_id", String.valueOf(householdId))
                .build();

        String url = uri.toString();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONObject recipe = response.getJSONObject("recipe");
                        tvName.setText(recipe.optString("name", ""));

                        String imageUrl = recipe.optString("image_url", "");
                        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                            Glide.with(this).load(imageUrl)
                                    .placeholder(R.drawable.food)
                                    .error(R.drawable.food)
                                    .into(imgRecipe);
                        } else {
                            imgRecipe.setImageResource(R.drawable.food);
                        }

                        String summary = response.optString("summary", "0/0 available");
                        tvSummary.setText(summary);

                        JSONArray items = response.getJSONArray("ingredients");
                        ingredientsAdapter = new IngredientsStatusAdapter(this, items);
                        recIngredients.setAdapter(ingredientsAdapter);

                    } catch (Exception e) {
                        Toast.makeText(this, "Parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                this::showVolleyErrorDetails
        );

        queue.add(request);
    }

    // ==========================
    // 2) LOAD INSTRUCTIONS (NEW)
    // ==========================
    private void loadRecipeInstructions() {

        Uri uri = Uri.parse("http://10.0.2.2/tbokhle/get_recipe_instructions.php")
                .buildUpon()
                .appendQueryParameter("recipe_id", String.valueOf(recipeId))
                .build();

        String url = uri.toString();

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    // If no steps in DB, show 1 placeholder step
                    if (response == null || response.length() == 0) {
                        JSONArray fallback = new JSONArray();
                        fallback.put(makeFallbackStep(1, "No instructions yet. Add steps in recipe_instructions table."));
                        instructionsAdapter = new InstructionsAdapter(this, fallback);
                        recInstructions.setAdapter(instructionsAdapter);
                    } else {
                        instructionsAdapter = new InstructionsAdapter(this, response);
                        recInstructions.setAdapter(instructionsAdapter);
                    }
                },
                error -> {
                    // On error, show fallback step (but do NOT crash)
                    JSONArray fallback = new JSONArray();
                    fallback.put(makeFallbackStep(1, "Failed to load instructions (check PHP returns JSON array)."));
                    instructionsAdapter = new InstructionsAdapter(this, fallback);
                    recInstructions.setAdapter(instructionsAdapter);
                }
        );

        queue.add(request);
    }

    private JSONObject makeFallbackStep(int stepNo, String text) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("step_no", stepNo);
            obj.put("instruction", text);
        } catch (Exception ignored) {}
        return obj;
    }

    private void showVolleyErrorDetails(VolleyError error) {
        error.printStackTrace();
        Toast.makeText(this, "Failed to load details", Toast.LENGTH_SHORT).show();
    }
}
