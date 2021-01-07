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

public class update_employer extends AppCompatActivity {

    private EditText task_description;
    private Button update_task_btn,verify_task_btn,delete_task_btn;
    private ProgressDialog pBar;
    private static final String STRING_EMPTY = "";
    private static final String KEY_TASK_ID = "task_id";
    private static final String KEY_TASK_DESC = "task_desc";
    private static final String KEY_EMPLOYEE_EMAIL = "employee_email";
    private static final String KEY_DEADLINE_DATE = "deadline_date";
    private static final String KEY_DEADLINE_TIME = "deadline_time";
    private String TaskDesc;
    private String EmployeeEmail;
    private String DeadlineDate, DeadlineTime;
    private Calendar calendar;
    private int year, month, day,hr,mn;
    private EditText TxtStopDate, TxtStopTime;
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
    String task_id;
    private static final String KEY_SESSION_ID = "session_id";

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(getApplicationContext(), Task.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_employer);

        SharedPreferences sp = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        if (sp.getBoolean(KEY_LOGGED, false)) {
            active_user_id = sp.getString(KEY_USER_ID,"");
        } else {
            Intent i = new Intent(getApplicationContext(), employeeLogin.class);
            startActivity(i);
        }

        Bundle extras = getIntent().getExtras();

        if(extras !=null){
            task_id = extras.getString("Task Id");
        }

        task_description = (EditText) findViewById(R.id.task_desc);
        TxtStopDate = (EditText)findViewById(R.id.date);
        TxtStopTime = (EditText)findViewById(R.id.time);
        update_task_btn = (Button) findViewById(R.id.btn_update_task);
        verify_task_btn = (Button) findViewById(R.id.btn_verify_task);
        delete_task_btn = (Button) findViewById(R.id.btn_delete_task);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        hr = calendar.get(Calendar.HOUR);
        mn = calendar.get(Calendar.MINUTE);


