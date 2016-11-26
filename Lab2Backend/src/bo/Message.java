package bo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="message")
public class Message {
	//@NamedQuery(query = "SELECT m FROM Message m where m.receiver = :receiver", name = "getMessagesTo")
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="messageId")
	private int id;
	
	@OneToOne
	@JoinColumn(name="sender", nullable=false)
	private User sender;
	
	@Column(name="message")
	private String message;

	@Column(name="subject")
	private String subject;
	
	@Column(name="timestamp", columnDefinition="DATETIME", nullable=false)
	private Date timestamp;
	
	@Column(name="type", nullable=false)
	private MessageType type;
	
	//Constructors
	public Message(){}
	
	public Message(User sender, String messageText, Date date, MessageType type) {
		this.message = messageText;
		this.sender = sender;
		this.timestamp = date;
		this.type = type;
	}
	
	public Message(User sender, String messageText, Date date, String subject, MessageType type) {
		this.message = messageText;
		this.sender = sender;
		this.timestamp = date;
		this.type = type;
		this.subject = subject;
	}
	//Getters and Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
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

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}
	
	public enum MessageType{
		PUBLIC, PRIVATE
	}

}
