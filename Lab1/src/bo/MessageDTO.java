package bo;

import java.util.Date;

public class MessageDTO {
	
	// Attributes
	private int id;
	private String message;
	private String sender;
	private String receiver;
	private Date timestamp;
	
	//Constructors
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

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	//Methods
	public String getHeader(){
		System.out.println("Getting Head: " + sender + " " + timestamp);
		return sender + " " + timestamp;
	}
	
}
