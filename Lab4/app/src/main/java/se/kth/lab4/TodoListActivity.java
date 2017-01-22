package se.kth.lab4;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodoListActivity extends BaseActivity {

    private static final String TAG = "Lab4";


    private RecyclerView recyclerChannelView ;
    private ChannelsAdapter adapter;
    private String m_Text;
    private List<Channels> channelList= new ArrayList<>();
    DatabaseHelper dbHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_main_channel);

        // Can get som date if was launched from background, how do we get login intent stuff though?
        // open main and attach intent info?

        Intent intent = getIntent();
        String payload = intent.getStringExtra("background");
        Log.d(TAG, "" + payload);

        // Control flag signaling if token was refreshed
        SharedPreferences sharedpreferences = getSharedPreferences(this.getString(R.string.prefs), Context.MODE_PRIVATE);
        if(sharedpreferences.getBoolean(getString(R.string.token_updated), false)){
            // Token was updated
            Log.d(TAG, "Token flag is raised, need to send update to server");
            // Do we need a reply from server? Probably should
            sendTokenToServer();
            // After we sent the new flag to the server, reset flag
            // TODO: Only reset flag on successfull update
            sharedpreferences.edit().putBoolean(getString(R.string.token_updated), false).apply();
        }else{
            Log.d(TAG, "Token up to date");
        }

        // Roberts stuff
        /*Database play things*/
        Log.i(TAG,Boolean.toString(dbHelper.doesDataExist(this,"FCM")));
     /*   dbHelper.insertInvite("Tommy");
        dbHelper.insertInvite("Tommy2");
        dbHelper.insertInvite("Tommy3");*/


        FloatingActionButton fab = (FloatingActionButton) findViewById (R.id.fab);
        fab.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                AlertBuilder();
            }
        });
        recyclerChannelView = (RecyclerView) findViewById(R.id.recycle_view);
        adapter = new ChannelsAdapter(channelList,this);
        LinearLayoutManager llmc = new LinearLayoutManager(this);
        recyclerChannelView.setLayoutManager(llmc);
        recyclerChannelView.setItemAnimator(new DefaultItemAnimator ());
        recyclerChannelView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setOnChannelClick(new ChannelsAdapter.ChannelClickListener(){
            @Override
            public void onChannelClick(View v, int pos) {
                String name = "todo/"+ channelList.get(pos).getName ();
                //  Toast.makeText(getApplicationContext (),name,Toast.LENGTH_SHORT).show ();
                Log.d ("MainChannel",name);
                if(name !=null || !name.isEmpty ()){
                    Intent oldIntent = getIntent();
                    Intent newIntent = new Intent(TodoListActivity.this, TodoActivity.class);
                    newIntent.putExtra("channelkey",name);
                    newIntent.putExtra("username", oldIntent.getStringExtra(MainActivity.EXTRA_USERNAME));
                    // TODO: Consider other solutions (Just passing data along for now)
                    newIntent.putExtra(MainActivity.EXTRA_IMAGE, oldIntent.getStringExtra(MainActivity.EXTRA_IMAGE));
                    newIntent.putExtra(MainActivity.EXTRA_USERNAME, oldIntent.getStringExtra(MainActivity.EXTRA_USERNAME));
                    startActivity(newIntent);
                }else{
                    Toast.makeText(getApplicationContext (),"No Network",Toast.LENGTH_SHORT).show ();
                }


            }

        });
    }

    // TODO: Make AsyncTask handle things like this
    private void sendTokenToServer() {
        // Probably not good practice to write to db directly
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference("user_FCMToken");

        SharedPreferences sharedpreferences = getSharedPreferences(this.getString(R.string.prefs), Context.MODE_PRIVATE);
        String token = sharedpreferences.getString(getString(R.string.FCMTOKEN), null);

        // TODO: Add some error control for invalid token
        if(token != null) {
            Map registerToFCM = new HashMap<>();
            registerToFCM.put("username", FirebaseAuth.getInstance().getCurrentUser().getEmail());
            registerToFCM.put("token", token);

            mDatabase.push().setValue(registerToFCM);
        }else{
            // This should never happen
            Log.d(TAG, "Something went wrong reading token from prefs");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchChannels(getString(R.string.DataBaseKey));
          /*Database Update Icon*/
        this.invalidateOptionsMenu();

    }

    public static void sendNotificationToUser(String user, final String message) {

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference("notification");

        Map notification = new HashMap<>();
        notification.put("username", "dnlostberg@gmail.com");
        notification.put("message", "You got a new invite");

        mDatabase.push().setValue(notification);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.action_add_task);
        if(dbHelper.hasList()){
            item.setIcon(android.R.drawable.ic_popup_reminder);
        }else{
            item.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                Intent oldIntent = getIntent();
                Intent intent = new Intent(this, InvitationsActivity.class);
                intent.putExtra(MainActivity.EXTRA_IMAGE, oldIntent.getStringExtra(MainActivity.EXTRA_IMAGE));
                intent.putExtra(MainActivity.EXTRA_USERNAME, oldIntent.getStringExtra(MainActivity.EXTRA_USERNAME));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fetchChannels(String dataBaseKey) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        database.getReference(getString(R.string.DataBaseKey)).addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        channelList.clear();
                                        // Convert the Datasnapshot to a map so we can extract the attributes
                                        for(DataSnapshot todoList : dataSnapshot.getChildren()){
                                            Log.d(TAG, "Checking group: " + todoList.getValue());
                                            DataSnapshot members = todoList.child("members");
                                            for(DataSnapshot member : members.getChildren()){
                                                String Token = (String) member.child("Token").getValue();
                                                if(member.child("Token").getValue()
                                                        .equals(FirebaseAuth.getInstance()
                                                                .getCurrentUser().getUid())){
                                                    Log.d(TAG, "User found");
                                                    Channels chans = new Channels();
                                                    chans.setName(todoList.getKey());
                                                    channelList.add(chans);
                                                }else{
                                                    Log.d(TAG, "User not found");
                                                }
                                            }
                                        }
                        adapter.notifyDataSetChanged();
                        ChannelsEmpty("No Network Available !");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("ChannelsActivity", "getUserError");
                        ChannelsEmpty("Error Loading !");
                    }
                }
        );
    }

    private void AlertBuilder(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Channel");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT );
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    Log.i("Alert",input.getText ().toString ());
                    m_Text = input.getText ().toString ();
                    if(m_Text!=null || !m_Text.isEmpty ()){

                        AsyncTaskTestActivity task = new AsyncTaskTestActivity();
                        task.add(adapter,channelList,getString(R.string.DataBaseKey));
                        task.execute(new String[] { "todo/"+m_Text,m_Text });
                        m_Text=null;}
                }catch (NullPointerException v){

                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    public void ChannelsEmpty(String str){
        if(channelList.isEmpty ()){
            Channels placeholder = new Channels ();
            placeholder.setName (str);
            channelList.add(placeholder);
            adapter.notifyDataSetChanged();
        }
    }

}

