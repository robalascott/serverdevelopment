package ui;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import bo.UserHandler;

@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {
	private String password;
	private String userName;
	private int id;
	private static final long serialVersionUID = 1L;
	private final static String errorLogin1 = "Incorrect Username and Password";
	private final static String doMain = "doMain";
	
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
		//UserHandler returns an id value 
		this.id = handler.login(userName, password);
		if(this.id!=-1){
			HttpSession session = SessionUtils.getSession();
			session.setAttribute("username", userName);
			session.setAttribute("id", id);
			return true;
		}else{
			//Not working
			FacesContext.getCurrentInstance().addMessage("password",new FacesMessage( errorLogin1));
			return false;
		}
	}
	
	public boolean doLogout(){
		HttpSession session = SessionUtils.getSession();
		session.invalidate();
		return false;
	}
	//Main menu handler used in different views
	public String doMain(){
		return doMain;
	}
}
