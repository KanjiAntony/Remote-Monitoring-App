package com.example.remotemonitoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class founderLogin extends AppCompatActivity {
    EditText founderEmail,founderPassword;
    Button loginBtn;
    TextView registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_login);

        // initialise the variables
        founderEmail = findViewById(R.id.Email);
        founderPassword = findViewById(R.id.Password);
        loginBtn = (Button)findViewById(R.id.companyLoginBtn);
        registerBtn = findViewById(R.id.createText);

        // go to the founder registration page
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), founderRegister.class));

            }
        });





    }


}