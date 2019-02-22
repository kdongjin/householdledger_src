package application;

import Controller.LoginController;
import Controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loder = new FXMLLoader(getClass().getResource("../View/Login.fxml"));
		Parent root = loder.load();
		LoginController loginController = loder.getController();

		loginController.LoginStage = primaryStage;
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		scene.getStylesheets().add(getClass().getResource("Login.css").toString());
		primaryStage.show();
		primaryStage.setTitle("Login");
		primaryStage.setResizable(false);
	
		
		
	
	}
}
