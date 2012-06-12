package au.edu.dsl.dlab.processtools;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import orbital.logic.sign.Signature;
import orbital.logic.sign.Symbol;

import org.apache.log4j.Logger;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.BellmanFordShortestPath;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.GraphPathImpl;

import au.edu.dsl.dlab.collections.functions.OrderConstrainedPartitionList;
import au.edu.dsl.dlab.collections.functions.PartitionList;
import au.edu.dsl.dlab.collections.functions.PartitionListElement;
import au.edu.dsl.dlab.collections.functions.PartitionListItem;
import au.edu.dsl.dlab.processtools.Vertex._GTYPE;
import au.edu.dsl.dlab.processtools.Vertex._TYPE;
import au.edu.dsl.dlab.processtools.logic.ClassicalLogicS;

/**
 * The following graph is a simple extension of the jGraph DefaultDirectedGraph. The idea is that any changes that need to be made to the type of 
 * graph used should go here. That way all other utility classes can remain as they are. 
 * @author edm92
 *
 */

public class Graph<T extends Vertex ,V extends Edge> extends DefaultDirectedGraph<T, V>{
	protected transient static Logger logger = Logger.getLogger("GraphClass");
	public static boolean SKIP_EMPTY_EFFECT_PATHS = true;
	public static final int MAX_PATH_LENGTH = 100; // Set higher if your graph isn't working out
	
	
	private static final long serialVersionUID = 1L;
	public HashSet<Effect> effects;
	public String filename = "";
	public String documentation = "";
	public String name = "";
	public String id = "";
	public String QOS = "";
	private transient UUID ID;
	public String getID() { return ID.toString(); };
	public TreeMap<String, String> effectMap = new TreeMap<String, String>();
	public int addition = 0;
	public char currentEffect = 'a';
	public T trueStart;
	public T trueEnd;
	
//	public Graph(){
//		this(V.getClass());
//	}
	public Graph(Class<V> arg0) {
		super(arg0);
	}
	
	@SuppressWarnings("unchecked")
	public Graph() {
		super((Class<? extends V>) Edge.class);
		ID = UUID.randomUUID();
	}

	public void printGraph(){
		System.out.println(this);
	}
	
	public List<GraphPath<T, V>> getPaths(){return getPaths(this.trueStart, this.trueEnd);	}
	
