package com.example.remotemonitoringapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class employeeRegister extends AppCompatActivity {

    private EditText reg_first_name;
    private EditText reg_last_name;
    private EditText reg_employee_id;
    private EditText reg_employee_email;
    private EditText reg_employee_phone;
    RadioGroup reg_employee_position;
    private EditText reg_employee_password;
    private EditText reg_employee_confirm_password;
    private Button reg_button;
    private TextView register_page;
    private ProgressDialog pBar;
    private static final String STRING_EMPTY = "";
    private static final String KEY_NAME = "userName";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_EMAIL = "userEmail";
    private static final String KEY_ID = "userID";
    private static final String KEY_TOKEN_ID = "deviceToken";
    private static final String KEY_COMPANY_NAME = "companyName";
    private static final String KEY_EMPLOYEE_DEPT = "department";
    private static final String KEY_EMPLOYEE_POSITION = "position";
    private static final String KEY_PASSWORD = "pass";
    private String RegName;
    private String RegPhone;
    private String RegID;
    private String RegCompanyName, RegEmployeeDept;
    private String employee_position;
    private String RegEmail;
    private String RegPassword;
    private String token;
    private int success;
    private String UserID;
    private static final String BASE_URL = "https://remote.shammahgifts.co.ke/Mobile/Employee/";
    private SessionHandler session;

    private RequestQueue mRequestQueue;

    private Spinner deptSpinner, companySpinner;
    private ArrayAdapter<CharSequence> adapter;

    public ArrayAdapter<String> spinnerArrayAdapter;
    public List<String> companyList;

    private SwipeRefreshLayout swipeContainer;

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
        setContentView(R.layout.activity_register);

        session = new SessionHandler(getApplicationContext());

        deptSpinner = (Spinner) findViewById(R.id.Department);
// Create an ArrayAdapter using the string array and a default spinner layout
       adapter = ArrayAdapter.createFromResource(this,
                R.array.department_category, android.R.layout.simple_spinner_item);
//// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
//// Apply the adapter to the spinner
        deptSpinner.setAdapter(adapter);

        String[] companies = new String[]{

        };

        companySpinner = (Spinner) findViewById(R.id.Company);

        companyList = new ArrayList<>(Arrays.asList(companies));
        // Initializing an ArrayAdapter
        spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item, companyList);

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        companySpinner.setAdapter(spinnerArrayAdapter);

        //automatic process
        try {

            volleyFetchCompaniesRequest(BASE_URL + "show_all_companies.php");
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
                    volleyFetchCompaniesRequest(BASE_URL + "show_all_companies.php");
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


        reg_first_name = (EditText) findViewById(R.id.FirstName);
        reg_last_name = (EditText) findViewById(R.id.LastName);
        reg_employee_id = (EditText) findViewById(R.id.ID);
        reg_employee_email = (EditText) findViewById(R.id.Email);
        reg_employee_phone = (EditText) findViewById(R.id.Phone);
        //reg_company_name = (EditText) findViewById(R.id.Company);
        reg_employee_password = (EditText) findViewById(R.id.Password);
        reg_employee_confirm_password = (EditText) findViewById(R.id.ConfirmPassword);
        reg_button = (Button) findViewById(R.id.registerBtn);

        reg_employee_position = findViewById(R.id.Position);

        //radio group
        reg_employee_position.setOrientation(LinearLayout.HORIZONTAL);

        RadioButton managerBtn = new RadioButton(this);
        managerBtn.setId(Integer.parseInt("1"));
        managerBtn.setText("Manager");

        //employee btn
        RadioButton employeeBtn = new RadioButton(this);
        employeeBtn.setId(Integer.parseInt("2"));
        employeeBtn.setText("Employee");

        reg_employee_position.addView(managerBtn);
        reg_employee_position.addView(employeeBtn);

        reg_employee_position.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                employee_position = radioButton.getText().toString();
                Toast.makeText(employeeRegister.this,radioButton.getText(),Toast.LENGTH_LONG).show();
            }
        });

        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext())) {
                    register();
                } else {
                    Toast.makeText(employeeRegister.this,
                            "Ensure you are connected to the internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM err", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        token = task.getResult();

                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        //Log.w("FCM token", token);
                        //Toast.makeText(founderRegister.this, token, Toast.LENGTH_SHORT).show();
                    }
                });


    }

    public void onLoginClick(View view) {

        Intent i = new Intent(getApplicationContext(), employeeLogin.class);
        startActivity(i);
        finish();

    }


    public void register() {

        if (!STRING_EMPTY.equals(reg_first_name.getText().toString()) &&
                !STRING_EMPTY.equals(reg_last_name.getText().toString()) &&
                !STRING_EMPTY.equals(reg_employee_id.getText().toString()) &&
                !STRING_EMPTY.equals(reg_employee_phone.getText().toString()) &&
                !STRING_EMPTY.equals(token) &&
                !STRING_EMPTY.equals(reg_employee_email.getText().toString()) && !STRING_EMPTY.equals(reg_employee_password.getText().toString()) &&
                !STRING_EMPTY.equals(reg_employee_confirm_password.getText().toString())) {

            RegName = reg_first_name.getText().toString() + " " + reg_last_name.getText().toString();
            RegID = reg_employee_id.getText().toString();
            RegPhone = reg_employee_phone.getText().toString();
            RegEmail = reg_employee_email.getText().toString();
            RegCompanyName = companySpinner.getSelectedItem().toString();
            RegEmployeeDept = deptSpinner.getSelectedItem().toString();

            if(reg_employee_password.getText().toString().equals(reg_employee_confirm_password.getText().toString())) {
                RegPassword = reg_employee_password.getText().toString();
                volleyJsonObjectRequest(BASE_URL + "register.php");
            } else {
                Toast.makeText(employeeRegister.this, "Passwords do not match", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(employeeRegister.this, "No field should be empty", Toast.LENGTH_LONG).show();
        }

    }

    public void load_dashboard() {
        Intent i = new Intent(getApplicationContext(), employeeLogin.class);
        startActivity(i);
        finish();
    }

    public void volleyJsonObjectRequest(String url) {

        String REQUEST_TAG = "employeeRegistration";

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating your account. Please wait...");
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

                            //Toast.makeText(employeeRegister.this, response, Toast.LENGTH_LONG).show();

                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = (JSONObject) jsonArray.get(0);

                            success = Integer.parseInt(jsonObject.getString("success"));
                            UserID = jsonObject.getString("user_id");

                            if (success == 1) {

                                    load_dashboard();

                            } else {
                                Toast.makeText(employeeRegister.this, "Failed to create your company.", Toast.LENGTH_LONG).show();
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
                httpParams.put(KEY_TOKEN_ID, token);
                httpParams.put(KEY_EMAIL, RegEmail);
                httpParams.put(KEY_COMPANY_NAME, RegCompanyName);
                httpParams.put(KEY_EMPLOYEE_DEPT, RegEmployeeDept);
                httpParams.put(KEY_EMPLOYEE_POSITION, employee_position);
                httpParams.put(KEY_PASSWORD, RegPassword);
                return httpParams;
            }

        };

        // Adding JsonObject request to request queue
        mRequestQueue.add(jsonObjectReq);
        // Adding JsonObject request to request queue
        // AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq, REQUEST_TAG);
    }

    public void volleyFetchCompaniesRequest(String url) throws JSONException {

        String  REQUEST_TAG = "companies";

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading page...");
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

                            final JSONObject jObject = (JSONObject)jsonArray.get(1);

                            String compareValue = jObject.getString("company");

                            if (jObject.getInt("success") == 1) {

                                for(int i=0; i< jsonArray.length(); i++) {
                                    JSONObject jObject2 = (JSONObject) jsonArray.get(i);
                                    companyList.add(jObject2.getString("company"));
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
                request_map.put(KEY_COMPANY_NAME,"Remote");
                return request_map;
            }


        };

        // Adding JsonObject request to request queue
        mRequestQueue.add(jsonObjectReq);
        // Adding JsonObject request to request queue
        //mRequestQueue.add(jsonObjectReq);
        //AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);
    }

}


