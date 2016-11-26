package ui;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import bo.UserListHandler;

@ManagedBean
@ViewScoped
public class UserListBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private Map<String, String> userList;
	private String cReciever;
	
	public UserListBean(){};   
	
	@PostConstruct
	public void init() {
		UserListHandler ur = new UserListHandler();
		userList = new HashMap<String,String>();
		userList = ur.getUserList();
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
