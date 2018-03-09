package controllers;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import application.Main;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/*//////////////////////////////////////////////////////////////////////////
@brief	This controller initializes the graphical interface and manages all
	        the users that are allowed to use the application. 
/*//////////////////////////////////////////////////////////////////////////
public class LoginController implements Initializable {
	@FXML Button				btnLogin;
	@FXML TextField			txtUser;
	@FXML PasswordField	txtPassword;
	@FXML Label					lblMessage;
	@FXML MenuItem			mniAddUser;

	public LoginController() {
		super();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		lblMessage.setText("Please Enter Your Credentials.");

		// Places the cursor in the "Username" input field by default.
		txtUser.requestFocus();

		// Set a keyboard listener to enables the use of TAB key to
		// move the cursor/focus to the next input field.
		txtUser.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().equals(KeyCode.TAB))
					txtPassword.requestFocus();
			}
		});

		// Listens for keyboard input when the "Password" field has focus. If
		// ENTER key is pressed, initiate the process to show next screen.
		txtPassword.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().equals(KeyCode.ENTER))
					showPrimary();
			}
		});

		btnLogin.setOnAction((event) -> {
			showPrimary();
		});
	}

	public void closeStage() {
		// Close the connection to the database.
		Main.db.Disconnect();

		// Determine which screen is currently active and closes it.
		Stage me = (Stage) btnLogin.getScene().getWindow();
		me.close();
	}

	public void showPrimary() {
		// Data validation and verification.
		if (validate() && authenticate()) {
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

	private boolean validate() {
		boolean result = false;
		// Username and Password cannot empty.
		if (!txtUser.getText().isEmpty()) {
			if (!txtPassword.getText().isEmpty()) {
				result = true;
			} else {
				lblMessage.setText("Please Enter Your Password.");
			}
		} else {
			lblMessage.setText("Please Enter Your Username.");
		}
		return result;
	}

	private boolean authenticate() {
		boolean result = false;

		try {
			// Get the password for the user from the database if it exists.
			String validAns = getPassword(txtUser.getText());
			if (validAns != "INVALID USERNAME") {

				// Encode the entered password using SHA256 to compare with
				// the stored password.
				MessageDigest md;
				md = MessageDigest.getInstance("SHA-256");
				md.update(txtPassword.getText().toString().getBytes());
				byte byteData[] = md.digest();
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < byteData.length; i++) {
					sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
				}

				// Verify if the password entered is valid.
				if (sb.toString().equals(validAns)) {
					result = true;
				} else {
					lblMessage.setText("Credentials Invalid. Please Try Again.");
				}
			} else {
				lblMessage.setText("Invalid username");
			}
		} catch (NoSuchAlgorithmException e) {
		}

		return result;
	}

	private String getPassword(String username) {
		String password = null;
		try {
			// Query the database and fetch the password for the entered
			// username.
			ResultSet rs = Main.db.Query("SELECT *  FROM tbl_user WHERE user = '" + username + "'");
			rs.next();
			password = rs.getString("password");
		} catch (SQLException e) {
			// Username entered could not be found in the database.
			password = "INVALID USERNAME";
		}
		return password;
	}
}
