

import java.util.LinkedList;

import be.fnord.util.processModel.Edge;
import be.fnord.util.processModel.Graph;
import be.fnord.util.processModel.Trace;
import be.fnord.util.processModel.Vertex;
import be.fnord.util.processModel.util.GraphChecker;
import be.fnord.util.processModel.util.GraphLoader;
import be.fnord.util.processModel.util.GraphTransformer;

/**
 * 
 * @author Evan Morrison edm92@uowmail.edu.au http://www.fnord.be
 * Apache License, Version 2.0, Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 *
 */
public class DecisionFreeGraphConversion {
	
	public static void main(String[] args) {
		/////////////////////////////////////////
		/* Initialize core app. */ new a.e();  // 
		/////////////////////////////////////////
		//// Real start of program below	/////
		/////////////////////////////////////////
		
//		Graph<Vertex,Edge> g1 = GraphLoader.loadModel("models/MultiGateTest.bpmn20.xml", a.e.DONT_SAVE_MESSAGES_AND_PARTICIPANTS);
		Graph<Vertex,Edge> g1 = GraphLoader.loadModel("models/Model1.bpmn20.xml", a.e.DONT_SAVE_MESSAGES_AND_PARTICIPANTS);
//		System.out.println("G1-" + g1);
		GraphChecker gc = new GraphChecker();
		if(!gc.CheckEventsAndGateways(g1)) a.e.println("Issue checking events and gateways"); 

		LinkedList<Graph<Vertex, Edge>> _decisionless = GraphTransformer.makeDecisionFree(g1);
		LinkedList<Graph<Vertex, Edge>> decisionless = GraphTransformer.removeDupesFromDecisionFreeGraphs(_decisionless);
				
		for(Graph<Vertex,Edge> g : decisionless){
			GraphChecker gcc = new GraphChecker();
			boolean isgood = gcc.CheckGraph(g);
//			a.e.println("Decision Free Graph: " + g);
//			a.e.println("Checking if well formed results in a return of : " + isgood);
			if(isgood){
				// Create some traces
				LinkedList<Trace> traces = GraphTransformer.createTrace(g);
				for(Trace trace : traces){
					a.e.println("Got a trace: " + trace.toString() );
				}
				g.toView();
			}
		}
		return ;
		
	}
	


}