	// Add and paths here
	public List<GraphPath<T, V>> getPaths(T startPoint, T endPoint){
		//System.err.println("Printing path between " + startPoint + "; " + endPoint);
		KShortestPaths<T, V> sp = new KShortestPaths<T, V>(this, startPoint, MAX_PATH_LENGTH);
		List<GraphPath<T, V>> results = sp.getPaths(endPoint);
		HashSet<String> seenANDgates = new HashSet<String>();
		int effectsSeen = 0;
		if(results == null ) return null;
		for(GraphPath<T, V> vvv : results){
			// Trim Search to paths with effects here
			T currentVertex = vvv.getStartVertex();
			if(!seenANDgates.contains(currentVertex.getID()) && currentVertex.isAND() && 
					currentVertex.getID().compareTo(startPoint.getID()) != 0 && 
					currentVertex.CorrespondingVertex.getID().compareTo(startPoint.getID()) != 0)
			{	seenANDgates.add(currentVertex.getID()); }
			
			for(V ttt : vvv.getEdgeList()){	
				
				if(currentVertex.getEffect().getFormula().length() > 0) effectsSeen++;
				currentVertex = this.getEdgeTarget(ttt);

				if(!seenANDgates.contains(currentVertex.getID()) && currentVertex.isAND() && currentVertex.CorrespondingVertex != null &&
						currentVertex.getID().compareTo(startPoint.getID()) != 0 && 
						currentVertex.CorrespondingVertex.getID().compareTo(startPoint.getID()) != 0)
				{	seenANDgates.add(currentVertex.getID()); }
			}
			
		}
		int numberToFind = (seenANDgates.size()) ;
		//System.err.println("Number of AND pairs = " + seenANDgates.size() + " " + seenANDgates + " ;;;; " + effectsSeen);
		if(!SKIP_EMPTY_EFFECT_PATHS) effectsSeen = 0;
		if(numberToFind > 0 && effectsSeen < 1){
			//System.err.println("here");
			// look for AND gateways
			for(int i = 0; i < numberToFind; i++){
				LinkedList<GraphPath<T, V>> removeList = new LinkedList<GraphPath<T, V>>();
				LinkedList<GraphPath<T, V>> addList = new LinkedList<GraphPath<T, V>>();
				for(GraphPath<T,V> gp : results){
					LinkedList<String> processed = new LinkedList<String>();
					LinkedList<String> ignoreAdding = new LinkedList<String>();
					T currentVertex = gp.getStartVertex();
					//System.out.println("Looking at :" + gp);
					for(V v : gp.getEdgeList()){						
						//System.err.println(currentVertex + " : " + currentVertex.CorrespondingVertex);
						if(!processed.contains(v.name) && !processed.contains(currentVertex.name) && 
								currentVertex.isAND() && 
								currentVertex.getID().compareTo(startPoint.getID()) != 0 && 
								currentVertex.CorrespondingVertex.getID().compareTo(startPoint.getID()) != 0){
							if(!ignoreAdding.contains(currentVertex.getID())){
								List<GraphPath<T,V>> StartPaths = computeStartPaths(currentVertex);
								List<GraphPath<T,V>> newPaths = computeNewPaths(currentVertex);
								List<GraphPath<T,V>> EndPaths = computeEndPaths(currentVertex);
								List<GraphPath<T,V>> mergedPaths = mergePaths(StartPaths, newPaths, EndPaths );
								
								if(mergedPaths != null){
									removeList.add(gp);
									addList.addAll(mergedPaths);
								}
								ignoreAdding.add(currentVertex.getID());
							}else{ removeList.add(gp); };							
							if(currentVertex.CorrespondingVertex != null) 					// This will break everything if the gateways are not
								processed.add(currentVertex.CorrespondingVertex.getID());   // added to the graph in the correct order
						}
						processed.add(v.getID()); 
						currentVertex = this.getEdgeTarget(v);	
						//if(removeList.size() >0) break;
					}
					//if(removeList.size() >0) break;
				}
				//System.out.println("Checking if i need to remove anything: " + removeList.size() + " " + removeList);
				// Remove old paths
				for(GraphPath<T, V> gp : removeList) results.remove(gp);
				// Add new paths
				
				for(GraphPath<T, V> gp : addList) { results.add(gp);}
			}
		}
		//System.out.println("Returning: " + results + " for " + numberToFind);
		return results;
	}
	
	private List<GraphPath<T, V>> mergePaths(List<GraphPath<T, V>> startPaths,
			List<GraphPath<T, V>> newPaths, List<GraphPath<T, V>> endPaths) {
		List<GraphPath<T, V>> totallyWild = new LinkedList<GraphPath<T, V>>();
		for(GraphPath<T, V> starters : startPaths){
			if(newPaths == null) continue;
			for(GraphPath<T, V> newies : newPaths){
				for(GraphPath<T,V> enders: endPaths){
					//System.err.println("New path :" + newies + " adding to " + starters);
					GraphPath<T,V> part1 = joinGraphPath(starters, newies);
					GraphPath<T,V> part2 = joinGraphPath(part1, enders);
					totallyWild.add(part2);
				}
				
			}
		}
		return totallyWild;
	}

	private void buildGraphPathEdgeList(Graph<T,V> newGraph, List<V> newEdges, GraphPath<T, V> inputPath){
		Graph<T,V> currentGraph = (Graph<T, V>) inputPath.getGraph();
		for(V edgeTest : inputPath.getEdgeList()){	// Should only be one for each branch
			T source = currentGraph.getEdgeSource(edgeTest);
			T target = currentGraph.getEdgeTarget(edgeTest);
			newEdges.add(makeEdge(source, target));
			newGraph.addVertex(source);
			newGraph.addVertex(target);
			newGraph.addEdge(source, target);
		}

	}
	
	private V makeEdge(T source, T target){
		EGraph<T,V> myEdgeMaker = new EGraph<T,V>();
		V myEdge = (V) myEdgeMaker.createEdge(source, target);
		return myEdge;
	}
	
