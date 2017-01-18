package com.kth.todoapp.todoapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class TodoActivity extends AppCompatActivity {
    private RecyclerView recyclerView ;
    private TodoAdapter adapter;
    private List<Todo> todolist= new ArrayList<>();;
 //   private String channelKey ="todo/nodejs";
    private String gmail = "robalascott@gmail.com";
    private String channelKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            channelKey  = bundle.getString("channelkey");
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(TodoActivity.this, TodoAddActivity.class);
                newIntent.putExtra("channelkey",channelKey);
                TodoActivity.this.startActivity(newIntent);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        adapter = new TodoAdapter(todolist,this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setOnClick(new TodoAdapter.ItemClickListener (){
            @Override
            public void onItemClick(View v, int pos) {
                Alert(pos);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        try{
        database.getReference(channelKey).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        todolist.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Todo todo = data.getValue(Todo.class);
                            todo.setKey (data.getKey ());
                            todolist.add(todo);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TodoActivity", "getUserError");
                        isEmptyList();
                    }
                }
        );
        }catch (NullPointerException e){
            isEmptyList();
        }

    }
    public void isEmptyList(){
        if(todolist.isEmpty ()){
            Todo placeholder = new Todo ();
            placeholder.setOwner ("No Network Available !");
            placeholder.setMessage ("Problem");
            placeholder.setDate ("Now");
            todolist.add(placeholder);
            adapter.notifyDataSetChanged();
        }
    }

    public boolean Alert(int pos1){
        final int pos = pos1;
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert");
        if(todolist.size ()<=0){
            alertDialog.setMessage("Do you want to Delete this Channel ??");
        }else{
            alertDialog.setMessage("Do you want to Delete this entry??");
        }
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String name = todolist.get(pos).getMessage ();
                        Toast.makeText(getApplicationContext (),"Deleted " + name,Toast.LENGTH_LONG).show ();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(channelKey);
                        databaseReference.child (todolist.get(pos).getKey ()).setValue (null);
                        todolist.remove(pos);
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
        return false;
    }
}
