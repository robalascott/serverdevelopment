package ui;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;

import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import bo.UserHandler;

@ManagedBean
@ViewScoped
public class RegisterBean {
	private String name;
	private String password;
	private String cPassword;
	private String firstname;
	private String lastname;
	private String message = null;
	final static String errorUserName1 ="Unable to complete registration: Please select a new username";
	final static String errorUserName2 ="Username cannot be empty";
	final static String successMsg ="RegisterSuccess";
	final static String passwordError1 ="Password must be at least 6 characters long";
	final static String passwordError2 ="The passwords don't match";
	final static String successMsg2 ="Successful: Created a User";
	
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	// Getters and Setters
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getcPassword() {
		return cPassword;
	}
	public void setcPassword(String cPassword) {
		this.cPassword = cPassword;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	// Validators
	public void validateName(FacesContext f, UIComponent c, Object obj){
		String s = (String) obj;
		if(s.length() == 0)
			throw new ValidatorException(new FacesMessage(errorUserName2));
	}
	
	public void validatePassword(FacesContext f, UIComponent c, Object obj){
		password = (String) obj;
		if(password.length() <= 6)
			throw new ValidatorException(new FacesMessage(passwordError1));
	}
	
	public void validateCPassword(FacesContext f, UIComponent c, Object obj){
		cPassword = (String) obj;
		if(!password.equals(cPassword))
			throw new ValidatorException(new FacesMessage(passwordError2));
	}
	
	// Functions
	public String doRegister(){
		if(UserHandler.register(name, password,firstname,lastname)){
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage("growl",new FacesMessage(FacesMessage.SEVERITY_INFO, successMsg2, name));
			context.getExternalContext().getFlash().setKeepMessages(true);
			return successMsg;
		}else{
			FacesContext.getCurrentInstance().addMessage(null,  new FacesMessage(FacesMessage.SEVERITY_WARN,errorUserName1,"Please Try Again!"));
			return "";
		}
	}	
}
