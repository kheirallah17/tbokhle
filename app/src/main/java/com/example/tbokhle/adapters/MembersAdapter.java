package com.example.tbokhle.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tbokhle.R;
import com.example.tbokhle.fragments.FragmentSix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {

    private final Context context;
    private final JSONArray members;
    private final FragmentSix fragment;
    private final RequestQueue queue;

    private final int householdId = 1;   // TEMP

    public MembersAdapter(Context context,
                          JSONArray members,
                          FragmentSix fragment) {
        this.context = context;
        this.members = members;
        this.fragment = fragment;
        this.queue = Volley.newRequestQueue(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvEmail, tvRole;
        ImageView imgAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvRole = itemView.findViewById(R.id.tvRole);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);

            itemView.setOnClickListener(v -> onRowClicked());
        }

        private void onRowClicked() {
            int pos = getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            try {
                JSONObject m = members.getJSONObject(pos);
                int userId = m.getInt("id");
                String name = m.optString("full_name");

                new AlertDialog.Builder(context)
                        .setTitle("Remove member")
                        .setMessage("Remove " + name + " from household?")
                        .setPositiveButton("Remove",
                                (d, w) -> removeMember(userId))
                        .setNegativeButton("Cancel", null)
                        .show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_members, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        try {
            JSONObject m = members.getJSONObject(position);

            h.tvName.setText(m.optString("full_name", ""));
            h.tvEmail.setText(m.optString("email", ""));
            h.imgAvatar.setImageResource(R.drawable.outline_account_circle_24);

            String role = m.optString("role", "member");
            if (role.equalsIgnoreCase("admin")) {
                h.tvRole.setText("Admin");
                h.tvRole.setBackgroundResource(R.drawable.bg_role_admin);
                h.tvRole.setTextColor(context.getColor(android.R.color.white));
            } else {
                h.tvRole.setText("Member");
                h.tvRole.setBackgroundResource(R.drawable.bg_role_member);
                h.tvRole.setTextColor(context.getColor(R.color.black));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return members == null ? 0 : members.length();
    }

    // ==========================
    // REMOVE MEMBER
    // ==========================
    private void removeMember(int userId) {
        String url = "http://10.0.2.2/tbokhle/remove_household_member.php";

        StringRequest req = new StringRequest(
                Request.Method.POST,
                url,
                res -> {
                    Toast.makeText(context, "Member removed", Toast.LENGTH_SHORT).show();
                    fragment.loadHouseholdMembers(); // ✅ TEMPLATE STYLE
                },
                err -> Toast.makeText(context, "Remove failed", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("household_id", String.valueOf(householdId));
                p.put("user_id", String.valueOf(userId));
                return p;
            }
        };

        queue.add(req);
    }
}



