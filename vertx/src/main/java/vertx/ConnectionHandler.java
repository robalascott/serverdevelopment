package vertx;

import java.util.ArrayList;

import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ConnectionHandler {
	
	ArrayList<ConnectionWrapper> socketList = new ArrayList<ConnectionWrapper>();
	
	public void addConnection(ServerWebSocket ws) {
		ConnectionWrapper wrapper = new ConnectionWrapper(ws, false);
		socketList.add(wrapper);
	}
	public boolean isAuthenticated(ServerWebSocket ws){
		return getWrapper(ws).isAuthenticated();
	}
	
	public void setAuthenticated(Boolean auth, String socketId, String name) {
		ConnectionWrapper connection = getWrapperById(socketId);
		if(name != null){
			connection.setUsername(name);
			connection.setCurrentRoom("General");
		}else{
			connection.setCurrentRoom(null);
			connection.setUsername(null);
		}
		connection.setAuthenticated(auth);
	}	
	
	private ConnectionWrapper getWrapperById(String id){
		for(ConnectionWrapper cw : socketList){
			if(cw.getSocket().textHandlerID().equals(id)){
				return cw;
			}
		}
		return null;
	}
	private ConnectionWrapper getWrapper(ServerWebSocket ws){
		for(ConnectionWrapper cw : socketList){
			if(cw.getSocket().equals(ws)){
				return cw;
			}
		}
		return null;
	}
	
	public ArrayList<String> getAuthenticatedUserNames() {
		ArrayList<String> userList = new ArrayList<String>();
		for(ConnectionWrapper cw : socketList){
			if(cw.isAuthenticated()){
				userList.add(cw.getUsername());
			}
		}
		return userList;
	}
	public String getCurrentRoom(ServerWebSocket ws) {
		return getWrapper(ws).getCurrentRoom();
	}
	public void setCurrentRoom(String socket, String currentRoom){
		getWrapperById(socket).setCurrentRoom(currentRoom);
	}
	public String getUserName(String socket) {
		return getWrapperById(socket).getUsername();
	}
	public void sendRoomUpdate(EventBus eb, ArrayList<String> roomList) {
		JsonObject roomUpdate = new JsonObject()
				.put("type", "updateRooms")
				.put("roomList", new JsonArray(roomList));
		for(ConnectionWrapper cw : socketList){
			if(cw.isAuthenticated())
				eb.send(cw.getSocket().textHandlerID(), roomUpdate.toString());
		}
	}
	public void sendUserUpdate(EventBus eb) {
		for(ConnectionWrapper cw : socketList){
			if(cw.isAuthenticated()){
	    		ArrayList<String> connectedUsersByName = new ArrayList<String>();
	        	// Get username from all connected sockets
	        	for(ConnectionWrapper othercw : socketList){
	        		if(othercw.isAuthenticated()){
	        			if(cw.getCurrentRoom().equals(othercw.getCurrentRoom())){
		    	    		connectedUsersByName.add(othercw.getUsername());
	        			}
	        		}
	        	}
	        	System.out.println("Sending update to: " + cw.getUsername());
				System.out.println("Updated list: " + connectedUsersByName.toString());
				eb.send(cw.getSocket().textHandlerID(), new JsonObject()
						.put("type", "updateUsersConnectedList")
						.put("userList", new JsonArray(connectedUsersByName))
						.toString()
				);
			}
    	}
	}
	public void sendMessageToAll(EventBus eb, String socketId, String message) {
		ConnectionWrapper sender = getWrapperById(socketId);
		for(ConnectionWrapper cw : socketList){
			if(sender.getCurrentRoom().equals(cw.getCurrentRoom())){
				eb.send(cw.getSocket().textHandlerID(), new JsonObject()
						.put("type", "message")
						.put("text", message)
						.put("user", sender.getUsername())
						.toString()
				);
			}
		}
	}
	public void removeConnection(EventBus eb, ServerWebSocket ws) {
		socketList.remove(this.getWrapper(ws));
		sendUserUpdate(eb);
	}
}
