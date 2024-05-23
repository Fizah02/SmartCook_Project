package com.example.smartcook;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class ProcedureViewHolder extends RecyclerView.ViewHolder {
    public TextView procedureNumberTextView;
    public TextView procedureDescriptionTextView;

    public ProcedureViewHolder(@NonNull View itemView) {
        super(itemView);

        procedureNumberTextView = itemView.findViewById(R.id.stepNumberTextView);
        procedureDescriptionTextView = itemView.findViewById(R.id.stepDescriptionTextView);
    }

    public void bind(ProcedureModel procedure) {

        procedureNumberTextView.setText(procedure.getNo());
        procedureDescriptionTextView.setText(procedure.getSteps());
    }
}
