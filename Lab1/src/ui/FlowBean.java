package ui;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import bo.MessageDTO;
import bo.MessageHandler;
import bo.UserDTO;
import bo.UserHandler;

@ManagedBean
@ViewScoped
public class FlowBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String displayName;
	private ArrayList<MessageDTO> messages;
	private String message;
	private String searchValue = "Search";
	private List<UserDTO> matches;

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

	public FlowBean(){}
	
	public String getDisplayName() {
		
		// Apparently this is how you get the session attribute (Not sure this is how it is supposed to be (With JAAS i did req.getremoteuser())
		HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		displayName = (String) req.getSession().getAttribute("username");
		System.out.println("User:" + displayName);
		return displayName;
	}

	public void setDisplayName(String displayName) {
		HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		displayName = req.getRemoteUser();
		System.out.println("User:" + displayName);
	}

	public List<MessageDTO> getMessages(){
		System.out.println("Calling messagehandler getting messages for: " + displayName);
		messages = MessageHandler.getMessages(displayName);
		if(messages == null)
			System.out.println("Messages ÄR NULL");
		else
			System.out.println("Size: " + messages.size());
			System.out.println("Message 1: " + messages.get(0).getMessage());
		return messages;
	}
	
	public void post(){
		
		String authenticatedUser;
		HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		authenticatedUser = (String) req.getSession().getAttribute("username");
		
		MessageHandler.post(authenticatedUser, message, "Daniel");
		
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
        
		if((matches = UserHandler.findUserByName(searchValue)) != null){
			System.out.println("User found");
			if(matches.size() != 1){
				FacesContext.getCurrentInstance().getExternalContext().getFlash().put("matches", matches);
				return "multiple";
			}
			displayName = searchValue;
			searchValue = "Search";
			return "found";
		}else{
			searchValue = "Error";
			return null;
		}
	}
}
