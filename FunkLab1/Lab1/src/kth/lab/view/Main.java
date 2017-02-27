package kth.lab.view;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application{
	Stage mainWindow;
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage mainWindow) throws Exception {
		this.mainWindow = mainWindow;
		mainWindow.setTitle("Drawz");   
		mainWindow.getIcons().add(new Image("/temp1.png"));
		mainWindow.setOnCloseRequest(e -> {e.consume();closeProgram(mainWindow);});
		BorderPane root = new BorderPane();
		VBox topContainer = new VBox();  
		MenuBar mainMenu = new MenuBar();  
		ToolBar toolBar = new ToolBar(); 
		topContainer.getChildren().add(mainMenu);
		topContainer.getChildren().add(toolBar);
		 
		root.setTop(topContainer);
		
		EventHandler<ActionEvent> action = changeTabPlacement();
		//Create and add the "File" sub-menu options. 
		Menu file = new Menu("File");
		MenuItem openFile = new MenuItem("Open File");
		openFile.setOnAction(action);
		MenuItem exitApp = new MenuItem("Exit");
		exitApp.setOnAction(action);
		file.getItems().addAll(openFile,exitApp);
		 
		//Create and add the "Edit" sub-menu options.
		Menu edit = new Menu("Edit");
		MenuItem properties = new MenuItem("Properties");
		properties.setOnAction(action);
		edit.getItems().add(properties);
		 
		//Create and add the "Help" sub-menu options.
		Menu help = new Menu("Help");
		MenuItem visitWebsite = new MenuItem("Visit Website");
		help.setOnAction(action);
		help.getItems().add(visitWebsite);
		 
		mainMenu.getMenus().addAll(file, edit, help);
		Scene scene= new Scene(root,600,600);
		mainWindow.setScene(scene);
		mainWindow.show();
	}

	private void closeProgram(Stage mainWindow2) {
		Boolean result  = Confirmbox.display("Exit", "Are you sure?");
		if(result){
			System.out.println("Exit");
			mainWindow2.close();
		}
		
	}

	private EventHandler<ActionEvent> changeTabPlacement() {
		
	  return new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                MenuItem mItem = (MenuItem) event.getSource();
                String side = mItem.getText();
                if ("Open File".equalsIgnoreCase(side)) {
                    System.out.println("Open File");
                } else if ("Properties".equalsIgnoreCase(side)) {
                    System.out.println("Properties");
                } else if ("Exit".equalsIgnoreCase(side)) {
                	closeProgram(mainWindow);
                } else if ("help".equalsIgnoreCase(side)) {
                    System.out.println("help");
                }
            }
        };
	}

}
