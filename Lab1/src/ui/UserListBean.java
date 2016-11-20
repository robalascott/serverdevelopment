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
	private Map<String,Integer> userList;
	private String cReciever;
	
	public UserListBean(){};   
	
	@PostConstruct
	public void init() {
		UserListHandler ur = new UserListHandler();
		userList = new HashMap<String,Integer>();
		userList = ur.getUserList();
	 }	 
	public Map<String,Integer> getUserList() {
		return userList;
	}
	public void setUserList(Map<String,Integer> userList) {
		this.userList = userList;
	}
	public String getcReciever() {
		return cReciever;
	}
	public void setcReciever(String cReciever) {
		this.cReciever = cReciever;
	}

}
