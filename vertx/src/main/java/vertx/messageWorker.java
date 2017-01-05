package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;


public class messageWorker extends AbstractVerticle {

	private MessageConsumer<Object> consumer;
	// TODO: Access userlist and roomlist in messageWorker somehow
  @Override
  public void start() throws Exception {
	  System.out.println("Starting worker");
    //System.out.println("[messageWorker] Starting in " + Thread.currentThread().getName());

	  consumer = vertx.eventBus().consumer("worker.processMessage", event -> {
			JsonObject message = new JsonObject(event.body().toString());
			JsonObject reply;
			switch(message.getString("type")){
				case "command": 
					switch(message.getString("command")){
						case "createRoom":
							reply = new JsonObject();
							System.out.println("User want to create a room");
							if(message.getJsonArray("roomList").contains(message.getString("room"))){
								// Room already exist
								vertx.eventBus().send(message.getString("socket"), reply
										  .put("type", "message")
										  .put("text", "Failed to Created new Room")
										  .put("user", "System")
										  .toString());
							}else{
								// Can add pass or w/e to room
								System.out.println("Check Passed");
								vertx.eventBus().send(message.getString("socket"), reply
										  .put("type", "message")
										  .put("text", "Created new Room")
										  .put("user", "System")
										  .toString());
								vertx.eventBus().send("CreateRoom", reply.clear()
										  .put("room", message.getString("room"))
										  .toString());
								vertx.eventBus().send("changeRoom", reply.clear()
										  .put("type", "command")
										  .put("command", "changeRoom")
										  .put("room", message.getString("room"))
										  .put("socket", message.getString("socket"))
										  );
							}
							break;
						case "changeRoom":
							reply = new JsonObject();
							System.out.println("User want to change room");
							if(message.getJsonArray("roomList").contains(message.getString("room"))){
								// Can add pass or w/e to room
								System.out.println("Check Passed");
								vertx.eventBus().send(message.getString("socket"), reply
										  .put("type", "changeroom")
										  .put("status", "OK")
										  .put("room", message.getString("room"))
										  .toString());
								vertx.eventBus().send("ChangeRoom", reply.clear()
										  .put("socket", message.getString("socket"))
										  .put("room", message.getString("room"))
										  .toString());
							}else{
								System.out.println("Room does not exist");
							}						    
							break;
						case "init":
							// TODO: Fix init, all users are listed when logging in
							reply = new JsonObject();
							System.out.println("Got init");
							System.out.println("Reading passed userlist: " + message.getJsonArray("userList").toString());
							vertx.eventBus().send(message.getString("socket"), reply
									  .put("type", "init")
									  .put("userList", message.getJsonArray("userList"))
									  .put("roomList", message.getJsonArray("roomList"))
									  .put("currentRoom", message.getString("currentroom"))
									  .toString());
							break;
						default:
							System.out.println("Unrecognized command");
					}
					break;
				case "message":
					// Just pass it along
					vertx.eventBus().send("SendMessage", message);
					break;
				case "logout":
					reply = new JsonObject();
					System.out.println("User want to logout");
					vertx.eventBus().send("Auth", reply.clear()
							  .put("auth", false)
							  .put("socket", message.getString("socket"))
							  );
					break;
				default:
					System.out.println("Unrecognized message type");
			}
	  });
			
  }
  
	@Override
    public void stop() throws Exception {
		// For some reason a Severe exception is thrown unless we manually 
		// unregister consumers when we stop the verticle
		// *edit, it is a known issue (3.3.3): https://github.com/eclipse/vert.x/issues/1625
		consumer.unregister();
    }
}
