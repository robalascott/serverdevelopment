package com.kth.todoapp.todoapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

public class TodoAddActivity extends AppCompatActivity {
    DatePicker datePicker;
    EditText message,owner;
    Toolbar toolbar;
    TextView labelOwner,labelMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_add);

        UI();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    saveTodoObject();
            }
        });
    }
    public void UI(){
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.owner = (EditText)findViewById(R.id.ownerTextEdit);
        this.message = (EditText)findViewById(R.id.messageTextEdit);
        this.datePicker = (DatePicker) findViewById(R.id.datePicker);
        this.labelOwner = (TextView)findViewById(R.id.ownerView);
        this.labelMessage = (TextView)findViewById(R.id.messageView);
    }

    public void saveTodoObject(){

        /*Firebase*/
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        String key = db.getReference("Java").push().getKey();
        /*TodoObject*/
        Todo todo = new Todo();
        todo.setOwner(owner.getText().toString());
        todo.setMessage(message.getText().toString());
        todo.setDate(buildDate());

        if(todo.checkValues()){
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put( key, todo.toFireBaseTodo());
            db.getReference("Java").updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        finish();
                    }
                }
            });
            todo.clear();
            owner.setText(null);
            message.setText(null);
        }else{
            Toast.makeText(this, "Fail to create",Toast.LENGTH_SHORT ).show();
        }
    }

    private String buildDate(){
        Date date = new Date();
        date.setMonth(datePicker.getMonth());
        date.setYear(datePicker.getYear());
        date.setDate(datePicker.getDayOfMonth());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return format.format(date);
    }
}
