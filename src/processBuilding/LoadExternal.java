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

package processBuilding;

// This module requires the xpdlLoader (will be added to git shortly), seperated from main due to dependencies.

//import xpdlLoader.OpenModel;

// This is deprecated. See processBuilding.Parser for implementation of BPMN reader from Signavo files


public class LoadExternal {
	public static String SOURCEFILE = std.string.endl + "Error in: processBuilding.LoadExternal.java" + std.string.endl;
	public static boolean DEBUG = true;// std.string.debug;	// Hopefully true when you edit :P
	public static void debug(String msg){
		if(DEBUG)
			std.calls.debug_(msg + SOURCEFILE);
	}
	

	public static process loadFile(String filename){
		debug("Not Loading " + std.string.filePath +  filename + " the external loader has been disabled");
		return null;
		//return OpenModel.readXPDL(std.string.filePath +  filename);
	}
}
