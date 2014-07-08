package be.fnord.components.dataconnection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import be.fnord.components.config.properties;

/**
 * My connection database functions.
 *
 * @author Evan
 */
public class myConnection implements intConnection {
	java.sql.Connection connect = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;
	Statement statement = null;

	@Override
	public boolean closeConnection() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public PreparedStatement createPreparedStatement(String query) {
		try {
			preparedStatement = connect.prepareStatement(query);
		} catch (SQLException e) {
			a.e.println(e.toString());
			return null;
		}
		return preparedStatement;
	}

	@Override
	public ResultSet executeQuery(String query) {
		try {
			statement = connect.createStatement();
			resultSet = statement.executeQuery(query);
			// System.out.println("Reunning Query " + query + " - " +
			// resultSet.getFetchSize()) ;
		} catch (Exception e) {
			a.e.println(e.toString());
			return null;
		}
		return resultSet;
	}

	@Override
	public TreeMap<String, List<Object>> getDataResults(ResultSet _results) {
		TreeMap<String, List<Object>> returnedResults = new TreeMap<String, List<Object>>();
		List<String[]> headers = getMetaDataResults(_results);
		int row = 0; // For naming
		try {
			int cols = _results.getMetaData().getColumnCount();
			while (_results.next()) {
				// System.out.println("New Row " + row);
				// It is possible to get the columns via name
				// also possible to get the columns via the column number
				// which starts at 1
				// e.g. resultSet.getSTring(2);
				LinkedList<Object> currentRow = new LinkedList<Object>();
				// System.out.println("New Row " + cols);
				for (int i = 0; i < cols; i++) {
					currentRow.add(_results.getObject(headers.get(i)[0]));
					// System.out.println("String: " +
					// resultSet.getObject(headers.get(i)[0]));

				}
				returnedResults.put(row + "", currentRow);
				row++;
			}
		} catch (Exception e) {
			a.e.println(e.toString());
		}
		;

		// System.out.println(returnedResults.size() + " <-- returned");
		return returnedResults;
	}

	@Override
	public List<String[]> getMetaDataResults(ResultSet results) {
		LinkedList<String[]> returnedResults = new LinkedList<String[]>();
		try {
			for (int i = 1; i <= results.getMetaData().getColumnCount(); i++) {
				// System.out.println("Column " +i + " "+
				// results.getMetaData().getColumnName(i));
				String[] strs = new String[2];
				strs[0] = results.getMetaData().getColumnName(i);
				strs[1] = results.getMetaData().getColumnTypeName(i);
				returnedResults.add(strs);

			}
		} catch (Exception e) {
			a.e.println(e.toString());
		}
		return returnedResults;
	}

	public boolean makeConnection() {

		return makeConnection(properties.databaseDriver,
				properties.databaseServer, properties.databaseName,
				properties.databaseUser, properties.databasePass);
	}

	@Override
	public boolean makeConnection(String database, String userName,
			String password) {

		return makeConnection(properties.databaseDriver,
				properties.databaseServer, database, userName, password);
	}

	@Override
	public boolean makeConnection(String driver, String serverName,
			String database, String userName, String password) {
		try {
			// This will load the MySQL driver, each DB has its own driver
			Class.forName(driver);
			// Setup the connection with the DB
			connect = DriverManager.getConnection("jdbc:" + serverName
					+ database + "?" + "user=" + userName + "&password="
					+ password);

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
		} catch (Exception e) {
			a.e.println(e.toString());
			return false;
		}

		return true;
	}

}
