package be.fnord.util.processModel;

import java.util.UUID;

/**
 * Verxt for semantic tracing
 *
 * @author Xiong Wen (xw926@uowmail.edu.au)
 */

public class Vertex_ST extends Graph<Vertex, Edge> {

	private static final long serialVersionUID = -4505887246692894685L;

	private static int uniqueID = 0;

	public static void main(String[] args) {

		//
		Vertex_ST test = new Vertex_ST();
		test.taskName = "newTask";
		test.immWFF = "immediate effect";
		test.esWFF = "a subset";
		System.out.println("task name: " + test.taskName);
		System.out.println("immWFF: " + test.immWFF);
		System.out.println("esWFF: " + test.esWFF);

	}

	public String esWFF = "";
	public String id = "";
	public String immWFF = "";

	public String taskName = "";

	public Vertex_ST() {

		super();
	}

	/**
	 * Constructor
	 *
	 * @param _taskName
	 *            task name
	 * @param _immWFF
	 *            immediate effect
	 * @param _esWFF
	 *            cumulative effect
	 * @author Xiong Wen (xw926@uowmail.edu.au)
	 */
	public Vertex_ST(String _taskName, String _immWFF, String _esWFF) {
		id = UUID.randomUUID().toString();
		taskName = _taskName + "_" + uniqueID++; // Evan added unique ID
													// addition to help make
													// each task name unique
		immWFF = _immWFF;
		esWFF = _esWFF;
	}

}
