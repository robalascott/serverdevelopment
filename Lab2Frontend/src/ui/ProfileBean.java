package ui;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import bo.UserDTO;

@ManagedBean
@SessionScoped
public class ProfileBean implements Serializable{
	private static final long serialVersionUID = 1L;
	private String userName;
	private String firstName;
	private String lastName;
	private KeyHolder key = new KeyHolder();
	
	public ProfileBean(){
		
	}
	
	@PostConstruct
	public void init() {
		//Not a good method 
		HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		this.userName = (String) req.getSession().getAttribute("username");
		System.out.println("This is username " + this.userName);
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			UserDTO user = mapper.readValue(new URL("http://" + key.getServerIP() + ":8080/Lab2Backend/user/getUserInfo/" + this.userName), UserDTO.class);
			System.out.println(user.getFirstName());
			this.userName = user.getUsername();
			this.firstName = user.getFirstName();
			this.lastName = user.getLastName();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	 }
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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
	public String doProfile(){
		return "profile";
	}
}
