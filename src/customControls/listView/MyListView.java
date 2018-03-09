package customControls.listView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.ListView;

public class MyListView extends ListView<String> {
	public MyListView() {
		super();
	}

	public void UniqueFill(ResultSet rs, String[] args) {
		List<String> list_of_item = new ArrayList<String>();

		try {
			while (rs.next()) {
				for (int i = 0; i < args.length; i++) {
					String param = rs.getString(args[i]);
					if (!(list_of_item.contains(param))) {
						list_of_item.add(param);
						this.getItems().add(param);
					}
				}
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
		}
	}

	public void UniqueFill(ResultSet rs, String[] args, String[] prefixes) {
		List<String> list_of_item = new ArrayList<String>();

		try {
			while (rs.next()) {
				for (int i = 0; i < args.length; i++) {
					String param = rs.getString(args[i]);
					String string = prefixes[i] + "\t:\t" + param;

					if (!(list_of_item.contains(param))) {
						list_of_item.add(param);
						this.getItems().add(string);
					}
				}
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
		}
	}

	public void UniqueFill(String[] args) {
		List<String> list_of_item = new ArrayList<String>();

		for (int i = 0; i < args.length; i++) {
			String param = args[i];

			if (!(list_of_item.contains(param))) {
				list_of_item.add(param);
				this.getItems().add(param);
			}
		}
	}

	public void Clear() {
		this.getItems().clear();
	}

}