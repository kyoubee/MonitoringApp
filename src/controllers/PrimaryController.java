package controllers;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.InfoWindow;
import com.lynden.gmapsfx.javascript.object.InfoWindowOptions;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import com.lynden.gmapsfx.shapes.Circle;
import com.lynden.gmapsfx.shapes.CircleOptions;

import application.Main;
import application.waterStreams;
import customControls.comboBox.MyComboBox;
import customControls.listView.MyListView;
import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

@SuppressWarnings("unused")
public class PrimaryController implements Initializable {
	private class googleMapController {
		GoogleMap		map;
		LatLong[]		coords;
		InfoWindow	WindowNode;
		MapOptions	mapOptions;

		public googleMapController() {
			super();
		}

		private class myMarker extends Marker {
			public myMarker(MarkerOptions markerOptions, int index, String Node, String Stream, Double Lon, Double Lat) {
				super(markerOptions);

				map.addUIEventHandler(this, UIEventType.click, (JSObject obj) -> {
					if (WindowNode != null) {
						WindowNode.close();
					}

					InfoWindowOptions infoOptions = new InfoWindowOptions();
					infoOptions.content("<p>" + Node + "</p>");
					infoOptions.position(coords[index]);

					WindowNode = new InfoWindow(infoOptions);
					WindowNode.open(map, this);

					markerClicked = true;

					load_sidePanel(Stream, Node);
					showSecondary(Stream, Node);
				});
			}
		}

		private void configureMap() {
			mapOptions = new MapOptions();
			mapOptions.center(new LatLong(-20.1653, 57.4964));
			mapOptions.mapType(MapTypeIdEnum.ROADMAP);
			mapOptions.overviewMapControl(false);
			mapOptions.panControl(false);
			mapOptions.rotateControl(false);
			mapOptions.scaleControl(true);
			mapOptions.streetViewControl(false);
			mapOptions.zoomControl(false);
			mapOptions.zoom(16);

			map = googleMapView.createMap(mapOptions);

			loadMarkers();
		}

		private void loadMarkers() {
			Integer rows = Main.db.Length("tbl_master");
			ResultSet rs = Main.db.Query("SELECT * from tbl_master");

			addMarkers(rows, rs);
		}

		private void addMarkers(int rows, ResultSet rs) {
			try {
				coords = new LatLong[rows];

				int i = 0;
				while (rs.next()) {
					String name = rs.getString("name");
					String stream = rs.getString("stream");
					Double lon = rs.getDouble("lon");
					Double lat = rs.getDouble("lat");
					Double credit = Double.parseDouble(rs.getString("Balance"));

					coords[i] = new LatLong(lat, lon);

					MarkerOptions markerOptions = new MarkerOptions();
					markerOptions.position(coords[i]);
					markerOptions.visible(true);
					markerOptions.title(name);

					Marker marker = new myMarker(markerOptions, i, name, stream, lon, lat);
					map.addMarker(marker);

					if (credit < creditThreshold) {
						addHighlights(lat, lon, "BLUE", 60);
					}
					i++;
				}

			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}

		private void centerMap(double latitude, double longitude) {
			LatLong centerCoords = new LatLong(latitude, longitude);

			mapOptions.center(centerCoords);
			mapOptions.zoom(17);
			map = googleMapView.createMap(mapOptions);

			loadMarkers();
		}

		private void addHighlights(double latitude, double longitude, String color, int radius) {
			LatLong centerCoords = new LatLong(latitude, longitude);

			CircleOptions circleOpts = new CircleOptions();
			circleOpts.center(centerCoords);
			circleOpts.fillColor(color);
			circleOpts.radius(radius);
			circleOpts.visible(true);
			circleOpts.draggable(false);

			Circle circularShape = new Circle(circleOpts);
			map.addMapShape(circularShape);
		}
	}

	private class alertWarnings {
		public alertWarnings() {
			super();
		}

		public void showAlerts() {

		}

		private LatLong coordMidpoint(LatLong A, LatLong B) {
			double deltaLon = Math.toRadians(B.getLongitude() - A.getLongitude());

			double latA = Math.toRadians(A.getLatitude());
			double lonA = Math.toRadians(A.getLongitude());

			double Bx = Math.cos(Math.toRadians(B.getLatitude())) * Math.cos(deltaLon);
			double By = Math.cos(Math.toRadians(B.getLatitude())) * Math.sin(deltaLon);
			double latC = Math.atan2(Math.sin(latA) + Math.sin(Math.toRadians(B.getLatitude())), Math.sqrt((Math.cos(latA) + Bx) * (Math.cos(latA) + Bx) + By * By));
			double lonC = lonA + Math.atan2(By, Math.cos(latA) + Bx);

			return new LatLong(Math.toDegrees(latC), Math.toDegrees(lonC));
		}
	}

