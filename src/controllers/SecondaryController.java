package controllers;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import application.HoverableNode;
import application.Main;
import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SecondaryController implements Initializable {
	private String													Table, Plot, Query, Number;
	private double													xMin	= 0, xMax = 0, yMin = 0, yMax = 0, xPre = 0, yPre = 0, xAvg = 0, yAvg = 0;
	private int															regressionPoints;
	private Timeline												timeline;
	private AnimationTimer									timer;
	private XYChart.Series<Double, Double>	series;

	@FXML TabPane														tabPane;
	@FXML NumberAxis												xAxis, yAxis;
	@FXML LineChart<Double, Double>					lineChart;
	@FXML RadioButton												rbtnDate, rbtnTime, rbtn1hr, rbtn2hr, rbtn6hr, rbtn12hr, rbtn24hr;
	@FXML ToggleGroup												rbgDisplayMode, rbgTimeInterval;
	@FXML DatePicker												dpStartDate, dpEndDate;
	@FXML Button														btnViewImage;

	public SecondaryController(String stream, String node) {
		ResultSet rs = Main.db.Query("SELECT * FROM tbl_master WHERE stream = '" + stream + "' AND name = '" + node + "'");
		try {
			while (rs.next()) {
				Number = rs.getString("number");
				Table = "tbl_" + Number + "_sensorVal";
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		/*
		 * regressionSlider.valueProperty().addListener(new ChangeListener<Number>()
		 * { public void changed(ObservableValue<? extends Number> ov, Number
		 * old_val, Number new_val) { regressionPoints = (int)
		 * Math.round(new_val.doubleValue()); load_chart(); } });
		 */
		initListeners();

		Plot = "wl";
		Query = null;
		regressionPoints = 12;
		series = new XYChart.Series<Double, Double>();

		xAxis.setAutoRanging(false);
		xAxis.setVisible(false);
		xAxis.setTickLabelsVisible(false);

		yAxis.setAutoRanging(false);

		lineChart.setAnimated(false);
		lineChart.setLegendVisible(false);

		tabPane.getSelectionModel().select(0);

		loadSidePanel();

		timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.setAutoReverse(true);

		timer = new AnimationTimer() {
			private long	lastUpdate	= 0;
			private long	duration		= 5_000_000_000L;

			@Override
			public void handle(long now) {
				if (now - lastUpdate >= duration) {

					load_chart();

					lastUpdate = now;
				}
			}
		};
		timeline.play();
		timer.start();
	}

	public void initListeners() {
		tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
			@Override
			public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
				switch (tabPane.getSelectionModel().getSelectedIndex()) {
					case 1 :
						Plot = "ri";
						break;
					case 2 :
						Plot = "ws";
						break;
					case 3 :
						Plot = "at";
						break;
					case 4 :
						Plot = "rh";
						break;
					case 5 :
						Plot = "wl";
						break;
					default :
						Plot = "wd";
						break;
				}
				load_chart();
			}

		});

		rbgDisplayMode.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				RadioButton chk = (RadioButton) newValue.getToggleGroup().getSelectedToggle(); // Cast
				// object
				// to
				// radio
				// button

				String rbtn = chk.getText();
				switch (rbtn) {
					case "Date Interval :" :
						regressionPoints = 0;
						dpStartDate.disableProperty().set(false);
						dpEndDate.disableProperty().set(false);

						for (Toggle t : rbgTimeInterval.getToggles()) {
							if (t instanceof RadioButton) {
								((RadioButton) t).disableProperty().set(true);
							}
						}
						break;
					case "Time Interval :" :
						Query = null;
						dpStartDate.disableProperty().set(true);
						dpEndDate.disableProperty().set(true);

						for (Toggle t : rbgTimeInterval.getToggles()) {
							if (t instanceof RadioButton) {
								((RadioButton) t).disableProperty().set(false);
							}
						}
						break;
					default :
						break;
				}

			}

		});

		rbgTimeInterval.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				RadioButton chk = (RadioButton) newValue.getToggleGroup().getSelectedToggle();

				String rbtn = chk.getText();
				switch (rbtn) {
					case "2 hours" :
						regressionPoints = 24;
						break;
					case "6 hours" :
						regressionPoints = 72;
						break;
					case "12 hours" :
						regressionPoints = 144;
						break;
					case "24 hours" :
						regressionPoints = 288;
						break;
					default :
						regressionPoints = 12;
						break;
				}

				load_chart();
			}

		});

		btnViewImage.setOnAction(event -> {
			showImageViewScreen();
		});

		dpEndDate.setOnAction(event -> {
			LocalDate startdate = dpStartDate.getValue();
			LocalDate enddate = dpEndDate.getValue();

			Query = "SELECT * FROM " + Table + " WHERE time >= '" + startdate + "' AND time <= '" + enddate.plusDays(1) + "'";
			load_chart();
		});
	}

	public void load_chart() {
		if ((regressionPoints == 0) && (Query != null)) {
			load_chart(Query);
		} else {
			load_chart(regressionPoints);
		}
	}

	public void load_chart(int numberofpoints) {
		lineChart.getData().clear();

		XYChart.Series<Double, Double> series = plotCoordinates("time", Plot, numberofpoints);

		xAxis.setLowerBound(xMin);
		xAxis.setUpperBound(xMax + xAvg);
		xAxis.setTickUnit(((xMax - xMin) / 10));

		if (yMin == yMax) {
			if ((yMax - yMin) > 0)
				yMax = yMax / 2;
			if (yMax == 0)
				yAvg = 1;
		}
		if (Math.abs(yMax - yMin) < 9e-1)
			yAvg = 1;
		if (yAvg == 0)
			yAvg = 1;

		yAxis.setLowerBound(yMin - yAvg);
		yAxis.setUpperBound(yMax + yAvg);
		yAxis.setTickUnit(((yMax - yMin) / 10) + (3 * yAvg));

		lineChart.getData().add(series);
	}

	public void load_chart(String query) {
		lineChart.getData().clear();

		series = plotCoordinates("time", Plot, query);

		xAxis.setLowerBound(xMin);
		xAxis.setUpperBound(xMax + xAvg);
		xAxis.setTickUnit(((xMax - xMin) / 10));

		if (yMin == yMax) {
			if ((yMax - yMin) > 0)
				yMax = yMax / 2;
			if (yMax == 0)
				yAvg = 1;
		}
		if (Math.abs(yMax - yMin) < 9e-1)
			yAvg = 1;
		if (yAvg == 0)
			yAvg = 1;

		yAxis.setLowerBound(yMin - yAvg);
		yAxis.setUpperBound(yMax + yAvg);
		yAxis.setTickUnit(((yMax - yMin) / 10) + (3 * yAvg));

		lineChart.getData().add(series);
	}

	public XYChart.Series<Double, Double> plotCoordinates(String xLabel, String yLabel, Integer count) {
		resetBounds();

		series.getData().clear();

		if (Main.db.Connect()) {
			ResultSet rs = Main.db.Tail(Table, count);

			int i = 0;
			try {
				resetBounds();
				while (rs.next()) {
					Date date = toDate(rs.getString(xLabel));

					Double x = date2julian(date);
					Double y = rs.getDouble(yLabel);

					calcBounds(x, y, i);

					XYChart.Data<Double, Double> data = new XYChart.Data<Double, Double>(x, y);

					data.setNode(new HoverableNode(String.format("%3.3g%n", y) + "\n" + date, 8));
					series.getData().add(data);
					i++;
				}

			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}

		}
		return series;
	}

	public XYChart.Series<Double, Double> plotCoordinates(String xLabel, String yLabel, String query) {
		resetBounds();

		series.getData().clear();

		if (Main.db.Connect()) {
			ResultSet rs = Main.db.Query(query);

			int i = 0;
			try {
				resetBounds();
				while (rs.next()) {
					Date date = toDate(rs.getString(xLabel));

					Double x = date2julian(date);
					Double y = rs.getDouble(yLabel);

					calcBounds(x, y, i);

					XYChart.Data<Double, Double> data = new XYChart.Data<Double, Double>(x, y);

					data.setNode(new HoverableNode(String.format("%3.3g%n", y) + "\n" + date, 8));
					series.getData().add(data);
					i++;
				}

			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}

		}
		return series;
	}

	private void resetBounds() {
		xMin = 0;
		yMin = 0;
		xMax = 0;
		yMax = 0;
		xPre = 0;
		yPre = 0;
		xAvg = 0;
		yAvg = 0;
	}

	private void calcBounds(Double x, Double y, int i) {
		if (i == 0) {
			xMin = x;
			yMin = y;
			xPre = x;
			yPre = y;
		}

		if (x < xMin)
			xMin = x;
		if (x > xMax)
			xMax = x;
		if (y < yMin)
			yMin = y;
		if (y > yMax)
			yMax = y;

		xAvg = (xAvg + Math.abs(x - xPre)) / 2;
		yAvg = (yAvg + Math.abs(y - yPre)) / 2;

		xPre = x;
		yPre = y;
	}

	private Date toDate(String stringDate) {
		java.util.Date date = null;

		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
			date = formatter.parse(stringDate);
		} catch (ParseException e1) {
			/*
			 * try { SimpleDateFormat formatter = new
			 * SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); date =
			 * formatter.parse(stringDate); } catch (ParseException e2) {
			 * e2.printStackTrace(); }
			 */
		}

		return date;
	}

	private Double date2julian(Date date_) {

		Calendar date = Calendar.getInstance();
		date.setTime(date_);

		int year = date.get(Calendar.YEAR);
		int month = date.get(Calendar.MONTH) + 1;
		int day = date.get(Calendar.DAY_OF_MONTH);
		int hour = date.get(Calendar.HOUR_OF_DAY);
		int minute = date.get(Calendar.MINUTE);
		int second = date.get(Calendar.SECOND);

		double extra = (100.0 * year) + month - 190002.5;
		double JD = (367.0 * year) - Math.floor(7.0 * (year + Math.floor((month + 9.0) / 12.0)) / 4.0) + Math.floor((275.0 * month) / 9.0) + day + ((hour + ((minute + (second / 60.0)) / 60.0)) / 24.0) + 1721013.5
				- ((0.5 * extra) / Math.abs(extra)) + 0.5;
		return JD;
	}

	private void loadSidePanel() {
		// regressionPoints = Main.db.Length(Table);

		/*
		 * regressionSlider.setMin(0);
		 * regressionSlider.setMax((int)(regressionPoints));
		 * regressionSlider.setValue((int)(regressionPoints * 0.2));
		 * //regressionSlider.setShowTickLabels(true);
		 * regressionSlider.setMajorTickUnit(Math.max((int)(regressionPoints / 5),
		 * 1)); regressionSlider.setBlockIncrement(1);
		 */

	}

	private void showImageViewScreen() {
		try {
			AnchorPane rootPane = null;
			ImageViewController controller = new ImageViewController(Number);

			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/screens/imageView.fxml"));

			fxmlLoader.setRoot(rootPane);
			fxmlLoader.setController(controller);
			Parent imageViewRoot = (Parent) fxmlLoader.load();

			Scene imageViewScene = new Scene(imageViewRoot, 600, 400);
			Stage imageViewStage = new Stage();

			imageViewStage.setTitle("Realtime Status at location : ");
			imageViewStage.setScene(imageViewScene);
			imageViewStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
