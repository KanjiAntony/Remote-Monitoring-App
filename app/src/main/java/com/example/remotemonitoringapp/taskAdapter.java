package com.example.remotemonitoringapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class taskAdapter extends RecyclerView.Adapter<taskViewHolder>
{

    List<taskData> list = Collections.emptyList();

    Context context;

    public taskAdapter(List<taskData> list, Context context)
    {
        this.list = list;
        this.context = context;
    }

    @Override
    public taskViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType)
    {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the layout

        View photoView = inflater.inflate(R.layout.activity_taskpage_employer,
                parent, false);

        taskViewHolder viewHolder = new taskViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final taskViewHolder viewHolder,
                                 final int position)
    {

        viewHolder.task.setText(list.get(position).task);
        viewHolder.deadline.setText(list.get(position).deadline);
        viewHolder.employee.setText(list.get(position).employee);
        /*viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, EditDrink.class);
                i.putExtra("Drink Id",list.get(position).message);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }

        });*/

        viewHolder.btn_task_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                Intent i = new Intent(context, comment_employer.class);
                i.putExtra("Task Id", list.get(position).task_id);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void clear() {
        list.clear();
    }

}

