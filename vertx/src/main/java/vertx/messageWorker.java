package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;


public class messageWorker extends AbstractVerticle {

	private MessageConsumer<Object> consumer;
	
  @Override
  public void start() throws Exception {
	  System.out.println("Starting worker");
    //System.out.println("[messageWorker] Starting in " + Thread.currentThread().getName());

	  consumer = vertx.eventBus().consumer("worker.processMessage", event -> {
			JsonObject message = new JsonObject(event.body().toString());
			switch(message.getString("type")){
				case "command": 
					break;
				case "message":
					break;
				case "logout":
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
