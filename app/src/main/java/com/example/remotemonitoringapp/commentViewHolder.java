package com.example.remotemonitoringapp;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class commentViewHolder extends RecyclerView.ViewHolder {
    TextView comment;
    TextView datetime;
    TextView commentBy;

    commentViewHolder(View itemView)
    {
        super(itemView);
        comment = (TextView)itemView.findViewById(R.id.commentsent);
        datetime = (TextView)itemView.findViewById(R.id.timecommentsent);
        commentBy = (TextView)itemView.findViewById(R.id.commentBy);
    }
}
