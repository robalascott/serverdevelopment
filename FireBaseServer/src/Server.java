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
			System.out.println(new File(".").getCanonicalPath());
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
				System.out.println("Child added (new request) previous name(what is this): " + previousName);
				System.out.println(dataSnapshot.getValue());
				processData((Map<String,Object>) dataSnapshot.getValue());
				
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

	private static void processData(Map<String,Object> user) {
	    String email = (String) user.get("username");
	    String message = (String) user.get("groupId");
	    System.out.println("Someone wants to invite " + email + " Bonus message: " + message);
	    getUserTokenFromEmail(email, message);
	}
	
	private static void getUserTokenFromEmail(String email, String message){
		DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user_FCMToken");
		
		// This is how you write: final Query query = ref.orderByChild("username").equalTo(email);
        final Query query = ref.orderByChild("username").equalTo(email);
        
        // TODO: Entry should be unique but could make sure it only return one
        query.addValueEventListener(new ValueEventListener()
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
                        sendNotification("/" + userId, message);
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
	
	private static void sendNotification(String reciever, String messageContent){
		// Declaration of Message Parameters
	    String message_url = new String("https://fcm.googleapis.com/fcm/send");
	    String message_sender_id = new String(reciever);
	    //Servers key
	    String message_key = new String("key=AAAAFpy5aYQ:APA91bEKL7EABW_98qa0qMON1mMHNRWmF-fJXMhy2IWbxTOgWqYOqQHFFfM1FqjOFvNmPXcnhuEpyFGgaEpwO8P3I529vRoV8-nRU3rQgOOHtUecR_BYYBh7OgGn7jhqfwjTP_fNSg3t");
	
	    // Generating a JSONObject for the content of the message
	    JSONObject message = new JSONObject();
	    message.put("message", messageContent);
	    
	    JSONObject notification = new JSONObject();
	    notification.put("body", "Awesome Notification");
	    notification.put("title", "Lab4");
	    
	    JSONObject protocol = new JSONObject();
	    // Works perfectly
	    protocol.put("to", reciever); // Ex: Token or "/topics/topic" must have topic infront of subscribed topic
	    protocol.put("data", message);
	    protocol.put("notification", notification);
	
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
	}
}
