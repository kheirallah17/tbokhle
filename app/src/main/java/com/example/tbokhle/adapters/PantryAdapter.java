package com.example.tbokhle.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tbokhle.R;
import com.example.tbokhle.model.PantryItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    // ✅ Base URL here (same approach as ShoppingAdapter)
    private static final String BASE_URL =
            "http://10.0.2.2/Tbokhle/";

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

        // 🟢 Status dot color
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

        // 🗑 DELETE FROM DB + UI
        holder.btnDelete.setOnClickListener(v -> {
            deleteItem(v, item.id);
            items.remove(position);
            notifyItemRemoved(position);
        });
    }

    // -----------------------------
    // 🔗 VOLLEY DELETE
    // -----------------------------
    private void deleteItem(View view, int id) {

        StringRequest req = new StringRequest(
                Request.Method.POST,
                BASE_URL + "pantry/delete_pantry.php",
                response -> { /* success */ },
                error -> error.printStackTrace()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                return params;
            }
        };

        Volley.newRequestQueue(view.getContext()).add(req);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // -----------------------------
    // VIEW HOLDER
    // -----------------------------
    static class PantryViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvCategory, tvDetails;
        View statusDot;
        ImageButton btnDelete;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvDetails = itemView.findViewById(R.id.tv_details);
            statusDot = itemView.findViewById(R.id.view_status);
            btnDelete = itemView.findViewById(R.id.btn_delete_pantry);
        }
    }
}