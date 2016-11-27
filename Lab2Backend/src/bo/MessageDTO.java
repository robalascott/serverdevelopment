package bo;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;

import bo.Message.MessageType;

public class MessageDTO {
	
	//Attributes
	private int id;
	private String message;
	private String sender;
	private String receiver;
	private String subject;
	private MessageType type;
	private Date timestamp;
	
	//Constructors
	
	//Need empty constructor for JSON-Mapping during REST-Operations
	public MessageDTO(){}
	
	public MessageDTO(int id, String message, String sender){
		this.id = id;
		this.sender = sender;
		this.message = message;
	}

	public MessageDTO(int id, String message, String sender, Date date) {
		this.id = id;
		this.message = message;
		this.sender = sender;
		this.timestamp = date;
	}

	public MessageDTO(String message, String sender) {
		this.message = message;
		this.sender = sender;
	}

	public MessageDTO(String authenticatedUser, String message, String displayName) {
		this.sender = authenticatedUser;
		this.message = message;
		this.receiver = displayName;
	}
	public MessageDTO(int id, String message, String sender, Date date,String sub) {
		this.id = id;
		this.message = message;
		this.sender = sender;
		this.timestamp = date;
		this.subject=sub;
	}
	

	//Getters and Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}
	
	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	//Methods
	//The JSON Converter insist that getters and setters have a property unless we specifically ignore it 
	@JsonIgnore
	public String getHeader(){
		System.out.println("Getting Head: " + sender + " " + timestamp);
		return sender + " " + timestamp;
	}
	@JsonIgnore
	public String getHeaderSub(){
		return getHeader() + "  <----" + subject + "---->";
	}
}
