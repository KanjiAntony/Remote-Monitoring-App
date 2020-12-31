 package com.example.remotemonitoringapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

 public class Task extends AppCompatActivity {

     taskAdapter adapter;
     RecyclerView recyclerView;
     BottomNavigationView bottom_view;
     String active_user_id;
     Toolbar toolbar;
     private SessionHandler session;
     private static final String KEY_SESSION_ID = "session_id";
     private static final String BASE_URL = "https://remote.shamalandscapes.com/Mobile/Employee/";
     private String KEY_LOGGED = "LoggedStat";
     private String PREF_NAME = "Pop-InSession";
     private static final String KEY_USER_ID = "UserId";
    private FloatingActionButton floatingActionButton;

    private String onlineUserID;

    private ProgressDialog loader;

    private String key = "";
    private String task;
    private String description;
     Handler mHandler;
     private RequestQueue mRequestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        SharedPreferences sp = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        if (sp.getBoolean(KEY_LOGGED, false)) {
            active_user_id = sp.getString(KEY_USER_ID, "");
        } else {
            Intent i = new Intent(getApplicationContext(), employeeLogin.class);
            startActivity(i);
        }

        //automatic process
        try {

            volleyJsonObjectRequest(BASE_URL + "show_manager_tasks.php");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        toolbar = findViewById(R.id.taskToolbar);
        setSupportActionBar(toolbar);
       getSupportActionBar().setTitle("Task");


        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Task.this, upload_employer.class);
                startActivity(intent);
                finish();

            }

        });

        /*mHandler = new Handler();

        mHandler.postDelayed(m_Runnable,3000);*/

    }

     private final Runnable m_Runnable = new Runnable()
     {
         public void run()

         {

             try {
                 adapter.clear();
                 volleyJsonObjectRequestAsync(BASE_URL + "show_manager_tasks.php");
             } catch (JSONException e) {
                 e.printStackTrace();
             }

             mHandler.postDelayed(m_Runnable, 1000);
         }

     };//runnable


     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_menu,menu);
         return super.onCreateOptionsMenu(menu);
     }

     @Override
     public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.back:
                Intent intent = new Intent(Task.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
        }
         return super.onOptionsItemSelected(item);
     }

     public void load_recycler(String json) throws JSONException
     {
         JSONArray jsonArray = new JSONArray(json);

         List<taskData> list = new ArrayList<>();
         //list = getData();

         for(int i=0; i<jsonArray.length();i++) {
             final JSONObject jObject = (JSONObject)jsonArray.get(i);
             list.add(new taskData("Name :"+jObject.getString("task"),"Deadline: "+jObject.getString("deadline"),
                     "Employee: "+jObject.getString("employee"),jObject.getString("task_id")));

         }

         recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
         adapter = new taskAdapter(list, getApplication());
         recyclerView.setAdapter(adapter);
         recyclerView.setLayoutManager(new LinearLayoutManager(Task.this));

     }

     public void volleyJsonObjectRequest(String url) throws JSONException {

         String  REQUEST_TAG = "managerTasks";

         final ProgressDialog progressDialog = new ProgressDialog(this);
         progressDialog.setMessage("Accessing available tasks...");
         progressDialog.show();

         StringRequest jsonObjectReq = new StringRequest(Request.Method.POST,url,
                 new Response.Listener<String>() {

                     @Override
                     public void onResponse(String response) {


                         progressDialog.hide();

                         try {

                             //Toast.makeText(Task.this,response,Toast.LENGTH_LONG).show();

                             load_recycler(response);

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
                 //request_map.put(KEY_SESSION_ID,active_user_id);
                 request_map.put(KEY_SESSION_ID,active_user_id);
                 return request_map;
             }


         };

         // Adding JsonObject request to request queue
         //mRequestQueue.add(jsonObjectReq);
         AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);
     }

     public void volleyJsonObjectRequestAsync(String url) throws JSONException {

         String  REQUEST_TAG = "managerTasks";

         /*final ProgressDialog progressDialog = new ProgressDialog(this);
         progressDialog.setMessage("Accessing available tasks...");
         progressDialog.show();*/

         // Instantiate the cache
         Cache cache = new DiskBasedCache(getCacheDir(),1024*1024*100); // 5MB cache size

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


                         //progressDialog.hide();

                         try {

                             //Toast.makeText(Task.this,response,Toast.LENGTH_LONG).show();

                             load_recycler(response);

                         } catch(JSONException e) {
                             e.printStackTrace();
                         }

                     }
                 }, new Response.ErrorListener() {
             @Override
             public void onErrorResponse(VolleyError error) {
                 VolleyLog.d("Error: " + error.getMessage());
                 //progressDialog.hide();
             }
         }){

             @Override
             protected Map<String,String> getParams() {
                 Map<String,String> request_map = new HashMap<>();
                 //request_map.put(KEY_SESSION_ID,active_user_id);
                 request_map.put(KEY_SESSION_ID,active_user_id);
                 return request_map;
             }


         };

         // Adding JsonObject request to request queue
         mRequestQueue.add(jsonObjectReq);
         // Adding JsonObject request to request queue
         //mRequestQueue.add(jsonObjectReq);
         AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);
     }
 }
