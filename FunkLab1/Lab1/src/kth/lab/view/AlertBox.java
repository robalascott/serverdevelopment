package kth.lab.view;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertBox {
	
	static void display(String title, String message){
		Stage windowAlert = new Stage();
		//Blocks background
		windowAlert.initModality(Modality.APPLICATION_MODAL);
		windowAlert.setTitle(title);
		windowAlert.setWidth(280);
		
		Label label = new Label();
		label.setText(message);
		Button exitButton = new Button("eXit");
		exitButton.setOnAction(e-> windowAlert.close());
		
		VBox layout = new VBox(10);
		layout.getChildren().addAll(label,exitButton);
		layout.setAlignment(Pos.CENTER);
		Scene scene = new Scene(layout);
		windowAlert.setScene(scene);
		windowAlert.showAndWait();
	}
	
}
