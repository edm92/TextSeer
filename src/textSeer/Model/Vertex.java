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



package textSeer.Model;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class Vertex {
	
	public static String SOURCEFILE = std.string.endl + "Error in: textSeer.Model.Vertex.java" + std.string.endl;
	public static boolean DEBUG = std.string.debug;	// Hopefully true when you edit :P
	public static void debug(String msg){
		if(DEBUG)
			std.calls.debug_(msg + SOURCEFILE);
	}

  public long marker = 1;	// used for xor splits in breath first search
  public TreeMap<Integer, String> visited;	// Graph ID 
  public static int totalVertices = 0;

  public int id = totalVertices++;;
  
  public String name = "";

  public TreeMap<String, Effect> CE;

  public Effect IE;

  public std.type.Node nodeType;

  public Object subNodeType;

  public List<Vertex> inNodes;

  public List<Vertex> outNodes;

  public List<SequenceEdge> inEdges;

  public List<SequenceEdge> outEdges;

  public Graph  parent;

  public void markVisited(Graph pGraph){
	  if(pGraph != null){
		  visited.remove(pGraph.ID);
		  visited.put(pGraph.ID, std.string.visted);
	  }
  }
  
  public boolean isVisited(Graph pGraph){
	  if(pGraph != null){
		  String result = visited.get(pGraph.ID);
		  
		  if(result.equals(std.string.visted))
			  return true;
		  else
			  return false;
	  }
	  return true;
  }
  
  public void init(){
	  visited = new TreeMap<Integer, String >();
	  if(parent != null)
		  visited.put(parent.ID, std.string.not_visited);
	  id = ++totalVertices;
	  inNodes = new LinkedList<Vertex>();
	  outNodes = new LinkedList<Vertex>();

	  inEdges = new LinkedList<SequenceEdge>();
	  outEdges = new LinkedList<SequenceEdge>();
	  IE = null;
	  

  }
  
  public Vertex(Graph myParent){
	  init();
	  parent = myParent;
	  name = generateName();
 }
 
  public Vertex(Graph myParent, String newName){
	  init();
	  parent = myParent;
	  name = newName;
  }
  
  public void addEffect(Effect e){
	  IE = e;
  }
 
  
  public String generateName() {
	  return "Vertex" + id; 
  }

  

  public void getCE() {
  }

  public void getIE() {
  }

  public String toString() {
	  String returnString = "";
	  returnString += "Vertex name: " + name + "(" +id + ")" + std.string.endl;
	  if(IE != null){
		  returnString += "\tEffects: " + IE.toString() +  std.string.endl;
	  }
	  
	  if(inNodes != null){
		  returnString += "\tInNodes: ";
		  for(Vertex in:inNodes){
			  if(in != null && in.name != null)
				  returnString += in.name + "(" + in.id + "), ";  
			  else
				  if(in == null) returnString += "NAME BNUL";
		  }
		// Cut final comma ;) and add a new line
		  returnString = returnString.substring(0, returnString.length()-2) + std.string.endl; 
	  }
	  if(outNodes != null){
		  returnString += "\tOutNodes: ";
		  for(Vertex in:outNodes){
			  if(in != null)
				  returnString += in.name + "(" + in.id + "), ";  
		  }
		  // Cut final comma ;) and add a new line
		  returnString = returnString.substring(0, returnString.length()-2) + std.string.endl; 
	  }
	  
	  
	  return returnString;
  }

}