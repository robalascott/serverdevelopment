package controller;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class CanvasController {
	
	private MainViewController mainViewController;
	@FXML private Group canvas;
	
	public void init(MainViewController mainViewController1) {
		this.mainViewController =  mainViewController1;
		  Rectangle r = new Rectangle();
	        r.setX(0);
		  	r.setY(0);
	        r.setWidth(500);
	        r.setHeight(500);
	        r.setFill(Color.WHITE);
	        canvas.getChildren().add(r);
		  Line line1 = new Line(2, 2, 7, 9);
	        line1.setStrokeWidth(5.0);
	        line1.setStroke(Color.GREEN);
	        
	        
	        canvas.getChildren().add(line1);
		
			 canvas.addEventHandler(MouseEvent.MOUSE_CLICKED,e-> {
	               //gc.clearRect(e.getX() - 2, e.getY() - 2, 5, 5);
				
				   Line line = new Line(e.getX(), e.getY() , e.getX() + 7, e.getY());
			        line.setStrokeWidth(5.0);
			        line.setStroke(Color.GREEN);
			        
			        canvas.getChildren().add(line);
	           }
	       );
		
	}		
	
	public void test(){
		  Rectangle r1 = new Rectangle();
	        r1.setY(0 );
	        r1.setWidth(10);
	        r1.setHeight(10);
	        r1.setFill(Color.RED);

	        canvas.getChildren().add(r1);
	}
}
