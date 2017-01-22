package se.kth.lab4;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.*;
import java.util.Map;
import java.util.HashMap;

public class TodoAddActivity extends BaseActivity {
    private DatePicker datePicker;
    private EditText message,owner;
    private TextView labelOwner,labelMessage;
    private String DatebaseKey;
    private String email = "Robert";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_todo_add);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            DatebaseKey  = bundle.getString("channelkey").concat("/todos/");
        }else{
            Log.i("TodaAdd","Nullpointer");
        }

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
        this.message = (EditText)findViewById(R.id.messageTextEdit);
        this.datePicker = (DatePicker) findViewById(R.id.datePicker);
        this.labelMessage = (TextView)findViewById(R.id.messageView);
    }

    public void saveTodoObject(){

        /*Firebase*/
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        String key = db.getReference(DatebaseKey).push().getKey();
        /*TodoObject*/
        Todo todo = new Todo();
        todo.setOwner(email);
        todo.setMessage(message.getText().toString());
        todo.setDate(buildDate());

        if(todo.checkValues()){
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put( key, todo.toFireBaseTodo());
            db.getReference(DatebaseKey).updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        finish();
                    }
                }
            });
            todo.clear();
            message.setText(null);
        }else{
            Toast.makeText(this, "Fail to create",Toast.LENGTH_SHORT ).show();
        }
    }

    @NonNull
    private String buildDate(){
        StringBuilder str = new StringBuilder();
        str.append (datePicker.getDayOfMonth ());
        if(datePicker.getMonth ()+1 <10){
            str.append ("/0"+(datePicker.getMonth ()+1));
        }else{
            str.append ("/"+(datePicker.getMonth ()+1));
        }
        str.append("/"+datePicker.getYear () +" ");
        String temp = String.valueOf (android.text.format.DateFormat.format(" HH:mm:ss", new java.util.Date()));
        str.append(temp);
        return str.toString ();
    }

}