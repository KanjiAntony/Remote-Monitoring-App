package com.example.remotemonitoringapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {

    public CardView Task, AoB, Personal_Communication, Communication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Task = (CardView) findViewById(R.id.Tasks);
        AoB =(CardView) findViewById(R.id.aoob);
        //Personal_Communication = (CardView) findViewById(R.id.personal_comm);
        Communication = (CardView) findViewById(R.id.comm);

        Task.setOnClickListener(this);
        AoB.setOnClickListener(this);
        //Personal_Communication.setOnClickListener(this);
        Communication.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        Intent i;

        switch (view.getId()) {
            case R.id.Tasks:
                i = new Intent(this, Task.class);
                startActivity(i);
                break;

        }

    }

    public void Logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),welcome.class));
        finish();
    }

    }

