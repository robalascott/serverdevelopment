import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Server {

	public static void main(String[] args) {
		
		// Init Firebase
		FirebaseOptions options;
		try {
			System.out.println("Server running from: " + new File(".").getCanonicalPath());
			options = new FirebaseOptions.Builder()
					  .setServiceAccount(new FileInputStream("./src/serviceAccountKey.json"))
					  .setDatabaseUrl("https://lab4-64221.firebaseio.com")
					  .build();
			FirebaseApp.initializeApp(options);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			System.out.println("Something went wrong reading serviceAccountKey");
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// As an admin, the app has access to read and write all data, regardless of Security Rules
		DatabaseReference ref = FirebaseDatabase
		    .getInstance().getReference().child("notification");
		
		// We only want to get the newly added child(last one)
		ref.limitToLast(1).addChildEventListener(new ChildEventListener() {
			
			@Override
			public void onCancelled(DatabaseError arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String previousName) {
				// TODO Auto-generated method stub
				System.out.println("New notification request added");
				System.out.println(dataSnapshot.getValue());
				processInviteRequest(dataSnapshot);
				
			}

			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onChildRemoved(DataSnapshot arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		// Keep server running
		while(true){
			
		}
	}

	/*
	 * Process the request and send the notification, then delete the request
	 */
	private static void processInviteRequest(DataSnapshot datasnapshot) {
		
		final String sender = (String) datasnapshot.child("sender").getValue(), 
				receiver = (String) datasnapshot.child("username").getValue(), 
				groupName = (String) datasnapshot.child("groupName").getValue(), 
				groupId = (String) datasnapshot.child("groupId").getValue();
		
	    System.out.println( sender + " wants to invite " + receiver + " to group: " + groupName + "with id: " + groupId);
	    getUserTokenFromEmail(receiver, groupName, sender);
	    
	    // 
	    datasnapshot.getRef().setValue(null);
	}
	
	private static void getUserTokenFromEmail(String email, String message, String sender){
		DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user_FCMToken");
		
		// This is how you write: final Query query = ref.orderByChild("username").equalTo(email);
        final Query query = ref.orderByChild("username").equalTo(email);
        
        // TODO: Entry should be unique but could make sure it only return one
        // The other listener keept listening, no wonder my computer got slow...
        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
            	System.out.println(dataSnapshot.getRef());
            	System.out.println(dataSnapshot.hasChildren());
            	if (dataSnapshot.hasChildren()) {
            		System.out.println("Has  children");
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                    	Map<?, ?> value = (Map<?, ?>) child.getValue();
                    	System.out.println(child.getValue());
                        String userId = (String) value.get("token");
                        System.out.println("Usertoken: " + userId);
                        sendNotification("/" + userId, message, sender);
                    }
                }
            	else {
                    System.out.println("No matching user");
                }
            }
			@Override
			public void onCancelled(DatabaseError arg0) {
				// TODO Auto-generated method stub
				System.out.println("Query cancelled");
			}
        });
	}
	
	private static void sendNotification(String reciever, String messageContent, String sender){
		
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		// Declaration of Message Parameters
	    String message_url = new String("https://fcm.googleapis.com/fcm/send");
	    String message_sender_id = new String(reciever);
	    //Servers key
	    String message_key = new String("key=AAAAFpy5aYQ:APA91bEKL7EABW_98qa0qMON1mMHNRWmF-fJXMhy2IWbxTOgWqYOqQHFFfM1FqjOFvNmPXcnhuEpyFGgaEpwO8P3I529vRoV8-nRU3rQgOOHtUecR_BYYBh7OgGn7jhqfwjTP_fNSg3t");
	
	    // Generating a JSONObject for the content of the message, can be extracted through the intent, even if launched from background
	    JSONObject message = new JSONObject();
	    message.put("groupname", messageContent);
	    message.put("groupid", messageContent);
	    message.put("sender", sender);
	    
	    JSONObject notification = new JSONObject();
	    notification.put("body", "Awesome Notification");
	    notification.put("title", "Lab4");
	    // Define intent-filter when notification is clicked if activity is in background
	    // If not defined, the Main Activity is launched
	    // notification.put("click_action", "OPEN_ACTIVITY_1");
	    
	    // Compose the message to send to the user
	    JSONObject protocol = new JSONObject();
	    protocol.put("to", reciever); // Ex: Token or "/topics/topic" must have topic infront of subscribed topic
	    protocol.put("data", message);
	    // We can set the notification key which will result in firebase handling and displaying the message
	    // although this gives us no controll over how the notification is displayed and/or processed onMessageReceived
	    // will never be called in client-app if this is used
	    //protocol.put("notification", notification);
	
	    // Send Protocol
	    try {
	        HttpClient httpClient = HttpClientBuilder.create().build();
	
	        HttpPost request = new HttpPost(message_url);
	        request.addHeader("Content-Type", "application/json");
	        request.addHeader("Authorization", message_key);
	
	        StringEntity params = new StringEntity(protocol.toString());
	        request.setEntity(params);
	        System.out.println(protocol);
	
	        HttpResponse response = httpClient.execute(request);
	        System.out.println(response.toString());
	    } catch (Exception e) {
	    }
	    
	    // Remove invite-request
	}
}
