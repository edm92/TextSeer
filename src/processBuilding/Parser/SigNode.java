package processBuilding.Parser;

import java.util.TreeMap;

public class SigNode extends SigObject{


	public TreeMap<String, SigObject> incomming;	// Reference is ID
	public TreeMap<String, SigObject> outgoing;
	
	SigNode(){
		super();
		
		incomming = new TreeMap<String, SigObject>();
		outgoing = new TreeMap<String, SigObject>();
	}
	
}

