package controller;
import javafx.fxml.FXML;
import javafx.scene.control.ToolBar;


public class MenuController {
	@FXML private ToolBar ToolBar;

	private MainViewController mainViewController;
	
	
	public void init(MainViewController mainViewController1) {
		this.mainViewController =  mainViewController1;
		
	}
	
	public void exitProgram(){
		 System.exit(0);
	}
}