package application;

import java.sql.ResultSet;
import java.sql.SQLException;

/*//////////////////////////////////////////////////////////////////////////////
 *	A class to implement a method to group all the sensor-node locations present
 *	in the database into their respective water-streams (drains, rivers, etc...) 
 *	and order the sensor locations from upstream to downstream based on their
 *	altitude 
/*/////////////////////////////////////////////////////////////////////////////
@SuppressWarnings("unused")
public class waterStreams {
    private class stream {
	private class node {
	    private String name;
	    private Integer rank;
	    private Double altitude;
	    private Double longitude;
	    private Double latitude;

	    public node() {
		super();
	    }

	    public void fill(String nodeName, double nodeAlt, double nodeLon, double nodeLat) {
		name = nodeName;
		altitude = nodeAlt;
		longitude = nodeLon;
		latitude = nodeLat;
	    }

	    public String getName() {
		return this.name;
	    }

	    public Double getAltitude() {
		return this.altitude;
	    }

	    public Double getLongitude() {
		return this.longitude;
	    }

	    public Double getLatitude() {
		return this.latitude;
	    }
	}

	public String name;
	public node[] nodes;

	public stream() {
	    super();
	}

	public void fill(String streamName, int numberNodes) {
	    name = streamName;
	    nodes = new node[numberNodes];

	    addNodes();
	}

	private void addNodes() {
	    int i = 0;
	    String query = "SELECT * FROM tbl_master WHERE stream = '" + this.name + "' ORDER BY alt";

	    ResultSet rs = Main.db.Query(query);
	    try {
		while (rs.next()) {
		    nodes[i] = new node();

		    String nodeName = rs.getString("name");
		    Double nodeAlt = rs.getDouble("alt");
		    Double nodeLon = rs.getDouble("lon");
		    Double nodeLat = rs.getDouble("lat");

		    nodes[i].fill(nodeName, nodeAlt, nodeLon, nodeLat);
		    i++;
		}
	    } catch (SQLException e) {
		System.out.println(e.getMessage());
	    }
	}
    }

    static public stream[] streams;
    private String baseQuery, overQuery;

    public waterStreams() {
	super();

	baseQuery = "SELECT stream, count(*) FROM tbl_master GROUP BY stream";
	overQuery = "SELECT COUNT(*) FROM (" + baseQuery + ") AS T";

	ResultSet rs = Main.db.Query(overQuery);
	try {
	    while (rs.next()) {
		streams = new stream[rs.getInt("count(*)")];
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	addStreams();
    }

    private void addStreams() {
	int i = 0;
	ResultSet rs = Main.db.Query(baseQuery);
	try {
	    while (rs.next()) {
		streams[i] = new stream();

		String streamName = rs.getString("Stream");
		Integer numberNodes = rs.getInt("COUNT(*)");

		streams[i].fill(streamName, numberNodes);
		i++;
	    }
	} catch (SQLException e) {
	    System.out.println(e.getMessage());
	}
    }
}
