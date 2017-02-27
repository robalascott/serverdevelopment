package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

public class ShapeMenuController {
	@FXML private VBox ShapeMenu;
	@FXML private ListView<Button> ListView = new ListView<Button>();

	
	

	private MainViewController mainViewController;
	
	
	public void init(MainViewController mainViewController1) {
		this.mainViewController =  mainViewController1;
		Button btn = new Button();
		ObservableList<Button> items =FXCollections.observableArrayList();
	    for (int i = 0; i < 8; i++) {
	        btn = new Button("Button" + i );
	        btn.setPrefSize(100, 50);
	        btn.setOnAction(e ->{
	        	System.out.println(getButtonName(e)); 
	        });
	        items.add(btn);

	      };
		ListView.setItems(items);
		
	}
	
	private String getButtonName(Event e){
		String s = null;
		try{
			s  = (String) e.getSource().toString();
			s = s.substring(s.indexOf("'")+1);
			s = s.substring(0, s.indexOf("'"));
		}catch(NullPointerException Error){
			s = "error";
		}
		return s;
	}
	
	
}
