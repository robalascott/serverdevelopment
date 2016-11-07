import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class PresidentBean implements Serializable{
	private String name ="";
	
	public boolean currentPresident(){
		//Example of a function
		if(name.equals("Obama")){
			return true;
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
