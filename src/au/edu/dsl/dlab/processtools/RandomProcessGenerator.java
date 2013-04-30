package au.edu.dsl.dlab.processtools;

import java.util.LinkedList;
import java.util.Random;
import org.jgrapht.graph.EdgeReversedGraph;

import au.edu.dsl.dlab.processtools.Vertex._GTYPE;
import au.edu.dsl.dlab.processtools.Vertex._TYPE;


/**
 * Random process generator. Currently generates processes that have randomally assigned effects. To make changes to the graph, vertex, 
 * edge or effect classes or functions please do so in the respective files. This should remain a utility class for process generation.  
 * @author edm92
 * @deprecated This class has been moved to be.fnord.util.processModel
 */

public class RandomProcessGenerator<T extends Vertex,V extends Edge> {
	
	/**
	 * Copy and paste from here to your own code for graph generation.
	 *  generateProcess will return the generated graph.
	 *  
	 *  Alternatively the graph can be collected using getGraph();
	 * 
	 */
//	public static void main(String[] args){
//		RandomProcessGenerator<Vertex,Edge> myProcess = new RandomProcessGenerator<Vertex,Edge>(new Graph<Vertex,Edge>(Edge.class));
//		
//		// Optional Configure //
//		myProcess.CHANCE_OF_GATEWAY = 5;	// Set roughly a 1 in 5 chance that next node will be a gateway
//											// Default of this variable is 5
//		myProcess.MIN_NUMBER_OF_NODES = 10; // Set minimum nodes for new graph. 
//											// Default of this variable is 10.
//		
//		// Generate //  
//		Graph<Vertex,Edge> myG = myProcess.generateProcess();
//        
//		// Display Output //
//			
//		System.out.println("Total nodes " + myProcess.countNodes());
//		Vertex.TO_STRING_WITH_EFFECTS = true;
//		myG.printGraph();						// Using returned graph as an example - you can use either way.
//		Vertex.TO_STRING_WITH_EFFECTS = false;
//		System.out.println("##############Showing Paths ###################");
//		myProcess.getGraph().printPaths();		// Requesting graph from class as an example - you can use either way.
//	}
	
	/*************************************************************************************
	 * ***********************************************************************************
	 * ***********************************************************************************
	 * 
	 * 	Below is the remainder of the utility function. You should not need to edit below here.
	 * 
	 * ***********************************************************************************
	 * *************************************************************************************/
	
	
	/**
	 * Initialize some variables. We will allow up to five random variables for up to 3 possible words from the random range. 
	 */
	public int MIN_NUMBER_OF_NODES = 10;
	public int CHANCE_OF_GATEWAY = 5; // Roughly a 1 in 5 chance that next node will be a gateway

	// Stores sub process prefixes
	public String nameApp = "";
	
	// See graph.java - this is an extension of a simple directed graph
	private Graph<T,V> g ;

	
	
	public RandomProcessGenerator(Graph<T,V> inputGraph){
		g = inputGraph;//new Graph<T,V>(V.class);
	}

	
	public Graph<T,V> generateProcess(){
		return generateProcess(MIN_NUMBER_OF_NODES);
	}
	
	@SuppressWarnings({ "unchecked", "static-access" })	// Needed due to issue creating new generic variables.
	public Graph<T,V> generateProcess(int maxVertex){
		Vertex.ADD_RANDOM_EFFECTS = true;
		
		T v = null;
		// Create a random number of nodes to aim for based on the seed. Create a start and an end 
		// and continue:
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(maxVertex);
		if(randomInt < 3) randomInt += 3;
		
		
		T current = buildingBlock();
						
		randomInt -= 2;
		
		while(randomInt < maxVertex){	
			
			//////////
			// Randomly choose to make a sequence or a gateway:
			////////////////////////////////////////////////////
			int gateWay = randomGenerator.nextInt(CHANCE_OF_GATEWAY);
			if(gateWay < 1){
				// We make a gateway
				// XOR or Parallel?
				int XOR = randomGenerator.nextInt(2);
				XOR = randomGenerator.nextInt(2);
				XOR = randomGenerator.nextInt(2);
				T endNode =  getFirstNextNode(current);
				if(XOR < 1){
					// XOR GATEWAY
					current = MakeGateway(current, endNode, _GTYPE.XOR);
				}else{
					// Parallel Gateway
					current = MakeGateway(current, endNode, _GTYPE.AND);
				}
				randomInt += 2; // We have 2 extra nodes now ;)
				T startGate = current;
				T endGate = getFirstNextNode(current);
				int paths =  randomGenerator.nextInt( randomInt / 2 > 2 ? randomInt / 2 : 2 ); 
				paths = paths > 2? paths : 2;
				for(int i = 0; i < paths; i++){				
					
					int used = SubNodeSequence(current, endGate, randomInt / paths  );
					randomInt+=used;
				}	// If you want to add some complexity you can burrow into this.  
				if(g.containsEdge(startGate, endGate)) g.removeEdge(startGate, endGate);
				current = endGate; // Goto the gateway close

			}else{
				// We make a sequence
				v = (T) ((Vertex)v).createNew(); ((Vertex)v).name = nameApp + ((Vertex)v).name;		// Really bad creation hack
				g.addVertex(v);
				T next = getFirstNextNode(current);
				NodeSequence(current, next, v);	// Simple huh?	
				current = v; // move forward in the graph
				randomInt++;
			}
		}

		return g;

	}
	
