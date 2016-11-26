package ui;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import bo.User;
import bo.UserHandler;

@ManagedBean
@SessionScoped
public class ProfileBean implements Serializable{
	private static final long serialVersionUID = 1L;
	private String userName;
	private String firstName;
	private String lastName;
	
	public ProfileBean(){
		
	}
	
	@PostConstruct
	public void init() {
		UserHandler handler = new UserHandler();
		//Not a good method 
		HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		this.userName = (String) req.getSession().getAttribute("username");
		System.out.println("this is username" + this.userName);
		User user = handler.userDetails(this.userName);
		System.out.println(user.getFirstname());
		this.userName = user.getUsername();
		this.firstName = user.getFirstname();
		this.lastName = user.getLastname();
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
