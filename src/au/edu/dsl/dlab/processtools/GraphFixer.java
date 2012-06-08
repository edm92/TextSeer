package au.edu.dsl.dlab.processtools;

/***************************
 * 
 * Function used to fix bad graphs, and also to create paths for scenarios
 * 
 * 
 */



import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.KShortestPaths;
import au.edu.dsl.dlab.processtools.Edge;
import au.edu.dsl.dlab.processtools.Graph;
import au.edu.dsl.dlab.processtools.Vertex;

public class GraphFixer {
	
	public boolean getScenarioPaths(Graph<Vertex,Edge> in){

		
		for(Vertex v: in.vertexSet()){
			if(!v.name.contains("+complete")) v.name += "+complete";
		}
		
		in.cleanup(false);
		if(in.trueEnd != null){
			Vertex newEnd = new Vertex("end+complete");
			in.addVertex(newEnd);
			in.addEdge(in.trueEnd, newEnd);
			in.cleanup(false);
		}
		
		while(fixOpenScenarioPaths(in)){
			in.cleanup(false);
			break;
		}
		
		while(fixCloseScenarioPaths(in)){
			in.cleanup(false);
			break;
		}
		
		
		
		return true;
	}
	
	public boolean fixOpenScenarioPaths(Graph<Vertex,Edge> in){
		boolean modifiedGraph = false;
		TreeMap<String, Vertex> openGateways = new TreeMap<String, Vertex>();
		TreeMap<String, Vertex> closeGateways = new TreeMap<String, Vertex>();
		TreeMap<String, LinkedList<GraphPath<Vertex,Edge>>> appearsIn = new TreeMap<String, LinkedList<GraphPath<Vertex,Edge>>>();
//		LinkedList<Graph<Vertex,Edge>> results = new LinkedList<Graph<Vertex,Edge>>();
		TreeMap<String, LinkedList<String>> matchingCloses = new TreeMap<String,LinkedList<String>>();
		
		// Get all gateways
		for(GraphPath<Vertex,Edge> gp : in.getPaths(in.trueStart, in.trueEnd)){
			Vertex s = gp.getStartVertex();
        	if(s.isGate()){
        		if(in.inDegreeOf(s) > 1) {// Join 
        			if(!closeGateways.containsKey(s.name)){
        				closeGateways.put(s.name,s);
        				if(!appearsIn.containsKey(s.name)) {
        					appearsIn.put(s.name, new LinkedList<GraphPath<Vertex,Edge>>());
        				}        				
        				if(!appearsIn.get(s.name).contains(gp)){
        					appearsIn.get(s.name).add(gp);
        				}
        			}
        		}else{	// Split
        			if(!openGateways.containsKey(s.name)){
        				openGateways.put(s.name,s);
        				if(!appearsIn.containsKey(s.name)) {
        					appearsIn.put(s.name, new LinkedList<GraphPath<Vertex,Edge>>());
        				}        				
        				if(!appearsIn.get(s.name).contains(gp)){
        					appearsIn.get(s.name).add(gp);
        				}
        			}      			
        		}
        	}
        	for(Edge e : gp.getEdgeList()){        	
        		s = in.getEdgeTarget(e);
            	if(s.isGate()){
            		if(in.inDegreeOf(s) > 1) {// Join 
            			if(!closeGateways.containsKey(s.name)){
            				closeGateways.put(s.name,s);
            				if(!appearsIn.containsKey(s.name)) {
            					appearsIn.put(s.name, new LinkedList<GraphPath<Vertex,Edge>>());
            				}        				
            				if(!appearsIn.get(s.name).contains(gp)){
            					appearsIn.get(s.name).add(gp);
            				}
            			}
            		}else{	// Split
            			if(!openGateways.containsKey(s.name)){
            				openGateways.put(s.name,s);
            				if(!appearsIn.containsKey(s.name)) {
            					appearsIn.put(s.name, new LinkedList<GraphPath<Vertex,Edge>>());
            				}        				
            				if(!appearsIn.get(s.name).contains(gp)){
            					appearsIn.get(s.name).add(gp);
            				}
            			}      			
            		}
            	}
        	}
		}
		
		// Go through open gates and look for matching closes
		for(String openName: openGateways.keySet()){
			String currentBest = "";
			int mostMatched = 0;
			for(String closeName : closeGateways.keySet()){
				LinkedList<GraphPath<Vertex,Edge>> sharedAppears = new LinkedList<GraphPath<Vertex,Edge>>();
				for(GraphPath<Vertex,Edge> g : appearsIn.get(openName)){
					if(appearsIn.get(closeName).contains(g)) sharedAppears.add(g);
				}
				//System.err.println("Testing " + openName + " with " + closeName);
								
				if(sharedAppears.size() == appearsIn.get(closeName).size() && appearsIn.get(closeName).size() == appearsIn.get(openName).size()){
					if(matchingCloses.get(openName) == null) matchingCloses.put(openName, new LinkedList<String>());
					matchingCloses.get(openName).add(closeName);
				}else{
					if(appearsIn.get(closeName).size() >= mostMatched){
						if(appearsIn.get(closeName).size() > mostMatched) currentBest = "";
						mostMatched = appearsIn.get(closeName).size();
						currentBest += closeName + "!;";
					}
				}
			}
			// Match any non-matched gateways
			if(matchingCloses.get(openName) == null || matchingCloses.get(openName).size() < 1){
				LinkedList<String> nextBestMatch = new LinkedList<String>();
				if(currentBest.contains("!;")){
					StringTokenizer name = new StringTokenizer(currentBest, "!;");
					while(name.hasMoreTokens())
						nextBestMatch.add(name.nextToken().trim());
				}else
					nextBestMatch.add(currentBest);
					
				matchingCloses.put(openName, nextBestMatch);
				
			}
			
		}
					
		// Compare corresponding gateways with matchingCloses
		for(String openName: openGateways.keySet()){
			if(openGateways.get(openName).CorrespondingVertex != null){
				if(!matchingCloses.keySet().contains(openName))
					matchingCloses.put(openName, new LinkedList<String>());
				if(!matchingCloses.get(openName).contains(openGateways.get(openName).CorrespondingVertex.name)){
					matchingCloses.get(openName).add(openGateways.get(openName).CorrespondingVertex.name);
				}
			}
		}
		
		// If there are any doubles in matchingCloses we need to create a new gateway
		for(String opening : matchingCloses.keySet()){
			if(matchingCloses.get(opening).size()>1){
				LinkedList<Vertex> gates = new LinkedList<Vertex>();
				for(String s: matchingCloses.get(opening)){
					gates.add(closeGateways.get(s));
				}
				LinkedList<Vertex> furthestGateways = in.findFurthestPairwise(gates);
				//System.err.println("Going to try making a gateway before : " + furthestGateways);
				for(Vertex makeBefore : furthestGateways){
					// figure out which path we're on
					
					Vertex myCloseGate = new Vertex("CGATE"+makeBefore.name+(makeBefore.isXOR() ? "XOR" : "AND" )+"-JOIN");
					if(makeBefore.isXOR()) myCloseGate.makeXORGate();
					else myCloseGate.makeANDGate();
					
					in.addVertex(myCloseGate);
					in.addEdge(myCloseGate, makeBefore);
					
					for(String doubleOpen : matchingCloses.keySet()){
						//System.err.println("Corrsponding of " + doubleOpen + " is " + openGateways.get(doubleOpen).CorrespondingVertex );
						if(openGateways.get(doubleOpen).CorrespondingVertex == makeBefore) continue;
						// Get all paths that go from doubleOpen to make before
						//System.err.println("Get all paths from " + doubleOpen + " to " + makeBefore.name);
						KShortestPaths<Vertex, Edge> sp = new KShortestPaths<Vertex, Edge>(in, openGateways.get(doubleOpen), Graph.MAX_PATH_LENGTH);
						List<GraphPath<Vertex, Edge>> shortestPath = sp.getPaths(makeBefore);
						if(shortestPath == null) continue;
						modifiedGraph = true;
						
						for(GraphPath<Vertex,Edge> gp : shortestPath){
							// Walk along graph till last then 
							for(Edge e: gp.getEdgeList()){
								if(in.getEdgeTarget(e) == makeBefore){
									Vertex src = in.getEdgeSource(e);
									in.removeEdge(e);
									in.addEdge(src, myCloseGate);
								}
							}
						}
					}
					myCloseGate.name = "CG" + openGateways.get(opening).name.replaceAll("XOR-SPLIT","").replaceAll("AND-SPLIT","") + (makeBefore.isXOR() ? "XOR" : "AND" )+"-JOIN";
					myCloseGate.setCorresponding(openGateways.get(opening));
				}
				
			}
		}
		
		
//		System.err.println("matchingCloses:" + matchingCloses);
//		System.err.println("opening gates:" + openGateways);
//		System.err.println("closing gates:" + closeGateways);
		
		
		return modifiedGraph;
	}
	
	
	
