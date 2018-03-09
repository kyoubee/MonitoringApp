package application;

import java.lang.ref.WeakReference;

import controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/*//////////////////////////////////////////////////////////////////////////////
 *	Top Level Class 
/*//////////////////////////////////////////////////////////////////////////////
public class Main extends Application {
	static public sqlManager db;

	/*
	 * ///////////////////////////////////////////////////////////////////////////
	 * /
	 * 
	 * @brief -> The main entry point for all JavaFX applications. The start
	 * method is called after the init method has returned, and after the system
	 * is ready for the application to begin running.
	 * 
	 * @param -> None.
	 * 
	 * @throws -> IOExeption if FXML file cannot be found.
	 * 
	 * @note -> This method is called on the JavaFX Application Thread. /
	 *////////////////////////////////////////////////////////////////////////////
	@Override
	public void start(Stage loginStage) {
		try {
			// Create controller to handle the JavaFX objects and methods required.
			LoginController controller = new LoginController();

			// Fetch details about the required JavaFX components to be placed in
			// the window and creates a layout to be applied onto the stage.
			// Layout has been created and customized using JavaFX SceneBuilder.
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/screens/Login.fxml"));
			fxmlLoader.setController(controller);
			Parent loginRoot = (Parent) fxmlLoader.load();
			Scene loginScene = new Scene(loginRoot);

			// Customize the stage and display the window.
			loginStage.setTitle("Urban Drainage Monitoring System");
			loginStage.setScene(loginScene);
			loginStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * ///////////////////////////////////////////////////////////////////////////
	 * /
	 * 
	 * @brief -> Entry point for JAVA applications.
	 * 
	 * @param -> None.
	 * 
	 * @note -> Initialization of all variables/objects must be carried out before
	 * the launch() method.
	 * 
	 * @throws -> SQLExeption if cannot connect to mySQL server. /
	 *////////////////////////////////////////////////////////////////////////////
	public static void main(String[] args) {
		// Custom mySQL database manager with in-line helper functions.
		String Server = "localhost";
		String DatabaseName = "wsnDatabase";
		String Username = "user";
		String Password = "password";
		db = new sqlManager(Server, DatabaseName, Username, Password);

		// Start the JavaFX application.
		launch(args);
	}

	/*
	 * ///////////////////////////////////////////////////////////////////////////
	 * /
	 * 
	 * @brief -> Method guarantees that garbage collection is completed to free
	 * resources.
	 * 
	 * @see -> <code>{@link System.gc()}</code> /
	 *////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("rawtypes")
	public static void gc() {
		WeakReference ref = new WeakReference<Object>(new Object());
		while (ref.get() != null) {
			System.gc();
		}
	}
}
