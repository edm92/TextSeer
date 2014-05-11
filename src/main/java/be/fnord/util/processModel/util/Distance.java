package be.fnord.util.processModel.util;

import java.util.LinkedList;

import a.e.SIM_RESULT;
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
	
	public static final boolean __DEBUG = true;
	public static double PENALTY_FOR_EXTRA_TRACE = a.e.PENALTY_FOR_EXTRA_TRACE;
	

	// Syntactic distances first
	public double distance(LinkedList<Trace> _a, LinkedList<Trace> _b, 
			ALG ALG_TYPE, WORD_MATCH_STRENGTH MATCHSTRENGH,
			SIM_RESULT RAT_OR_WHO){
		double score = 0;
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
			if(t== null || t.nodes.size() < 1) return 0;
			for (Vertex v : t.nodes) {
				trace.add(v.name);
	        }
			trace.add(t.MY_ID + "");
			if(trace.size() > 1)
				sentences1.add(trace);
			if(__DEBUG) a.e.println(trace.toString());
		}
		for(Trace t: _b){
			LinkedList<String> trace = new LinkedList<String>();
			for (Vertex v : t.nodes) {
				trace.add(v.name);
	        }
			trace.add(t.MY_ID + "");
			if(trace.size() > 1)
				sentences2.add(trace);
			if(__DEBUG) a.e.println(trace.toString());
		}
		int loopBreaker = 500;
		
		boolean switcher = false;
		do{
			if(--loopBreaker < 1) break;
			switcher = false;
			SimilarPair mostSim = getMostSimilar(sentences1, sentences2, ALG_TYPE, MATCHSTRENGH, RAT_OR_WHO);
			if(__DEBUG) a.e.println(mostSim.toString());
			score += mostSim.simScore;
//			a.e.println("Score " + score);
			LinkedList<LinkedList<String>> remove = new LinkedList<LinkedList<String>>();
			for(LinkedList<String> s : sentences1){
				String last = s.getLast();
				if(__DEBUG) a.e.println("Last " + last);
				if(last != null && last.trim().equals(mostSim.first_id + "")) remove.add(s);
			}
			for(LinkedList<String> ss : remove)
				sentences1.remove(ss);
			remove = new LinkedList<LinkedList<String>>();
			for(LinkedList<String> s : sentences2){
				String last = s.getLast();
				if(__DEBUG) a.e.println("Last " + last);
				if(last != null && last.trim().equals(mostSim.second_id + "")) remove.add(s);
			}
			for(LinkedList<String> ss : remove)
				sentences2.remove(ss);
			if(sentences1.size() < 2 || sentences2.size() < 2) loopBreaker = loopBreaker > 1 ? 1 : loopBreaker;
			if(sentences1.size() > 0 && sentences2.size() > 0) switcher = true;
		}while(switcher);
		
		if(sentences1.size() > 0 || sentences2.size() > 0){
			// Add in a penalty for having extra traces
			int count = max(sentences1.size() , sentences2.size());
			double penalty = count * PENALTY_FOR_EXTRA_TRACE;
			if(score > penalty) 
				score = score - penalty;
			else
				score = 0;
		}
		
		return score;
	}
	
	public int max(int a, int b){
		return a > b ? a : b;
	}
	
	private SimilarPair getMostSimilar(LinkedList<LinkedList<String>> sentences1,
			LinkedList<LinkedList<String>> sentences2, 
			ALG ALG_TYPE, WORD_MATCH_STRENGTH MATCHSTRENGH,
			SIM_RESULT RAT_OR_WHO) {
		
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
								ALG_TYPE, 
								MATCHSTRENGH.MATCH_NUMBER);
				DamerauLevenshtein d = new DamerauLevenshtein(myNewSentences.getFirst(),myNewSentences.getSecond());
				int numChar = myNewSentences.extra ;
				
				
				
				double sim = (double)d.getSimilarity();
				double oldSim = sim;
				if(!RAT_OR_WHO.TYPE)
					sim = 1 - (sim / (double)numChar);
				if(sim > sp.simScore){
					sp.first = _fsentences;
					sp.second = _ssentences;
					sp.simScore = sim;
					sp.first_id = id1;
					sp.second_id = id2;
				}
				
				if(__DEBUG) a.e.println("Distance between " + myNewSentences.getFirst() + " and " + 
						myNewSentences.getSecond() + " is " + sim + " - Char Dif =" + numChar + " (" + oldSim + ") " + (1-sim) );
				
				if(id1 != 0) _fsentences.add(id1 + "");
				if(id2 != 0) _ssentences.add(id2 + "");
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
        double distance = dc.computeDistance("models/proc1.bpmn20.xml", "models/proc3.bpmn20.xml",
        		ALG.SIMPLE_COMPARE_ALG, WORD_MATCH_STRENGTH.EXACT, SIM_RESULT.WHOLE_NUMBER);
        a.e.println("Distance between the two models is " + distance);

	}
	
	public double computeDistance(String file1, String file2, 
			ALG ALG_TYPE, WORD_MATCH_STRENGTH MATCHSTRENGH, SIM_RESULT RAT_OR_WHO){
        GraphLoader gLoader = new GraphLoader();
        Graph<Vertex, Edge> g1 = gLoader.loadModel(file1, a.e.DONT_SAVE_MESSAGES_AND_PARTICIPANTS);
        Graph<Vertex, Edge> g2 = gLoader.loadModel(file2, a.e.DONT_SAVE_MESSAGES_AND_PARTICIPANTS);
        
        return computeDistance(g1, g2, ALG_TYPE, MATCHSTRENGH, RAT_OR_WHO);

	}
	
	public double computeDistance(Graph<Vertex,Edge> g1, Graph<Vertex,Edge> g2, 
			ALG ALG_TYPE, WORD_MATCH_STRENGTH MATCHSTRENGH, SIM_RESULT RAT_OR_WHO){
		double _distance = 0;
        GraphChecker gc1 = new GraphChecker();
        GraphChecker gc2 = new GraphChecker();
        if(!gc1.CheckGraph(g1)) { 
        	if(__DEBUG) a.e.err("Error in graph + " + g1.name + " aborting."); 
        	return 0; };
        if(!gc2.CheckGraph(g2)) { 
        	if(__DEBUG) a.e.err("Error in graph + " + g2.name + " aborting."); 
        	return 0; 
        	};
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
//                for(Trace t : traces) a.e.println("ID 1 = " + t.MY_ID);
                if(traces != null)
                	traces1.addAll(traces);
            }
        }
        
        for (Graph<Vertex, Edge> g : decisionless2) {
            GraphChecker gcc = new GraphChecker();
            boolean isgood = gcc.CheckGraph(g);
            if (isgood) {
                LinkedList<Trace> traces = gt2.createTrace(g);
//                for(Trace t : traces) a.e.println("ID 1 = " + t.MY_ID);
                if(traces != null)
                	traces2.addAll(traces);
            }
        }
        
        if(traces2 != null && traces1 != null)
        	_distance = distance(traces1, traces2, ALG_TYPE, MATCHSTRENGH, RAT_OR_WHO);
        
        //a.e.println("Distance is : " + _distance);
        
        return _distance;
	}
	
	
	

}
