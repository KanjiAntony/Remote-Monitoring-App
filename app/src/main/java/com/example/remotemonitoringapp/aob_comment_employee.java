package com.example.remotemonitoringapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
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
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class aob_comment_employee extends AppCompatActivity {

    private ImageButton create_comment_btn;
    private ProgressDialog pBar;
    private static final String STRING_EMPTY = "";
    private static final String KEY_COMMENT = "comment";
    private static final String KEY_AOB_ID = "aob_id";
    private String comment;
    private EditText commentTxt;
    private TextView aob,aob_sender,aob_receiver;
    private int success;
    private static final String BASE_URL = "https://remote.shammahgifts.co.ke/Mobile/Employee/";
    private SessionHandler session;

    private RequestQueue mRequestQueue;
    private SwipeRefreshLayout swipeContainer;

    private String KEY_LOGGED = "EventsLoggedStat";
    private String PREF_NAME = "Pop-InEventsSession";
    private static final String KEY_USER_ID = "EventUserId";
    String active_user_id;
    String aob_id;
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
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(getApplicationContext(), aob_employee.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aob_comment_employer);

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

        list = new ArrayList<>();
        layout = findViewById(R.id.layout1);
        layout_2 = findViewById(R.id.layout2);
        scrollView = findViewById(R.id.scrollView);

        aob = (TextView)findViewById(R.id.aob_title);
        aob_sender = (TextView)findViewById(R.id.aob_sender);
        aob_receiver = (TextView)findViewById(R.id.aob_receiver);

        //automatic process
        try {
            volleyJsonObjectRequest(BASE_URL + "get_aob_info.php");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        commentTxt = (EditText)findViewById(R.id.commentText);
        create_comment_btn = (ImageButton)findViewById(R.id.btnComment);

        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://remoteapp-ae875-default-rtdb.firebaseio.com/messages/Remote_AppAOB");

        create_comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = commentTxt.getText().toString();

                if(!messageText.equals("") && !aob_id.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", active_user_id);
                    map.put("aob_id", aob_id);
                    reference1.push().setValue(map);
                    //reference2.push().setValue(map);
                    commentTxt.setText("");
                } else {

                    Toast.makeText(aob_comment_employee.this, "No field should be empty", Toast.LENGTH_LONG).show();

                }
            }
        });

        reference1.orderByChild("aob_id").equalTo(aob_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userId = map.get("user").toString();

                if(userId.equals(active_user_id)){
                    addMessageBox(message,"", 1);
                }
                else{
                    addMessageBox(message,"", 2);
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


    }

    public void addMessageBox(String message,String sender_text, int type){
        TextView textView = new TextView(aob_comment_employee.this);
        textView.setText(message);

        TextView textView2 = new TextView(aob_comment_employee.this);
        textView2.setText(sender_text);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 7.0f;

        if(type == 1) {
            lp2.gravity = Gravity.RIGHT;
            lp2.topMargin = 10;
            textView.setPadding(20,20,20,20);
            textView.setBackgroundResource(R.drawable.bubble_in);
        }
        else{
            lp2.gravity = Gravity.LEFT;
            lp2.topMargin = 10;
            textView.setPadding(20,20,20,20);
            textView.setBackgroundResource(R.drawable.bubble_out);
            textView.setTextColor(Color.WHITE);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        layout.addView(textView2);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    public void volleyJsonObjectRequest(String url) throws JSONException {

        String REQUEST_TAG = "getAOBData";

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching AOB description...");
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
                            //UserID = jsonObject.getString("user_id");

                            if (success == 1) {
                                aob.setText(jsonObject.getString("aob"));
                                aob_sender.setText(jsonObject.getString("sender"));
                                aob_receiver.setText(jsonObject.getString("receiver"));
                            } else {
                                Toast.makeText(aob_comment_employee.this, "Failed to get AOB description.", Toast.LENGTH_LONG).show();
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
                httpParams.put(KEY_AOB_ID, aob_id);
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
