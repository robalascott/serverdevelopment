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
	
	@ManagedProperty(value = "#{LoginBean.id}")
	private int senderName;
	private Integer recieverName;
	private String message;
	private String subject;
	
	public MessageBean(){
		
	}


	public int getSenderName() {
		return senderName;
	}

	public void setSenderName(int senderName) {
		this.senderName = senderName;
	}

	public Integer getRecieverName() {
		return recieverName;
	}

	public void setRecieverName(Integer recieverName) {
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
		System.out.println(this.senderName +" "+this.recieverName+ " " + this.subject +" "+this.message);
		/*
		if((handler.addNewMessage(this.senderName, this.recieverName, this.message))){
			System.out.println("Message sent");
		}else{
			System.out.println("Message not sent");
		}
		*/
	}
}
