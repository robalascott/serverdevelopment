package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class FormatController {
	    @FXML private ColorPicker colourChooser;
	    @FXML private ColorPicker fillChooser;
	    @FXML private Slider widthChooser;
	    @FXML private Label labelWidth;
	    @FXML private HBox FormatMenu;
	    MainViewController mainViewController;
		
	    public void init(MainViewController mainViewController1) {
	    	this.mainViewController = mainViewController1;
	        fillChooser.setValue(Color.BLACK);
	        String val = Integer.toString((int) widthChooser.getValue());
	        labelWidth.setText(val);
	        widthChooser.valueProperty().addListener(new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					String value = Integer.toString((int) widthChooser.getValue());
				   labelWidth.setText(value);
					
				}
	        	
	          
	        });
		}
}