	public boolean fixCloseScenarioPaths(Graph<Vertex,Edge> in){
		boolean modifiedGraph = false;
		TreeMap<String, Vertex> openGateways = new TreeMap<String, Vertex>();
		TreeMap<String, Vertex> closeGateways = new TreeMap<String, Vertex>();
		TreeMap<String, LinkedList<GraphPath<Vertex,Edge>>> appearsIn = new TreeMap<String, LinkedList<GraphPath<Vertex,Edge>>>();
//		LinkedList<Graph<Vertex,Edge>> results = new LinkedList<Graph<Vertex,Edge>>();
		TreeMap<String, LinkedList<String>> matchingCloses = new TreeMap<String,LinkedList<String>>();
		
		// Get all gateways
		for(GraphPath<Vertex,Edge> gp : in.getPaths(in.trueStart, in.trueEnd)){
			Vertex s = gp.getStartVertex();
        	if(s.isGate()){
        		if(in.inDegreeOf(s) > 1) {// Join 
        			if(!closeGateways.containsKey(s.name)){
        				closeGateways.put(s.name,s);
        				if(!appearsIn.containsKey(s.name)) {
        					appearsIn.put(s.name, new LinkedList<GraphPath<Vertex,Edge>>());
        				}        				
        				if(!appearsIn.get(s.name).contains(gp)){
        					appearsIn.get(s.name).add(gp);
        				}
        			}
        		}else{	// Split
        			if(!openGateways.containsKey(s.name)){
        				openGateways.put(s.name,s);
        				if(!appearsIn.containsKey(s.name)) {
        					appearsIn.put(s.name, new LinkedList<GraphPath<Vertex,Edge>>());
        				}        				
        				if(!appearsIn.get(s.name).contains(gp)){
        					appearsIn.get(s.name).add(gp);
        				}
        			}      			
        		}
        	}
        	for(Edge e : gp.getEdgeList()){        	
        		s = in.getEdgeTarget(e);
            	if(s.isGate()){
            		if(in.inDegreeOf(s) > 1) {// Join 
            			if(!closeGateways.containsKey(s.name)){
            				closeGateways.put(s.name,s);
            				if(!appearsIn.containsKey(s.name)) {
            					appearsIn.put(s.name, new LinkedList<GraphPath<Vertex,Edge>>());
            				}        				
            				if(!appearsIn.get(s.name).contains(gp)){
            					appearsIn.get(s.name).add(gp);
            				}
            			}
            		}else{	// Split
            			if(!openGateways.containsKey(s.name)){
            				openGateways.put(s.name,s);
            				if(!appearsIn.containsKey(s.name)) {
            					appearsIn.put(s.name, new LinkedList<GraphPath<Vertex,Edge>>());
            				}        				
            				if(!appearsIn.get(s.name).contains(gp)){
            					appearsIn.get(s.name).add(gp);
            				}
            			}      			
            		}
            	}
        	}
		}
		
		// Go through close gates and look for opens closes
		for(String closeName: closeGateways.keySet()){
			String currentBest = "";
			int mostMatched = 0;
			for(String openName : openGateways.keySet()){
				LinkedList<GraphPath<Vertex,Edge>> sharedAppears = new LinkedList<GraphPath<Vertex,Edge>>();
				for(GraphPath<Vertex,Edge> g : appearsIn.get(closeName)){
					if(appearsIn.get(openName).contains(g)) sharedAppears.add(g);
				}
				//System.err.println("Testing " + openName + " with " + closeName);
								
				if(sharedAppears.size() == appearsIn.get(openName).size() && appearsIn.get(openName).size() == appearsIn.get(closeName).size()){
					if(matchingCloses.get(closeName) == null) matchingCloses.put(openName, new LinkedList<String>());
					matchingCloses.get(openName).add(openName);
				}else{
					if(appearsIn.get(openName).size() >= mostMatched){
						if(appearsIn.get(openName).size() > mostMatched) currentBest = "";
						mostMatched = appearsIn.get(openName).size();
						currentBest += openName + "!;";
					}
				}
			}
			// Match any non-matched gateways
			if(matchingCloses.get(closeName) == null || matchingCloses.get(closeName).size() < 1){
				LinkedList<String> nextBestMatch = new LinkedList<String>();
				if(currentBest.contains("!;")){
					StringTokenizer name = new StringTokenizer(currentBest, "!;");
					while(name.hasMoreTokens())
						nextBestMatch.add(name.nextToken().trim());
				}else
					nextBestMatch.add(currentBest);
					
				matchingCloses.put(closeName, nextBestMatch);
				
			}
			
		}
					
		// Compare corresponding gateways with matchingCloses
//		for(String closeName: closeGateways.keySet()){
//			if(closeGateways.get(closeName).CorrespondingVertex != null){
//				if(!matchingCloses.keySet().contains(closeName))
//					matchingCloses.put(closeName, new LinkedList<String>());
//				if(!matchingCloses.get(closeName).contains(closeGateways.get(closeName).CorrespondingVertex.name)){
//					matchingCloses.get(closeName).add(closeGateways.get(closeName).CorrespondingVertex.name);
//				}
//			}
//		}
		
		LinkedList<String> removal = new LinkedList<String>();
		for(String name : matchingCloses.keySet()){
			if(!closeGateways.keySet().contains(name))
				removal.add(name);
		}
		for(String name : removal)
			matchingCloses.remove(name);
		
		
		
		
		// If there are any doubles in matchingCloses we need to create a new gateway
		for(String closeing : matchingCloses.keySet()){
			if(matchingCloses.get(closeing).size()>1){
				LinkedList<Vertex> gates = new LinkedList<Vertex>();
				for(String s: matchingCloses.get(closeing)){
					gates.add(openGateways.get(s));
				}
				LinkedList<Vertex> closestGateways = in.findFurthestPairwise(gates);
				//System.err.println("Going to try making a gateway after : " + closestGateways + " to fix " + closeing);
				for(Vertex makeAfter : closestGateways){
					// figure out which path we're on
					
					Vertex myOpenGate = new Vertex("OGATE"+makeAfter.name+(makeAfter.isXOR() ? "XOR" : "AND" )+"-JOIN");
					if(makeAfter.isXOR()) myOpenGate.makeXORGate();
					else myOpenGate.makeANDGate();
					
					in.addVertex(myOpenGate);
					in.addEdge(makeAfter, myOpenGate);
					// Get all paths that go from doubleOpen to make before
					KShortestPaths<Vertex, Edge> sp = new KShortestPaths<Vertex, Edge>(in, makeAfter, Graph.MAX_PATH_LENGTH);
					List<GraphPath<Vertex, Edge>> shortestPath = sp.getPaths(closeGateways.get(closeing));
					if(shortestPath == null) continue;
					modifiedGraph = true;

					//System.err.println("Results between " + makeAfter.name + " and " + closeing + " " + shortestPath);
					for(GraphPath<Vertex,Edge> gp : shortestPath){
						
						// Walk along graph till last then 
						for(Edge e: gp.getEdgeList()){
							if(in.getEdgeSource(e) == makeAfter){
								//System.err.println("Adding vertex after " + in.getEdgeSource(e));
								Vertex trg = in.getEdgeTarget(e);
								in.removeEdge(e);
								in.addEdge(myOpenGate, trg);
							}
						}
					}
					myOpenGate.name = "OG" + closeGateways.get(closeing).name.replaceAll("XOR-JOIN","").replaceAll("AND-JOIN","") + (makeAfter.isXOR() ? "XOR" : "AND" )+"-SPLIT";
					myOpenGate.setCorresponding(closeGateways.get(closeing));
				}
				
			}
		}
		
		
//		System.err.println("matchingCloses:" + matchingCloses);
//		System.err.println("opening gates:" + openGateways);
//		System.err.println("closing gates:" + closeGateways);
		
		
		return modifiedGraph;
	}

	
	
	
}
