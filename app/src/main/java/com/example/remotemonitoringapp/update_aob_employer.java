package com.example.remotemonitoringapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class update_aob_employer extends AppCompatActivity {

    private EditText aob_title;
    private Button update_aob_btn,delete_aob_btn;
    private ProgressDialog pBar;
    private static final String STRING_EMPTY = "";
    private static final String KEY_AOB_ID = "aob_id";
    private static final String KEY_AOB_TITLE = "aob_title";
    private static final String KEY_EMPLOYEE_EMAIL = "employee_email";
    private String aobTitle;
    private String EmployeeEmail;
    private String UserID;
    private int success;
    private static final String BASE_URL = "https://remote.shammahgifts.co.ke/Mobile/Employee/";
    private SessionHandler session;

    private RequestQueue mRequestQueue;

    private Spinner deptSpinner, employeeSpiner;
    private ArrayAdapter<CharSequence> adapter;

    public ArrayAdapter<String> spinnerArrayAdapter;
    public List<String> employeeList;
    private SwipeRefreshLayout swipeContainer;

    private String KEY_LOGGED = "LoggedStat";
    private String PREF_NAME = "Pop-InSession";
    private static final String KEY_USER_ID = "UserId";
    String active_user_id;
    String aob_id;
    private static final String KEY_SESSION_ID = "session_id";

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(getApplicationContext(), aob_employer.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_aob_employer);

        SharedPreferences sp = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        if (sp.getBoolean(KEY_LOGGED, false)) {
            active_user_id = sp.getString(KEY_USER_ID,"");
        } else {
            Intent i = new Intent(getApplicationContext(), employeeLogin.class);
            startActivity(i);
        }

        Bundle extras = getIntent().getExtras();

        if(extras !=null){
            aob_id = extras.getString("AOB Id");
        }

        aob_title = (EditText) findViewById(R.id.aob_title);
        update_aob_btn = (Button) findViewById(R.id.btn_update_aob);
        delete_aob_btn = (Button) findViewById(R.id.btn_delete_aob);
        

        String[] employees = new String[]{

        };

        employeeSpiner = (Spinner) findViewById(R.id.employee);

        employeeList = new ArrayList<>(Arrays.asList(employees));
        // Initializing an ArrayAdapter
        spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item, employeeList);

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        employeeSpiner.setAdapter(spinnerArrayAdapter);

        //automatic process
        try {

            volleyFetchSpecificAOBRequest(BASE_URL + "get_employee_aob_update_data.php");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swiperefresh_recycler);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                try {
                    volleyFetchSpecificAOBRequest(BASE_URL + "get_employee_aob_update_data.php");
                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        update_aob_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext())) {
                    update_aob();
                } else {
                    Toast.makeText(update_aob_employer.this,
                            "Ensure you are connected to the internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        //on delete button clicked
        delete_aob_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show dialog

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(update_aob_employer.this);
                alertDialogBuilder.setTitle("Delete AOB...");
                alertDialogBuilder.setMessage("Are You sure?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                volleyDeleteAOBRequest(BASE_URL+"delete_aob.php");
                            }
                        });

                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

    }

    public void update_aob() {

        if (!STRING_EMPTY.equals(aob_title.getText().toString())) {

            aobTitle = aob_title.getText().toString();
            EmployeeEmail = employeeSpiner.getSelectedItem().toString();

            volleyJsonObjectRequest(BASE_URL + "update_aob.php");


        } else {
            Toast.makeText(update_aob_employer.this, "No field should be empty", Toast.LENGTH_LONG).show();
        }

    }

    public void load_dashboard() {
        Intent i = new Intent(getApplicationContext(), aob_employer.class);
        startActivity(i);
        finish();
    }

    public void volleyJsonObjectRequest(String url) {

        String REQUEST_TAG = "updateAOB";

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating AOB. Please wait...");
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

                            Toast.makeText(update_aob_employer.this, response, Toast.LENGTH_LONG).show();

                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = (JSONObject) jsonArray.get(0);

                            success = Integer.parseInt(jsonObject.getString("success"));

                            if (success == 1) {
                                Toast.makeText(update_aob_employer.this, "AOB updated", Toast.LENGTH_LONG).show();
                                load_dashboard();
                            } else {
                                Toast.makeText(update_aob_employer.this, "Failed to update AOB.", Toast.LENGTH_LONG).show();
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
                httpParams.put(KEY_AOB_TITLE, aobTitle);
                httpParams.put(KEY_EMPLOYEE_EMAIL, EmployeeEmail);
                httpParams.put(KEY_SESSION_ID, active_user_id);
                httpParams.put(KEY_AOB_ID, aob_id);
                return httpParams;
            }

        };

        // Adding JsonObject request to request queue
        mRequestQueue.add(jsonObjectReq);
        // Adding JsonObject request to request queue
        // AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq, REQUEST_TAG);
    }

    public void volleyFetchSpecificAOBRequest(String url) throws JSONException {

        String  REQUEST_TAG = "companies";

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading AOB...");
        progressDialog.show();

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(),1024*1024*5); // 5MB cache size

        // Setup the network to use HttpURLConnection as the HTTP client
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with cache and network
        mRequestQueue = new RequestQueue(cache,network);

        // Start the RequestQueue
        mRequestQueue.start();

        StringRequest jsonObjectReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {


                        progressDialog.hide();

                        try {

                            //Toast.makeText(employeeRegister.this,response,Toast.LENGTH_LONG).show();

                            JSONArray jsonArray = new JSONArray(response);

                            final JSONObject jObject = (JSONObject)jsonArray.get(0);

                            if (jObject.getInt("success") == 1) {

                                for(int i=0; i< jsonArray.length(); i++) {
                                    JSONObject jObject2 = (JSONObject) jsonArray.get(i);
                                    aob_title.setText(jObject2.getString("aob"));
                                    employeeList.add(jObject2.getString("employee"));
                                    spinnerArrayAdapter.notifyDataSetChanged();
                                }
                            }

                        } catch(JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error: " + error.getMessage());
                progressDialog.hide();
            }
        }){

            @Override
            protected Map<String,String> getParams() {
                Map<String,String> request_map = new HashMap<>();
                request_map.put(KEY_SESSION_ID,active_user_id);
                request_map.put(KEY_AOB_ID,aob_id);
                return request_map;
            }


        };

        // Adding JsonObject request to request queue
        mRequestQueue.add(jsonObjectReq);
        // Adding JsonObject request to request queue
        //mRequestQueue.add(jsonObjectReq);
        //AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);
    }

    public void volleyDeleteAOBRequest(String url)  {

        String  REQUEST_TAG = "deleteAOB";


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Deleting AOB...");
        progressDialog.show();

        StringRequest jsonObjectReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        //Toast.makeText(EditDrink.this, response, Toast.LENGTH_LONG).show();

                        try {
                            //JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            //for(int i=0;i<obj.length();i++) {
                            if((jsonObject.get("success").toString()).equals("1")) {
                                Toast.makeText(update_aob_employer.this, jsonObject.get("message").toString(), Toast.LENGTH_LONG).show();

                                Intent i = new Intent(update_aob_employer.this, aob_employer.class);
                                startActivity(i);

                            } else {
                                Toast.makeText(update_aob_employer.this, jsonObject.get("message").toString(), Toast.LENGTH_LONG).show();
                            }

                            //Toast.makeText(update_aob_employer.this, jsonObject.get("success").toString(), Toast.LENGTH_LONG).show();



                        } catch(JSONException e) {
                            e.printStackTrace();
                        }

                        progressDialog.hide();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error: " + error.getMessage());
                progressDialog.hide();
            }
        }){

            @Override
            protected Map<String,String> getParams() {
                Map<String,String> request_map = new HashMap<>();
                request_map.put(KEY_SESSION_ID,active_user_id);
                request_map.put(KEY_AOB_ID,aob_id);
                return request_map;
            }

        };

        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);
    }
}