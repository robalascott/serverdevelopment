package controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;


public class ShapeMenuController {
	@FXML private VBox ShapeMenu;
	private MainViewController mainViewController;
	private ToggleGroup toggleGroup = new ToggleGroup();
	
	public void init(MainViewController mainViewController1) {
		this.mainViewController =  mainViewController1;
		ToggleButton btn = new ToggleButton();
	
		ShapeMenu.setPadding(new Insets(5, 5, 5, 5));
		ShapeMenu.setSpacing(5);
	
	    for (int i = 0; i < 8; i++) {
	        btn = new ToggleButton("Button" + i );
	        btn.setPrefSize(100, 50);
	        btn.setToggleGroup(toggleGroup);
	        ShapeMenu.getChildren().add(btn);
	      };
	      toggleGroup.selectedToggleProperty().addListener(E-> {
	    	  	System.out.println("E" + E.toString());
	    	  
	        });
	}
	

	
	
}
