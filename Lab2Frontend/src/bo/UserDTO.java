package bo;

public class UserDTO {
	
	private String username;
	private String firstName;
	private String lastName;
	private String password;
	
	public UserDTO(){}
	
	public UserDTO(String username) {
		this.username = username;
	}
	
	public UserDTO(String username, String password, String firstname, String lastname) {
		this.username = username;
		this.password = password;
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