	@SuppressWarnings("unchecked")
	private GraphPath<T, V> joinGraphPath(GraphPath<T, V> starters, GraphPath<T, V> enders){
		EGraph<T,V> myGraphJoiner = new EGraph<T,V>();
		List<V> newEdges = new LinkedList<V>();
		List<V> newEdges2 = new LinkedList<V>();
		buildGraphPathEdgeList(myGraphJoiner.basicGraph ,newEdges, starters);
		V lastOne = (V) ((LinkedList<GraphPath<T, V>>) newEdges).getLast();
		buildGraphPathEdgeList(myGraphJoiner.basicGraph, newEdges2, enders);
		V lastOne2 = (V) ((LinkedList<GraphPath<T, V>>) newEdges2).getFirst();
		
		newEdges.addAll(newEdges2);
		
		T source = starters.getGraph().getEdgeTarget(lastOne);
		T target = enders.getGraph().getEdgeSource(lastOne2);
		newEdges.add(myGraphJoiner.basicGraph.addEdge(source, target));
		
		
		//(Graph<V,E> graph, V startVertex, V endVertex, java.util.List<E> edgeList, double weight) 
		GraphPath<T, V> result = new GraphPathImpl<T,V>(myGraphJoiner.basicGraph, starters.getStartVertex(), enders.getEndVertex(), newEdges, 0);
		
		//System.err.println("New GraphPath is:" + result);
		
		return result;
	}

	
	private List<GraphPath<T, V>> computeEndPaths(T currentVertex) {
		KShortestPaths<T, V> sp = new KShortestPaths<T, V>(this, currentVertex, MAX_PATH_LENGTH);
		List<GraphPath<T, V>> results = sp.getPaths(this.trueEnd);
		return results;
	}

	private List<GraphPath<T, V>> computeStartPaths(T currentVertex) {
		KShortestPaths<T, V> sp = new KShortestPaths<T, V>(this, this.trueStart, MAX_PATH_LENGTH);
		List<GraphPath<T, V>> results = sp.getPaths(currentVertex);
		return results;
	}

	@SuppressWarnings("unchecked")
	private List<GraphPath<T, V>> computeNewPaths(T startVertex) {
		List<GraphPath<T, V>> results = new LinkedList<GraphPath<T, V>>();
		TreeMap<String,V> edgeMap = new TreeMap<String,V>();
		PartitionList<T> oldConstrainedPartitionList = null;
		//System.out.println("Coming on " + startVertex);
		if( this.getPaths(startVertex, (T) startVertex.CorrespondingVertex) == null) return null;
		for(GraphPath<T, V> gp : this.getPaths(startVertex, (T) startVertex.CorrespondingVertex)){
			PartitionListElement<T> list = new PartitionListElement<T>();
			T currentVertex = gp.getStartVertex();
			//list.add(currentVertex);
			for(V v : gp.getEdgeList()){
				edgeMap.put(currentVertex.getID() + getEdgeTarget(v).getID(), v);
				currentVertex = this.getEdgeTarget(v);
				if(currentVertex.getID().compareTo(gp.getStartVertex().CorrespondingVertex.getID())!= 0)
					list.add(currentVertex);
			}
			PartitionList<T> constrainedPartitionList = OrderConstrainedPartitionList.makePartitions( list);
			if(oldConstrainedPartitionList != null){
				constrainedPartitionList = OrderConstrainedPartitionList.joinSets(constrainedPartitionList, oldConstrainedPartitionList);
			}
			oldConstrainedPartitionList = constrainedPartitionList;			
		}
		
		for(PartitionListItem<T> newPartition: oldConstrainedPartitionList){
			PartitionListElement<T> ele = newPartition.get(0);
			List<V> newEdges = new LinkedList<V>();
			EGraph<T,V> newGraph = new EGraph<T,V>();
			//ele.add((T) startVertex.CorrespondingVertex);
			T currentNode = startVertex;
			newGraph.basicGraph.addVertex(currentNode);
			for(T element : ele){
				newGraph.basicGraph.addVertex(element);
				if(startVertex.getID().compareTo(currentNode.getID()) != 0){
					EGraph myEdgeMaker = new EGraph<T,V>();
					V myEdge = (V) myEdgeMaker.createEdge(currentNode, element);
					newEdges.add(myEdge);
					newGraph.basicGraph.addEdge(currentNode, element);
				};
				currentNode = element;
			}

			//System.out.println("New Graph edges: " + newEdges);
			GraphPath<T, V> newGraphPath = new GraphPathImpl<T, V>(newGraph.basicGraph, startVertex, (T)startVertex.CorrespondingVertex, newEdges, 0);
			
			results.add(newGraphPath);
		}
		//startVertex.makeAANDGate();
		if(results.size() > 0)
			return results;
		return null;
	}
	

	
	public void printScenarioPaths(){
		HashSet<String> paths = new HashSet<String>();
		for(GraphPath<T, V> gp : getPaths()){
			LinkedList<String> viewedNode = new LinkedList<String>();
        	T s = gp.getStartVertex();
        	String currentPath = s + " -> "; 
        	for(V e : gp.getEdgeList()){
        		if(!viewedNode.contains(this.getEdgeTarget(e).name)){
        			currentPath += this.getEdgeTarget(e) + " -> ";
        			viewedNode.add(this.getEdgeTarget(e).name);
        		}
        	}
        	if(currentPath.length() > 3) currentPath = currentPath.substring(0, currentPath.length()-3);
        	paths.add(currentPath);
        	//System.out.println("Path:" + currentPath);
		}
		
		for(String path : paths){
			System.out.println("Path: " + path);
		}
	}
	
