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

import java.util.LinkedList;
import java.util.List;

import textSeer.Model.Graph;

public class process {
	public static String SOURCEFILE = std.string.endl + "Error in: processBuilding.process.java" + std.string.endl;
	public static boolean DEBUG = std.string.debug;	// Hopefully true when you edit :P
	public static void debug(String msg){
		if(DEBUG)
			std.calls.debug_(msg + SOURCEFILE);
	}
	
	
	
	public static int totalProcesses = 0;
	public List<Graph> endEffectScenarios;
	public String name;
	public int id;
	public Graph structure;
	
	public process(){
		structure = new Graph();
		id = ++totalProcesses;
		name = "P" + totalProcesses;
		endEffectScenarios = new LinkedList<Graph>();
	}

}
