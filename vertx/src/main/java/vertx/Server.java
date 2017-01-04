package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Server extends AbstractVerticle {

	@Override
	public void start() throws Exception {
	  
	vertx
		.deployVerticle("vertx.WorkerVerticle",
				new DeploymentOptions().setWorker(true));
	vertx
	.deployVerticle("vertx.messageWorker",
			new DeploymentOptions().setWorker(true));
	vertx.createHttpServer().websocketHandler(new Handler<ServerWebSocket>(){

		@Override
		public void handle(final ServerWebSocket ws) {
			
			// TODO: Read about vertex shared data 
			boolean Authenticated = false;
			System.out.println("Someone connected?");
			
			EventBus eb = vertx.eventBus();
			//JsonObject jsonObject = new JsonObject(Json.encode(ws));
			//eb.send("messageWorker", ws);
			
			// Register a message-handler for the new websocket
			ws.handler(new Handler<Buffer>() {

				// Handle messages received on the socket
				
				// Could not figure out how to pass socket to the worker
				// or even how you are supposed to do, so now i just pass
				// the message to send as result
				// TODO: Pass socket to worker or Parse data in reply for status
				@Override
				public void handle(Buffer message) {
					if(Authenticated){
						eb.send("worker.processMessage", message.toJsonObject(), r -> {
							ws.writeFinalTextFrame(r.result().body().toString());
						});
					}else{
						eb.send("worker.authenticate", message.toJsonObject(), r -> {	
							ws.writeFinalTextFrame(r.result().body().toString());
						});
					}
				}
            });
		}
	    }).listen(1337);
  }
  }