	public void printPaths(){
		if(this.trueStart == null || this.trueEnd == null) { System.out.println("No paths found"); return;}
		// Print all paths through the model        
        KShortestPaths<T, V> sp = new KShortestPaths<T, V>(this, this.trueStart, MAX_PATH_LENGTH);
        int i = 0;
        for(GraphPath<T, V> gp : sp.getPaths(this.trueEnd)){
        	i++;
        	T s = gp.getStartVertex();
        	String currentPath = s + " -> "; 
        	for(V e : gp.getEdgeList()){
        		currentPath += this.getEdgeTarget(e) + " -> ";
        	}
        	if(currentPath.length() > 3) currentPath = currentPath.substring(0, currentPath.length()-3);
        	System.out.println("Path:" + currentPath);
        }
        System.out.println("Total paths: " + i);
	}

	public void cleanup(boolean print) {
		Set<T> vSet = this.vertexSet();
		for(T node: vSet){
			if(trueStart == null)
				if(this.inDegreeOf(node) == 0) trueStart = node;
			if(trueEnd == null)
				if(this.outDegreeOf(node) == 0) trueEnd = node;
		}
		if(trueStart == null)
		if(vSet.size() >= 0)
			for(T node: vSet){
				trueStart = trueEnd = node;
			}
		
		if(trueStart == null || trueEnd == null) return;

		trueStart.setCorresponding(trueEnd);
		// Fix non-nice gateways
		fixGateWays(print);
		
	}
	
	private void fixGateWays(boolean print) {
		int openAND = 0;
		int openXOR = 0;
		int closeAND = 0;
		int closeXOR = 0;
		for(T v: vertexSet()){
			if(v.type == _TYPE.GATEWAY){
				//System.err.println(v.name + " has " + inDegreeOf(v) + " : " + outDegreeOf(v));
				if(inDegreeOf((T) v) > 1) {	// Join
					if(v.gtype == _GTYPE.AND){closeAND++;
					}else closeXOR++;
				}else{ // Split
					if(v.gtype == _GTYPE.AND){openAND++;
					}else openXOR++;
		}}}
		String msg = "";
		if(openAND == closeAND && openXOR == closeXOR){
			msg = "gateway count matches";
			// Add corresponding gates
			fixCorrespondingGates();
			
		}else{
			msg = "gateway count mismatch";
			if(closeAND != openAND){ 
				msg += " ; And's are out";
				if(closeAND < openAND)
					msg += " ; less closing AND gates than opening AND gates";
				else
					msg += " ; more closing AND gates than opening AND gates";
			}else{
				msg += " ; XOR's are out";
				if(closeXOR < openXOR){
					msg += " ; less closing XOR gates than opening XOR gates";
					fixLessCloseGates();
				}else{
					msg += " ; more closing XOR gates than opening XOR gates";
				}
			}
		}
		fixLessCloseGates();
		if(print)
		System.err.println("msg:" + msg);
		
	}
	
	public LinkedList<T> findFurthestList(LinkedList<T> compare){
		LinkedList<T> results = new LinkedList<T>();
		double currentFurthest = 0;
		while(true){
			
			pair latest = findFurthestPath(compare);
			if(latest.distance > currentFurthest){
				results = new LinkedList<T>();
			}
			
			if(latest.distance >= currentFurthest){
				results.add(latest.node);
				currentFurthest = latest.distance;
				compare.remove(latest.node);
			}else{
				break;
			}
			
		}
		
		return results;
	}
	
