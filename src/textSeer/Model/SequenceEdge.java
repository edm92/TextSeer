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

import java.util.TreeMap;

public class SequenceEdge {
	public static String SOURCEFILE = std.string.endl + "Error in: textSeer.Model.SequenceEdge.java" + std.string.endl;
	public static boolean DEBUG = std.string.debug;	// Hopefully true when you edit :P
	public static void debug(String msg){
		if(DEBUG)
			std.calls.debug_(msg + SOURCEFILE);
	}
	

  public static int totalEdges = 0;;

  public int id = totalEdges++;;

  public String name;

  public Vertex source;

  public Vertex target;

  public Graph parent;

  public TreeMap<String, Effect> CE;

  public Effect IE;

  public Boolean isDefault;


  public SequenceEdge(Graph myParent){
	  init();
	  parent = myParent;
	  name = generateName();
 }
 
  public SequenceEdge(Graph myParent, String newName){
	  init();
	  parent = myParent;
	  name = newName;
  }
  
  public void init(){
	  id = ++totalEdges;
  }
  
  public String generateName() {
	  return "SequenceEdge" + id; 
  }

  /*
   * If you change the checker here then fix finalize
   */
  public void addSource(Vertex n){
	  source = n;
	  if(source != null)
	  if(source.outEdges != null && !source.outEdges.contains(this))
		  source.outEdges.add(this);
	  if(target != null){
		  if(target.inNodes != null && !target.inNodes.contains(source))
			  target.inNodes.add(source);
	  }
  }
  
  /*
   * If you change the checker here then fix finalize
   */
  public void addTarget(Vertex n){
	  target = n;
	  if(source != null){
		  if(source.outNodes != null && !source.outNodes.contains(target))
			  source.outNodes.add(target);
	  }
	  if(target != null && target.inEdges != null && !target.inEdges.contains(this))
		  target.inEdges.add(this);
  }
  
  public void finalize(){
	  if(source != null){
		  if(source.outNodes != null && !source.outNodes.contains(target))
			  source.outNodes.add(target);
	  }
	  if(target != null && target.inEdges != null && !target.inEdges.contains(this))
		  target.inEdges.add(this);
	  if(source != null && source.outEdges != null && !source.outEdges.contains(this))
		  source.outEdges.add(this);
	  if(target != null){
		  if(target.inNodes != null && !target.inNodes.contains(source))
			  target.inNodes.add(source);
	  }
  }

  public String toString() {
  	  String returnString = "";
  	  
  	  return returnString;
    }

}