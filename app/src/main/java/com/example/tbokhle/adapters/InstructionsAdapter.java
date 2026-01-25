package com.example.tbokhle.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tbokhle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InstructionsAdapter extends RecyclerView.Adapter<InstructionsAdapter.ViewHolder> {

    private final Context context;
    private final JSONArray steps;

    public InstructionsAdapter(Context context, JSONArray steps) {
        this.context = context;
        this.steps = steps;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvStepNo, tvInstruction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvStepNo = itemView.findViewById(R.id.tvStepNo);
            tvInstruction = itemView.findViewById(R.id.tvInstruction);

            // (Optional) click listener in your style
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        try {
                            JSONObject obj = steps.getJSONObject(pos);
                            String inst = obj.optString("instruction", "");
                            Toast.makeText(context, inst, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e("STEP_CLICK", e.toString());
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public InstructionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_instruction, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionsAdapter.ViewHolder holder, int position) {
        try {
            JSONObject obj = steps.getJSONObject(position);

            int stepNo = obj.optInt("step_no", position + 1);
            String instruction = obj.optString("instruction", "");

            holder.tvStepNo.setText(stepNo + ".");
            holder.tvInstruction.setText(instruction);

        } catch (JSONException e) {
            Log.e("STEP_BIND", e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return steps == null ? 0 : steps.length();
    }
}