	public LinkedList<T> findClosestPairwise(LinkedList<T> compare){
		LinkedList<T> results = new LinkedList<T>();
		LinkedList<T> resultsRemove = new LinkedList<T>();		
		T last = null;
		for(T check: compare){
			for(T check2 : compare){
				if(check != check2){
					T best = findClosest(check, check2);
					if(last == null && best != null) { last = best; results.add(last); };
					if(best == null) continue;
					T newbest = findFurthest(best, last);
					if(best != last && newbest != last){
						resultsRemove.add(last);
						results = new LinkedList<T>();
						results.add(newbest);
					}
					
				}
			}
		}		
		for(T removeMe: resultsRemove){
			//System.err.println("Removing " + removeMe);
			results.remove(removeMe);
		}		
		return results;
	}
	
	

	
	public LinkedList<T> findFurthestPairwise(LinkedList<T> compare){
		LinkedList<T> results = new LinkedList<T>();
		LinkedList<T> resultsRemove = new LinkedList<T>();
		
		T last = null;
		for(T check: compare){
			for(T check2 : compare){
				if(check != check2){
					T best = findFurthest(check, check2);
					if(last == null && best != null) { last = best; results.add(last); };
					if(best == null) continue;
					T newbest = findFurthest(best, last);
					if(best != last && newbest != last){
						resultsRemove.add(last);
						results = new LinkedList<T>();
						results.add(newbest);
					}
					
				}
			}
		}
		
		for(T removeMe: resultsRemove){
			//System.err.println("Removing " + removeMe);
			results.remove(removeMe);
		}
		
		return results;
	}
	
	private T findClosest(T first, T second) {
		if(trueStart == null) cleanup(false);
		if(trueStart == null) return null;
		if(first == second) return null;
		if(first == null || second == null) return null;
		KShortestPaths<T, V> sp = new KShortestPaths<T, V>(this, first, MAX_PATH_LENGTH);
		List<GraphPath<T, V>> shortestPath = sp.getPaths(second);

		//System.err.println("Shortest between " + first + " and " + second + " is " + shortestPath);
		if(shortestPath != null) {
			//System.err.println("Returning  " + second);
			return first; 
		}
		
		sp = new KShortestPaths<T, V>(this, first, MAX_PATH_LENGTH);
		shortestPath = sp.getPaths(second);
		if(shortestPath != null) return second;
		
		return null;
	}
	
	private T findFurthest(T first, T second) {
		if(trueStart == null) cleanup(false);
		if(trueStart == null) return null;
		if(first == second) return null;
		if(first == null || second == null) return null;
		KShortestPaths<T, V> sp = new KShortestPaths<T, V>(this, first, MAX_PATH_LENGTH);
		List<GraphPath<T, V>> shortestPath = sp.getPaths(second);

		//System.err.println("Shortest between " + first + " and " + second + " is " + shortestPath);
		if(shortestPath != null) {
			//System.err.println("Returning  " + second);
			return second; 
		}
		
		sp = new KShortestPaths<T, V>(this, first, MAX_PATH_LENGTH);
		shortestPath = sp.getPaths(second);
		if(shortestPath != null) return first;
		
		return null;
	}

	class pair{
		public double distance; 
		public T node;
	};
	
	public pair findFurthestPath(LinkedList<T> compare){
		if(trueStart == null) cleanup(false);
		if(trueStart == null) return null;
//		double shortest = 10000000;
		double furthest = 0;
		pair last = new pair();
//		T first = null;
		for(T item: compare){
			List<V> shortestPath = BellmanFordShortestPath.findPathBetween(this, trueStart, item);
			double value = 0;
			for(V pathEdge : shortestPath){
				value += getEdgeWeight(pathEdge);
			}
//			if(value < shortest && value > 0){
//				shortest = value;
//				first = item;
//			}
			if(furthest < value){
				furthest = value;
				last.node = item;
				last.distance = furthest;
			}			
		}
		
		
		return last;
	}

