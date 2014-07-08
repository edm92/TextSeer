package be.fnord.util.processModel;

import java.util.UUID;

import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * edge for semantic tracing
 *
 * @author Xiong Wen (xw926@uowmail.edu.au)
 */

public class Edge_ST extends DefaultWeightedEdge {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {

	}

	public UUID id = UUID.randomUUID();
	public String name = "";
	public Vertex_ST source = null;
	public Vertex_ST target = null;

	public String type = "";

	public Edge_ST() {
		super();
	}

	public Edge_ST(String _name) {
		this(_name, "");
	}

	public Edge_ST(String _name, String _type) {
		name = _name;
		type = _type;
	}

	public Edge_ST(String _name, String _type, Vertex_ST _source,
			Vertex_ST _target) {
		name = _name;
		type = _type;
		source = _source;
		target = _target;
	}

	/**
	 * Constructor
	 *
	 * @param _source
	 *            the source vertex of the edge
	 * @param _target
	 *            the target vertex of the edge
	 */
	public Edge_ST(Vertex_ST _source, Vertex_ST _target) {
		this("", "", _source, _target);
	}

	/**
	 * Get the source vertex
	 *
	 * @return source the source vertex
	 */
	@Override
	public Vertex_ST getSource() {
		return source;
	}

	/**
	 * Get the target vertex
	 *
	 * @return target the target vertex
	 */
	@Override
	public Vertex_ST getTarget() {
		return target;
	}

	/**
	 * Set the source vertex
	 *
	 * @param source
	 *            the source vertex to be set
	 */
	public void setSource(Vertex_ST source) {
		this.source = source;
	}

	/**
	 * Set the target vertex
	 *
	 * @param target
	 *            the target vertex to be set
	 */
	public void setTarget(Vertex_ST target) {
		this.target = target;
	}

	/**
	 * Print out the edge in the form of "[acc](source es -> target es)"
	 */
	@Override
	public String toString() {
		if (source == null) {
			return "";
		}
		return "[acc](" + source.esWFF + "->" + target.esWFF + ")"
				+ (name.length() > 0 ? "{" + name + "}" : "");
	}

}
