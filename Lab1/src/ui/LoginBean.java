package ui;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import bo.UserHandler;

@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {
	private String password;
	private String userName;
	private static final long serialVersionUID = 1L;
	private final static String errorLogin1 = "Incorrect Username and Password";
	private final static String errorLogin2 = "Please enter correct Username or Password";
	
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
	public boolean doLogin(){
		UserHandler handler = new UserHandler();
		
		if(handler.login(userName, password)){
			HttpSession session = SessionUtils.getSession();
			session.setAttribute("username", userName);
			return true;
		}else{
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage( errorLogin1));
			return false;
		}
	}
	
	public boolean doLogout(){
		HttpSession session = SessionUtils.getSession();
		session.invalidate();
		return false;
	}
}
