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

import java.util.ArrayList;

/**
 * Since we want more control over the notifications, stacking them for example, we use
 * data-messages and create our own Notifications via the Notification-Manager. There are two types
 * of messages FCM can send, notifications and data but notifications are only processed if the app
 * is in the foreground.
 */
public class MessageHandlerService extends FirebaseMessagingService {

    // TODO: Just a funny thought
    // If someone else login on the same device, we don't check who the notification belong to,
    // im guessing that we would register both tokens and receive invites designated to both
    // accounts on both accounts

    final private String TAG = "Lab4";

    // We receive a notification (Currently contain: groupname, groupid and sender)
    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {

        // For debug, remove DB
        //this.getApplicationContext().deleteDatabase("FCM");

        // Insert notification data to SQLite for later access
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.insertInvite(remoteMessage);
        dbHelper.close();

        /* --- Decide what to do with notification, depending on if running or in background --- */
        // TODO: Extend baseActivity were it is needed
        // Currently we set flag in BaseActivity, not all activities extend this, so if we are in
        // login screen or for example, we would generate a Notification
        if(MyApplication.isActivityVisible()){
            Log.d(TAG, "Got notification while app active");
            // TODO: Alert user that he/she got an invite
            // Perform a local broadcast via LocalBroadcastManager, this is to be intercepted
            // by a BroadcastReceiver in the Active activity
            Intent intent = new Intent("custom_event");
            intent.putExtra("groupname", remoteMessage.getData().get("groupname"));
            intent.putExtra("sender", remoteMessage.getData().get("sender"));
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }else{
            Log.d(TAG, "Got notification while app inactive");
            generateNotification();
        }

    }

    public void generateNotification(){

        // Identifiers
        final int NOTIFICATION_ID_INVITES = 1;
        final String GROUP_KEY_EMAILS = "se.kth.lab4.NOTIFICATIONS";

        /* --- Get data from SQLite to present summary to user --- */
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        ArrayList<Invitation> invites = dbHelper.getInvitationLatest(3);
        Log.d(TAG, "Grabbed some old invites, found " + invites.size());
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

        // Dynamically create the style of the bigger notification summary
        android.support.v7.app.NotificationCompat.InboxStyle style =
                new NotificationCompat.InboxStyle();
        for(int i=0; i<invites.size() && i<4;i++){
            style.addLine(invites.get(i).getSender() + " invited you to "
                    + invites.get(i).getGroupName());
        }
        style.setBigContentTitle("You got " + amount + " pending invitations");
        style.setSummaryText("Click to see pending invites");

        // TODO: Change small icon to something pretty
        Notification summaryNotification = new NotificationCompat.Builder(this)
                .setContentTitle("You got " + Integer.toString(amount) + " pending invitations")
                .setSmallIcon(R.drawable.icon_notification)
                .setLargeIcon(largeIcon)
                .setAutoCancel(false)
                .setContentIntent(pIntent)
                .setStyle(style)
                .setGroup(GROUP_KEY_EMAILS)
                .setGroupSummary(true)
                .build();

        // Issue the notification, since we want them all to stack we use the same id
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID_INVITES, summaryNotification);
    }
}
