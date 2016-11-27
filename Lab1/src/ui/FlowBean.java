package ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

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
	private String searchValue;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public FlowBean() {
	}

	public void loadData() {
		System.out.println(displayName);
	}

	public String getDisplayName() {
		// Apparently this is how you get the session attribute (Not sure this
		// is how it is supposed to be (With JAAS i did req.getremoteuser())
		if (displayName == null) {
			HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
					.getRequest();
			displayName = (String) req.getSession().getAttribute("username");
			System.out.println(this + ": Displayname not set, setting it to authenticated user: " + displayName);
		} else
			System.out.println(this + ": GetDisplayName:" + displayName);
		return displayName;
	}

	public void setDisplayName(String displayName) {
		// HttpServletRequest req =
		// (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		// displayName = req.getRemoteUser();
		this.displayName = displayName;
		System.out.println("SetDisplayname:" + displayName);
	}

	public List<MessageDTO> getMessages() {
		System.out.println("Calling messagehandler getting messages for: " + displayName);
		messages = MessageHandler.getMessages(displayName);
		if (messages.size() == 0) {
			FacesContext.getCurrentInstance().addMessage("growl",
					new FacesMessage(FacesMessage.SEVERITY_INFO, "No Messages", "Please Try Again!"));
			messages.add(new MessageDTO("This looks empty, why don't you post something!?", "System Overlord"));
		}
		return messages;
	}

	public List<MessageDTO> getInboxMessages() {
		System.out.println("Calling messagehandler getting messages for: " + displayName);
		messages = MessageHandler.getInboxMessages(displayName);
		if (messages.size() == 0) {
			FacesContext.getCurrentInstance().addMessage("growl",
					new FacesMessage(FacesMessage.SEVERITY_INFO, "No Messages", "Please Try Again!"));
			messages.add(new MessageDTO("This looks empty, are you unloved!?", "System Overlord"));
		}
		return messages;
	}

	public String post() {

		String authenticatedUser;
		HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
		authenticatedUser = (String) req.getSession().getAttribute("username");

		MessageHandler.post(authenticatedUser, message, displayName);

		message = null;
		return "RedirectMain";
	}

	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	// Called by search function with the searchValue parameter, then looks up
	// matching users in the DB and returns them as a list of users.
	// Then we either display the matching users or navigate to a user-page
	// (single match) via the serverfacelet
	public String display() {

		// TODO: Multiple matches/No matching result
		matches = UserHandler.findUserByName(searchValue);
		System.out.println(this + ": Display called with: " + searchValue + " " + matches.size());
		if (matches.isEmpty()) {
	
			searchValue = "";
			return "redirectMain";
		}
		displayName = searchValue;
		System.out.println("Displayname set to: " + displayName);
		searchValue = "Search";
		return "found";

	}

	public String doInbox() {
		return "inbox";
	}
}
