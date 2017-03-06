package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CanvasController implements Initializable{
	@FXML Canvas canvas;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		final GraphicsContext gc = canvas.getGraphicsContext2D(); 
		
	
	    gc.setFill(Color.AQUAMARINE);
	    gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

}
