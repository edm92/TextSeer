/**
 * TextSeer, development prototype for ProcessSeer
    Copyright (C) 2011 Evan Morrison

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * @author Evan Morrison (edm92@uow.edu.au)
 *
 */


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
