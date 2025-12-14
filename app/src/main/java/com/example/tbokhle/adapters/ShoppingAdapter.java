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

import com.example.tbokhle.R;
import com.example.tbokhle.model.ShoppingItem;

import java.util.ArrayList;
import java.util.List;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.VH> {

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ShoppingItem item = items.get(position);

        h.tvName.setText(item.name);
        h.tvMeta.setText(item.metaText());


        h.cbDone.setOnCheckedChangeListener(null);
        h.cbDone.setChecked(item.done);


        if (item.done) {
            h.tvName.setPaintFlags(h.tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            h.tvName.setAlpha(0.5f);
            h.tvMeta.setAlpha(0.5f);
        } else {
            h.tvName.setPaintFlags(h.tvName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            h.tvName.setAlpha(1f);
            h.tvMeta.setAlpha(0.8f);
        }

        h.cbDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) listener.onToggleDone(position, isChecked);
        });

        h.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(position);
        });
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