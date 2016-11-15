package ui;

import java.io.Serializable;
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
import bo.UserHandler;

@ManagedBean
@ViewScoped
public class FlowBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String displayName;
	private ArrayList<MessageDTO> messages;

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

	public ArrayList<MessageDTO> getMessages(){
		System.out.println("Calling messagehandler getting messages for: " + displayName);
		messages = MessageHandler.getMessages(displayName);
		if(messages == null)
			System.out.println("Messages ÄR NULL");
		else
			System.out.println("Size: " + messages.size());
		return messages;
	}
}
