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
	String name;
	String password;
	String cPassword;
	String message = null;
	
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
			throw new ValidatorException(new FacesMessage("Name cannot be empty"));
	}
	
	public void validatePassword(FacesContext f, UIComponent c, Object obj){
		password = (String) obj;
		if(password.length() < 6)
			throw new ValidatorException(new FacesMessage("Password must be atleast 6 characters long"));
	}
	
	public void validateCPassword(FacesContext f, UIComponent c, Object obj){
		cPassword = (String) obj;
		if(!password.equals(cPassword))
			throw new ValidatorException(new FacesMessage("The passwords don't match"));
	}
	
	// Functions
	public String doRegister(){
		if(UserHandler.register(name, password)){
			this.message="";
			return "RegisterSuccess";
		}else{
			this.message = "Unable to complete registration, please try again later";
			return "RegisterFailed";
		}
	}
	public String doRedirect(){
		
			return "RedirectLogin";

	}		
}
