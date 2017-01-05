package vertx;

import java.util.ArrayList;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

// TODO: Test Codec and Sending Objects instead of JsonObjects, 
// test alternative solutions, Fix 2 bugs, init request on client sometimes 
// fire so fast that the server has yet to set Aut to true, init also display
// incorrect users(it sends a list with all connected users not only the 
// desired group
public class Server extends AbstractVerticle {

	// Create a handler for all our connected Clients
	ConnectionHandler connectionHandler = new ConnectionHandler();
	ArrayList<String> rooms = new ArrayList<String>();
	// We need to manually unregister consumer as workaround for 
	// "java.lang.IllegalStateException: Result is already complete: succeeded"
	private MessageConsumer<Object> consumer, consumer2, consumer3, consumer4;
	
	// Start our Server verticle
	@Override
	public void start() throws Exception {
		rooms.add("General");
		rooms.add("Java");
		//------Deploy Worker Verticles------//
		vertx
			.deployVerticle("vertx.WorkerVerticle",
					new DeploymentOptions().setWorker(true));
		vertx
			.deployVerticle("vertx.messageWorker",
					new DeploymentOptions().setWorker(true));
		
		// Register listener on eventbus
		EventBus eb = vertx.eventBus();
		// Listen for Authentication Changes
		consumer = eb.consumer("Auth", event -> {
			JsonObject message = new JsonObject(event.body().toString());
			connectionHandler.setAuthenticated(message.getBoolean("auth"), message.getString("socket"), message.getString("username"));
			connectionHandler.sendUserUpdate(eb);
		});
		consumer3 = eb.consumer("CreateRoom", event -> {
			System.out.println("Create room event");
			JsonObject message = new JsonObject(event.body().toString());
			rooms.add(message.getString("room").toString());
			connectionHandler.sendRoomUpdate(eb, rooms);
		});
		consumer2 = eb.consumer("ChangeRoom", event -> {
			System.out.println("Change Room event");
			JsonObject message = new JsonObject(event.body().toString());
			System.out.println("User: " + connectionHandler.getUserName(message.getString("socket")) + " wants to change room to: " + message.getString("room"));
			connectionHandler.setCurrentRoom(message.getString("socket"), message.getString("room"));
			connectionHandler.sendUserUpdate(eb);
		});
		consumer4 = eb.consumer("SendMessage", event -> {
			System.out.println("Send Message event");
			JsonObject message = new JsonObject(event.body().toString());
			connectionHandler.sendMessageToAll(eb, message.getString("socket"), message.getString("text"));
		});
		
		// Create the server with websocket-handler
		vertx.createHttpServer().websocketHandler(new Handler<ServerWebSocket>(){
			
			// Someone connects to the server
			@Override
			public void handle(final ServerWebSocket ws) {
				
				System.out.println("Client connected: " + ws.remoteAddress());
				
				// Add connection to the connectionHandler
				connectionHandler.addConnection(ws);
				
				// Register a message-handler for the websocket
				ws.handler(new Handler<Buffer>() {
					
					// Handle messages received on the socket
					@Override
					public void handle(Buffer message) {
						System.out.println("Got message: " + message.toJsonObject());
						if(connectionHandler.isAuthenticated(ws)){
							System.out.println("Sending to message-worker");
							eb.send("worker.processMessage", message.toJsonObject()
									.put("socket", ws.textHandlerID())
									.put("userList", new JsonArray(connectionHandler.getAuthenticatedUserNames()))
									.put("roomList", new JsonArray(rooms))
									.put("currentroom", connectionHandler.getCurrentRoom(ws))
							);
								
						}else{
							System.out.println("Sending to auth-worker");
							eb.send("worker.authenticate", message.toJsonObject()
									.put("socket", ws.textHandlerID())
							);
									
						}
					}
					
	            });
				ws.endHandler(new Handler<Void>(){
					@Override
					public void handle(Void event) {
						System.out.println(connectionHandler.getUserName(ws.textHandlerID()) + " disconnected");
						connectionHandler.removeConnection(eb, ws);
					}
				});	
			}
	    }).listen(1337);
	}
	
	@Override
    public void stop() throws Exception {
		// For some reason a Severe exception is thrown unless we manually 
		// unregister consumers when we stop the verticle
		// *edit, it is a known issue (3.3.3): https://github.com/eclipse/vert.x/issues/1625
		consumer.unregister();
		consumer2.unregister();
		consumer3.unregister();
		consumer4.unregister();
    }
}