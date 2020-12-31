package com.example.remotemonitoringapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;


public class comment_employer extends AppCompatActivity {

    private ImageButton create_comment_btn;
    private ProgressDialog pBar;
    private static final String STRING_EMPTY = "";
    private static final String KEY_COMMENT = "comment";
    private static final String KEY_TASK_ID = "task_id";
    private String comment;
    private EditText commentTxt;
    private String UserID;
    private int success;
    private static final String BASE_URL = "https://remote.shamalandscapes.com/Mobile/Employee/";
    private SessionHandler session;

    private RequestQueue mRequestQueue;
    private SwipeRefreshLayout swipeContainer;

    private String KEY_LOGGED = "LoggedStat";
    private String PREF_NAME = "Pop-InSession";
    private static final String KEY_USER_ID = "UserId";
    String active_user_id;
    String task_id;
    private static final String KEY_SESSION_ID = "session_id";
    Handler mHandler;
    commentAdapter adapter;
    RecyclerView recyclerView;
    List<commentData> list;
    private static final String TAG = "MainActivity";
    Firebase reference1, reference2;
    LinearLayout layout;
    RelativeLayout layout_2;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_employer);

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

        list = new ArrayList<>();
        layout = findViewById(R.id.layout1);
        layout_2 = findViewById(R.id.layout2);
        scrollView = findViewById(R.id.scrollView);


        //automatic process
        /*try {

            volleyJsonObjectRequestAsync(BASE_URL + "show_task_comments.php");
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        commentTxt = (EditText)findViewById(R.id.commentText);
        create_comment_btn = (ImageButton)findViewById(R.id.btnComment);

        /*create_comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext())) {
                    create_comment();
                } else {
                    Toast.makeText(comment_employer.this,
                            "Ensure you are connected to the internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });*/

        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://remoteapp-ae875-default-rtdb.firebaseio.com/messages/Kanji_Marto");
        //reference2 = new Firebase("https://remoteapp-ae875-default-rtdb.firebaseio.com/messages/" + UserDetails.chatWith + "_" + UserDetails.username);

        create_comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = commentTxt.getText().toString();

                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", active_user_id);
                    map.put("task_id", task_id);
                    reference1.push().setValue(map);
                    //reference2.push().setValue(map);
                    commentTxt.setText("");
                }
            }
        });

        reference1.orderByChild("task_id").equalTo(task_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();

                if(userName.equals(active_user_id)){
                    addMessageBox(message,userName, 1);
                }
                else{
                    addMessageBox(message,userName, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



        /*mHandler = new Handler();

        mHandler.postDelayed(m_Runnable,3000);*/

    }

    public void addMessageBox(String message,String username, int type){
        TextView textView = new TextView(comment_employer.this);
        textView.setText(message);

        TextView textView2 = new TextView(comment_employer.this);
        textView2.setText("Sent by: "+username);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 7.0f;

        if(type == 1) {
            lp2.gravity = Gravity.LEFT;
            lp2.topMargin = 10;
            textView.setPadding(20,20,20,20);
            textView.setBackgroundResource(R.drawable.bubble_in);
        }
        else{
            lp2.gravity = Gravity.RIGHT;
            lp2.topMargin = 10;
            textView.setPadding(20,20,20,20);
            textView.setBackgroundResource(R.drawable.bubble_out);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        layout.addView(textView2);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    /*private final Runnable m_Runnable = new Runnable()
    {
        public void run()

        {

            try {
                adapter.clear();
                //adapter.notifyDataSetChanged();
                volleyJsonObjectRequestAsync(BASE_URL + "show_task_comments.php");
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mHandler.postDelayed(m_Runnable, 5000);
        }

    };*/



    public void create_comment() {

        if (!STRING_EMPTY.equals(commentTxt.getText().toString())) {

            comment = commentTxt.getText().toString();

            volleyJsonObjectRequest(BASE_URL + "create_manager_comment.php");


        } else {
            Toast.makeText(comment_employer.this, "No field should be empty", Toast.LENGTH_LONG).show();
        }

    }

    public void volleyJsonObjectRequest(String url) {

        String REQUEST_TAG = "createComment";

        /*final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending comment...");
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
                                Toast.makeText(comment_employer.this, "Comment sent", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(comment_employer.this, "Failed to send comment.", Toast.LENGTH_LONG).show();
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
                httpParams.put(KEY_COMMENT, comment);
                httpParams.put(KEY_TASK_ID, task_id);
                httpParams.put(KEY_SESSION_ID, active_user_id);
                return httpParams;
            }

        };

        // Adding JsonObject request to request queue
        mRequestQueue.add(jsonObjectReq);
        // Adding JsonObject request to request queue
        // AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq, REQUEST_TAG);
    }

    /*public void load_recycler(String json) throws JSONException
    {
        JSONArray jsonArray = new JSONArray(json);

        //list = getData();

        JSONObject jsonObject = (JSONObject) jsonArray.get(0);

        success = Integer.parseInt(jsonObject.getString("success"));
        //UserID = jsonObject.getString("user_id");

        if (success == 1) {

            for (int i = 0; i < jsonArray.length(); i++) {
                final JSONObject jObject = (JSONObject) jsonArray.get(i);
                list.add(new commentData(jObject.getString("comment"), "Posted  on: " + jObject.getString("datetime"),
                        "By: " + jObject.getString("commentBy")));

            }

        } else {

            list.add(new commentData("No comment","null","null"));

        }

        recyclerView = (RecyclerView)findViewById(R.id.commentRecycler);
        adapter = new commentAdapter(list, getApplication());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(comment_employer.this));
        //adapter.notifyItemRangeChanged(0, list.size());

    }*/

    /*public void volleyJsonObjectRequestAsync(String url) throws JSONException {

        String  REQUEST_TAG = "managerComment";

         *//*final ProgressDialog progressDialog = new ProgressDialog(this);
         progressDialog.setMessage("Accessing available tasks...");
         progressDialog.show();*//*

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
                request_map.put(KEY_TASK_ID,task_id);
                return request_map;
            }


        };

        // Adding JsonObject request to request queue
        mRequestQueue.add(jsonObjectReq);
        // Adding JsonObject request to request queue
        //mRequestQueue.add(jsonObjectReq);
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);
    }*/
}
