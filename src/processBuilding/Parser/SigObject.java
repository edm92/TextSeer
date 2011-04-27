package processBuilding.Parser;

public class SigObject {
	public String Name;
	public String ID;
	public String type;
	public String annote;
	
	public SigObject(String name, String id){
		Name = name;
		ID = id;
	}
	
	public SigObject(){
		Name = "";
		ID = "";		
		type ="";
		annote ="";
	}

}
