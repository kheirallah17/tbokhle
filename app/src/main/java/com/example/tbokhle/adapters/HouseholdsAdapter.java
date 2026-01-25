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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.tbokhle.SessionManager; // 🔹 ADDED

public class HouseholdsAdapter extends RecyclerView.Adapter<HouseholdsAdapter.ViewHolder> {

    private final Context context;
    private final JSONArray households;
    private final RequestQueue queue;

    private final int userId;
    private final Runnable reloadCallback;

    public HouseholdsAdapter(Context context, JSONArray households, int userId, Runnable reloadCallback) {
        this.context = context;
        this.households = households;
        this.userId = userId;
        this.reloadCallback = reloadCallback;
        this.queue = Volley.newRequestQueue(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvHouseholdName, tvRole;
        ImageView imgAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvHouseholdName = itemView.findViewById(R.id.tvHouseholdName);
            tvRole = itemView.findViewById(R.id.tvRole);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);

            itemView.setOnClickListener(v -> selectHousehold());     // 🔹 CHANGED
            itemView.setOnLongClickListener(v -> {                   // 🔹 ADDED
                handleLeave();
                return true;
            });
        }

        // 🔹 ADDED: switch household
        private void selectHousehold() {
            int pos = getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            try {
                JSONObject h = households.getJSONObject(pos);
                int householdId = h.getInt("id");

                SessionManager sm = new SessionManager(context);
                sm.setHouseholdId(householdId); // ✅ ACTIVE HOUSEHOLD SET

                Toast.makeText(context, "Household switched", Toast.LENGTH_SHORT).show();

                reloadCallback.run(); // 🔄 reload UI

            } catch (JSONException ignored) {}
        }

        // 🔹 MOVED logic here (unchanged behavior)
        private void handleLeave() {
            int pos = getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            try {
                JSONObject h = households.getJSONObject(pos);
                int householdId = h.getInt("id");
                String role = h.getString("role");

                if ("admin".equalsIgnoreCase(role)) {
                    Toast.makeText(context,
                            "Admin cannot leave household",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                new AlertDialog.Builder(context)
                        .setTitle("Leave household")
                        .setMessage("Are you sure you want to leave this household?")
                        .setPositiveButton("Leave", (d, w) ->
                                leaveHousehold(householdId))
                        .setNegativeButton("Cancel", null)
                        .show();

            } catch (JSONException ignored) {}
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_household, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        try {
            JSONObject obj = households.getJSONObject(position);
            h.tvHouseholdName.setText(obj.optString("household_name"));
            h.tvRole.setText(obj.optString("role", "member"));
        } catch (JSONException ignored) {}
    }

    @Override
    public int getItemCount() {
        return households.length();
    }

    private void leaveHousehold(int householdId) {
        String url = "http://10.0.2.2/tbokhle/leave_household.php";

        StringRequest req = new StringRequest(
                Request.Method.POST,
                url,
                res -> {
                    Toast.makeText(context, "Left household", Toast.LENGTH_SHORT).show();
                    reloadCallback.run();
                },
                err -> Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> p = new java.util.HashMap<>();
                p.put("user_id", String.valueOf(userId));
                p.put("household_id", String.valueOf(householdId));
                return p;
            }
        };

        queue.add(req);
    }
}

