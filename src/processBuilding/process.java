package processBuilding;

import java.util.LinkedList;
import java.util.List;

import textSeer.Model.Graph;

public class process {
	public static String SOURCEFILE = std.string.endl + "Error in: processBuilding.process.java" + std.string.endl;
	public static boolean DEBUG = std.string.debug;	// Hopefully true when you edit :P
	public static void debug(String msg){
		if(DEBUG)
			std.calls.display(msg + SOURCEFILE);
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
