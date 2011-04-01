package textSeer.Model;

public class Gateway extends Vertex {
	
	// To test if gateway : 
	// o.getClass().equals(Gateway.class)
	
	// Actual types 
	public static enum gatetype {
		XOR, 
		AND 
	}
	
	
	
	// Type of this particular gateway
	public gatetype type;
	
	public Gateway(Graph parent){
		super(parent);
	}
	
	public Gateway(Graph parent, String name){
		super(parent, name);
	}
	
}