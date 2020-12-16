package com.example.remotemonitoringapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class employeeRegister extends AppCompatActivity {
    //public static final int D = Log.d(TAG, "msg");
    public static final String tag = "tag";
    public static final String TAG = "TAG";
    public static final String TAG1 = "TAG";
    public static final String TAG2 = "TAG";
    EditText mFirstName, mLastName, mEmail, mPosition, mID, mPassword;
    Spinner mDepartment, mCompany;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth firebaseAuth;
    //ProgressBar progressBar;
    FirebaseFirestore fstore;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirstName = findViewById(R.id.FirstName);
        mLastName = findViewById(R.id.LastName);
        mEmail = findViewById(R.id.Email);
        mCompany= findViewById(R.id.Company);
       mDepartment = findViewById(R.id.Department);
       // mPosition = findViewById(R.id.Position);
        mID = findViewById(R.id.ID);
        mPassword = findViewById(R.id.Password);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mLoginBtn = findViewById(R.id.createText);

        firebaseAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        //progressBar = findViewById(R.id.progressBar);

        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();

        }

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String ID = mID.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                final String FirstName = mFirstName.getText().toString();
                final String LastName = mLastName.getText().toString();
                final String Email = mEmail.getText().toString();
                //final String Department = mDepartment.getText().toString().trim();
                final String Company = String.valueOf(mCompany.getDropDownVerticalOffset());
                String Position = mPosition.getText().toString().trim();

                if (TextUtils.isEmpty(Email)) {
                    mEmail.setError("Email is required.");
                    return;
                }


                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required.");
                    return;
                }

                if (password.length() > 6) {
                    mPassword.setError("Password must be >=6 Characters.");
                    return;
                }
                //progressBar.setVisibility(View.VISIBLE);




                mLoginBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getApplicationContext(), employeeLogin.class));

                    }
                });


            }

        });


    }
}


