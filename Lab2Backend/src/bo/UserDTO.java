package bo;

public class UserDTO {
	
	private String username;
	private String firstName;
	private String lastName;
	// I am not sure if password should be passed like this(only used when registering)
	private String password;
	
	public UserDTO(){}
	
	public UserDTO(String username) {
		this.username = username;
	}
	
	public UserDTO(String username, String firstname, String lastname) {
		this.username = username;
		this.firstName = firstname;
		this.lastName = lastname;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
	
}
