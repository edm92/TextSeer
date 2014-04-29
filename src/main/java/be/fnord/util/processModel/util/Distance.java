package be.fnord.util.processModel.util;

import java.util.LinkedList;

import a.e.WORD_MATCH_STRENGTH;
import be.fnord.util.functions.Poset.Pair;
import be.fnord.util.functions.language.DamerauLevenshtein;
import be.fnord.util.functions.language.Sentences;
import be.fnord.util.functions.language.Sentences.ALG;
import be.fnord.util.processModel.Edge;
import be.fnord.util.processModel.Graph;
import be.fnord.util.processModel.Trace;
import be.fnord.util.processModel.Vertex;


/**
 * The following functions demonstrate various distance functions. 
 * @author edm92
 *
 */

public class Distance {

	// Syntactic distances first
	public int distance(LinkedList<Trace> _a, LinkedList<Trace> _b){
		
		LinkedList<LinkedList<String>> sentences1 = new LinkedList<LinkedList<String>>();
		LinkedList<LinkedList<String>> sentences2 = new LinkedList<LinkedList<String>>();
		Trace.TO_STRING_WITH_WFFS = false;
		
		// Clean up strings
		for(Trace t: _a){
//			String s = t.toString().replaceAll("<", " ").replaceAll(">", " ").replace("Trace", "").replaceAll("\\_", " ").replaceAll("\\(", " ")
//					.replaceAll("\\)", " ")
//					.replaceAll(",", "")
//					.replaceAll("  ", " ");
			LinkedList<String> trace = new LinkedList<String>();
			for (Vertex v : t.nodes) {
				trace.add(v.name);
	        }
			trace.add(t._ID + "");
			if(trace.size() > 1)
				sentences1.add(trace);
			a.e.println(trace.toString());
		}
		for(Trace t: _b){
			LinkedList<String> trace = new LinkedList<String>();
			for (Vertex v : t.nodes) {
				trace.add(v.name);
	        }
			trace.add(t._ID + "");
			if(trace.size() > 1)
				sentences2.add(trace);
			a.e.println(trace.toString());
		}
		
		SimilarPair mostSim = getMostSimilar(sentences1, sentences2);
		a.e.println(mostSim.toString());
		
		
		return 0;
	}
	
	private SimilarPair getMostSimilar(LinkedList<LinkedList<String>> sentences1,
			LinkedList<LinkedList<String>> sentences2) {
		
		SimilarPair sp = new SimilarPair();
		Sentences s = new Sentences();
		
		for(LinkedList<String> _fsentences: sentences1){
			for(LinkedList<String> _ssentences: sentences2){
				int id1 = 0;
				int id2 = 0;
				try{
					id1 = Integer.parseInt(_fsentences.getLast());_fsentences.removeLast();
					id2 = Integer.parseInt(_ssentences.getLast());_ssentences.removeLast();
				}catch(Exception e){
//					a.e.println("Error parse int");
//					e.printStackTrace();
				}
//				a.e.println(_fsentences.toString() + " --");
				
				Pair<String,String> myNewSentences = 
						s.CreateStringOfTwoSetsOfSentences(_fsentences, _ssentences, 
								ALG.SIMPLE_COMPARE_ALG, 
								WORD_MATCH_STRENGTH.EXACT.MATCH_NUMBER);
				DamerauLevenshtein d = new DamerauLevenshtein(myNewSentences.getFirst(),myNewSentences.getSecond());
				int numChar = myNewSentences.extra ;
				double sim = 1 - ((double)d.getSimilarity() / (double)numChar);
				if(sim > sp.simScore){
					sp.first = _fsentences;
					sp.second = _ssentences;
					sp.simScore = sim;
					sp.first_id = id1;
					sp.second_id = id2;
				}
			}
		}
		return sp;
	}

	class SimilarPair{
		public LinkedList<String> first = new LinkedList<String>();
		public LinkedList<String> second = new LinkedList<String>(); 
		public double simScore = 0; 
		public int first_id = 0;
		public int second_id = 0;
		
		public String toString(){
			String result = "First: " + a.e.endl;
			a.e.incIndent();
			result += a.e.tab + first + a.e.endl;
			a.e.decIndent();
			result += "Second: " + a.e.endl;
			a.e.incIndent();
			result += a.e.tab + second + a.e.endl;
			a.e.decIndent();
			result += "Score: " + simScore + "; id = " + first_id + "," + second_id;
			
			return result;
		}
	}
	
	
	// Semantic distances
	
	public static void main(String[] args) {
		
        /////////////////////////////////////////
        /* Initialize core app. */
        new a.e();  //
        /////////////////////////////////////////
        //// Real start of program below	/////
        /////////////////////////////////////////
        Distance dc = new Distance();
        double distance = dc.computeDistance("models/proc1.bpmn20.xml", "models/proc3.bpmn20.xml");

	}
	
	public double computeDistance(String file1, String file2){
        GraphLoader gLoader = new GraphLoader();
        Graph<Vertex, Edge> g1 = gLoader.loadModel(file1, a.e.DONT_SAVE_MESSAGES_AND_PARTICIPANTS);
        Graph<Vertex, Edge> g2 = gLoader.loadModel(file2, a.e.DONT_SAVE_MESSAGES_AND_PARTICIPANTS);
        
        return computeDistance(g1, g2);

	}
	
	public double computeDistance(Graph<Vertex,Edge> g1, Graph<Vertex,Edge> g2){
		double _distance = 0;
        GraphChecker gc1 = new GraphChecker();
        GraphChecker gc2 = new GraphChecker();
        
        if (!gc1.CheckEventsAndGateways(g1)) a.e.println("Issue checking events and gateways");
        if (!gc2.CheckEventsAndGateways(g1)) a.e.println("Issue checking events and gateways");
        
        GraphTransformer gt1 = new GraphTransformer();
        GraphTransformer gt2 = new GraphTransformer();
        
        LinkedList<Graph<Vertex, Edge>> _decisionless1 = gt1.makeDecisionFree(g1);
        LinkedList<Graph<Vertex, Edge>> decisionless1 = gt1.removeDupesFromDecisionFreeGraphs(_decisionless1);

        LinkedList<Graph<Vertex, Edge>> _decisionless2 = gt2.makeDecisionFree(g2);
        LinkedList<Graph<Vertex, Edge>> decisionless2 = gt2.removeDupesFromDecisionFreeGraphs(_decisionless2);

        
        LinkedList<Trace> traces1 = new LinkedList<Trace>();
        LinkedList<Trace> traces2 = new LinkedList<Trace>();
        
        for (Graph<Vertex, Edge> g : decisionless1) {
            GraphChecker gcc = new GraphChecker();
            boolean isgood = gcc.CheckGraph(g);
            if (isgood) {
                LinkedList<Trace> traces = gt1.createTrace(g);
                if(traces != null)
                	traces1.addAll(traces);
            }
        }
        
        for (Graph<Vertex, Edge> g : decisionless2) {
            GraphChecker gcc = new GraphChecker();
            boolean isgood = gcc.CheckGraph(g);
            if (isgood) {
                LinkedList<Trace> traces = gt2.createTrace(g);
                if(traces != null)
                	traces2.addAll(traces);
            }
        }
        
        if(traces2 != null && traces1 != null)
        	_distance = distance(traces1, traces2);
        
        a.e.println("Distance is : " + _distance);
        
        return _distance;
	}

}
