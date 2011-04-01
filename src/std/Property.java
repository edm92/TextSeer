package std;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Property {

	public static String getProperty(String propertyName){
		Properties props = new Properties();
		String message = std.string.propertyNotFound;
        //try retrieve data from file
           try {
           props.load(new FileInputStream("textseer.properties.dat"));
           message = props.getProperty(propertyName);
           }
           catch(IOException e)
           {
        	   // e.printStackTrace();
        	   message = std.string.propertyNotFound;
           }
           //std.calls.display(propertyName + " : " + message + "[loaded]");
           return message;
	}
}
