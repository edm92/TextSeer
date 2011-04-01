package textSeer.Model;

import java.util.Iterator;
import java.util.TreeMap;

public class Effect {

  public static int totalEffects = 0;;

  public int id;

  public String name;

  public TreeMap<String, Predicate> effects;

  public Vertex myVertex;
  public SequenceEdge mySequenceEdge;

  public Effect(){
	  init();
	  name = generateName();
 }
 
  public Effect(String newName){
	  init();
	  name = newName;
  }
  
  public void init(){
	  effects = new TreeMap<String,Predicate>();
	  id = ++totalEffects;
  }
  
  public String generateName() {
	  return "Effect" + id; 
  }
  
  public void addPredicate(Predicate newAddition){
	  if(!effects.containsKey(newAddition.name)){
		  effects.put(newAddition.name, newAddition);
	  }
  }

  public String toValue() {
	  String returnString = "";
	  Iterator<String> it = effects.keySet().iterator();
	  while(it.hasNext()){
		  returnString += effects.get( it.next()).toValue() + " & ";
	  }	 
	  if(returnString.length() > 1)
		  returnString = returnString.substring(0, returnString.length() - 3);
	  return returnString;
  }
  
  public String toString() {
	  String returnString = name + "(";
	  Iterator<String> it = effects.keySet().iterator();
	  while(it.hasNext()){
		  returnString += effects.get( it.next()).toValue();
		 
	  }
	  return returnString + ")";
  }

}