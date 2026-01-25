package com.example.tbokhle.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tbokhle.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class IngredientsStatusAdapter extends RecyclerView.Adapter<IngredientsStatusAdapter.ViewHolder> {

    JSONArray items;
    Context context;

    public IngredientsStatusAdapter(Context context, JSONArray items) {
        this.context = context;
        this.items = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIngName, tvIngStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIngName = itemView.findViewById(R.id.tvIngName);
            tvIngStatus = itemView.findViewById(R.id.tvIngStatus);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_ingredient_status, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        try {
            JSONObject obj = items.getJSONObject(position);

            String name = obj.optString("name", "");
            double needed = obj.optDouble("needed_qty", 0);
            double available = obj.optDouble("available_qty", 0);
            String unit = obj.optString("unit", "");

            boolean ok = obj.optInt("is_available", 0) == 1;

            h.tvIngName.setText(name);
            h.tvIngStatus.setText(
                    (ok ? "Available: " : "Missing: ")
                            + available + "/" + needed + " " + unit
            );

        } catch (Exception ignored) {}
    }

    @Override
    public int getItemCount() {
        return (items == null) ? 0 : items.length();
    }
}
