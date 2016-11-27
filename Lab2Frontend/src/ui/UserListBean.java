package ui;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import bo.UserDTO;

@ManagedBean
@ViewScoped
public class UserListBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private Map<String, String> userList;
	private String cReciever;
	private String serverIP = "localhost";
	private String failed = "Failed to load";
	
	public UserListBean(){};   
	
	@PostConstruct
	public void init() {
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			userList = mapper.readValue(new URL("http://" + serverIP + ":8080/Lab2Backend/user/getAllUsers"), new TypeReference<HashMap<String,String>>(){});
		} catch (IOException e) {
			FacesContext.getCurrentInstance().addMessage(null,  new FacesMessage(FacesMessage.SEVERITY_WARN,failed,failed));
			e.printStackTrace();
		}
	}
	
	public Map<String,String> getUserList() {
		return userList;
	}
	
	public void setUserList(Map<String,String> userList) {
		this.userList = userList;
	}
	
	public String getcReciever() {
		return cReciever;
	}
	
	public void setcReciever(String cReciever) {
		this.cReciever = cReciever;
	}

}
