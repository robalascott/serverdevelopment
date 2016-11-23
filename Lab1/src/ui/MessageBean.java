package ui;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import bo.MessageHandler;

@ManagedBean
@ViewScoped
public class MessageBean implements Serializable{
	private static final long serialVersionUID = 1L;
	private final static String messageXhtml = "newMessage";
	private final static String mainXhtml = "main";
	
	@ManagedProperty(value = "#{LoginBean.userName}")
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
	
	/*Navigation method for newMessage()*/
	public String doMessage(){
		return messageXhtml;
	}
	/*This send message to Bo*/
	public String sendMessage(){
		MessageHandler handler = new MessageHandler();
		System.out.println(this.senderName +" "+this.recieverName+ " " + this.subject +" "+this.message);
		
		if((handler.addNewMessage(this.senderName, this.recieverName, this.subject,this.message))){
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO, "Message:Sent","Sent"));
			context.getExternalContext().getFlash().setKeepMessages(true);
			return mainXhtml;
		}else{
			 FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR, "Message:Failed to send","Failed to Send"));
			System.out.println("Message not sent");
			return null;
		}
	
	}
}
