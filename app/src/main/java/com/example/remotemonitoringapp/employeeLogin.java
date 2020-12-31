package com.example.remotemonitoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.ProgressDialog;
import android.widget.TextView;
import android.widget.Toast;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class employeeLogin extends AppCompatActivity {

    private EditText email_address;
    private EditText user_password;
    private Button login_button;
    private TextView register_page;
    private ProgressDialog pBar;
    private static final String STRING_EMPTY = "";
    private static final String KEY_EMAIL = "loginEmail";
    private static final String KEY_PASSWORD = "loginPass";
    private String UserEmail;
    private String Password;
    private int success;
    private String UserID;
    private static final String BASE_URL = "https://remote.shamalandscapes.com/Mobile/Employee/";
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
        setContentView(R.layout.activity_login);

        session = new SessionHandler(getApplicationContext());


        //User user = session.getUserDetails();


        /*if(!user.getUserID().isEmpty()) {
            load_dashboard();
        }*/
        //String barcode = getIntent().getStringExtra("code");

        email_address = (EditText) findViewById(R.id.Email);
        user_password = (EditText) findViewById(R.id.Password);
        login_button = (Button) findViewById(R.id.employeeLoginBtn);
        register_page = (TextView) findViewById(R.id.createText);
        /*if(!(null == user)) {
            assert user != null;
            email_address.setText(user.getUserID());
            load_dashboard();

        } else {
            email_address.setText("Bado");
        }*/

        register_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext())) {
                    onSignUpClick();
                } else {
                    Toast.makeText(employeeLogin.this,
                            "Unable to connect to internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext())) {
                    login();
                } else {
                    Toast.makeText(employeeLogin.this,
                            "Unable to connect to internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    public void onSignUpClick() {

        Intent i = new Intent(getApplicationContext(), employeeRegister.class);
        startActivity(i);
        finish();

    }

    /*public void onResetPassClick(View view) {

        Intent i = new Intent(getApplicationContext(), ResetPasswordPage.class);
        startActivity(i);
        finish();

    }*/


    public void login() {

        if (!STRING_EMPTY.equals(email_address.getText().toString()) && !STRING_EMPTY.equals(user_password.getText().toString())) {
            UserEmail = email_address.getText().toString();
            Password = user_password.getText().toString();
            volleyJsonObjectRequest(BASE_URL + "login.php");
        } else {
            Toast.makeText(employeeLogin.this, "No field should be empty", Toast.LENGTH_LONG).show();
        }

    }

    public void load_dashboard() {
        Intent i = new Intent(getApplicationContext(), Task.class);
        startActivity(i);
        finish();
    }

    public void load_employee_dashboard() {
        Intent i = new Intent(getApplicationContext(), taskpage_employee.class);
        startActivity(i);
        finish();
    }

    public void volleyJsonObjectRequest(String url) {

        String REQUEST_TAG = "employeeLogin";

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Wait as we log you in...");
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

                        Toast.makeText(employeeLogin.this, response, Toast.LENGTH_LONG).show();

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = (JSONObject) jsonArray.get(0);

                            success = Integer.parseInt(jsonObject.getString("success"));
                            UserID = jsonObject.getString("user_id");
                            String UserType = jsonObject.getString("user_type");


                            if (success == 1) {
                                //set session
                                if(UserType.equals("Manager")) {
                                    session.login(UserID);
                                    load_dashboard();
                                } else {
                                    session.events_login(UserID);
                                    load_employee_dashboard();
                                }
                            } else {
                                Toast.makeText(employeeLogin.this, "Failed to log you in.", Toast.LENGTH_LONG).show();
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
                httpParams.put(KEY_EMAIL, UserEmail);
                httpParams.put(KEY_PASSWORD, Password);
                return httpParams;
            }

        };

        // Adding JsonObject request to request queue
        mRequestQueue.add(jsonObjectReq);
        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq, REQUEST_TAG);
    }

}