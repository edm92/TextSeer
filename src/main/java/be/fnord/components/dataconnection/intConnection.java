package be.fnord.components.dataconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

public interface intConnection {
	Connection connect = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;
	Statement statement = null;

	/**
	 * Close the current connection
	 *
	 * @return
	 */
	public boolean closeConnection();

	/**
	 * Created a prepared statement. Should be executed using
	 * executeQuery.execute *
	 *
	 * @param query
	 * @return
	 */
	public PreparedStatement createPreparedStatement(String query);

	/**
	 * Execute an SQL query
	 *
	 * @param query
	 * @return
	 */
	public ResultSet executeQuery(String query);

	/**
	 * Return results from query
	 *
	 * @param results
	 * @return
	 */
	public Map<String, List<Object>> getDataResults(ResultSet results);

	/**
	 * Return result column headers
	 *
	 * @param results
	 * @return
	 */
	public List<String[]> getMetaDataResults(ResultSet results);

	/**
	 * Make a connection to a database using default mysql connector
	 *
	 * @param database
	 * @param userName
	 * @param password
	 * @return
	 */
	public boolean makeConnection(String database, String userName,
			String password);

	/**
	 * Make a connection to a database using a particular driver
	 *
	 * @param driver
	 *            e.g. com.mysql.jdbc.Driver
	 * @param serverName
	 *            e.g. mysql://localhost/
	 * @param database
	 *            e.g. feedback
	 * @param userName
	 *            e.g. root
	 * @param password
	 *            e.g. pass
	 * @return
	 */
	public boolean makeConnection(String driver, String serverName,
			String database, String userName, String password);
}
