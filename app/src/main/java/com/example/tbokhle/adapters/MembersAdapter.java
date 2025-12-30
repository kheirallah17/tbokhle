package com.example.tbokhle.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tbokhle.R;
import com.example.tbokhle.model.Member;

import java.util.List;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {

    private final Context context;
    private final List<Member> members;

    public MembersAdapter(Context context, List<Member> members) {
        this.context = context;
        this.members = members;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvEmail, tvRole;
        public ImageView imgAvatar;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvRole = itemView.findViewById(R.id.tvRole);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }
    }

    @Override
    public MembersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_members, parent, false);
        return new MembersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MembersAdapter.ViewHolder holder, int position) {
        Member member = members.get(position);

        holder.tvName.setText(member.name);
        holder.tvEmail.setText(member.email);
        holder.imgAvatar.setImageResource(member.imageResId);

        if (member.isAdmin) {
            holder.tvRole.setText("Admin");
            holder.tvRole.setBackgroundResource(R.drawable.bg_role_admin);
            holder.tvRole.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        } else {
            holder.tvRole.setText("Member");
            holder.tvRole.setBackgroundResource(R.drawable.bg_role_member);
            holder.tvRole.setTextColor(ContextCompat.getColor(context, R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return members.size();
    }
}
