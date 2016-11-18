package ui;

import javax.faces.bean.ManagedBean;

import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class MessageBean {
	private final static String messageXhtml = "newMessage";
	private String senderName;
	private String recieverName;
	private String message;
	private String subject;
	
	public MessageBean(){
		
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getRecieverName() {
		return recieverName;
	}

	public void setRecieverName(String recieverName) {
		this.recieverName = recieverName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public void sendMessage(){
		
	}
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	//Navigation method for newMessage()
	public String doMessage(){
		return messageXhtml;
	}

}
