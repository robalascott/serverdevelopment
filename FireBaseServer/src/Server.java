import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;

public class Server {

	public static void main(String[] args) {
	    // Declaration of Message Parameters
	    String message_url = new String("https://fcm.googleapis.com/fcm/send");
	    String message_sender_id = new String("/topics/test");
	    //Servers key
	    String message_key = new String("key=AAAAFpy5aYQ:APA91bEKL7EABW_98qa0qMON1mMHNRWmF-fJXMhy2IWbxTOgWqYOqQHFFfM1FqjOFvNmPXcnhuEpyFGgaEpwO8P3I529vRoV8-nRU3rQgOOHtUecR_BYYBh7OgGn7jhqfwjTP_fNSg3t");

	    // Generating a JSONObject for the content of the message
	    JSONObject message = new JSONObject();
	    message.put("message", "TEXT");
	    
	    JSONObject notification = new JSONObject();
	    notification.put("body", "body");
	    notification.put("title", "title");
	    
	    JSONObject protocol = new JSONObject();
	    // Works perfectly
	    protocol.put("to", "/topics/test");
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
