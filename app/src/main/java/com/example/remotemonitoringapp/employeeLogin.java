package com.example.remotemonitoringapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class employeeLogin extends AppCompatActivity {
    EditText employeeEmail,employeePassword;
    Button loginBtn;
    TextView registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialise the variables
        employeeEmail = findViewById(R.id.Email);
        employeePassword = findViewById(R.id.Password);
        loginBtn = (Button)findViewById(R.id.employeeLoginBtn);
        registerBtn = findViewById(R.id.createText);

        // go to the employee registration page i.e both the dept manager and dept employees
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), employeeRegister.class));

            }
        });





    }


}