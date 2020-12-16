package com.example.remotemonitoringapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.example.remotemonitoringapp.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class founderRegister extends AppCompatActivity {

    private EditText reg_first_name;
    private EditText reg_last_name;
    private EditText reg_founder_id;
    private EditText reg_founder_email;
    private EditText reg_founder_phone;
    private EditText reg_company_name;
    private EditText reg_founder_password;
    private EditText reg_founder_confirm_password;
    private Button reg_button;
    private TextView register_page;
    private ProgressDialog pBar;
    private static final String STRING_EMPTY = "";
    private static final String KEY_NAME = "userName";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_EMAIL = "userEmail";
    private static final String KEY_ID = "userID";
    private static final String KEY_COMPANY_NAME = "companyName";
    private static final String KEY_PASSWORD = "pass";
    private String RegName;
    private String RegPhone;
    private String RegID;
    private String RegCompanyName;
    private String RegEmail;
    private String RegPassword;
    private int success;
    private String UserID;
    private static final String BASE_URL = "https://remote.shamalandscapes.com/Mobile/Founder/";
    private SessionHandler session;

    private RequestQueue mRequestQueue;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_register);

        session = new SessionHandler(getApplicationContext());


        reg_first_name = (EditText) findViewById(R.id.FirstName);
        reg_last_name = (EditText) findViewById(R.id.LastName);
        reg_founder_id = (EditText) findViewById(R.id.ID);
        reg_founder_email = (EditText) findViewById(R.id.Email);
        reg_founder_phone = (EditText) findViewById(R.id.Phone);
        reg_company_name = (EditText) findViewById(R.id.Company);
        reg_founder_password = (EditText) findViewById(R.id.Password);
        reg_founder_confirm_password = (EditText) findViewById(R.id.ConfirmPassword);
        reg_button = (Button) findViewById(R.id.registerBtn);

        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext())) {
                    register();
                } else {
                    Toast.makeText(founderRegister.this,
                            "Ensure you are connected to the internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    public void onLoginClick(View view) {

        Intent i = new Intent(getApplicationContext(), founderLogin.class);
        startActivity(i);
        finish();

    }


    public void register() {

        if (!STRING_EMPTY.equals(reg_first_name.getText().toString()) &&
                !STRING_EMPTY.equals(reg_last_name.getText().toString()) &&
                !STRING_EMPTY.equals(reg_founder_id.getText().toString()) &&
                !STRING_EMPTY.equals(reg_founder_phone.getText().toString()) &&
                !STRING_EMPTY.equals(reg_company_name.getText().toString()) &&
                !STRING_EMPTY.equals(reg_founder_email.getText().toString()) && !STRING_EMPTY.equals(reg_founder_password.getText().toString()) &&
                !STRING_EMPTY.equals(reg_founder_confirm_password.getText().toString())) {

                    RegName = reg_first_name.getText().toString() + " " + reg_last_name.getText().toString();
                    RegID = reg_founder_id.getText().toString();
                    RegPhone = reg_founder_phone.getText().toString();
                    RegEmail = reg_founder_email.getText().toString();
                    RegCompanyName = reg_company_name.getText().toString();

                    if(reg_founder_password.getText().toString().equals(reg_founder_confirm_password.getText().toString())) {
                        RegPassword = reg_founder_password.getText().toString();
                        volleyJsonObjectRequest(BASE_URL + "register.php");
                    } else {
                        Toast.makeText(founderRegister.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                    }

        } else {
            Toast.makeText(founderRegister.this, "No field should be empty", Toast.LENGTH_LONG).show();
        }

    }

    public void load_dashboard() {
        Intent i = new Intent(getApplicationContext(), communication_design_employer.class);
        startActivity(i);
        finish();
    }

    public void volleyJsonObjectRequest(String url) {

        String REQUEST_TAG = "founderRegistration";

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating your company. Please wait...");
        progressDialog.show();

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(),1024*1024*5); // 5MB cache size

        // Setup the network to use HttpURLConnection as the HTTP client
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with cache and network
        mRequestQueue = new RequestQueue(cache,network);

        // Start the RequestQueue
        mRequestQueue.start();

        StringRequest jsonObjectReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            //Toast.makeText(founderRegister.this, response, Toast.LENGTH_LONG).show();

                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = (JSONObject) jsonArray.get(0);

                            success = Integer.parseInt(jsonObject.getString("success"));
                            UserID = jsonObject.getString("user_id");

                            if (success == 1) {
                                //set session
                                session.login(UserID);
                                load_dashboard();
                            } else {
                                Toast.makeText(founderRegister.this, "Failed to create your company.", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.getMessage();
                        }


                        progressDialog.hide();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error: " + error.getMessage());
                progressDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> httpParams = new HashMap<>();
                httpParams.put(KEY_NAME, RegName);
                httpParams.put(KEY_PHONE, RegPhone);
                httpParams.put(KEY_ID, RegID);
                httpParams.put(KEY_EMAIL, RegEmail);
                httpParams.put(KEY_COMPANY_NAME, RegCompanyName);
                httpParams.put(KEY_PASSWORD, RegPassword);
                return httpParams;
            }

        };

        // Adding JsonObject request to request queue
        mRequestQueue.add(jsonObjectReq);
        // Adding JsonObject request to request queue
       // AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq, REQUEST_TAG);
    }

}