        findViewById(R.id.btn_calender_stop_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calling the method uploadBitmap to upload image
                //volleyJsonObjectRequest(bitmap);
                //showDate(year, month+1, day);
                setDate(view,300);
            }
        });

        findViewById(R.id.btn_calender_stop_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calling the method uploadBitmap to upload image
                //volleyJsonObjectRequest(bitmap);
                //showDate(year, month+1, day);
                setDate(view,500);
            }
        });

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

            volleyFetchSpecificTaskRequest(BASE_URL + "get_manager_task_update_data.php");
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
                    volleyFetchSpecificTaskRequest(BASE_URL + "get_manager_task_update_data.php");
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

        update_task_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext())) {
                    update_task();
                } else {
                    Toast.makeText(update_employer.this,
                            "Ensure you are connected to the internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        //on delete button clicked
        delete_task_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show dialog

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(update_employer.this);
                alertDialogBuilder.setTitle("Delete task...");
                alertDialogBuilder.setMessage("Are You sure?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                volleyDeleteTaskRequest(BASE_URL+"delete_task.php");
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

        //on verify button clicked
        verify_task_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show dialog

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(update_employer.this);
                alertDialogBuilder.setTitle("Verify task...");
                alertDialogBuilder.setMessage("Are You sure?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                volleyVerifyTaskRequest(BASE_URL+"verify_task.php");
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



    // start of choose date methods

    @SuppressWarnings("deprecation")
    public void setDate(View view, int status_id) {
        showDialog(status_id);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 300) {
            return new DatePickerDialog(this,
                    stopDateListener, year, month, day);
        } else if (id == 500) {
            return new TimePickerDialog(this,
                    stopTimeListener, hr, mn,true);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener stopDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    stopDate(arg1, arg2+1, arg3);
                }
            };

    private void stopDate(int year, int month, int day) {
        TxtStopDate.setText(new StringBuilder().append(year).append("-")
                .append(month).append("-").append(day));
    }

    private TimePickerDialog.OnTimeSetListener stopTimeListener = new
            TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker arg0,
                                      int arg1, int arg2) {
                    stopTime(arg1, arg2);
                }
            };

    private void stopTime(int hour, int min) {
        TxtStopTime.setText(new StringBuilder().append(hour).append(":")
                .append(min));
    }

    // end of choose date methods

    public void update_task() {

        if (!STRING_EMPTY.equals(task_description.getText().toString()) &&
                !STRING_EMPTY.equals(TxtStopDate.getText().toString()) &&
                !STRING_EMPTY.equals(TxtStopTime.getText().toString())) {

            TaskDesc = task_description.getText().toString();
            DeadlineDate = TxtStopDate.getText().toString();
            DeadlineTime = TxtStopTime.getText().toString();
            EmployeeEmail = employeeSpiner.getSelectedItem().toString();

            volleyJsonObjectRequest(BASE_URL + "update_task.php");


        } else {
            Toast.makeText(update_employer.this, "No field should be empty", Toast.LENGTH_LONG).show();
        }

    }

    public void load_dashboard() {
        Intent i = new Intent(getApplicationContext(), Task.class);
        startActivity(i);
        finish();
    }

    public void volleyJsonObjectRequest(String url) {

        String REQUEST_TAG = "updateTask";

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating task. Please wait...");
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

                            //Toast.makeText(upload_employer.this, response, Toast.LENGTH_LONG).show();

                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = (JSONObject) jsonArray.get(0);

                            success = Integer.parseInt(jsonObject.getString("success"));

                            if (success == 1) {
                                Toast.makeText(update_employer.this, "Task updated", Toast.LENGTH_LONG).show();
                                load_dashboard();
                            } else {
                                Toast.makeText(update_employer.this, "Failed to update task.", Toast.LENGTH_LONG).show();
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
                httpParams.put(KEY_TASK_DESC, TaskDesc);
                httpParams.put(KEY_EMPLOYEE_EMAIL, EmployeeEmail);
                httpParams.put(KEY_DEADLINE_DATE, DeadlineDate);
                httpParams.put(KEY_DEADLINE_TIME, DeadlineTime);
                httpParams.put(KEY_SESSION_ID, active_user_id);
                httpParams.put(KEY_TASK_ID, task_id);
                return httpParams;
            }

        };

        // Adding JsonObject request to request queue
        mRequestQueue.add(jsonObjectReq);
        // Adding JsonObject request to request queue
        // AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq, REQUEST_TAG);
    }

    public void volleyFetchSpecificTaskRequest(String url) throws JSONException {

        String  REQUEST_TAG = "companies";

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading task...");
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
                                    task_description.setText(jObject2.getString("task"));
                                    TxtStopDate.setText(jObject2.getString("deadline_date"));
                                    TxtStopTime.setText(jObject2.getString("deadline_time"));
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
                request_map.put(KEY_TASK_ID,task_id);
                return request_map;
            }


        };

        // Adding JsonObject request to request queue
        mRequestQueue.add(jsonObjectReq);
        // Adding JsonObject request to request queue
        //mRequestQueue.add(jsonObjectReq);
        //AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);
    }

    public void volleyDeleteTaskRequest(String url)  {

        String  REQUEST_TAG = "deleteTask";


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Deleting task...");
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
                                Toast.makeText(update_employer.this, jsonObject.get("message").toString(), Toast.LENGTH_LONG).show();

                                Intent i = new Intent(update_employer.this, Task.class);
                                startActivity(i);

                            } else {
                                Toast.makeText(update_employer.this, jsonObject.get("message").toString(), Toast.LENGTH_LONG).show();
                            }

                           // Toast.makeText(update_employer.this, jsonObject.get("success").toString(), Toast.LENGTH_LONG).show();



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
                request_map.put(KEY_TASK_ID,task_id);
                return request_map;
            }

        };

        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);
    }

    public void volleyVerifyTaskRequest(String url)  {

        String  REQUEST_TAG = "verifyTask";


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Verifying task...");
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
                                Toast.makeText(update_employer.this, jsonObject.get("message").toString(), Toast.LENGTH_LONG).show();

                                Intent i = new Intent(update_employer.this, Task.class);
                                startActivity(i);

                            } else {
                                Toast.makeText(update_employer.this, jsonObject.get("message").toString(), Toast.LENGTH_LONG).show();
                            }

                            // Toast.makeText(update_employer.this, jsonObject.get("success").toString(), Toast.LENGTH_LONG).show();



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
                request_map.put(KEY_TASK_ID,task_id);
                return request_map;
            }

        };

        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);
    }
}