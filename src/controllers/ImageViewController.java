package controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import application.Main;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

/*//////////////////////////////////////////////////////////////////////////////
 *	A method to restore images that has been encoded to Base64 and stored in a 
 *	MySQL databse as a BLOB onand visualize them in the
 *	window using a javafx.scene.image.ImageView container.
/*/////////////////////////////////////////////////////////////////////////////
public class ImageViewController implements Initializable {
	@FXML ImageView				imgCanvas;

	static private String	Number;

	/*
	 * ///////////////////////////////////////////////////////////////////////////
	 * /
	 * 
	 * @brief -> Class constructor.
	 * 
	 * @param -> number (String) : /
	 *////////////////////////////////////////////////////////////////////////////
	public ImageViewController(String number) {
		super();
		Number = number;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		viewImage();
	}

	/*
	 * ///////////////////////////////////////////////////////////////////////////
	 * /
	 * 
	 * @brief -> Tries to open a connection to the mySQL server
	 * 
	 * @param -> raw (byte[]) : the string of bytes representing the encoded
	 * image.
	 * 
	 * @param -> width (int) : size of the original image width.
	 * 
	 * @param -> height (int) : size of the original image height.
	 * 
	 * @return -> A javafx.scene.image.Image that can be used to display in an
	 * ImageView.
	 * 
	 * @throws -> IOExeption if an error occurs during reading. /
	 *////////////////////////////////////////////////////////////////////////////
	private Image blob2image(byte[] raw, int width, int height) {
		WritableImage image = new WritableImage(width, height);
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(raw);
			BufferedImage read = ImageIO.read(bis);
			image = SwingFXUtils.toFXImage(read, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	/*
	 * ///////////////////////////////////////////////////////////////////////////
	 * /
	 * 
	 * @brief -> Fetch the encode image from the database and decode it for
	 * visualization.
	 * 
	 * @param -> None.
	 * 
	 * @return -> None.
	 * 
	 * @throws -> SQLExeption if mySQL server is unreachable. /
	 *////////////////////////////////////////////////////////////////////////////
	private void viewImage() {
		byte[] decodedValue = null;

		ResultSet rs = Main.db.Tail("tbl_" + Number + "_sensorImg", 1);
		try {
			while (rs.next()) {
				String x = "";
				int size = rs.getInt("size");
				for (int i = 0; i < size; i++) {
					x += new String(rs.getBytes("chunk" + i), "UTF-8");
				}

				decodedValue = Base64.getDecoder().decode(x.getBytes("UTF-8"));
			}
		} catch (SQLException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// Display the decode image.
		imgCanvas.setImage(blob2image(decodedValue, 640, 480));
	}

}
