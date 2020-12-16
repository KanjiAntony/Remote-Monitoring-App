 package com.example.remotemonitoringapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

 public class Task extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;

    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;

    private ProgressDialog loader;

    private String key = "";
    private String task;
    private String description;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        toolbar = findViewById(R.id.taskToolbar);
        setSupportActionBar(toolbar);
       getSupportActionBar().setTitle("Task");

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loader = new ProgressDialog(this);

        mUser =  mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserID);


        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();

            }

        });

    }

            private void addTask() {
                AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
                LayoutInflater inflater = LayoutInflater.from(this);

                View myView = inflater.inflate(R.layout.input_file, null);
                myDialog.setView(myView);


                AlertDialog dialog = myDialog.create();
                dialog.setCancelable(false);

                final EditText task = myView.findViewById(R.id.task);
                final EditText description = myView.findViewById((R.id.description));
                Button save = myView.findViewById(R.id.saveBtn);
                Button cancel = myView.findViewById(R.id.CancelBtn);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                save.setOnClickListener((new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }));

                String mTask = task.getText().toString().trim();
                String mDescription = description.getText().toString().trim();
                String id = reference.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());

                if (TextUtils.isEmpty(mTask)) {
                    task.setError("Task is required");
                    return;
                }

                if (TextUtils.isEmpty(mDescription)) {
                    task.setError("Description is required");
                    return;
                } else {
                    loader.setMessage("Adding your data");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    Model model = new Model(mTask, mDescription, id, date);
                    reference.child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Task.this, "Task has been inserted successfully", Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            } else {
                                String error = task.getException().toString();
                                Toast.makeText(Task.this, "Failed", Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            }

                        }
                    });


                    dialog.dismiss();

                }


                dialog.show();


            }

     @Override
     public void onStart(){
        super.onStart();

         FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>()
                 .setQuery(reference, Model.class)
                 .build();

        FirebaseRecyclerAdapter<Model, myViewHolder> adapter = new FirebaseRecyclerAdapter<Model, myViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull myViewHolder holder, final int position, @NonNull final Model model) {
                holder.setDate(model.getDate());
                holder.setTask(model.getTask());
                holder.setDesc(model.getDescription());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        key = getRef(position).getKey();
                        task = model.getTask();
                        description = model.getDescription();

                    }
                });

            }

            @NonNull
            @Override
            public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout,parent,false);
               return new myViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

         }


     public static class myViewHolder extends RecyclerView.ViewHolder{
               View mView;

                public myViewHolder(@NonNull View itemView) {
                    super(itemView);
                    mView = itemView;
                }

                public void setTask(String task){
                    TextView taskTextView = mView.findViewById(R.id.taskTv);
                    taskTextView.setText(task);
                }

                public void setDesc(String desc){
                    TextView descTextView = mView.findViewById(R.id.descriptionTv);
                    descTextView.setText(desc);
                }

                public void setDate(String date){
                    TextView dateTextView = mView.findViewById(R.id.dateTv);

                }
            }

            private void updateTask(){
               AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
               LayoutInflater inflater = LayoutInflater.from(this);
               View view = inflater.inflate(R.layout.update_data,null);
               myDialog.setView(view);

               final AlertDialog dialog = myDialog.create();

               final EditText mTask = view.findViewById(R.id.mEditTextTask);
               final EditText mDescription = view.findViewById(R.id.mEditTextDescription);

               mTask.setText(task);
               mDescription.setSelection(task.length());

               mDescription.setText(description);
               mDescription.setSelection(description.length());

               Button delButton = view.findViewById(R.id.btnDelete);
               Button updateButton = view.findViewById(R.id.btnUpdate);

               updateButton.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       task = mTask.getText().toString().trim();
                       description = mDescription.getText().toString().trim();

                       String date = DateFormat.getDateInstance().format(new Date());

                       Model model = new Model(task, description,key,date);

                       reference.child(key).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                               if (task.isSuccessful()){
                                   Toast.makeText(Task.this, "Data has been updated successfully", Toast.LENGTH_SHORT).show();
                               }else {
                                   String error = task.getException().toString();
                                   Toast.makeText(Task.this, "Update failed"+error, Toast.LENGTH_SHORT).show();
                               }
                           }
                       });

                       dialog.dismiss();
                   }
               });

               delButton.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                               if(task.isSuccessful()){
                                   Toast.makeText(Task.this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                               }else{
                                   String error = task.getException().toString();
                                   Toast.makeText(Task.this, "Failed to delete task"+error, Toast.LENGTH_SHORT).show();

                               }
                           }
                       });

                       dialog.dismiss();
                   }
               });

               dialog.show();

            }

     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_menu,menu);
         return super.onCreateOptionsMenu(menu);
     }

     @Override
     public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.back:
            mAuth.signOut();
                Intent intent = new Intent(Task.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
        }
         return super.onOptionsItemSelected(item);
     }
 }
