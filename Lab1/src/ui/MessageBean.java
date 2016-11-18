package ui;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.view.ViewScoped;

import bo.MessageHandler;



@ManagedBean
@ViewScoped
public class MessageBean implements Serializable{
	private static final long serialVersionUID = 1L;
	private final static String messageXhtml = "newMessage";
	
	@ManagedProperty(value = "#{userName}")
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

	public void sendMessage(){
		MessageHandler handler = new MessageHandler();
		
		if(!(handler.addNewMessage(this.senderName, this.senderName, this.message))){
			System.out.println("Message sent");
		}else{
			System.out.println("Message not sent");
		}
		
	}
}
