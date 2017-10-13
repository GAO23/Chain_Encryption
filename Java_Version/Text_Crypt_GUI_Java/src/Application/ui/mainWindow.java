package Application.ui;
import javafx.stage.Stage;
import Application.logic.model;
import javafx.event.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
public class mainWindow {

	private model model;
	private Stage stage;
	private Parent root;
	
	public mainWindow(Stage stage) throws Exception{
		/* This is how you do it without forms
		root = new StackPane();
		Button bt = new Button("Browse");
		// bt.setOnAction(e -> doSomething()); also works 
		bt.setOnAction(new browseHandler()); 
		root.getChildren().add(bt);
		this.stage.setScene(scene);
		*/
		model model = new model(this);
		root = FXMLLoader.load(getClass().getResource("Forms/main_screen.fxml"));
		Scene scene = new Scene(root, 500, 500);
		this.stage = stage;
		this.stage.setScene(scene);
		this.stage.show();
	}
	
	
	private class browseHandler implements EventHandler<ActionEvent> {
	    
		@Override
	     public void handle(ActionEvent event) {
	         System.exit(0);
	     }
	}
		
	
	
}