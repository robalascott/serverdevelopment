package se.kth.lab4;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;




public class MainChannelActivity extends BaseActivity {
    private RecyclerView recyclerChannelView ;
    private ChannelsAdapter adapter;
    private String m_Text;
    private static final String TAG = "Lab4";
    private List<Channels> channelList= new ArrayList<>();;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState, R.layout.activity_main_channel);

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
                    Intent newIntent = new Intent(MainChannelActivity.this, TodoListActivity.class);
                    newIntent.putExtra("channelkey",name);
                    startActivity(newIntent);
                }else{
                    Toast.makeText(getApplicationContext (),"No Network",Toast.LENGTH_SHORT).show ();
                }


            }

        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        fetchChannels(getString(R.string.DataBaseKey));

    }
    @Override
    protected void onResume() {
        super.onResume();
        fetchChannels(getString(R.string.DataBaseKey));
    }

    private void fetchChannels(String dataBaseKey) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference(getString(R.string.DataBaseKey)).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        channelList.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Log.i("ChannelsActivity",data.getKey ());
                            Channels chans = new Channels ();
                            chans.setName (data.getKey ());
                            if(chans.getName().isEmpty () || chans.getName()==null){
                                Log.w("ChannelsActivity", "Emtpy channel");
                            }else{
                                channelList.add(chans);
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
                        //   saveTodoObject("todo/"+m_Text,m_Text);
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