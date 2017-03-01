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

public class MainController implements Initializable {
	@FXML
	BorderPane MainView;
	@FXML
	MenuBar MainMenu;
	@FXML
	VBox ShapeMenu;
	@FXML
	Group Canvas;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Button btn = new Button("Button" );
       ShapeMenu.getChildren().add(btn);
		
		
		Rectangle r = new Rectangle();
		r.setY(0);
		r.setWidth(400);
		r.setHeight(400);
		r.setFill(Color.WHITE);
		Canvas.getChildren().add(r);
		Canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			// gc.clearRect(e.getX() - 2, e.getY() - 2, 5, 5);

			Line line = new Line(e.getX(), e.getY(), e.getX() + 20, e.getY());
			line.setStrokeWidth(5.0);
			line.setStroke(Color.ROSYBROWN);

			Canvas.getChildren().add(line);
		});
	}
}
