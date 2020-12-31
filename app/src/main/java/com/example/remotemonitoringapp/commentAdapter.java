package com.example.remotemonitoringapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class commentAdapter extends RecyclerView.Adapter<commentViewHolder>
{

    List<commentData> list = Collections.emptyList();

    Context context;

    public commentAdapter(List<commentData> list, Context context)
    {
        this.list = list;
        this.context = context;
    }

    @Override
    public commentViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType)
    {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the layout

        View photoView = inflater.inflate(R.layout.activity_comment_design_employer,
                parent, false);

        commentViewHolder viewHolder = new commentViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final commentViewHolder viewHolder,
                                 final int position)
    {

        viewHolder.comment.setText(list.get(position).comment);
        viewHolder.datetime.setText(list.get(position).datetime);
        viewHolder.commentBy.setText(list.get(position).commentBy);
        /*viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, EditDrink.class);
                i.putExtra("Drink Id",list.get(position).message);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }

        });*/

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

