package se.kth.lab4;

import android.os.Bundle;
import android.util.Log;

/**
 * Created by Daniel on 2017-01-21.
 */

public class InvitationsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_invite);

        Log.d("Lab4", "Invite window");
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        int i=0;
        for(String inv : dbHelper.getAllInvitations()){
            i++;
            Log.d("Lab4", "Invitation " + i + ": " + inv);
        }
    }
}
