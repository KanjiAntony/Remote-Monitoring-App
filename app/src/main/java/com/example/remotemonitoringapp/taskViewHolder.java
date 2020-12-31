package com.example.remotemonitoringapp;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class taskViewHolder extends RecyclerView.ViewHolder {
    TextView task;
    TextView deadline;
    TextView employee;
    Button btn_task_update;
    Button btn_task_comment;

    taskViewHolder(View itemView)
    {
        super(itemView);
        task = (TextView)itemView.findViewById(R.id.task_tab);
        deadline = (TextView)itemView.findViewById(R.id.datetimedeadline);
        employee = (TextView)itemView.findViewById(R.id.employeeTask);
        btn_task_update = (Button)itemView.findViewById(R.id.btnTaskUpdate);
        btn_task_comment = (Button)itemView.findViewById(R.id.btnTaskComment);
    }
}
