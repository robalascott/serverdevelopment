package se.kth.lab4;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Daniel on 2017-01-18.
 */

/**
 * The template for our activities, responsible for populating the toolbar, handling themes and
 * other things in common over several Activities
 *
 * Should override onCreate and pass the extending activity:s layout as parameter
 */
public class BaseActivity extends AppCompatActivity{

    private static final String TAG = "Lab4";
    private MyReceiver receiver;

    protected void onCreate(Bundle savedInstanceState, int layoutId)
    {
        super.onCreate(savedInstanceState);
        // Set the view for the Activity
        setContentView(layoutId);

        // Get intent to process passed data
        Intent intent = getIntent();
        String userName = intent.getStringExtra(MainActivity.EXTRA_USERNAME);
        //String token = intent.getStringExtra(MainActivity.EXTRA_TOKEN);

        // Seem like this is needed, might consider Async task to handle network-related tasks
        // instead, was just for fun though
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Get image from google via supplied URL
        Drawable userIMG = null;
        try {
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(intent.getStringExtra(MainActivity.EXTRA_IMAGE)).getContent());
            userIMG = new BitmapDrawable(getResources(), bitmap);
        } catch (MalformedURLException e) {
            // TODO: Should probably handle errors
            e.printStackTrace();
        } catch (IOException e) {
            // TODO: Should probably handle errors
            e.printStackTrace();
        }

        // Adding Custom Toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get actionbar and configure items
        final ActionBar ab = getSupportActionBar();
        // Actionbar display the users name and picture
        if (ab != null) {
            ab.setTitle(userName);
            ab.setIcon(userIMG);
        }

        receiver = new MyReceiver(new Handler(), this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("custom_event"));

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set flag indicating the app is in foreground
        MyApplication.activityResumed();
        //((MyApplication) getApplication()).activityResumed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Remove flag indicating that app is in foreground
        MyApplication.activityPaused();
        //((MyApplication) getApplication()).activityPaused();
        // Remove receiver listening for notification messages
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
}
