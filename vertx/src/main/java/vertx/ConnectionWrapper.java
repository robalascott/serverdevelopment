package vertx;

import io.vertx.core.http.ServerWebSocket;

public class ConnectionWrapper {
	ServerWebSocket ws;
	boolean authenticated;
	private String username;
	private String currentRoom = "General";
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public ConnectionWrapper(ServerWebSocket ws, boolean isAuthenticated) {
		this.ws = ws;
		this.authenticated = isAuthenticated;
	}
	
	public boolean isAuthenticated() {
		return authenticated;
	}
	
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}
	
	public ServerWebSocket getSocket(){
		return ws;
	}

	public String getCurrentRoom() {
		return currentRoom;
	}

	public void setCurrentRoom(String currentRoom) {
		this.currentRoom = currentRoom;
	}

}
