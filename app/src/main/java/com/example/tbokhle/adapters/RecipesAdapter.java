package com.example.tbokhle.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.tbokhle.R;
import com.example.tbokhle.fragments.FragmentFour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    private final Context context;
    private final FragmentFour fragment;
    private final RequestQueue queue;

    private JSONArray recipes;

    public RecipesAdapter(Context context, JSONArray recipes, FragmentFour fragment) {
        this.context = context;
        this.recipes = recipes;
        this.fragment = fragment;
        this.queue = Volley.newRequestQueue(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgRecipe;
        TextView tvIngredientsTag, tvRecipeName, tvTime, tvDifficulty, tvKcal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgRecipe = itemView.findViewById(R.id.imgRecipe);
            tvIngredientsTag = itemView.findViewById(R.id.tvIngredientsTag);
            tvRecipeName = itemView.findViewById(R.id.tvRecipeName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDifficulty = itemView.findViewById(R.id.tvDifficulty);
            tvKcal = itemView.findViewById(R.id.tvKcal);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        try {
                            JSONObject r = recipes.getJSONObject(pos);
                            int recipeId = r.getInt("id");

                            android.content.Intent i = new android.content.Intent(context, com.example.tbokhle.RecipeDetailsActivity.class);
                            i.putExtra("recipe_id", recipeId);
                            context.startActivity(i);

                        } catch (Exception e) {
                            Toast.makeText(context, "Click error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_recipes, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        try {
            JSONObject r = recipes.getJSONObject(position);

            h.tvRecipeName.setText(r.optString("name", ""));
            h.tvTime.setText(r.optString("time", ""));
            h.tvDifficulty.setText(r.optString("difficulty", ""));
            h.tvKcal.setText(r.optString("kcal", ""));

            // Placeholder until status loads
            h.tvIngredientsTag.setText("...");

            String imageUrl = r.optString("image_url", "");
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.food)
                        .error(R.drawable.food)
                        .into(h.imgRecipe);
            } else {
                h.imgRecipe.setImageResource(R.drawable.food);
            }

            int recipeId = r.getInt("id");
            loadIngredientStatusIntoTag(recipeId, h.tvIngredientsTag);

        } catch (JSONException e) {
            Log.e("RECIPE_BIND", e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return (recipes == null) ? 0 : recipes.length();
    }

    // ==========================
    // INGREDIENT STATUS (available / total)
    // ==========================
    private void loadIngredientStatusIntoTag(int recipeId, TextView targetTv) {

        Uri uri = Uri.parse("http://10.0.2.2/tbokhle/get_recipe_ingredients_status.php")
                .buildUpon()
                .appendQueryParameter("recipe_id", String.valueOf(recipeId))
                .build();

        String url = uri.toString();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        int available = response.getInt("availableCount");
                        int total = response.getInt("totalCount");
                        targetTv.setText(available + " / " + total + " ingredients");
                    } catch (Exception e) {
                        targetTv.setText("0 / 0 ingredients");
                        Log.e("ING_STATUS_PARSE", e.toString());
                    }
                },
                error -> {
                    targetTv.setText("0 / 0 ingredients");
                    Log.e("ING_STATUS_ERR", error.toString());
                }
        );

        queue.add(request);
    }
}
