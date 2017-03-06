package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import manager.UndoRedoManager;

public class MainController implements Initializable {
	@FXML MenuController menuController;
	@FXML FormatController formatController;
	@FXML ShapeMenuController shapeMenuController;
	@FXML BorderPane MainView;
	@FXML CanvasController canvasController;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		MediatorController.getInstance().registerController(menuController);
		MediatorController.getInstance().registerController(formatController);
		MediatorController.getInstance().registerController(shapeMenuController);
		MediatorController.getInstance().registerController(canvasController);
	}
}
