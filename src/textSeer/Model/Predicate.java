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


public class Predicate {

  public static int totalPredicates=0;

  public int id;

  public String name;

  private String value = "";
  
  public Predicate(){
	  init(generateName(), "");
 }
 
  public Predicate(String newName){
	  init(newName, "");	  
  }
  
  public Predicate(String myValue, Boolean valueOnly){
	  init(generateName(), myValue);	  
  }
  
  public Predicate(String newName, String myValue){
	  init(newName, myValue);	  
  }
  
  public void init(String newName, String myValue){
	  id = ++totalPredicates;
	  name = newName;
	  value = myValue;

  }
  
  public String generateName() {

	  return "Predicate" + totalPredicates; 
  }


  	public String toValue(){
  		//std.calls.display("Value check : " + value);
  		return value;
  	}
  	
    public String toString() {
  	  String returnString = name + ":" + value;
  	  
  	  return returnString;
    }

}