package ui;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import bo.MessageDTO;
import bo.MessageDTO.MessageType;
import bo.UserDTO;

@ManagedBean
@ViewScoped
public class FlowBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String displayName;
	private ArrayList<MessageDTO> messages;
	private String message;
	private String searchValue;
	private List<UserDTO> matches;
	private String serverIP = "localhost";

	public String getMessage() {
		return message;
	}

	public List<UserDTO> getMatches() {
		return matches;
	}

	public void setMatches(List<UserDTO> matches) {
		this.matches = matches;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public FlowBean(){}
	
	public void loadData(){
		System.out.println(displayName);
	}
	
	public String getDisplayName() {
			// Apparently this is how you get the session attribute (Not sure this is how it is supposed to be (With JAAS i did req.getremoteuser())
		if(displayName == null){
			HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
			displayName = (String) req.getSession().getAttribute("username");
			System.out.println(this + ": Displayname not set, setting it to authenticated user: " + displayName);
		}else
			System.out.println(this + ": GetDisplayName:" + displayName);
		return displayName;
	}

	public void setDisplayName(String displayName) {
		//HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		//displayName = req.getRemoteUser();
		this.displayName = displayName;
		System.out.println("SetDisplayname:" + displayName);
	}

	public List<MessageDTO> getMessages(){
		
		System.out.println("Calling messagehandler getting messages for: " + displayName);
		//messages = MessageHandler.getMessages(displayName);
		ObjectMapper mapper = new ObjectMapper();
		//TODO: Fix exceptions
		try {
			messages = mapper.readValue(new URL("http://" + serverIP + ":8080/Lab2Backend/message/getMessages/" + displayName + "/" + MessageType.PUBLIC), new TypeReference<List<MessageDTO>>(){});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(messages.size() == 0){
			System.out.println("No messages");
			messages.add(new MessageDTO("This looks empty, why don't you post something!?", "System Overlord"));
		}
		
		return messages;
	}
	
	public void post(){
		
		HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		String authenticatedUser = (String) req.getSession().getAttribute("username");
		
		//Create a Client with a target address pointing to the backend Rest-service
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target("http://" + serverIP + ":8080/Lab2Backend/message/post/");
		
		//Create a DTO to transmit the Message-data
		MessageDTO msg = new MessageDTO(authenticatedUser, message, displayName, MessageType.PUBLIC);
		
		//TODO: Handle response (Might get error)
		//Post to target with the "msg" parameter and expect a response
		Response response = target.request().post(Entity.json(msg), Response.class);
		String value = response.readEntity(String.class);
		System.out.println("FlowBean:Post:Response value: " + value);
		response.close(); 
		
		message = null;
	}

	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}
	
	// Called by search function with the searchValue parameter, then looks up matching users in the DB and returns them as a list of users.
	// Then we either display the matching users or navigate to a user-page (single match) via the serverfacelet
	public String display(){
        
		
		System.out.println("FlowBean:display(): " + "getting matches");
		//messages = MessageHandler.getMessages(displayName);
		ObjectMapper mapper = new ObjectMapper();
		//TODO: Fix exceptions
		try {
			matches = mapper.readValue(new URL("http://" + serverIP + ":8080/Lab2Backend/user/findUserByName/" + searchValue), new TypeReference<List<UserDTO>>(){});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//TODO: Multiple matches/No matching result
		System.out.println(this + ": Display called with: " + searchValue);
		if(matches != null){
			System.out.println("User found");
			if(matches.size() != 1){
				FacesContext.getCurrentInstance().getExternalContext().getFlash().put("matches", matches);
				return "multiple";
			}
			//TODO: Use value of match, it is not certain that the match is the exact searchValue
			displayName = searchValue;
			System.out.println("Displayname set to: " + displayName);
			searchValue = "Search";
			return "found";
		}else{
			searchValue = "Error";
			return null;
		}
	}
	public String doInbox(){
		return "inbox";
	}
}
