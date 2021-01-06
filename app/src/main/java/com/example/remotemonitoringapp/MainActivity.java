package com.example.remotemonitoringapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity{

   public Button founderLogin, employeeLogin;

    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        founderLogin = findViewById(R.id.founderLogin);
        employeeLogin = findViewById(R.id.employeeLogin);

        founderLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick (View view) {
                Intent i = new Intent(getApplicationContext(), founderLogin.class);
                startActivity(i);
                finish();
            }

        });

        employeeLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), employeeLogin.class);
                startActivity(i);
                finish();

            }

        });


    }

}