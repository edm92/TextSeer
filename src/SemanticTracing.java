import java.util.LinkedHashSet;
import java.util.LinkedList;

import orbital.algorithm.template.Backtracking;

import org.jbpt.pm.AndGateway;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.OrGateway;
import org.jbpt.pm.XorGateway;
import org.jbpt.pm.bpmn.Task;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

import be.fnord.util.logic.Accumulate;
import be.fnord.util.logic.Accumulate_4ST;
import be.fnord.util.logic.WFF;
import be.fnord.util.processModel.Edge;
import be.fnord.util.processModel.Graph;
import be.fnord.util.processModel.Trace;
import be.fnord.util.processModel.Vertex;
import be.fnord.util.processModel.Graph_ST;
import be.fnord.util.processModel.Vertex_ST;
import be.fnord.util.processModel.Edge_ST;
import be.fnord.util.processModel.util.GraphChecker;
import be.fnord.util.processModel.util.GraphLoader;
import be.fnord.util.processModel.util.GraphTransformer;

import org.jgrapht.graph.DefaultDirectedGraph;





/**
 * This class is for tracing the semantic route in a BPMN semantic accumulation
 * 
 * @author Xiong Wen (xw926@uowmail.edu.au)
 *
 */
public class SemanticTracing {
	
	/**
	 * Load the process model from xml file, get the traces
	 * @return traces 
	 * @author Evan
	 */
	public static LinkedList<Trace> loadModel() {

		LinkedList<Trace> traces = new LinkedList<Trace>();
		// For details of below refer to Decision free graph conversion and
		// model loading
		Graph<Vertex, Edge> g1 =
			GraphLoader.loadModel(
				"models/example.bpmn", a.e.DONT_SAVE_MESSAGES_AND_PARTICIPANTS);
		LinkedList<Graph<Vertex, Edge>> _decisionless =
			GraphTransformer.makeDecisionFree(g1);
		LinkedList<Graph<Vertex, Edge>> decisionless =
			GraphTransformer.removeDupesFromDecisionFreeGraphs(_decisionless);
		for (Graph<Vertex, Edge> g : decisionless) {
			GraphChecker gcc = new GraphChecker();
			boolean isgood = gcc.CheckGraph(g);
			if (isgood) {
				LinkedList<Trace> _traces = GraphTransformer.createTrace(g);
				traces.addAll(_traces);
			}

		}

		return traces;
	}
	
	/**
	 * Given an effect scenario, it can find out from which previous effect scenario it is resulted
	 * @param gST	the graph object which contains effect scenarios(vertices) and the links(edges) between them
	 * @param esWFF	the effect scenario which needs to be tracked    
	 */
	public static void backTrack(Graph_ST<Vertex_ST, Edge_ST> gST, String esWFF) {
		
		if(!gST.edgeSet().isEmpty()){
			for(Edge_ST e: gST.edgeSet()){
				//build up links between effect scenarios	
				if(gST.getEdgeTarget(e).esWFF.equals(esWFF)){
					a.e.println("Given an effect scenario, it can find out from which previous effect scenario it is resulted");
					a.e.println("\nGiven effect scenario: " + esWFF);
					a.e.println("Found accumulation:  " + gST.getEdgeSource(e).esWFF + " --> " + gST.getEdgeTarget(e).esWFF);
				}
			}
		}
		
	}
	
	
	public static void main(String[] args) {

		// Graph class
		Graph_ST<Vertex_ST, Edge_ST> gST = new Graph_ST<Vertex_ST, Edge_ST>();

		// Setup the environment
		new a.e();
		Vertex.TO_STRING_WITH_WFFS = true;

		// TODO 1. load the BPMN model in the form of texture
		// Store our traces in a list
		LinkedList<Trace> traces = loadModel(); // Loading a model and convert
												// into a set of traces

		// TODO 2. perform accumulation
		 Accumulate_4ST acc = new Accumulate_4ST();
//		Accumulate acc = new Accumulate();

		// Iterate through each trace.
		if (traces != null) {
			for (Trace t : traces) {
				//test for printing out the nodes
				System.out.println("New Model");
//				for (Vertex v : t.getNodes()) {
//					a.e.println("Vertex task: " + v.name);
////					System.out.println("vertex id: " + v.id);
//					a.e.println("Vertex effect: " + v.getWFF());
//					// question: how to filter out the subsets(effect scenario)?
//				}
				
//				System.out.println("\n\n");
				gST = acc.trace_acc(t, "(((a & b) -> ~c) & ((a&c) -> ~d))",gST); //accumulation
				
				//given effect scenario: (c) & (d) or (a) & (d) or ((b) & (c)) & (d)  
				//we can track back to the previous effect scenario which resulted to it
				//System.out.println( gST.vertexSet().toString() );
				String str1 = "(c) & (d)";
				String str2 = "(a) & (d)";
				String str3 = "((b) & (c)) & (d)";
				backTrack(gST, str1);
				backTrack(gST, str2);
				backTrack(gST, str3);
				
				//test for edges in the new built graph
//				System.out.println("edges count " + gST.edgeSet().size());
//				for(Edge_ST e: gST.edgeSet()){
//					System.out.println(gST.getEdgeSource(e).esWFF + " to " + gST.getEdgeTarget(e).esWFF);
////					System.out.println(gST.getEdgeSource(e).esWFF);
//				}
				
				//test for display of the vertices in the new Graph_ST
//				System.out.println("\nThe number of nodes in Graph_ST: " + gST.vertexSet().size());
//				for(Vertex_ST vst: gST.vertexSet()){
//					
//					System.out.println("\n---Begin a node in the graph---");
//					System.out.println("taskName: " + vst.taskName);
//					System.out.println("immediate effect: " + vst.immWFF);
//					System.out.println("cummulative effect: " + vst.esWFF);
//					System.out.println("---End a node in the graph----\n");
//					
//				}
				 
				 
//				
//				System.out.println("\n\n");
//				for (WFF e : _ee) {
//					if (e.isEmpty())
//						continue;
//					System.out.println("Effect scenario resulting from acc: " +
//						e);
//					System.out.println("Checking if " + e +
//						" is consistent : " + e.issat());
//
//				}
			}
		}

	}

}