	private void fixCorrespondingGates() {
		TreeMap<String, Integer> scoreTallyOut = new TreeMap<String, Integer>();
		TreeMap<String, Integer> scoreTallyIn = new TreeMap<String, Integer>();
		LinkedList<String> gateways = new LinkedList<String>();
		
		KShortestPaths<T, V> sp = new KShortestPaths<T, V>(this, this.trueStart, MAX_PATH_LENGTH);
		for(GraphPath<T, V> gp : sp.getPaths(this.trueEnd)){
			// Walk along paths keeping score of open and close gateways
			T start = gp.getStartVertex();
			T end = gp.getEndVertex();
			T current = start;
			//System.err.println("Viewing current path " + gp);
			//System.err.println("Starting with " + start);
			do{		
				if(current == null) break;
				if(current.type == _TYPE.GATEWAY){
					//scoreTallyIn.put(current.name, 0);
					if(inDegreeOf(current) > 1){
						// gatway join
						scoreTallyIn.put(current.name, inDegreeOf(current));
						gateways.add(current.name);
					}else if(outDegreeOf(current) > 1){
						scoreTallyOut.put(current.name, outDegreeOf(current));
						gateways.add(current.name);
					}
				}				
				current = next(current, gp);
			}while(current != null || current != end);
		}
		
		loopyfix(gateways, scoreTallyIn, scoreTallyOut);
		
	}

	private void loopyfix(LinkedList<String> gateways,
			TreeMap<String, Integer> scoreTallyIn,
			TreeMap<String, Integer> scoreTallyOut) {
		String last = gateways.get(0);
		boolean starter = true;
		LinkedList<String> rmList = new LinkedList<String>();
		LinkedList<String> contList = new LinkedList<String>();
		for(String s: gateways){
			if(starter) {starter = false; continue;};
			if(scoreTallyOut.get(last) != null && scoreTallyIn.get(s) != null)
			if(scoreTallyOut.get(last) > 1 && scoreTallyIn.get(s) > 1){
				T pntSt = null; T pntEd = null; 
				for(T v: vertexSet()){
					if(v.name.compareTo(last) == 0)
						pntSt = v;
				}
				for(T v: vertexSet()){
					if(v.name.compareTo(s) == 0)
						pntEd = v;
				}
				if(pntSt != null && pntEd != null){
					//System.err.println("Setting correspoinding" + pntSt + "," + pntEd);
					pntSt.setCorresponding(pntEd);
					rmList.add(last); rmList.add(s);
				}
			}
			last = s;
		}
		for(String ss: gateways){
			if(!rmList.contains(ss))
				contList.add(ss);
		}
		if(contList.size() > 1) loopyfix(contList, scoreTallyIn, scoreTallyOut);
		
	}

	private void fixLessCloseGates() {
		TreeMap<String, Integer> scoreTallyOut = new TreeMap<String, Integer>();
		TreeMap<String, Integer> scoreTallyIn = new TreeMap<String, Integer>();
		LinkedList<String> gateways = new LinkedList<String>();
		// Make paths through the graph
		KShortestPaths<T, V> sp = new KShortestPaths<T, V>(this, this.trueStart, MAX_PATH_LENGTH);
		for(GraphPath<T, V> gp : sp.getPaths(this.trueEnd)){
			// Walk along paths keeping score of open and close gateways
			T start = gp.getStartVertex();
			T end = gp.getEndVertex();
			T current = start;
			//System.err.println("Viewing current path " + gp);
			//System.err.println("Starting with " + start);
			do{		
				if(current == null) break;
				if(current.type == _TYPE.GATEWAY){
					//scoreTallyIn.put(current.name, 0);
					if(inDegreeOf(current) > 1){
						// gatway join
						scoreTallyIn.put(current.name, inDegreeOf(current));
						gateways.add(current.name);
					}else if(outDegreeOf(current) > 1){
						scoreTallyOut.put(current.name, outDegreeOf(current));
						gateways.add(current.name);
					}
				}				
				current = next(current, gp);
			}while(current != null || current != end);
			
			
			// Now check along path
			String last = "";
			boolean first = true;
			for(String currentGate: gateways){
				if(first){ last = currentGate; first = false; continue;}; // Skip first gateway
				
				if(!(scoreTallyOut.get(last) == null || scoreTallyIn.get(currentGate) == null)){
				if(scoreTallyOut.get(last) > scoreTallyIn.get(currentGate)){
//					System.err.println("Found point of interest: " + last + ":" + scoreTallyOut.get(last) + " vs. " + currentGate + ":" + scoreTallyIn.get(currentGate));
				}//else System.err.println("No point of interest: " + last + ":" + scoreTallyOut.get(last) + " vs. " + currentGate + ":" + scoreTallyIn.get(currentGate));
				}
				last = currentGate;
			}
		}
	}

	private T next(T current, GraphPath<T, V> gp) {
		for(V e: outgoingEdgesOf(current)){
			if(gp.getEdgeList().contains(e)){
				return gp.getGraph().getEdgeTarget(e);
			}
		}
		return null;
	}

