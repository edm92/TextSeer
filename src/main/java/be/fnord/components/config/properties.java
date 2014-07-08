package be.fnord.components.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class properties {
	public static String databaseDriver = "com.mysql.jdbc.Driver";
	public static String databaseName = "activiti";
	public static String databasePass = "";
	public static String databaseServer = "mysql://localhost/";
	public static String databaseUser = "root";
	public static boolean debugMode = true;
	public static String endl = "";
	public static boolean loaded = false;
	public static long sessionLength = 0;

	public static String sls = "";

	public static void loadProperties() {
		a.e.endl = endl = System.getProperty("line.separator");

		if (loaded) {
			return;
		}
		loaded = true;

		try {
			properties newP = new properties();
			Properties in = newP.getPropertiesFromClasspath("config.ini");
			if (in == null) {
				System.out.println("Null in");
				System.exit(-1);
			}
			;

			databaseDriver = in.getProperty("databaseDriver");
			databaseServer = in.getProperty("databaseServer");
			databaseName = in.getProperty("databaseName");
			databaseUser = in.getProperty("databaseUser");
			databasePass = in.getProperty("databasePass");
			debugMode = in.getProperty("debugMode").toLowerCase()
					.compareTo("true") == 0 ? true : false;
			sessionLength = Long.parseLong(in.getProperty("sessionLength"));
			if (sessionLength == 0) {
				sessionLength = 1209600000; // If 0 then make it 1 weeks
			}
		} catch (IOException e) {
			System.err.println("Need properties loaded, can not start");
			e.printStackTrace();
			System.exit(-1);
		}

	}

	private Properties getPropertiesFromClasspath(String propFileName)
			throws IOException {
		Properties props = new Properties();
		InputStream inputStream = this.getClass().getClassLoader()
				.getResourceAsStream(propFileName);

		if (inputStream == null) {
			throw new FileNotFoundException("property file '" + propFileName
					+ "' not found in the classpath");
		}

		props.load(inputStream);
		return props;
	}
}
