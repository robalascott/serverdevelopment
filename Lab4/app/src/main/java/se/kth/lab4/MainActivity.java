package se.kth.lab4;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    // Pass message to new activity
    public final static String EXTRA_USERNAME = "se.kth.lab4.USERNAME";
    public static final String EXTRA_IMAGE = "se.kth.lab4.PHOTO";
    public static final String EXTRA_TOKEN = "se.kth.lab4.TOKEN";

    private GoogleApiClient mGoogleApiClient;
    // Feels wrong (Got it from getting started guide)
    private static final int RC_SIGN_IN = 9001;
    // Tag for debug/log
    private static final String TAG = "Lab4";
    private TextView mStatusTextView;
    private ProgressDialog mProgressDialog;

    // Unsure about best practices, should you have a LoginActivity or check if logged in on every
    // activity. Is it better to have the google login as main-activity? What if we want to show the
    // login activity again? on disconnect or logout or w/e?

    // When opening from background? Login and start other activity?
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent launchIntent = getIntent();

        // Configure Google Sign In, can request certain user info
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.serverId))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Check if we already got a valid login
        // This is stupid, we just asume that firebaseauth is working) furthermore, if credentials
        // are cached we dont even sign in to firebaseauth
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // Get cached result
            GoogleSignInResult result = opr.get();
            if(result.isSuccess()){
                // If launched from notification, the background flag should be set from backend
                if(launchIntent.getBooleanExtra("background", false)){
                    Log.d(TAG, "Launched from notificationClick");
                    // Start Home Activity
                    Intent intent = new Intent(this, InvitationsActivity.class);
                    // Could pass data to the activity
                    Log.d(TAG, "Passing data");
                    // Not sure if this is the best way to pass usercredentials
                    intent.putExtra(EXTRA_USERNAME, result.getSignInAccount().getGivenName());
                    intent.putExtra(EXTRA_IMAGE, result.getSignInAccount().getPhotoUrl().toString());
                    intent.putExtra(EXTRA_TOKEN, result.getSignInAccount().getIdToken());
                    startActivity(intent);
                // If launched by user
                }else {
                    // Start Home Activity
                    Intent intent = new Intent(this, TodoListActivity.class);
                    // Could pass data to the activity
                    Log.d(TAG, "Passing data");
                    // Not sure if this is the best way to pass usercredentials
                    intent.putExtra(EXTRA_USERNAME, result.getSignInAccount().getGivenName());
                    intent.putExtra(EXTRA_IMAGE, result.getSignInAccount().getPhotoUrl().toString());
                    intent.putExtra(EXTRA_TOKEN, result.getSignInAccount().getIdToken());
                    startActivity(intent);
                }
            }else{
                // Start Login Activity
                Intent intent = new Intent(this, LoginActivity.class);
                // Could pass data to the activity
                String message = "Some Data";
                intent.putExtra(EXTRA_USERNAME, message);
                startActivity(intent);
            }
        }else{
            // There were no cached result
            // Start Login Activity
            Intent intent = new Intent(this, LoginActivity.class);
            // Could pass data to the activity
            String message = "Some Data";
            intent.putExtra(EXTRA_USERNAME, message);
            startActivity(intent);
        }
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed");
    }
}
