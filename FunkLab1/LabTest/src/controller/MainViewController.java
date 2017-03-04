package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

public class MainViewController implements Initializable{
	@FXML BorderPane MainView;
	@FXML MenuController menuController;
	@FXML ShapeMenuController shapeMenuController;
	@FXML CanvasController canvasController;
	@FXML FormatController FormatController;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		menuController.init(this);
		shapeMenuController.init(this);	
		canvasController.init(this);
		FormatController.init(this);
	}
}
