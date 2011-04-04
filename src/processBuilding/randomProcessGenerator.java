package processBuilding;

import java.util.LinkedList;
import java.util.Random;

import textSeer.Model.Effect;
import textSeer.Model.Gateway;
import textSeer.Model.Graph;
import textSeer.Model.Predicate;
import textSeer.Model.SequenceEdge;
import textSeer.Model.Vertex;


public class randomProcessGenerator {
	public static int MAX_RANDOM_PREDICATES = 5;
	public static int MAX_RANDOM_PREDICATE_RANGE = 5;
	public static String RANDOM_RANGE[] = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
		"l", "m", "n"};
	
	public static void CREATE_RANDOM_RANGE(){
		RANDOM_RANGE[0] = "a";
		RANDOM_RANGE = new String[17577];
		for(int i = 0; i < 26; i++){
			for(int j = 0; j < 26; j++){
				for(int k = 0; k < 26; k++){
					RANDOM_RANGE[(26*26*i) + (26*j) + k] = "" + Character.toChars(i+97)[0] + Character.toChars(j+97)[0] + Character.toChars(k+97)[0];
					//System.out.println((26*26*i) + (26*j) + k + " = " + RANDOM_RANGE[(26*26*i) + (26*j) + k] );
				}
				
			}
		}
	}
	
	public static process generateProcess(int maxVertex, int maxPredicates){
		MAX_RANDOM_PREDICATES = maxPredicates;
		process returnProcess = new process();
		Vertex v;
		
		// Create a random number of nodes to aim for based on the seed. Create a start and an end 
		// and continue:
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(maxVertex);
		if(randomInt < 3) randomInt += 3;
		Graph g = new Graph();
		Vertex start = buildingBlock(g);
		Vertex next = start.outNodes.get(0);
		randomInt -= 2;
		
		while(randomInt > 0){
			//////////
			// Randomly choose to make a sequence or a gateway:
			////////////////////////////////////////////////////
			int gateWay = randomGenerator.nextInt(2);
			if(gateWay < 1){
				// We make a gateway
				// XOR or Parallel?
				int XOR = randomGenerator.nextInt(2);
				if(XOR < 1){
					// XOR GATEWAY
					start = MakeGateway(start, g, Gateway.gatetype.XOR);
				}else{
					// Parallel Gateway
					start = MakeGateway(start, g, Gateway.gatetype.AND);
				}
				randomInt -= 2; // We have 2 extra nodes now ;)
				
				int paths =  randomGenerator.nextInt( (randomInt / 2) > 1? randomInt/2:2 );
				paths = paths > 2? paths : 2;
				
				next = start.outNodes.get(0);
				for(int i = 0; i < paths; i++){
					v = new Vertex(g);	
					if( randomGenerator.nextInt(2) > 0)
						v.addEffect(generateEffects());
					
					NodeSequence(start, next, g, v);
					randomInt--;
				}	// If you want to add some complexity you can burrow into this. 
				RemoveGateNodeRef(g);
				
			}else{
				// We make a sequence
				v = new Vertex(g);
				if( randomGenerator.nextInt(2) > 0)
					v.addEffect(generateEffects());
				
				next = start.outNodes.get(0);
				NodeSequence(start, next, g, v);	// Simple huh?	
				start = v; // move forward in the graph
				randomInt--;
			}
			
			
//			v = new Vertex(g);
//			NodeSequence(start, next, g, v);
//			start = MakeGateway(v, g, Gateway.gatetype.XOR);
//			
//			v = new Vertex(g);
//			next = start.outNodes.get(0);
//			NodeSequence(start, next, g, v);
//			v = new Vertex(g);
//			NodeSequence(start, next, g, v);
			
			
			
		}
		
		
		returnProcess.structure = g;
		return returnProcess;
		
	}
	
	public static void RemoveGateNodeRef(Graph G){
		for(Vertex n : G.allNodes){
			if(n.getClass().equals(Gateway.class)){
				LinkedList<Vertex> remove = new LinkedList<Vertex>();
				for(Vertex v: n.outNodes)
					if(v.getClass().equals(Gateway.class)){
						remove.add(v);
					}
				for(Vertex v: remove){
					n.outNodes.remove(v);
				}
				remove = new LinkedList<Vertex>();
				for(Vertex v: n.inNodes)
					if(v.getClass().equals(Gateway.class)){
						remove.add(v);
					}
				for(Vertex v: remove){
					n.inNodes.remove(v);
				}
				
			}
		}
	}
	
	public static Vertex buildingBlock(Graph g){
		Vertex start = new Vertex(g); start.name = "StartNode_" + start.name;
		Vertex end = new Vertex(g); end.name = "EndNode_" + end.name;
		SequenceEdge e = new SequenceEdge(g);
		e.addSource(start);
		e.addTarget(end);
		e.finalize();
		g.addNode(start);
		g.addNode(end);
		g.addEdge(e);
		g.finalize();
		
		return start;
	}
	
	public static Vertex MakeGateway(Vertex current, Graph g, Gateway.gatetype gateType){
		Gateway newOpenGate = new Gateway(g); newOpenGate.name = "gateOpen" + newOpenGate.name;
		newOpenGate.type = gateType;
		Gateway newCloseGate = new Gateway(g); newCloseGate.name = "gateClose" + newCloseGate.name;
		newCloseGate.type = gateType;
		
		// Copy outnodes
		LinkedList<Vertex> oldOutNodes = new LinkedList<Vertex>();
		LinkedList<SequenceEdge> oldOutEdges = new LinkedList<SequenceEdge>();
		for(Vertex outNode: current.outNodes){
			oldOutNodes.add(outNode);
			outNode.inNodes.remove(current);
			outNode.inNodes.add(newCloseGate);
			newCloseGate.outNodes.add(outNode);
		}
		for(Vertex outNode: oldOutNodes){
			current.outNodes.remove(outNode);
		}
		for(SequenceEdge outEdge: current.outEdges){
			oldOutEdges.add(outEdge);
			outEdge.addSource(newCloseGate);
			newCloseGate.outEdges.add(outEdge);
		}
		// Cleanup
		for(SequenceEdge e: oldOutEdges){
			current.outEdges.remove(e);
			//e.finalize();
		}
		
		current.outNodes.add(newOpenGate);
		newOpenGate.inNodes.add(current);
		newOpenGate.outNodes.add(newCloseGate);
		newCloseGate.inNodes.add(newOpenGate);
		SequenceEdge e = new SequenceEdge(g);
		e.addSource(current);
		e.addTarget(newOpenGate);
		current.outEdges.add(e);
		e.finalize();
		g.addEdge(e);
		g.addNode(newOpenGate);
		g.addNode(newCloseGate);
		g.finalize();
		
		
		return newOpenGate;
	}
	
	public static void NodeSequence(Vertex current, Vertex next, Graph g, Vertex newNode){
		
		
		current.outNodes.add(newNode);
		newNode.inNodes.add(current);
		newNode.outNodes.add(next);
		next.inNodes.add(newNode);
		current.outNodes.remove(next);
		next.inNodes.remove(current);
		LinkedList<SequenceEdge> remove = new LinkedList<SequenceEdge>();
		for(SequenceEdge en : current.outEdges){
			if(en.source == current)
				remove.add(en);
		}
		for(SequenceEdge en : remove){
			current.outEdges.remove(en);
			g.edges.remove(en);
		}
		
		remove = new LinkedList<SequenceEdge>();
		for(SequenceEdge en : next.inEdges){
			if(en.target == next)
				remove.add(en);
		}
		for(SequenceEdge en : remove){
			next.inEdges.remove(en);
		}
		
		SequenceEdge e = new SequenceEdge(g);
		SequenceEdge e2 = new SequenceEdge(g);
		e.addSource(current);
		e.addTarget(newNode);
		e2.addSource(newNode);
		e2.addTarget(next);

		current.outEdges.add(e);
		newNode.outEdges.add(e2);
		newNode.inEdges.add(e);
		next.inEdges.add(e2);
		e.finalize();e2.finalize();
		g.addNode(newNode);
		g.addEdge(e);
		g.addEdge(e2);
		g.finalize();
		
		//return newNode;
	}
	
	
	public static Effect generateEffects(){
		boolean used[] = new boolean[MAX_RANDOM_PREDICATES];	// Need better implementation of this
		Effect returnEffect = new Effect();
		Random randomGenerator = new Random();
		String predicate = "";
		int numPredicates = randomGenerator.nextInt(MAX_RANDOM_PREDICATES);
		for(int i = 0; i < numPredicates; i++){
			int ChoosePredicate = randomGenerator.nextInt(MAX_RANDOM_PREDICATE_RANGE);
			if(!used[ChoosePredicate]){
				used[ChoosePredicate] = true;
				predicate += RANDOM_RANGE[ChoosePredicate] + "(x) & ";
			}
		}
		if(predicate.length() > 3){ 
			predicate = predicate.substring(0, predicate.length() - 3); // Remove final &
			returnEffect.addPredicate(new Predicate(predicate, true));
		}
		
		
		return returnEffect;
		
	}
	
}
