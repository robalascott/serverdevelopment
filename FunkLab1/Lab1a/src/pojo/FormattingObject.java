package pojo;

import java.util.Observable;

import javafx.scene.paint.Color;

public class FormattingObject extends Observable{
	private Color colour;
	private int width;
	private String fill;

	public FormattingObject(Color colour1,int width1,String fill1){
		this.colour = colour1;
		this.width = width1;
		this.fill = fill1;
		setChanged();
	    notifyObservers();
	}
	
	public Color getColour() {
		return colour;
	}

	public void setColour(Color colour) {
		this.colour = colour;
		setChanged();
	    notifyObservers();
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		setChanged();
	    notifyObservers();
	}

	public String getFill() {
		return fill;
	}

	public void setFill(String fill) {
		this.fill = fill;
		setChanged();
	    notifyObservers();
	}

	public String toStringAll(){
		return colour.toString() +" " + fill + " " + width ;
	}
}
