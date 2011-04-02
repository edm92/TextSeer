package processBuilding;

import java.util.LinkedList;
import java.util.Random;

import textSeer.Model.Effect;
import textSeer.Model.Gateway;
import textSeer.Model.Predicate;
import textSeer.Model.SequenceEdge;
import textSeer.Model.Vertex;


public class randomProcessGenerator {
	public static int MAX_RANDOM_PREDICATES = 5;
	public static int MAX_RANDOM_PREDICATE_RANGE = 5;
	public static String RANDOM_RANGE[] = { "a", "b", "c", "d", "e" };
	
	public static process generateProcess(int maxVertex){
		process returnProcess = new process();
		
		
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(maxVertex);
		if(randomInt < 3) randomInt += 3;
		
		for(int i = 0 ; i < randomInt; i++){
			Vertex node = new Vertex(returnProcess.structure);
			int addeffects = randomGenerator.nextInt(2);
			Effect e = null;
			if(addeffects > 0){
				e = generateEffects();
				node.addEffect(e);
			}
			returnProcess.structure.addNode(node);
		}
		
		std.calls.showResult("Random nodes:" + returnProcess.structure.allNodes.size() + std.string.endl);
		
		//LinkedList<Vertex> nodes = returnProcess.structure.allNodes;
		LinkedList<Vertex> addLast = new LinkedList<Vertex>();
		Vertex current = null;
		for(int j = 0; j  <  returnProcess.structure.allNodes.size(); j++){
			if(j + 2 < returnProcess.structure.allNodes.size()){
				int split = randomGenerator.nextInt(2);
				Vertex next = null;
				if(split > 0){
					int XOR = randomGenerator.nextInt(2);
					if(XOR > 0 ){
						Gateway node = new Gateway(returnProcess.structure);
						next = new Gateway(returnProcess.structure);
						node.type = Gateway.gatetype.XOR;
						((Gateway)next).type = Gateway.gatetype.XOR;
						addLast.add(node);
						addLast.add(next);
						current = node;
					}else{
						Gateway node = new Gateway(returnProcess.structure);
						next = new Gateway(returnProcess.structure);
						node.type = Gateway.gatetype.AND;
						((Gateway)next).type = Gateway.gatetype.AND;
						addLast.add(node);
						addLast.add(next);
						current = node;
					}
					//j -= 1;
					int SPLIT_TO = randomGenerator.nextInt(2);
					SPLIT_TO += 1;
					for(int i = 1; i < SPLIT_TO; i++){
						SequenceEdge e = new SequenceEdge(returnProcess.structure);
						e.addSource(current);
						e.addTarget(returnProcess.structure.allNodes.get(j+SPLIT_TO));
						SequenceEdge e2 = new SequenceEdge(returnProcess.structure);
						e2.addSource(returnProcess.structure.allNodes.get(j+SPLIT_TO));
						e2.addTarget(next);
						
						j = j+SPLIT_TO;
						current = next;
						e2.finalize();
						e.finalize();
						returnProcess.structure.addEdge(e);
						returnProcess.structure.addEdge(e2);
					}
				}
				else
					if(j + 1 < returnProcess.structure.allNodes.size() ){
						if(current != null){
							SequenceEdge e = new SequenceEdge(returnProcess.structure);
							e.addSource(current);
							e.addTarget(returnProcess.structure.allNodes.get(j));
							e.finalize();
							returnProcess.structure.addEdge(e);
							std.calls.display("Added edge");
						}else
							current = returnProcess.structure.allNodes.get(j);
					}
			}
			
			for(Vertex v:addLast){
				returnProcess.structure.addNode(v);
			}
			returnProcess.structure.finalize();
			
			
		}
		
		return returnProcess;
		
	}
	
	public static Effect generateEffects(){
		boolean used[] = {false,false,false,false,false};	// Need better implementation of this
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
