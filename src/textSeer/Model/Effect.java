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

import java.util.Iterator;
import java.util.TreeMap;

public class Effect {
	public static String SOURCEFILE = std.string.endl + "Error in: textSeer.Model.Effect.java" + std.string.endl;
	public static boolean DEBUG = std.string.debug;	// Hopefully true when you edit :P
	public static void debug(String msg){
		if(DEBUG)
			std.calls.debug_(msg + SOURCEFILE);
	}

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