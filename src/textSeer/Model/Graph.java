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

public class Graph {
	public static String SOURCEFILE = std.string.endl + "Error in: textSeer.Model.Graph.java" + std.string.endl;
	public static boolean DEBUG = std.string.debug;	// Hopefully true when you edit :P
	public static void debug(String msg){
		if(DEBUG)
			std.calls.debug_(msg + SOURCEFILE);
	}
	

  public static int totalProcesses = 0;

  public int ID;

  public String name;

  public List<SequenceEdge> edges;

  public List<Vertex> startNodes;

  public List<Vertex> endNodes;

  public List<Vertex> allNodes;
  
  public List<Effect> GraphEffects;

  public void unVisitNodes(){
	  for(Vertex v:allNodes){
		  v.visited.remove(this.ID);
		  v.visited.put(this.ID, std.string.not_visited);
	  }
  }
  
  public Graph(){
	  initGraph();
	  name = generateName();
 }
 
  public Graph(String newName){
	  initGraph();
	  name = newName;
  }
  
  public Graph(Graph g){
	  initGraph();
	  name = generateName();
	  
	  
	  for(Vertex c:g.allNodes)
		  if(!allNodes.contains(c))
			  allNodes.add(c);
	  
  }
  
  
  public void initGraph(){
	  ID = ++totalProcesses;
	  allNodes = new LinkedList<Vertex>();
	  endNodes = new LinkedList<Vertex>();
	  edges = new LinkedList<SequenceEdge>();
	  startNodes = new LinkedList<Vertex>();
	  GraphEffects = new LinkedList<Effect>();

  }

  public String generateName() {
	  return "Process" + totalProcesses; 
  }

  public void copy( Graph in) {
	  // Copy each new element. 
	  // Shouldn't need to do much more of a check for existing elements but if there 
	  // are issues then someone will need to expand the .contains check
	  for(Vertex c:in.allNodes)
		  if(!allNodes.contains(c))
			  allNodes.add(c);
	  
	  for(Vertex d:in.endNodes)
		  if(!endNodes.contains(d))
			  endNodes.add(d);
	  
	  for(Vertex e:in.startNodes)
		  if(!startNodes.contains(e))
			  startNodes.add(e);
	  
	  for(SequenceEdge f:in.edges)
		  if(!edges.contains(f))
			  edges.add(f);
	  // Self Finalize to propergate any extra nodes 
	  this.finalize();
  }
  
  public void finalize(){
	  // Function will search for elements to add to end and start. 
	  for(Vertex v:allNodes){
		  if(v.inNodes.isEmpty())
			  startNodes.add(v);
		  if(v.outNodes.isEmpty())
			  endNodes.add(v);
	  }
	  for(SequenceEdge e:edges){
		  if(e.source != null && e.source.inNodes.isEmpty() && !startNodes.contains(e.source))
			  startNodes.add(e.source);
		  if(e.target != null && e.target.outNodes.isEmpty() && !endNodes.contains(e.target))
			  endNodes.add(e.target);
	  }
  }

  public void ScenarioAddNode(Vertex v){
	  if(v != null){
		  if(!v.visited.containsKey(this.ID))
			  v.visited.put(this.ID, std.string.not_visited);
		  if(!allNodes.contains(v))
			  allNodes.add(v);
	  }
  }
  
  // Slow construction of process model ensures fastest search
  public void addNode(Vertex v){
	  if(v != null){
		  if(!v.visited.containsKey(this.ID))
			  v.visited.put(this.ID, std.string.not_visited);
		  if(!allNodes.contains(v))
			  allNodes.add(v);
		  if(v.inEdges != null)
		  for(SequenceEdge e:v.inEdges){
			  if(!edges.contains(e))
				  addEdge(e);
		  }
		  if(v.outEdges != null)
		  for(SequenceEdge e:v.outEdges){
			  if(!edges.contains(e))
				  addEdge(e);
		  }
		  if(v.inNodes != null)
		  for(Vertex n:v.inNodes){
			  if(!allNodes.contains(n))
				  addNode(n);
		  }
		  if(v.outNodes != null)
		  for(Vertex n:v.outNodes){
			  if(!allNodes.contains(n))
				  addNode(n);
		  }
		  
	  }
  }
  
  
  // Slow construction of process model ensures fastest search
  public void addEdge(SequenceEdge e) {
	  // Make sure to call finalize on the graph after this proceedure.
	  
	  if(!edges.contains(e))
		  edges.add(e);
	  if(e.target != null )
		  addNode(e.target);
	  if(e.source != null )
		  addNode(e.source);
  
  }
  
  
  public String toString() {
	  String returnString = "";
	  returnString += "Edges" + std.string.endl;
	  for(SequenceEdge e:edges){
		  returnString += (e.source == null ? "" : e.source.name + std.string.rightArrow) +  
		  				e.name + 
		  				(e.source == null ? "" : std.string.rightArrow + e.target.name)
		  				+ std.string.endl;
	  }
	  if(allNodes != null){
		  returnString += "Verticies" + std.string.endl + "-------------" + std.string.endl;
		  for(Vertex v:allNodes){
			  returnString += v.toString();
		  }
		  
	  }
	  return returnString;
  }

}