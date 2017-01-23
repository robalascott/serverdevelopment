package se.kth.lab4;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daniel on 2017-01-19.
 */

public class MyReceiver extends BroadcastReceiver{

    private final Handler handler; // Handler used to execute code on the UI thread
    private Context ui;

    public MyReceiver(Handler handler, Context ui) {
        this.handler = handler;
        this.ui = ui;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d("Lab4", "Receiver got a message");
        handler.post(new Runnable() {
            @Override
            public void run() {
               // UI Code
                AlertDialog alertDialog = new AlertDialog.Builder(ui).create();
                alertDialog.setTitle(intent.getStringExtra("sender") + " has invited you to group: "
                        + intent.getStringExtra("groupname"));
                alertDialog.setMessage("Would you like to accept the invite");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Accept",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(intent.getStringExtra("groupId") + "/members/");
                                // TODO: Add feedback on result, now we just assume it worked
                                Map member = new HashMap<>();
                                member.put("Email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                member.put("Token", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                Log.d("Lab4", "Sending update to db");
                                databaseReference.push().setValue(member);
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Decline",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
    }
}
