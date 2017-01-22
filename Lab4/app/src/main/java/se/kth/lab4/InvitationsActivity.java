package se.kth.lab4;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Daniel on 2017-01-21.
 */

public class InvitationsActivity extends BaseActivity {
    private RecyclerView recyclerInvView ;
    private InvitationsAdapter InvAdapter;
    private DatabaseHelper db = new DatabaseHelper(this);
    private String TAG = "DBase";
    ArrayList<InviteObject> InviteList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_invite);

        Log.d("Lab4", "Invite window");

        InviteList = db.getAllInvitations();
        recyclerInvView  = (RecyclerView) findViewById(R.id.recycle_view_invite);

        InvAdapter= new InvitationsAdapter(InviteList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerInvView.setLayoutManager(llm);
        recyclerInvView.setItemAnimator(new DefaultItemAnimator());
        recyclerInvView .setAdapter(InvAdapter);
        InvAdapter.notifyDataSetChanged();
        InvAdapter.setOnClick(new InvitationsAdapter.InviteClickListener (){
            @Override
            public void onItemClick(View v, int pos) {
                Alert(pos);

            }
        });

        Log.i(TAG,db.getDatabaseName());
        Log.i(TAG,Boolean.toString(db.hasList()));
        int i=0;
        for(InviteObject inv : db.getAllInvitations()){
            i++;
            Log.d("Lab4", "Invitation " + i + ": " + inv.getGroupname());
        }
    }
    public boolean Alert(int pos1){
        final int pos = pos1;
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Do you want to join this group??");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(db.deleteRow(InviteList.get(pos).getGroupname())){
                            Toast.makeText(getApplicationContext (),"Joined Group " +InviteList.get(pos).getGroupname(),Toast.LENGTH_LONG).show ();
                            InviteList.remove(pos);
                            InvAdapter.notifyDataSetChanged();
                        }else {
                            Toast.makeText(getApplicationContext (),"Something went wong",Toast.LENGTH_LONG).show ();
                        }

                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if(db.deleteRow(InviteList.get(pos).getGroupname())){
                            InviteList.remove(pos);
                            InvAdapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext (),"Deleted Group Invite ",Toast.LENGTH_LONG).show ();
                        }else {
                            Toast.makeText(getApplicationContext (),"Something went wong",Toast.LENGTH_LONG).show ();
                        }
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Do nothing",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext (),"Don't Care ... I see",Toast.LENGTH_LONG).show ();
                    }
                });
        alertDialog.show();
        return false;
    }
}
