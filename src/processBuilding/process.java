package processBuilding;

import java.util.LinkedList;
import java.util.List;

import textSeer.Model.Graph;

public class process {
	public static int totalProcesses = 0;
	public List<Graph> endEffectScenarios;
	public String name;
	public int id;
	public Graph structure;
	
	public process(){
		structure = new Graph();
		id = totalProcesses++;
		name = "P" + id;
		endEffectScenarios = new LinkedList<Graph>();
	}

}
