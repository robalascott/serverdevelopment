package ui;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.primefaces.context.RequestContext;

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
	//Login Confirmation to db via handler
	//Future exit point for RESTful or Winks
	public String doLogin(){
		
		/* Copied code for help
		//Product obj = mapper.readValue(new URL("http://localhost:8080/RestFulTest/json/product/get"), Product.class);
		//System.out.println("Successfully recieved object from rest; Name of product: " + obj.getName());
		
		//URL url = new URL("http://localhost:8080/Lab2Backend/message/getTest");
		//HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		//conn.setRequestMethod("GET");
		//conn.setRequestProperty("Accept", "application/json");
		
		ObjectMapper mapper = new ObjectMapper();
		Car car = mapper.readValue(new URL("http://localhost:8080/Lab2Backend/message/getTest"), bo.Car.class);
		String test = mapper.readValue(new URL("http://localhost:8080/Lab2Backend/message/getTest"), String.class);
		
		//conn.disconnect();
		*/
		
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
		
		/* Old Code with handler
		UserHandler handler = new UserHandler();
		//UserHandler returns an id value 
		this.id = handler.login(userName, password);
		if(this.id!=-1){
			HttpSession session = SessionUtils.getSession();
			session.setAttribute("username", userName);
			session.setAttribute("id", id);
			return "success";
		}else{
			FacesContext.getCurrentInstance().addMessage(null,  new FacesMessage(FacesMessage.SEVERITY_WARN,errorLogin1,"Please Try Again!"));
			return "failed";

		}
		*/
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
