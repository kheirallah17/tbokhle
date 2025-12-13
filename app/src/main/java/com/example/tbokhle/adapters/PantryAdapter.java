package com.example.tbokhle.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tbokhle.R;
import com.example.tbokhle.model.PantryItem;

import java.util.ArrayList;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private final List<PantryItem> items = new ArrayList<>();


    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);

        holder.tvName.setText(item.name);
        holder.tvCategory.setText(item.category);

        String details = item.quantity + " " + item.unit + " • " + item.expiryText();
        holder.tvDetails.setText(details);

        switch (item.status()) {
            case "RED":
                holder.statusDot.setBackgroundColor(Color.parseColor("#E74C3C"));
                break;
            case "AMBER":
                holder.statusDot.setBackgroundColor(Color.parseColor("#F39C12"));
                break;
            default:
                holder.statusDot.setBackgroundColor(Color.parseColor("#2ECC71"));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    static class PantryViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvCategory, tvDetails;
        View statusDot;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvDetails = itemView.findViewById(R.id.tv_details);
            statusDot = itemView.findViewById(R.id.view_status);
        }
    }
}
