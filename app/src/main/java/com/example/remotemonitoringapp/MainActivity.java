package com.example.remotemonitoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity{

   public Button founderLogin, employeeLogin;

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