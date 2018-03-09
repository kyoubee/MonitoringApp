package application;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/*//////////////////////////////////////////////////////////////////////////////
 *	This class extends the data points added onto a javafx.scene.chart.LineChart
 *	to implement a MouseEvent to display a description associated with each data
 *	point whenever the cursor is hovered over the point.
/*/////////////////////////////////////////////////////////////////////////////
public class HoverableNode extends StackPane {
	/*////////////////////////////////////////////////////////////////////////////
	@brief  ->  Class constructor. 
	@param	->  Data  (String) : the description to be associated with the data
															 point on the graph; e.g. the value of f(x) at x.
	@param	->  Size (Integer) : the radius of the node to be added on the graph.
	/*////////////////////////////////////////////////////////////////////////////
  public HoverableNode(String Data, Integer Size) {
  	// Creates a label to hold the description.
		final Label LABEL = createDataThresholdLabel(Data);

		setPrefSize(Size, Size);
		setAlignment(Pos.CENTER);
		
		// Add a label onto the LineChart and display the description when the node
		// get focus from the cursor.
		setOnMouseEntered(new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent mouseEvent) {
				getChildren().setAll(LABEL);
				// Hide the mouse cursor to avoid obstructing the label.
				setCursor(Cursor.NONE);
		
				toFront();
	    }
		});
	
		// Remove the label from the Chart as soon as the cursor is displaced.
		setOnMouseExited(new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent mouseEvent) {
				getChildren().clear();
				// Re-enables the mouse cursor.
				setCursor(Cursor.CROSSHAIR);
	    }
		});
  }

	/*////////////////////////////////////////////////////////////////////////////
	@brief  ->  Method to generate a customized label. 
	@param	->  Data  (String) : the description to be associated with the data
															 point on the graph; e.g. the value of f(x) at x.
	@return	->	A javafx.scene.control.Label with the description of this specific
	 						node.
	/*////////////////////////////////////////////////////////////////////////////
	private Label createDataThresholdLabel(String data) {
		final Label LABEL = new Label(data);
		
		LABEL.getStyleClass().addAll("default-color0", "chart-line-symbol", "chart-series-line");
		LABEL.setStyle("-fx-font-size: 10;");
		LABEL.setTextFill(Color.RED);
		LABEL.setMinSize(60, Label.USE_PREF_SIZE);
		LABEL.setWrapText(true);

		return LABEL;
  }
}