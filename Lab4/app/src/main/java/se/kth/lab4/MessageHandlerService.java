package se.kth.lab4;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
/**
 * Since we want more control over the notifications, stacking them for example, we use
 * data-messages and create our own Notifications via the Notification-Manager. There are two types
 * of messages FCM can send, notifications and data but notifications are only processed if the app
 * is in the foreground.
 */
public class MessageHandlerService extends FirebaseMessagingService {

    final private String TAG = "Lab4";

    // We receive a notification
    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {

        // For debug, remove DB
        this.getApplicationContext().deleteDatabase("FCM");

        Log.d(TAG, "Notification Message: " + remoteMessage.getData());
        Log.d(TAG, "Read data from message: " + remoteMessage.getData().get("message"));
        // Insert notification data to SQLite for later access
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.insertInvite(remoteMessage);
        dbHelper.close();

        /* --- Decide what to do with notification, depending on if running or in background --- */
        // TODO: Currently set in BaseActivity, not all activities extend this
        if(MyApplication.isActivityVisible()){
            Log.d("Lab4", "Got notification while app active");
            // TODO: Alert user that he/she got an invite
            // Perform a local broadcast via LocalBroadcastManager, this is to be intercepted
            // by a BroadcastReceiver in the Active activity
            Intent intent = new Intent("custom_event");
            intent.putExtra("data", remoteMessage.getData().get("message"));
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }else{
            Log.d("Lab4", "Got notification while app inactive");
            generateNotification();
        }

    }

    public void generateNotification(){

        // Identifiers
        final int NOTIFICATION_ID_INVITES = 1;
        final String GROUP_KEY_EMAILS = "se.kth.lab4.NOTIFICATIONS";

        /* --- Get data from SQLite to present summary to user --- */
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        //dbHelper.getInvitationLatest();
        int amount = dbHelper.getInvitationCount();
        dbHelper.close();

        // Create the intent to be launched if user click on the notification
        Intent intent = new Intent(this, MainActivity.class);
        // Signal that the activity was launched from a notification
        intent.putExtra("background", true);
        PendingIntent pIntent = PendingIntent
                .getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        /* --- Create the notification itself --- */
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.icon_notification);

        // TODO: add lines and summary
        Notification summaryNotification = new NotificationCompat.Builder(this)
                .setContentTitle("You got " + Integer.toString(amount) + " pending invitations")
                .setSmallIcon(R.drawable.icon_notification)
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

        // Issue the notification, since we want them all to stack we use the same id
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID_INVITES, summaryNotification);
    }
}
