package com.example.remotemonitoringapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

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

public class employer_dashboard extends AppCompatActivity {

    public CardView Task, AoB, Communication, Reports;
    public Button logout_btn;
    String active_user_id,company_id;
    int success;
    Toolbar toolbar;
    private SessionHandler session;
    private static final String KEY_SESSION_ID = "session_id";
    private static final String BASE_URL = "https://remote.shammahgifts.co.ke/Mobile/Employee/";
    private String KEY_LOGGED = "LoggedStat";
    private String PREF_NAME = "Pop-InSession";
    private static final String KEY_USER_ID = "UserId";

    private String KEY_EMPLOYEE_LOGGED = "EventsLoggedStat";
    private String PREF_EMPLOYEE_NAME = "Pop-InEventsSession";
    private RequestQueue mRequestQueue;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(getApplicationContext(), employer_dashboard.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionHandler(getApplicationContext());


        SharedPreferences sp = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        if (sp.getBoolean(KEY_LOGGED, false)) {
            active_user_id = sp.getString(KEY_USER_ID, "");

        } else {
            Intent i = new Intent(getApplicationContext(), employeeLogin.class);
            startActivity(i);
        }

        Task = (CardView) findViewById(R.id.Tasks);
        AoB =(CardView) findViewById(R.id.aob);
        Reports =(CardView) findViewById(R.id.reports);
        Communication = (CardView) findViewById(R.id.comm);
        logout_btn = (Button) findViewById(R.id.logout_btn);

        //automatic process
        try {
            volleyJsonObjectRequestAsync(BASE_URL + "get_employee_info.php");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), Task.class);
                startActivity(i);
                finish();


            }
        });

        Reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), reports_employer.class);
                startActivity(i);
                finish();


            }
        });

        AoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), aob_employer.class);
                startActivity(i);
                finish();


            }
        });

        Communication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), communication_employer.class);
                i.putExtra("Company Id", company_id);
                startActivity(i);
                finish();


            }
        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(session.isLoggedIn()) {
                    session.logout();
                }

                Intent i = new Intent(getApplicationContext(), employeeLogin.class);
                startActivity(i);
                finish();


            }
        });

    }

    public void volleyJsonObjectRequestAsync(String url) throws JSONException {

        String REQUEST_TAG = "getUserData";

        /*final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching user information...");
        progressDialog.show();*/

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

                            //Toast.makeText(upload_employer.this, response, Toast.LENGTH_LONG).show();

                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = (JSONObject) jsonArray.get(0);

                            success = Integer.parseInt(jsonObject.getString("success"));
                            //UserID = jsonObject.getString("user_id");

                            if (success == 1) {
                                company_id = jsonObject.getString("company_id");
                            } else {
                                Toast.makeText(employer_dashboard.this, "Failed to get user information.", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.getMessage();
                        }


                        //progressDialog.hide();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error: " + error.getMessage());
                //progressDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> httpParams = new HashMap<>();
                httpParams.put(KEY_SESSION_ID, active_user_id);
                return httpParams;
            }

        };

        // Adding JsonObject request to request queue
        mRequestQueue.add(jsonObjectReq);
        // Adding JsonObject request to request queue
        // AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq, REQUEST_TAG);
    }

    }

