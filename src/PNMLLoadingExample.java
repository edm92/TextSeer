
import au.edu.dsl.dlab.processtools.Edge;
import au.edu.dsl.dlab.processtools.Graph;
import au.edu.dsl.dlab.processtools.GraphFixer;
import au.edu.dsl.dlab.processtools.Vertex;
import au.edu.dsl.dlab.processtools.parser.PNML;


public class PNMLLoadingExample {

	public static void main(String [] args){
		// Turn off skip switch. By default this is on due to speed increases during effect accumulation 
		Graph.SKIP_EMPTY_EFFECT_PATHS = false;
		// Turn this off if you need to work with accumulation later.
		
		PNML<Vertex, Edge> myLoader = new PNML<Vertex, Edge>();
		Graph<Vertex,Edge> myResult = myLoader.loadModel("models/pnml/77.pnml.xml");

		
		myResult.cleanup(false);
		//myResult.printScenarioPaths();
		
		// Fix graphs that have weird edges so that they can be displayed correctly.
		GraphFixer miner = new GraphFixer();
		miner.getScenarioPaths(myResult);
		
		myResult.cleanup(false);
		myResult.printScenarioPaths();
		//System.out.println(PNML.createPNet(myResult));
		
		
		
	}
	
}
