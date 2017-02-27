import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Confirmbox {
	static Boolean answer;

	
	static boolean display(String title, String message){
		Stage windowConfirm = new Stage();
		
		//Blocks background
		windowConfirm.initModality(Modality.APPLICATION_MODAL);
		windowConfirm.setTitle(message);
		windowConfirm.setWidth(280);
		windowConfirm.getIcons().add(new Image("/temp1.png"));
		//Need to block exit point for error
		windowConfirm.setOnCloseRequest(e -> {e.consume();});
		
		Button yesButton = new Button("Yes");
		yesButton.setPrefSize(100, 20);
		Button noButton = new Button("No");
		noButton.setPrefSize(100, 20);
		yesButton.setOnAction(e->{
			answer= true;
			windowConfirm.close();
		});
		
		noButton.setOnAction(e->{
			answer= false;
			windowConfirm.close();
		});
		//Question Buttons
		HBox hbox = new HBox();
		 hbox.setPadding(new Insets(15, 12, 15, 12));
		hbox.setSpacing(10);
		hbox.setStyle("-fx-background-color: #336699;");
		hbox.getChildren().addAll(yesButton,noButton);
		Scene scene = new Scene(hbox);
		windowConfirm.setScene(scene);
		windowConfirm.showAndWait();
		return answer;
	}
}
