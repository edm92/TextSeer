package be.fnord.util.processModel;

import java.util.UUID;

import org.jgrapht.graph.DefaultWeightedEdge;

import be.fnord.util.logic.WFF;

/**
 * @author Evan Morrison edm92@uowmail.edu.au http://www.fnord.be Apache
 *         License, Version 2.0, Apache License Version 2.0, January 2004
 *         http://www.apache.org/licenses/
 */
public class Edge extends DefaultWeightedEdge {
	private static final long serialVersionUID = 1L;
	public UUID id = UUID.randomUUID();
	private UUID ID;
	private WFF immWFF = new WFF();
	public String name = "";
	public Vertex source = null;

	public Vertex target = null;

	public String type = "";

	private String WFF;

	public Edge() {
		super();
		ID = UUID.randomUUID();
	}

	public Edge(String _name) {
		this(_name, "");
	}

	public Edge(String _name, String _type) {
		name = _name;
		type = _type;
	}

	public Edge(String _name, String _type, Vertex _source, Vertex _target) {
		name = _name;
		type = _type;
		source = _source;
		target = _target;
	}

	public Edge(Vertex _source, Vertex _target) {
		this("", "", _source, _target);
	}

	// public Edge(String _name){this(); name = _name;}
	public void addWFF(String _WFF) {
		WFF = _WFF;
	}

	public String getID() {
		return ID.toString();
	}

	public String getName() {
		return name;
	}

	@Override
	public Vertex getSource() {
		return source;
	}

	@Override
	public Vertex getTarget() {
		return target;
	}

	public String getType() {
		return type;
	}

	public WFF getWFF() {
		immWFF.setFormula(WFF);
		return immWFF;
	}

	// TODO Implement this at some time
	public Edge makeCopy() {
		return this;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSource(Vertex source) {
		this.source = source;
	}

	;

	public void setTarget(Vertex target) {
		this.target = target;
	}

	;

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "[E](" + source + "->" + target + ")"
				+ (name.length() > 0 ? "{" + name + "}" : "");
	}

	@SuppressWarnings("unchecked")
	public String toString(Graph<?, ?> in) {
		String result = "";
		try {
			Graph<Vertex, Edge> newG = (Graph<Vertex, Edge>) in;

			if (newG != null) {
				if (newG.getEdgeSource(this) != null
						&& newG.getEdgeTarget(this) != null) {
					result = "{" + newG.getEdgeSource(this).name + "} --&gt; {"
							+ newG.getEdgeTarget(this).name + "}";
				}
			}
		} catch (Exception e) {
			return "";
		}
		;

		return result;
	}
}
