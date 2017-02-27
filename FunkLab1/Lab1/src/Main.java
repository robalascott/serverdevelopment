import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Main extends Application{

	Button testBtn,test2Btn,test3Btn;
	Canvas canvas;

	
	public static void main(String[] args) {
		// You can pass windows size so on.
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception{
		//Main code area
		primaryStage.setTitle("Drawz");
		
		primaryStage.setOnCloseRequest(e -> 
		{e.consume();
		closeProgram(primaryStage);});
		
		//Button just like android
		testBtn = new Button();
		testBtn.setText("Click bait");
		//Place to put a class to handle methods 
		testBtn.setOnAction(e-> AlertBox.display("Gotcha", "B**ch"));
		
		
		test2Btn = new Button();
		test2Btn.setText("Click bait 2");
		//Place to put a class to handle methods 
		test2Btn.setOnAction(e-> {
			boolean result = Confirmbox.display("Gotcha", "B**ch");
			System.out.println(result);
			});
		
		test3Btn = new Button();
		test3Btn.setText("Clear");
		//Place to put a class to handle methods 
		test3Btn.setOnAction(e-> reset(canvas,Color.BLUE));
		
		//Create sence areas using simple layout
		VBox layout = new VBox(10);
	
		canvas = new Canvas(300, 250);
		final GraphicsContext gc = canvas.getGraphicsContext2D();
		 canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,e-> {
			               //gc.clearRect(e.getX() - 2, e.getY() - 2, 5, 5);
			               gc.fillRect(e.getX() - 2, e.getY() - 2, 7, 9);
			           }
			       );
		   canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, 
			        new EventHandler<MouseEvent>() {
			            @Override
			            public void handle(MouseEvent t) {            
			                if (t.getClickCount() >1) {
			                    reset(canvas, Color.BLUE);
			                }  
			            }
			        });
		
		
	    drawShapes(gc);
		layout.getChildren().addAll(testBtn,test3Btn,test2Btn,canvas);
		Scene scene = new Scene(layout,600,600);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		
	}

	private void closeProgram(Stage primaryStage) {
		Boolean result  = Confirmbox.display("Exit", "Are you sure?");
		if(result){
			System.out.println("Exit");
			primaryStage.close();
		}
		
	}

	 private void drawShapes(GraphicsContext gc) {
		  gc.setFill(Color.GREEN);
	        gc.setStroke(Color.BLUE);
	        gc.setLineWidth(5);
	        gc.strokeLine(40, 10, 10, 40);
	        gc.fillOval(10, 60, 30, 30);
	 }

	 private void moveCanvas(Canvas canvas, int x, int y) {
		    canvas.setTranslateX(x);
		    canvas.setTranslateY(y);
		}
	 //Reset The Image 
	 private void reset(Canvas canvas, Color color) {
		    GraphicsContext gc = canvas.getGraphicsContext2D();
		    gc.setFill(color);
		    gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		}
}
