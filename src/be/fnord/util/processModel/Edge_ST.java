package be.fnord.util.processModel;

import java.util.UUID;

import org.jgrapht.graph.DefaultWeightedEdge;


/**
 * edge for semantic tracing
 * @author Xiong Wen (xw926@uowmail.edu.au)
 *
 */

public class Edge_ST extends DefaultWeightedEdge {

	private static final long serialVersionUID = 1L;
	public String name = "";
	public String type = "";
	public UUID id = UUID.randomUUID();
	public Vertex_ST source = null;
	public Vertex_ST target = null;
	
	/**
	 * Get the source vertex
	 * @return source the source vertex
	 *
	 */
	public Vertex_ST getSource() {	return source;}
	
	/**
	 * Set the source vertex
	 * @param source the source vertex to be set
	 *
	 */
	public void setSource(Vertex_ST source) {	this.source = source;}
	
	/**
	 * Get the target vertex
	 * @return target the target vertex
	 *
	 */
	public Vertex_ST getTarget() {	return target;}
	
	
	/**
	 * Set the target vertex
	 * @param target the target vertex to be set
	 *
	 */
	public void setTarget(Vertex_ST target) {	this.target = target;}
	
	public Edge_ST(){
		super();
	}
	public Edge_ST(String _name){	this(_name, "");}
	public Edge_ST(String _name, String _type){this.name = _name; this.type = _type;	}
	
	/**
	 * Constructor
	 * @param _source the source vertex of the edge
	 * @param _target the target vertex of the edge
	 *
	 */
	public Edge_ST(Vertex_ST _source, Vertex_ST _target){this("","", _source, _target);}
	
	public Edge_ST(String _name, String _type, Vertex_ST _source, Vertex_ST _target){
		this.name = _name; this.type = _type; this.source = _source; this.target = _target;	}
	
	/**
	 * Print out the edge in the form of "[acc](source es -> target es)"
	 *
	 */
	public String toString()
	{	
		if(this.source == null) return "";
		return "[acc](" + this.source.esWFF + "->" + this.target.esWFF +")" + ((this.name.length() > 0) ? "{"+name+"}" : ""); }
	
	
	
	

	public static void main(String[] args) {

		// TODO Auto-generated method stub

	}

}
