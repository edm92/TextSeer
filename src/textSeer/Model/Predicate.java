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
	  return "Predicate" + id; 
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