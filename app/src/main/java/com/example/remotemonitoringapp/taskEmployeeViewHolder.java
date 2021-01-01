package com.example.remotemonitoringapp;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class taskEmployeeViewHolder extends RecyclerView.ViewHolder {
    TextView task;
    TextView deadline;
    Button btn_task_comment;

    taskEmployeeViewHolder(View itemView)
    {
        super(itemView);
        task = (TextView)itemView.findViewById(R.id.task_tab);
        deadline = (TextView)itemView.findViewById(R.id.datetimedeadline);
        btn_task_comment = (Button)itemView.findViewById(R.id.btnEmployeeTaskComment);
    }
}
