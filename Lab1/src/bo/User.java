package bo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name="user")
@NamedQueries({
	@NamedQuery(query = "SELECT e FROM User e where e.username = :username AND e.password = :password", name = "login"),
	@NamedQuery(query = "SELECT e FROM User e where e.username = :username", name = "userdetails"),
	@NamedQuery(query = "SELECT e FROM User e", name = "userlist")
})

public class User {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private int id;
	
	@Column(name="username")
	private String username;

	@Column(name="firstname")
	private String firstname;
	
	@Column(name="lastname")
	private String lastname;
	
	@Column(name="password", nullable=false)
	private String password;
	
	@ManyToMany()
	@JoinTable(
			name="USER_MESSAGE",
			joinColumns = @JoinColumn(name="id"),
			inverseJoinColumns = @JoinColumn(name="messageId")
	)
	protected List<Message> RecievedMessages = new ArrayList<Message>();
	
	public User(){}

	public User(String username, String password,String first, String last) {
		this.username = username;
		this.password = password;
		this.firstname = first;
		this.lastname = last;
	}

	public int getId() {
		return id;
	}

	public List<Message> getRecievedMessages() {
		return RecievedMessages;
	}

	public void setRecievedMessages(List<Message> recievedMessages) {
		RecievedMessages = recievedMessages;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "User [id=" + this.id + ", firstname=" + this.firstname + ", lastname=" + this.lastname
				+ "]";};
}
