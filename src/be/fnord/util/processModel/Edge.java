package be.fnord.util.processModel;

import java.util.UUID;

import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * 
 * @author Evan Morrison edm92@uowmail.edu.au http://www.fnord.be
 * Apache License, Version 2.0, Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */
public class Edge extends DefaultWeightedEdge{
	private static final long serialVersionUID = 1L;
	public String name = "";
	public String type = "";
	public UUID id = UUID.randomUUID();
	public Vertex source = null;
	public Vertex target = null;

	public String getName() {	return name;}
	public void setName(String name) {this.name = name;	}
	public String getType() {return type;}
	public void setType(String type) {this.type = type;	}
	public Vertex getSource() {	return source;}
	public void setSource(Vertex source) {	this.source = source;}
	public Vertex getTarget() {	return target;}
	public void setTarget(Vertex target) {	this.target = target;}

	public Edge(String _name){	this(_name, "");}
	public Edge(String _name, String _type){this.name = _name; this.type = _type;	}
	public Edge(Vertex _source, Vertex _target){this("","", _source, _target);}
	
	public Edge(String _name, String _type, Vertex _source, Vertex _target){
		this.name = _name; this.type = _type; this.source = _source; this.target = _target;	}
	
	public String toString()
	{	return "[E](" + this.source + "->" + this.target +")" + ((this.name.length() > 0) ? "{"+name+"}" : ""); }
	
	// TODO Implement this at some time
	public Edge makeCopy(){ return this; }
}
