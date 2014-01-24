package be.fnord.util.processModel;

import java.util.LinkedHashSet;
import java.util.UUID;

import be.fnord.util.logic.WFF;


/**Verxt for semantic tracing
 * 
 * @author Xiong Wen (xw926@uowmail.edu.au)
 *
 */


public class Vertex_ST extends Graph<Vertex, Edge>{

	private static int uniqueID = 0; 
	
	public String id = "";
	public String taskName = "";
	public String immWFF = "";
	public String esWFF = "";
	public Vertex_ST() {

		super();
	}
	
	/**
	 * Constructor
	 * @param _taskName task name
	 * @param _immWFF immediate effect
	 * @param _esWFF  cumulative effect
	 * @author Xiong Wen (xw926@uowmail.edu.au)
	 *
	 */
	public Vertex_ST(String _taskName, String _immWFF, String _esWFF){
		this.id = UUID.randomUUID().toString(); 
		this.taskName = _taskName + "_" + uniqueID++; // Evan added unique ID addition to help make each task name unique
		this.immWFF = _immWFF;
		this.esWFF = _esWFF;		
	}
	
	
	public static void main(String[] args) {

		// TODO Auto-generated method stub
		Vertex_ST test = new Vertex_ST();
		test.taskName = "newTask";
		test.immWFF = "immediate effect";
		test.esWFF = "a subset";
		System.out.println("task name: " + test.taskName);
		System.out.println("immWFF: " + test.immWFF);
		System.out.println("esWFF: " + test.esWFF);

	}

}
