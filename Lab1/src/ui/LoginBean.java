package ui;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {
	private String password;
	private String userName;
	private static final long serialVersionUID = 1L;
	public String getPassword() {
		return password;
	}
	public LoginBean(){
		
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	//Login Confirmation to db via handler
	//Future exit point for RESTful or Winks
	public String doLogin(){
		return "main";
		
	}
}
