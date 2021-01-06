package com.example.remotemonitoringapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class aobAdapter extends RecyclerView.Adapter<aobViewHolder>
{

    List<aobData> list = Collections.emptyList();

    Context context;

    public aobAdapter(List<aobData> list, Context context)
    {
        this.list = list;
        this.context = context;
    }

    @Override
    public aobViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType)
    {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the layout

        View photoView = inflater.inflate(R.layout.activity_aob_design,
                parent, false);

        aobViewHolder viewHolder = new aobViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final aobViewHolder viewHolder,
                                 final int position)
    {

        viewHolder.aob.setText(list.get(position).aob);
        viewHolder.creator.setText(list.get(position).created_by);
        viewHolder.addressed_to.setText(list.get(position).received_by);
        /*viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, EditDrink.class);
                i.putExtra("Drink Id",list.get(position).message);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }

        });*/

        viewHolder.btn_aob_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                Intent i = new Intent(context, aob_comment_employer.class);
                i.putExtra("AOB Id", list.get(position).aob_id);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

            }
        });

        viewHolder.btn_aob_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                Intent i = new Intent(context, update_aob_employer.class);
                i.putExtra("AOB Id", list.get(position).aob_id);
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