	private int										creditThreshold	= 5;
	private boolean								markerClicked		= false;
	private googleMapController		mapController;
	private alertWarnings					warnings				= new alertWarnings();
	private waterStreams					streams;
	private Timeline							timeline;
	private AnimationTimer				timer;

	@FXML protected GoogleMapView	googleMapView;
	@FXML protected MyComboBox		cmbStream;
	@FXML MyComboBox							cmbNode;
	@FXML MyListView							lstDetailsNames;
	@FXML MyListView							lstDetailsValues;
	@FXML MenuItem								mniClose;
	@FXML ToggleButton						tglButton;

	public PrimaryController() {
		super();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.setAutoReverse(true);

		mapController = new googleMapController();

		googleMapView.addMapInializedListener(() -> mapController.configureMap());
		streams = new waterStreams();
		load_cmbStream();

		timer = new AnimationTimer() {
			private long	lastUpdate	= 0;
			private long	duration		= 5_000_000_000L;

			@Override
			public void handle(long now) {
				if (now - lastUpdate >= duration) {

					lastUpdate = now;
				}
			}
		};
		timeline.play();
		timer.start();
	}

	public void load_cmbStream() {
		String query = "SELECT Stream from tbl_master";
		ResultSet rs = Main.db.Query(query);

		markerClicked = false;

		cmbStream.UniqueFill(rs, "stream");
		cmbNode.Clear();
		lstDetailsNames.Clear();
		lstDetailsValues.Clear();
	}

	public void load_cmbNode() {
		String stream = cmbStream.getSelectionModel().getSelectedItem().toString();
		String query = "SELECT Name from tbl_master WHERE stream = '" + stream + "'";
		ResultSet rs = Main.db.Query(query);

		cmbNode.UniqueFill(rs, "name");
		markerClicked = false;
	}

	public void load_lstDetails() {
		String node = cmbNode.getSelectionModel().getSelectedItem();
		String stream = cmbStream.getSelectionModel().getSelectedItem();
		String number = null;
		Double lon = 0.0, lat = 0.0;

		if ((node != null) && (stream != null)) {
			try {
				lstDetailsNames.Clear();
				lstDetailsValues.Clear();

				String query1 = "SELECT alt, lon, lat, number, balance FROM tbl_master WHERE stream = '" + stream + "' AND name = '" + node + "'";
				String[] args1 = {"alt", "lon", "lat", "number", "balance"};
				String[] prfx1 = {"Altitude", "Longitude", "Latitude", "Number", "Balance"};
				ResultSet rs1 = Main.db.Query(query1);
				lstDetailsNames.UniqueFill(prfx1);
				lstDetailsValues.UniqueFill(rs1, args1);

				rs1.beforeFirst();;
				while (rs1.next()) {
					number = rs1.getString("number");
					lon = rs1.getDouble("lon");
					lat = rs1.getDouble("lat");
				}

				String query2 = "SELECT * FROM tbl_" + number + "_sensorVal ORDER BY ID DESC LIMIT 1";
				String[] args2 = {"ri", "wl", "ws", "wd", "rh", "at", "bp"};
				String[] prfx2 = {"Rain Intensity", "Water Level", "Wind Speed", "Wind Direction", "Relative Humidity", "Ambient Temperature", "Barometric Pressure"};
				ResultSet rs2 = Main.db.Query(query2);
				lstDetailsNames.UniqueFill(prfx2);
				lstDetailsValues.UniqueFill(rs2, args2);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (markerClicked != true) {
				mapController.centerMap(lat, lon);
				// mapController.addHighlights(lat, lon);
			}
		}
	}

	public void load_sidePanel(String stream, String node) {
		if ((stream != null) && (node != null)) {
			lstDetailsNames.Clear();
			lstDetailsValues.Clear();
			cmbStream.setValue(stream);
			cmbNode.setValue(node);
		} else {

		}
	}

	public void showSecondary(String Stream, String Node) {
		try {
			AnchorPane rootPane = null;
			SecondaryController controller = new SecondaryController(Stream, Node);

			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/screens/Secondary.fxml"));

			fxmlLoader.setRoot(rootPane);
			fxmlLoader.setController(controller);
			Parent secondaryRoot = (Parent) fxmlLoader.load();

			Scene secondaryScene = new Scene(secondaryRoot, 1000, 562);
			Stage secondaryStage = new Stage();

			secondaryStage.setTitle("Realtime Status at location : " + Stream + ", " + Node);
			secondaryStage.setScene(secondaryScene);
			secondaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void check_nodeCredit() {

	}

	public void closeStage() {
		Stage me = (Stage) tglButton.getScene().getWindow();
		me.close();
	}
}