	public String fixEffects(String cleanDoc) {
		if(cleanDoc == "") return "";
		String resultEffect = fixKnowledge(cleanDoc);
		
		//System.err.println("Cleaned " + cleanDoc + " to " + resultEffect);
		return resultEffect;
	}

	private String fixEffect(String cleanDoc) {
		String resultEffect = "";
		if(!effectMap.containsKey(cleanDoc)){
			resultEffect = increaseEffect(currentEffect);; 
			effectMap.put(cleanDoc, resultEffect);
		}else
			resultEffect = effectMap.get(cleanDoc);
		return resultEffect;
	}

	private String increaseEffect(char currentEffect2) {
		Character current = currentEffect2;
		char rest = (char)((int)current + 1);
		if(!(rest >= 'a' && rest <= 'z' )){
			if(!(rest >= 'A' && rest <= 'Z') && !(rest >= 'Z' && rest <= 'a'))
				rest = 'A';
			else if(!(rest >= 'A' && rest <= 'Z')){
				addition++;
				rest = 'a';
			}
		}
		currentEffect = rest;
//		if(addition == 0)
//			return ""+ rest;
		return ((char)(addition + ((int)'A'))) +""+ rest;
	}
	
	public String revertResults(String inputEffect){
		String results = inputEffect;
		// Get each 'symbol' and then fulfil mapping:
		try{
			ClassicalLogicS logic = new ClassicalLogicS();
	    	Signature sigma = logic.scanSignature(inputEffect);

	        
	    	for (Iterator<?> i = sigma.iterator(); i.hasNext(); ) {
	    		Symbol o = (Symbol) i.next();	     
	    		if(o.toString() != null){
	    			
    				String key = "";
    				for(String igloo : effectMap.keySet()){
    					if(effectMap.get(igloo).compareTo(o.toString()) == 0){
    						key = igloo;
    					}
    				}
	    			if(key.length() > 0){
	    				//System.err.println("Revert " + o.toString() + " to " + effectMap.get(key) + " with key " + key);
	    				results = results.replaceAll(o.toString(),key);
	    			}else
	    				logger.debug("Error getting " + o.toString() + " from " + effectMap);
	    		}
	    	}
	    	
	    	
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return results;
	}

	public String fixKnowledge(String inputKnowledge) {
		String newKnowledge = inputKnowledge;
		// Get each 'symbol' and then fulfil mapping:
		try{
			ClassicalLogicS logic = new ClassicalLogicS();
	    	Signature sigma = logic.scanSignature(inputKnowledge);
	        TreeMap<String,String> symbols = new TreeMap<String,String>(); 
	        
	    	for (Iterator<?> i = sigma.iterator(); i.hasNext(); ) {
	    		Symbol o = (Symbol) i.next();	  
	    		String newEffect = fixEffect(o.toString());
	    		symbols.put(o.toString(), newEffect);
	    		effectMap.put(o.toString(), newEffect);
	    		//System.err.println("Putting " + o.toString() + " : " + newEffect);
	    	}
	    	
	    	for(String mySymbol : symbols.keySet()){
	    		newKnowledge =  newKnowledge.replaceAll(mySymbol, symbols.get(mySymbol));
	    	}
		}catch(Exception e){
			e.printStackTrace();
		}
		//if(newKnowledge.length() > 3) newKnowledge = newKnowledge.substring(0, newKnowledge.length()-3);
		return newKnowledge;
	}

	/**
	 *  get the next vertex name to the list, in case there are multiple next vertex, return string list.  
	 * @param currentNodeName
	 * @return
	 */
	
	public LinkedList<String> getNextVertex(String currentNodeName){
		LinkedList<String> nextNodes = new LinkedList<String>();

			for(V e: edgeSet())
			if(getEdgeSource(e).name.equals(currentNodeName))
	            nextNodes.add(getEdgeSource(e).name);
//		}
		return nextNodes;
	}


}

class EGraph<T extends Vertex,V extends Edge>{
	public Graph<T,V> basicGraph;
	
	
	
	public EGraph(){
		basicGraph = new Graph<T,V>();
	}
	public V createEdge(T source, T target){
		basicGraph.addVertex(source);
		basicGraph.addVertex(target);
		
		basicGraph.addEdge(source, target);
		for(V returnEdge: basicGraph.edgeSet()){
			return returnEdge;
		}
		return null;
	}
}
