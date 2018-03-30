package application;

import java.lang.ref.WeakReference;

import controllers.SplashController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/*//////////////////////////////////////////////////////////////////////////////
 *	Top Level Class 
/*//////////////////////////////////////////////////////////////////////////////
public class Main extends Application {
	static public sqlManager db;
	
	@Override
	public void init() {
		// Custom mySQL database manager with in-line helper functions.
		String Server = "uom-cseresearchlab.dyndns.org";
		String DatabaseName = "wsnDatabase";
		String Username = "wsnAdmin";
		String Password = "nimdansw";
		db = new sqlManager(Server, DatabaseName, Username, Password);
	}
	
  /*//////////////////////////////////////////////////////////////////////////
	 * @brief  -> The main entry point for all JavaFX applications. The start
	 *            method is called after the init() method has returned, and
	 *            after the system is ready for the application to begin running.
   * @param  -> None.
   * @throws -> IOExeption if FXML file cannot be found.
   * @note   -> This method is called on the JavaFX Application Thread.
	/*///////////////////////////////////////////////////////////////////////////
	@Override
	public void start(Stage splashStage) {
		setUserAgentStylesheet(STYLESHEET_CASPIAN);
		
		try {
			// Create controller to handle the JavaFX objects and methods required.
			SplashController controller = new SplashController();

			// Fetch details about the required JavaFX components to be placed in
			// the window and creates a layout to be applied onto the stage.
			// Layout has been created and customized using JavaFX SceneBuilder.
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/screens/Splash.fxml"));
			fxmlLoader.setController(controller);
			Parent splashRoot = (Parent) fxmlLoader.load();
			splashRoot.setStyle("-fx-background-color: transparent;");
			Scene loginScene = new Scene(splashRoot, Color.TRANSPARENT);

			// Customize the stage and display the window.
			splashStage.initStyle(StageStyle.TRANSPARENT);
			splashStage.setTitle("Urban Drainage Monitoring System");
			splashStage.setScene(loginScene);
			splashStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
  /*/////////////////////////////////////////////////////////////////////////// 
   * @brief  -> Entry point for JAVA applications.
	 * @param  -> None.
	 * @throws -> SQLExeption if cannot connect to mySQL server.
	 * @note   -> Initialization of all variables/objects must be carried out
	 *            before the launch() method.
	/*///////////////////////////////////////////////////////////////////////////
	public static void main(String[] args) {
		// Start the JavaFX application.
		launch(args);
	}

	/*///////////////////////////////////////////////////////////////////////////
	 * @brief -> Method guarantees that garbage collection is completed to free
	 * resources.
	 * 
	 * @see -> <code>{@link System.gc()}</code>
	/*///////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("rawtypes")
	public static void gc() {
		WeakReference ref = new WeakReference<Object>(new Object());
		while (ref.get() != null) {
			System.gc();
		}
	}
}
