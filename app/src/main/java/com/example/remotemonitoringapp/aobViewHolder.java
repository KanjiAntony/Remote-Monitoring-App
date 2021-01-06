package com.example.remotemonitoringapp;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class aobViewHolder extends RecyclerView.ViewHolder {
    TextView aob;
    TextView creator;
    TextView addressed_to;
    Button btn_aob_update;
    Button btn_aob_comment;

    aobViewHolder(View itemView)
    {
        super(itemView);
        aob = (TextView)itemView.findViewById(R.id.aob);
        creator = (TextView)itemView.findViewById(R.id.creator);
        addressed_to = (TextView)itemView.findViewById(R.id.addressed_to);
        btn_aob_update = (Button)itemView.findViewById(R.id.btnAOBUpdate);
        btn_aob_comment = (Button)itemView.findViewById(R.id.btnAOBComment);
    }
}
