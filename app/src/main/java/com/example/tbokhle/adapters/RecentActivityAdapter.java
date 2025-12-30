package com.example.tbokhle.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tbokhle.R;
import com.example.tbokhle.model.RecentActivity;

import java.util.List;

public class RecentActivityAdapter extends RecyclerView.Adapter<RecentActivityAdapter.VH> {

    private final Context context;
    private final List<RecentActivity> items;

    public RecentActivityAdapter(Context context, List<RecentActivity> items) {
        this.context = context;
        this.items = items;
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView tvMessage, tvTimeAgo;

        VH(View itemView) {
            super(itemView);

            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_recent_activity, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH h, int position) {
        RecentActivity a = items.get(position);
        h.tvMessage.setText(a.message);
        h.tvTimeAgo.setText(a.timeAgo);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

