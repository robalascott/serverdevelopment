package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * An example of worker verticle
 */
public class WorkerVerticle extends AbstractVerticle {
	
	private MessageConsumer<Object> consumer;
	
	@Override
	public void start() throws Exception {
		System.out.println("[Worker] Starting in " + Thread.currentThread().getName());
		
		// TODO: We could create a config file (json) with more advanced settings for db-connection
		// Setup Database Connection
		JsonObject config = new JsonObject().put("connection_string","mongodb://127.0.0.1:27017/NodeTest");
		MongoClient client = MongoClient.createShared(vertx, config);
		
		// Eventlister for messages on the eventbus
		consumer = vertx.eventBus().consumer("worker.authenticate", event -> {
			JsonObject message = new JsonObject(event.body().toString());
			
			String password;
			String username;
			JsonObject query;
			
			switch(message.getString("type")){
				case "authentication":
					
					JsonObject reply = new JsonObject();
					
					// Extract data from message
					username = message.getString("username");
					password = message.getString("password");
					System.out.println("[Worker] looking for: (name: " + username + ", password: " + password + ")");
					
					// Create query for DB
					query = new JsonObject().put("name", username).put("password", password);
					
					// Query MongoDb
					client.findOne("users", query, null, res -> {
					  // If the db-query was successful
					  if (res.succeeded()) {
						  if(res.result() == null){
							  System.out.println("No user found");
							  // Should not need this 2 times
							  vertx.eventBus().send(message.getString("socket"), reply
									  .put("type", "authentication")
									  .put("status", "failed")
									  .toString());
						  }else{
							  System.out.println("User found");
							  vertx.eventBus().send(message.getString("socket"), reply
									  .put("type", "authentication")
									  .put("status", "success")
									  .put("name", username)
									  .toString());
							  System.out.println("Sending Auth");
							  vertx.eventBus().send("Auth", reply.clear()
									  .put("auth", true)
									  .put("username", username)
									  .put("socket", message.getString("socket"))
									  .toString());							  
						  }
					  } else {
						  // Error
						  vertx.eventBus().send(message.getString("socket"), reply
								  .put("type", "authentication")
								  .put("status", "failed")
								  .toString());
						  res.cause().printStackTrace();
					  }
					});
					break;
				case "register":
					System.out.println("In registrartion");
					username = message.getString("username");
					password = message.getString("password");
					
					query = new JsonObject().put("name", username).put("password", password);
					
					reply = new JsonObject();
					
					// TODO: Db-functions findUser or w/e
					client.findOne("users", query, null, res -> {
					boolean proceed = false;
					  // If the db-query was successful
					  if (res.succeeded()) {
						  if(res.result() == null){
							  // No user found
							  System.out.println("No user found");
							  proceed = true;							  
						  }else{
							  System.out.println("User found");
						  }
					  } else {
						  res.cause().printStackTrace();
					  }
					  
					  if(proceed){
						  JsonObject newUser = new JsonObject();
						  newUser.put("name", username);
						  newUser.put("password", password);
						  client.save("users", newUser, res2 -> {
	
							  if (res2.succeeded()) {
								  vertx.eventBus().send(message.getString("socket"), new JsonObject()
										  .put("type", "authentication")
										  .put("status", "success")
										  .put("name", username)
										  .toString());
							  } else {
								  vertx.eventBus().send(message.getString("socket"), new JsonObject()
										  .put("type", "authentication")
										  .put("status", "failed")
										  .toString());
								  res.cause().printStackTrace();
							  }
	
						  });
					  }else{
						  vertx.eventBus().send(message.getString("socket"), new JsonObject()
								  .put("type", "authentication")
								  .put("status", "failed")
								  .toString());
					  }			
					});
					break;
				default:
					// TODO: Reply to main-thread
					System.out.println("Unrecognized type");
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
