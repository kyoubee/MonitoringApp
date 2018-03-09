package customControls.comboBox;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.ComboBox;

public class MyComboBox extends ComboBox<String> {
    public MyComboBox() {
	super();
    }

    public void UniqueFill(ResultSet rs, String arg) {
	this.Clear();

	List<String> list_of_item = new ArrayList<String>();

	try {
	    while (rs.next()) {
		String param = rs.getString(arg);
		if (!list_of_item.contains(param)) {
		    list_of_item.add(param);
		    this.getItems().add(param);
		}
	    }
	} catch (SQLException e) {
	    System.out.println(e.getMessage());
	}
    }

    public void UniqueFill(ResultSet rs, String[] args) {
	this.Clear();

	List<String> list_of_item = new ArrayList<String>();

	try {
	    while (rs.next()) {
		String param = rs.getString(args[0]);
		if (!list_of_item.contains(param)) {
		    list_of_item.add(param);
		    this.getItems().add(param);
		}
	    }
	} catch (SQLException e) {
	    System.out.println(e.getMessage());
	}
    }

    public void Clear() {
	this.getItems().clear();
    }
}