package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.util.Duration;

/*//////////////////////////////////////////////////////////////////////////
 * @brief -> This controller displays a splash screen for 5 seconds, while
 					   the application loads all components in Main.init(), then opens 
 					   the primary screen. 
/*//////////////////////////////////////////////////////////////////////////
public class SplashController implements Initializable {
	@FXML Button btnClose;
	
	public SplashController() {
		super();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		PauseTransition delay = new PauseTransition(Duration.seconds(15));
		delay.setOnFinished(e -> {
				if (Main.db.Connect()) {
					closeStage();
					showPrimary();
				} else {
	        Alert alert = new Alert(Alert.AlertType.INFORMATION);
	        alert.setTitle("Error 404");
	        alert.setHeaderText(null);
	        alert.setContentText("Could not connect to the remote server.\nPlease check your internet connection or retry later.");
	        alert.setOnHidden(evt -> Platform.exit());
	        alert.show();
				}
			});
		delay.play();
	}
	
	private void closeStage() {
		// Determine which screen is currently active and closes it.
		Stage me = (Stage) btnClose.getScene().getWindow();
		me.close();
	}
	
	public void showPrimary() {
		// Data validation and verification.
		if (true) {//(validate() && authenticate()) {
			try {
				// Initialize the controller to manage the main screen of the
				// application.
				PrimaryController controller = new PrimaryController();

				// Load and display the main screen.
				FXMLLoader fxmlLoader;
				fxmlLoader = new FXMLLoader(getClass().getResource("/screens/Primary.fxml"));
				fxmlLoader.setController(controller);
				Parent primaryRoot = (Parent) fxmlLoader.load();
				Scene primaryScene = new Scene(primaryRoot);
				// Creates a top-level container (window) onto which the other
				// components
				// will be placed on.
				Stage primaryStage = new Stage();
				primaryStage.setTitle("Realtime Urban Drainage Monitoring System");
				primaryStage.setScene(primaryScene);
				primaryStage.setMaximized(true);
				primaryStage.setResizable(true);
				primaryStage.show();

				// Close the login screen.
				closeStage();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