	/**
	 * Get next node in the line (note this throws away alternative gateway paths)
	 * @param current
	 * @return
	 */
	public T getFirstNextNode(T current){
		EdgeReversedGraph<T,V> EG = new EdgeReversedGraph<T,V>(g);
		T result = null;
		for(V e: g.outgoingEdgesOf(current)){
			T n = EG.getEdgeSource(e); // This is weird?
			result = n;
			return result;
		}
		return result;
	}
	
	/**
	 * Get previous node to the current. (This does not provide an iterator and should not be used on gateways).
	 * @param current
	 * @return
	 */
	public T getFirstPrevNode(T current){
		EdgeReversedGraph<T, V> EG = new EdgeReversedGraph<T, V>(g);
		T result = null;		
		for(V e: g.incomingEdgesOf(current)){
			T n = EG.getEdgeTarget(e); // This is weird?
			result = n;
			return result;
		}
		return result;
	}


	/**
	 * Create a start and end node for a new graph
	 * @param g
	 * @return
	 */
	@SuppressWarnings({ "static-access", "unchecked" })
	public T buildingBlock(){
		T v = null;	// Dodgy creation hack
		T start = (T) ((Vertex)v).createNew(); ((Vertex)start).name = nameApp + "StartNode_" + ((Vertex)start).name;
		T end = (T) ((Vertex)v).createNew(); ((Vertex)end).name =  nameApp + "EndNode_" + ((Vertex)end).name;
		
		g.trueStart = start;
		g.trueEnd = end;
		
		g.addVertex(start);
		g.addVertex(end);
		g.addEdge(start,end);
		return start;
	}
	
	/**
	 * Add a gateway to the current graph after the current node.
	 * @param current
	 * @param g
	 * @param gateType
	 * @return The gateway opening node
	 */
	@SuppressWarnings({ "static-access", "unchecked" })
	public T MakeGateway(T start, T current, _GTYPE gateType){
		T v = null;	// Dodgy creation hack
		T newOpenGate = (T) ((Vertex)v).createNew(); ((Vertex)newOpenGate).name = nameApp + "gateOpen:" + ((Vertex)newOpenGate).name;
		T newCloseGate = (T) ((Vertex)v).createNew(); ((Vertex)newCloseGate).name = nameApp+ "gateClose:" + ((Vertex)newCloseGate).name;
		
		newOpenGate.setCorresponding(newCloseGate);
		
		if(gateType == _GTYPE.AND){
			newOpenGate.makeANDGate();
			newCloseGate.makeANDGate();
		}else{
			newOpenGate.makeXORGate();
			newCloseGate.makeXORGate();
		}

		// Copy the existing outgoing nodes of current node
		LinkedList<T> oldOutNodes = new LinkedList<T>();
		LinkedList<V> oldOutEdges = new LinkedList<V>();
		
		for(V e: g.outgoingEdgesOf(start)){
			oldOutNodes.add(g.getEdgeTarget(e));
			oldOutEdges.add(e);
		}
		
		for(V e: oldOutEdges){
			g.removeEdge(e);
		}
		
		g.addVertex(newOpenGate);
		g.addVertex(newCloseGate);
		
		g.addEdge(start, newOpenGate);
		g.addEdge(newOpenGate,newCloseGate);
		g.addEdge(newCloseGate,current);
		
		for(T o: oldOutNodes)
			g.addEdge(newCloseGate,o);

		return newOpenGate;
	}
	
	public int countNodes(){
		return g.vertexSet().size();
	}
	public T getTrueStart(){return g.trueStart; }
	public T getTrueEnd(){return g.trueEnd; }
	public Graph<T,V> getGraph(){return g; }

	public int SubNodeSequence(T current, T next, int maxVertex ){
		V e = g.getEdge(current, next);
		int numberNodesUsed = 0;
		if(e != null && g.containsEdge(e)){
			if( ((Vertex)current).type == _TYPE.NODE){
				g.removeEdge(e);
			}
			RandomProcessGenerator<T,V> subSequence = new RandomProcessGenerator<T,V>(new Graph<T,V>());
			subSequence.nameApp = nameApp + "Sub";
			subSequence.generateProcess(maxVertex);
			if(!g.containsVertex(current) ) g.addVertex(current);
			for(T v: subSequence.getGraph().vertexSet()){
				g.addVertex(v);
			}
			for(V se: subSequence.getGraph().edgeSet()){
				g.addEdge(subSequence.getGraph().getEdgeSource(se), subSequence.getGraph().getEdgeTarget(se));
			}
			
			g.addEdge(current,subSequence.getTrueStart());
			g.addEdge(subSequence.getTrueEnd(),next);
			numberNodesUsed = subSequence.countNodes();
		}
		return numberNodesUsed;
	}

	
	/**
	 * Insert a new node into the graph between the current and the next vertices. This will remove the edge between current and next. 
	 * @param current Current node
	 * @param next Next in sequence
	 * @param g The graph we are changing
	 * @param newNode The node to insert between current and next
	 */
	public void NodeSequence(T current, T next, T newNode){
		V e = g.getEdge(current, next);
		if(e != null && g.containsEdge(e)){
			if(current.type == _TYPE.NODE){
				g.removeEdge(e);
			}
			if(!g.containsVertex(current) ) g.addVertex(current);
			if(!g.containsVertex(newNode) ) g.addVertex(newNode);
			g.addEdge(current,newNode);
			g.addEdge(newNode,next);
		}
	}



	
}
