package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*//////////////////////////////////////////////////////////////////////////////
 *	A class to facilitate the communication and data queries to a MySQL database
 *	Server.
/*//////////////////////////////////////////////////////////////////////////////
public class sqlManager {
	static String							url					= null;
	static String							user				= null;
	static String							pass				= null;
	static boolean						isConnected	= false;
	static ResultSet					rs					= null;
	static Connection					conn				= null;
	static PreparedStatement	stmt				= null;

	/*
	 * ///////////////////////////////////////////////////////////////////////////
	 * /
	 * 
	 * @brief -> Class constructor.
	 * 
	 * @param -> server (String) : the IP address to the mySQl server.
	 * 
	 * @param -> dbName (String) : the database to connect to on the mySQL server.
	 * 
	 * @param -> usr (String) : the user-name to use to connect to the server.
	 * 
	 * @param -> pswd (String) : the password associated the user-name above. /
	 *////////////////////////////////////////////////////////////////////////////
	public sqlManager(String server, String dbName, String usr, String pswd) {
		url = "";
		url += "jdbc:mysql://" + server + "/" + dbName;
		url += "?autoReconnect=true";
		// url += "&useSSL=true";
		user = usr;
		pass = pswd;

		// Test the connection to the server.
		if (Connect()) {
			System.out.println("Connection to the mySQL Server was successful");
			Disconnect();
		}
	}

	/*
	 * ///////////////////////////////////////////////////////////////////////////
	 * /
	 * 
	 * @brief -> Tries to open a connection to the mySQL server
	 * 
	 * @param -> None.
	 * 
	 * @return -> Result of the operation; True is connection is successful.
	 * 
	 * @throws -> SQLExeption if mySQL server is unreachable.
	 * 
	 * @note -> This method will maintain the connection open until it is closed
	 * by calling Disconnect() method. /
	 *////////////////////////////////////////////////////////////////////////////
	public boolean Connect() {
		// Check if connection is already opened.
		if (isConnected == false) {
			// If connection closed, load appropriate mySQL drivers and open the
			// connection to the server.
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(url, user, pass);
				// Mark the connection as open to avoid duplicate connections.
				isConnected = true;
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return isConnected;
	}

	/*
	 * ///////////////////////////////////////////////////////////////////////////
	 * /
	 * 
	 * @brief -> Close the connection to the mySQL server if it is open.
	 * 
	 * @param -> None.
	 * 
	 * @return -> Result of the operation; True is connection is successful.
	 * 
	 * @throws -> SQLExeption if mySQL server is unreachable. /
	 *////////////////////////////////////////////////////////////////////////////
	public boolean Disconnect() {
		// Check if connection has been marked as "opened".
		if (isConnected == true) {
			try {
				conn.close();
				isConnected = false;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return !isConnected;
	}

	/*
	 * ///////////////////////////////////////////////////////////////////////////
	 * /
	 * 
	 * @brief -> Retrieves information from the database.
	 * 
	 * @param -> String query : a string of mySQL commands to query the database.
	 * 
	 * @return -> A java.sql.ResultSet object that contains the data produced by
	 * the query; never null.
	 * 
	 * @throws -> SQLExeption if a database access error occurs.
	 * SQLTimeoutException if the driver has determined that the timeout value has
	 * been exceeded and has at least attempted to cancel the currently running
	 * Statement. /
	 *////////////////////////////////////////////////////////////////////////////
	public ResultSet Query(String query) {
		// Check is connection is opened, if not try to connect.
		if (Connect()) {
			try {
				// Execute a PrepareStatement and return the return ResultSet.
				stmt = conn.prepareStatement(query);
				rs = stmt.executeQuery();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		// Return results.
		return rs;
	}

	/*
	 * ///////////////////////////////////////////////////////////////////////////
	 * /
	 * 
	 * @brief -> Retrieve the 'n' last rows from a table within the database.
	 * 
	 * @param -> tblName (String) - the table from which to retrieve information.
	 * 
	 * @param -> rows (Integer) - how many rows to retrieve.
	 * 
	 * @return -> A java.sql.ResultSet object that contains the data produced by
	 * the query; never null.
	 * 
	 * @throws -> SQLExeption if a database access error occurs.
	 * SQLTimeoutException if the driver has determined that the timeout value has
	 * been exceeded and has at least attempted to cancel the currently running
	 * Statement. /
	 *////////////////////////////////////////////////////////////////////////////
	public ResultSet Tail(String tblName, Integer rows) {
		// Prepare the statement.
		String query = "SELECT * FROM " + tblName + " ORDER BY ID DESC LIMIT " + rows;
		// Check is connection is opened, if not try to connect.
		if (Connect()) {
			try {
				// Execute a PrepareStatement and return the return ResultSet.
				stmt = conn.prepareStatement(query);
				rs = stmt.executeQuery();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		// Return results to the caller.
		return rs;
	}

	/*
	 * ///////////////////////////////////////////////////////////////////////////
	 * /
	 * 
	 * @brief -> Count the number of rows present in a table.
	 * 
	 * @param -> tblName (String) - the table from which to retrieve information.
	 * 
	 * @return -> An integer representing how many rows are present in the table,
	 * returns a value of -1 in case of error.
	 * 
	 * @throws -> SQLExeption if a database access error occurs.
	 * SQLTimeoutException if the driver has determined that the timeout value has
	 * been exceeded and has at least attempted to cancel the currently running
	 * Statement. /
	 *////////////////////////////////////////////////////////////////////////////
	public Integer Length(String tblName) {
		// Initialize the result with value of -1, to notify caller in case of
		// unsuccessful completion of the method.
		int rows = -1;
		// Prepare the statement.
		String query = "SELECT COUNT(*) FROM " + tblName;
		// Check is connection is opened, if not try to connect.
		if (Connect()) {
			try {
				// Execute a PrepareStatement and return the return ResultSet.
				stmt = conn.prepareStatement(query);
				rs = stmt.executeQuery();
				// Read the ResultSet and extract the required information.
				while (rs.next()) {
					rows = rs.getInt("COUNT(*)");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		// Return results to the caller.
		return rows;
	}
}
