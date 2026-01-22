package com.example.tbokhle.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tbokhle.R;
import com.example.tbokhle.model.ShoppingItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.VH> {

    // ✅ BASE URL (put here since network package was problematic)
    private static final String BASE_URL =
            "http://10.0.2.2/Tbokhle/";

    public interface Listener {
        void onToggleDone(int position, boolean done);
        void onDelete(int position);
    }

    private final List<ShoppingItem> items = new ArrayList<>();
    private final Listener listener;

    public ShoppingAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<ShoppingItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public List<ShoppingItem> getItems() {
        return items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shopping, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {

        ShoppingItem item = items.get(position);

        h.tvName.setText(item.name);
        h.tvMeta.setText(item.metaText());

        // prevent unwanted triggers
        h.cbDone.setOnCheckedChangeListener(null);
        h.cbDone.setChecked(item.done);

        if (item.done) {
            h.tvName.setPaintFlags(
                    h.tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
            );
            h.tvName.setAlpha(0.5f);
            h.tvMeta.setAlpha(0.5f);
        } else {
            h.tvName.setPaintFlags(
                    h.tvName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)
            );
            h.tvName.setAlpha(1f);
            h.tvMeta.setAlpha(0.8f);
        }

        // ✅ CHECKBOX → UPDATE DB
        h.cbDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.done = isChecked;
            updateDone(h.itemView, item.id, isChecked);

            if (listener != null) {
                listener.onToggleDone(position, isChecked);
            }
        });

        // ✅ DELETE → DELETE FROM DB
        h.btnDelete.setOnClickListener(v -> {
            deleteItem(v, item.id);

            if (listener != null) {
                listener.onDelete(position);
            }
        });
    }

    // -----------------------------
    // 🔗 VOLLEY CALLS
    // -----------------------------

    private void updateDone(View view, int id, boolean done) {

        StringRequest req = new StringRequest(
                Request.Method.POST,
                BASE_URL + "shopping/update_done.php",
                response -> { /* success, no UI change needed */ },
                error -> error.printStackTrace()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                params.put("is_done", done ? "1" : "0");
                return params;
            }
        };

        Volley.newRequestQueue(view.getContext()).add(req);
    }

    private void deleteItem(View view, int id) {

        StringRequest req = new StringRequest(
                Request.Method.POST,
                BASE_URL + "shopping/delete_shopping.php",
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

    static class VH extends RecyclerView.ViewHolder {

        CheckBox cbDone;
        TextView tvName, tvMeta;
        ImageButton btnDelete;

        VH(@NonNull View itemView) {
            super(itemView);
            cbDone = itemView.findViewById(R.id.cb_done);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvMeta = itemView.findViewById(R.id.tv_item_meta);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}