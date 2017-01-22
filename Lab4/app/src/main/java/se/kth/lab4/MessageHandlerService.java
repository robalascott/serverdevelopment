package se.kth.lab4;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
/**
 * Since we want more control over the notifications, stacking them for example, we use
 * data-messages and create our own Notifications via the Notification-Manager. There are two types
 * of messages FCM can send, notifications and data but notifications are only processed if the app
 * is in the foreground.
 */
public class MessageHandlerService extends FirebaseMessagingService {

    final private String TAG = "Lab4";
    final private String GROUP_KEY_EMAILS = "se.kth.lab4.NOTIFICATIONS";

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.close();

        //generateNotification();
        // TODO: Don't add notification when app in foreground

        // Can only be used if FCM send a notification-message, has two types, other is data-message
        // Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message: " + remoteMessage.getData());

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("background", "hello");
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.googleg_color);

        // Test purpose
        int amount = 0;
        // Create an InboxStyle notification
        Notification summaryNotification = new NotificationCompat.Builder(this)
                .setContentTitle("You got " + amount + " pending invitations")
                .setSmallIcon(R.drawable.googleg_color)
                .setLargeIcon(largeIcon)
                .setAutoCancel(false)
                .setContentIntent(pIntent)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine("Alex Faaborg   Check this out")
                        .addLine("Jeff Chang   Launch Party")
                        .setBigContentTitle("2 new messages")
                        .setSummaryText("johndoe@gmail.com"))
                .setGroup(GROUP_KEY_EMAILS)
                .setGroupSummary(true)
                .build();

        // Issue the notification
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);
        notificationManager.notify(1, summaryNotification);

        /* LocalBroadcastManager can broadcast so that another Broadcastmanager can capture and use
           the intent
        Intent intent = new Intent("custom_event");
        intent.putExtra("data", remoteMessage.getData().get("message"));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        */
    }

    public void generateNotification(){

    }
}
