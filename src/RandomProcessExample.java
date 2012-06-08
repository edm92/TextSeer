import au.edu.dsl.dlab.processtools.Edge;
import au.edu.dsl.dlab.processtools.Graph;
import au.edu.dsl.dlab.processtools.RandomProcessGenerator;
import au.edu.dsl.dlab.processtools.Vertex;


public class RandomProcessExample {
	public static void main(String[] args){
		RandomProcessGenerator<Vertex,Edge> myProcess = new RandomProcessGenerator<Vertex,Edge>(new Graph<Vertex,Edge>());
		
		// Optional Configure //
		myProcess.CHANCE_OF_GATEWAY = 2;	// Set roughly a 1 in 5 chance that next node will be a gateway
											// Default of this variable is 5
		myProcess.MIN_NUMBER_OF_NODES = 10; // Set minimum nodes for new graph. 
											// Default of this variable is 10.
		
		// Generate //  
		Graph<Vertex,Edge> myG = myProcess.generateProcess();
        
		// Display Output //		
		System.out.println("Total nodes " + myProcess.countNodes());
		Vertex.TO_STRING_WITH_EFFECTS = true;
		myG.printGraph();						// Using returned graph as an example - you can use either way.
		//Vertex.TO_STRING_WITH_EFFECTS = false;
		System.out.println("##############Showing Paths ###################");
		myProcess.getGraph().printPaths();		// Requesting graph from class as an example - you can use either way.
		
		
		
	}
}