class AsyncTaskTestActivity  extends AsyncTask<String, Void, String> {
    private ChannelsAdapter adapter;
    private List<Channels> channelList;
    private String DataBaseKey;
    private static final String TAG = "Lab4";

    public void add(ChannelsAdapter adapt,List<Channels> chan,String DataBas){
        this.adapter = adapt;
        this.channelList = chan;
        this.DataBaseKey = DataBas;
    };

    @Override
    protected String doInBackground(String... params) {
        createObject(params[0],params[1]);
        Log.i ("Async",params[0]);
        return null;
    }

    /* Create a new todolist and register yourself as a member of the group
     *
     */
    private void createObject(String param, String param1) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        Log.d("Lab4","Param?: " + param);
        String key = db.getReference(param.concat("/members/")).push().getKey();
        //Log.d("Lab4", key);

        //Todo todo = new Todo("Welcome","New Channel",setDate());
        // Create new group and register yourself as a member to group
        Map<String,Object> member = new HashMap<>();
        member.put("Token",FirebaseAuth.getInstance().getCurrentUser().getUid());
        member.put("Email",FirebaseAuth.getInstance().getCurrentUser().getEmail());
        //if(todo.checkValues()){
            Map<String, Object> childUpdates = new HashMap<> ();
            childUpdates.put( key, member);
            db.getReference(param.concat("/members/")).updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        Log.d("Lab4", "Everything went fine");
                        return;
                    }else{
                        Log.d("Lab4", "Could not create new group");
                    }
                }
            });
        //}
    }

    private String setDate(){
        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat();
        String DateToStr = format.format(curDate);
        String pattern = "yyyy/MM/dd HH:mm";
        DateToStr = format.format(curDate);
        return DateToStr;
    }

    @Override
    protected void onPostExecute(String result) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference(DataBaseKey).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        channelList.clear();
                        // Convert the Datasnapshot to a map so we can extract the attributes
                        for(DataSnapshot todoList : dataSnapshot.getChildren()){
                            Log.d(TAG, "Checking group: " + todoList.getValue());
                            DataSnapshot members = todoList.child("members");
                            for(DataSnapshot member : members.getChildren()){
                                String Token = (String) member.child("Token").getValue();
                                if(member.child("Token").getValue()
                                        .equals(FirebaseAuth.getInstance()
                                                .getCurrentUser().getUid())){
                                    Log.d(TAG, "User found");
                                    Channels chans = new Channels();
                                    chans.setName(todoList.getKey());
                                    channelList.add(chans);
                                }else{
                                    Log.d(TAG, "User not found");
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("ChannelsActivity", "getUserError");
                    }
                }
        );

    }

}