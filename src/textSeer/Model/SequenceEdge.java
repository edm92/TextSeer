package textSeer.Model;

import java.util.TreeMap;

public class SequenceEdge {

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
	  if(target.inEdges != null && !target.inEdges.contains(this))
		  target.inEdges.add(this);
  }
  
  public void finalize(){
	  if(source != null){
		  if(source.outNodes != null && !source.outNodes.contains(target))
			  source.outNodes.add(target);
	  }
	  if(target.inEdges != null && !target.inEdges.contains(this))
		  target.inEdges.add(this);
	  if(source.outEdges != null && !source.outEdges.contains(this))
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