import java.util.LinkedHashSet;
import java.util.LinkedList;

import be.fnord.util.logic.Accumulate;
import be.fnord.util.logic.WFF;
import be.fnord.util.processModel.Edge;
import be.fnord.util.processModel.Graph;
import be.fnord.util.processModel.Trace;
import be.fnord.util.processModel.Vertex;
import be.fnord.util.processModel.util.GraphChecker;
import be.fnord.util.processModel.util.GraphLoader;
import be.fnord.util.processModel.util.GraphTransformer;


public class AccumulationExample {

	public static void main(String [] args){
		// Setup the environment
		new a.e();
		Vertex.TO_STRING_WITH_WFFS = true;
		
		// Store our traces in a list
		LinkedList<Trace> traces = loadModel(); 	// Loading a model and convert into a set of traces	
		
		Accumulate acc = new Accumulate();
		
		
		// Iterate through each trace. 
		if(traces != null){
			for(Trace t: traces){
//				System.out.println("New Model");
//				for(Vertex v: t.getNodes()){
//					a.e.println("Vertex effect " + v.getWFF());
//				}
				LinkedHashSet<WFF> _ee = acc.trace_acc(t, "((a & b) -> ~c)");
				// Display Results 
				for(WFF e: _ee){
					if(e.isEmpty()) continue;
					System.out.println("Effect scenario resulting from acc: " + e);
					System.out.println("Checking if " + e + " is consistent : " + e.issat());

				}
			}
		}
		
		

	}
	
	public static LinkedList<Trace> loadModel(){
		LinkedList<Trace> traces = new LinkedList<Trace>(); 
		// For details of below refer to Decision free graph conversion and model loading 
				Graph<Vertex,Edge> g1 = GraphLoader.loadModel("models/Model1.bpmn20.xml", a.e.DONT_SAVE_MESSAGES_AND_PARTICIPANTS);
				LinkedList<Graph<Vertex, Edge>> _decisionless = GraphTransformer.makeDecisionFree(g1);
				LinkedList<Graph<Vertex, Edge>> decisionless = GraphTransformer.removeDupesFromDecisionFreeGraphs(_decisionless);				
				for(Graph<Vertex,Edge> g : decisionless){
					GraphChecker gcc = new GraphChecker();
					boolean isgood = gcc.CheckGraph(g);
					if(isgood)	{ 
						LinkedList<Trace> _traces  = GraphTransformer.createTrace(g);
						traces.addAll(_traces);
					}
					
				}
				
		return traces;
	}
	
}
