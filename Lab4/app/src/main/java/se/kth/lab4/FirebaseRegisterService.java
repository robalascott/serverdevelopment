package se.kth.lab4;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Xirath on 2017-01-09.
 */

public class FirebaseRegisterService extends FirebaseInstanceIdService {

    String TAG = "Lab4";
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        // What about login? this is called before login right?
        // Add custom implementation, as needed.
        SharedPreferences sharedpreferences = getSharedPreferences(this.getString(R.string.prefs), Context.MODE_PRIVATE);

        // To implement: Only if user is registered, i.e. UserId is available in preference, update token on server.
        //int userId = SharedPreferenceUtils.getInstance(this).getIntValue(getString(R.string.user_id), 0);
        int userId = sharedpreferences.getInt(this.getString(R.string.user_id), 0);
        if(userId != 0){
            // Implement code to update registration token to server
            Log.d(TAG, "User is registered, sending token");
        }else{
            // Token was updated but no user was registered, set flag so that home will send token
            // to server
            sharedpreferences.edit()
                    .putBoolean(this.getString(R.string.token_updated), true).apply();
            sharedpreferences.edit()
                    .putString(this.getString(R.string.FCMTOKEN) , refreshedToken).apply();
        }
        /*
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference("notification");

        Map notification = new HashMap<>();
        notification.put("username", "dnlostberg@gmail.com");

        mDatabase.push().setValue(notification);
        */
    }
}
