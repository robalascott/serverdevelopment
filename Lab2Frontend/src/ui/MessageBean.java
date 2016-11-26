package ui;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.view.ViewScoped;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import bo.MessageDTO;
import bo.MessageDTO.MessageType;

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
	private String serverIP = "localhost";
	
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
		//MessageHandler handler = new MessageHandler();
		
		//Create a Client with a target address pointing to the backend Rest-service
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target("http://" + serverIP + ":8080/Lab2Backend/message/post/");
		
		//Create a DTO to transmit the Message-data
		MessageDTO msg = new MessageDTO(senderName, message, recieverName, subject, MessageType.PRIVATE);
		
		//TODO: Handle response (Might get error)
		//Post to target with the "msg" parameter and expect a response
		Response response = target.request().post(Entity.json(msg), Response.class);
		String value = response.readEntity(String.class);
		System.out.println("FlowBean:Post:Response value: " + value);
		response.close(); 
		
		message = null;
		
		System.out.println(this.senderName +" "+this.recieverName+ " " + this.subject +" "+this.message);
		
		//TODO: Replace with responses to response value
		/*
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
		*/
		return null;
	
	}
}
