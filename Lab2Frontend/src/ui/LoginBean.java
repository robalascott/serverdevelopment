package ui;


import java.io.Serializable;
import java.net.URL;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.codehaus.jackson.map.ObjectMapper;


@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {
	private String password;
	private String userName;
	private int id;
	private static final long serialVersionUID = 1L;
	private String serverIP = "localhost";
	private final static String errorLogin1 = "Incorrect Username and Password";
	private final static String doMain = "doMain";
	//Test
	public LoginBean(){
		
	}
	
	public String getPassword() {
		return password;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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

	public String doLogin(){		
		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println("doLogin: Fetching value from backend");
			this.id = mapper.readValue(new URL("http://" + serverIP + ":8080/Lab2Backend/user/login/" + userName + "/" + password), int.class);
			System.out.println("Got: " + id + " from Backend");
			if(this.id!=-1){
				HttpSession session = SessionUtils.getSession();
				session.setAttribute("username", userName);
				session.setAttribute("id", id);
				return "success";
			}else{
				FacesContext.getCurrentInstance().addMessage(null,  new FacesMessage(FacesMessage.SEVERITY_WARN,errorLogin1,"Please Try Again!"));
				return "failed";

			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
		
	}
	
	public boolean authenticated(){
		System.out.println("User authenticated");
		return true;
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
