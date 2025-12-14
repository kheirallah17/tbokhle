package com.example.tbokhle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.VH> {

    private final List<Recipe> data;
    Context c;

    public RecipesAdapter(List<Recipe> data,Context c) {
        this.data = data;
        this.c = c;
    }

    public static class VH extends RecyclerView.ViewHolder {
        ImageView imgRecipe;
        TextView tvIngredientsTag, tvRecipeName, tvTime, tvDifficulty, tvKcal;

        public VH(View v) {
            super(v);
            imgRecipe = v.findViewById(R.id.imgRecipe);
            tvIngredientsTag = v.findViewById(R.id.tvIngredientsTag);
            tvRecipeName = v.findViewById(R.id.tvRecipeName);
            tvTime = v.findViewById(R.id.tvTime);
            tvDifficulty = v.findViewById(R.id.tvDifficulty);
            tvKcal = v.findViewById(R.id.tvKcal);

            v.setOnClickListener(view -> {
                // optional click behavior
                // int pos = getBindingAdapterPosition();
            });
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_recipes, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH h, int position) {
        Recipe r = data.get(position);
        h.tvRecipeName.setText(r.name);
        h.tvIngredientsTag.setText(r.ingredientsTag);
        h.tvTime.setText(r.time);
        h.tvDifficulty.setText(r.difficulty);
        h.tvKcal.setText(r.kcal);
        h.imgRecipe.setImageResource(r.imageResId);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
