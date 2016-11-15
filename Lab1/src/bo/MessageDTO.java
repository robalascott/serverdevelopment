package bo;


public class MessageDTO {
	
	private int id;
	private String message;
	private String receiver;
	private String sender;
	
	public MessageDTO(){
		
	}

	public MessageDTO(int id, String message, String receiver, String sender) {
		this.id = id;
		this.message = message;
		this.receiver = receiver;
		this.sender = sender;
	}

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
		message = message;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		receiver = receiver;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		sender = sender;
	}

}
