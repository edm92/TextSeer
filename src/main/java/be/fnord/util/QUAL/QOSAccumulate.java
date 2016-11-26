package be.fnord.util.QUAL;

import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import be.fnord.util.processModel.Edge;
import be.fnord.util.processModel.Graph;
import be.fnord.util.processModel.Trace;
import be.fnord.util.processModel.Vertex;
import be.fnord.util.processModel.util.GraphChecker;
import be.fnord.util.processModel.util.GraphLoader;
import be.fnord.util.processModel.util.GraphTransformer;

public class QOSAccumulate {

	protected transient static Logger logger = Logger
			.getLogger("QUALAccumulation");

	public static void main(String[] args) {
		// Load model
		// Setup the environment
		new a.e();
		
		Vertex.TO_STRING_WITH_WFFS = true;

		// Store our traces in a list

		LinkedList<Trace> traces = loadModel("models/QuickModel.bpmn"); // Loading
		String kb = "";
		
		
		
		for(Trace t: traces){
			// Accumulate trace
			LinkedHashSet<JSONEFFECT> last = null;
			
			JSONEFFECT _lst = null;
			int step = 0;
			
			a.e.println("Trace : " + t.toString());
			a.e.incIndent();
			
			for (Vertex v : t.getNodes()) {
				
				JSONEFFECT j = new JSONEFFECT(v.getWFF().getFormula());
				// Show the vertex and current Effect
//				a.e.println(v.name + " : " + j.toString());
				if(!j.isEmpty()){
					if(last== null){
						if(_lst == null)
							_lst = j;
						else{
							LinkedHashSet<JSONEFFECT> __n = pairwise_acc(_lst, j ,kb);
							if(last == null) last = __n;
							
						}
					}else
					if(last != null){
						LinkedHashSet<JSONEFFECT> _res = pairwise_acc(last, j, kb);
						last = _res;
						
					};
					
					if(last != null){
						for(JSONEFFECT l : last)
							a.e.println("Step " + step + ":" + l.QOS.toString());
					}
					
					
				}
				++step;
			}
			
			a.e.decIndent();
		}
		
	}
	
	public static LinkedList<Trace> loadModel(String name) {
		LinkedList<Trace> traces = new LinkedList<Trace>();
		// For details of below refer to Decision free graph conversion and
		// model loading
		GraphLoader gLoader = new GraphLoader();
		GraphTransformer gt = new GraphTransformer();

		Graph<Vertex, Edge> g1 = gLoader.loadModel(name,
				a.e.DONT_SAVE_MESSAGES_AND_PARTICIPANTS);

		// a.e.println("Graph: " + g1);

		LinkedList<Graph<Vertex, Edge>> _decisionless = gt.makeDecisionFree(g1);

		LinkedList<Graph<Vertex, Edge>> decisionless = gt
				.removeDupesFromDecisionFreeGraphs(_decisionless);
		for (Graph<Vertex, Edge> g : decisionless) {
			GraphChecker gcc = new GraphChecker();
			boolean isgood = gcc.CheckGraph(g);
			if (isgood) {
				LinkedList<Trace> _traces = gt.createTrace(g);
//				a.e.println(_traces.toString());
				traces.addAll(_traces);
			}

		}

		return traces;
	}


	public static LinkedHashSet<JSONEFFECT> pairwise_acc(JSONEFFECT source,
			JSONEFFECT target, String KB) {

		return source.pairwise_acc(source, target, KB);

	}

	public static LinkedHashSet<JSONEFFECT> pairwise_acc(JSONEFFECT source,
			JSONEFFECT target, String KB, boolean distrobute) {
		// Do distribution stuff here
		return pairwise_acc(source, target, KB);
	}
	
	public static LinkedHashSet<JSONEFFECT> pairwise_acc(LinkedHashSet<JSONEFFECT> source,
			JSONEFFECT target, String KB, boolean distrobute){
		return pairwise_acc(
					source,
					target, KB);
	}
	
	public static LinkedHashSet<JSONEFFECT> pairwise_acc(LinkedHashSet<JSONEFFECT> source,
			JSONEFFECT target, String KB) {
		LinkedHashSet<JSONEFFECT> _res = new LinkedHashSet<JSONEFFECT>();
		for(JSONEFFECT e: source ){
			_res.addAll(pairwise_acc(e, target, KB));
		}
		return _res;
	}
	
	
	public static LinkedHashSet<JSONEFFECT> pairwise_acc(LinkedHashSet<JSONEFFECT> source,
			LinkedHashSet<JSONEFFECT> target, String KB,boolean distrobute) {
		return pairwise_acc(
				source,
				 target, KB);
	}
	
	public static LinkedHashSet<JSONEFFECT> pairwise_acc(LinkedHashSet<JSONEFFECT> source,
			LinkedHashSet<JSONEFFECT> target, String KB) {
		LinkedHashSet<JSONEFFECT> _res = new LinkedHashSet<JSONEFFECT>();
		for(JSONEFFECT e: target ){
			_res.addAll(pairwise_acc(source, e, KB));
		}
		return _res;
	}
	
	
	
	// Shitty doesn't seem to work? Why didn't I finish this
	public static LinkedHashSet<JSONEFFECT> trace_acc(Trace src, String kb) {
		LinkedHashSet<JSONEFFECT> _result = new LinkedHashSet<JSONEFFECT>();

		_result.add(new JSONEFFECT());
		for (Vertex v : src.getNodes()) {
			LinkedHashSet<JSONEFFECT> intEff = new LinkedHashSet<JSONEFFECT>();
			intEff.addAll(_result);
			
			for (JSONEFFECT ce : _result) {
				if (!(v.jsEFF == null)) {
					intEff.remove(ce);
					QOSAccumulate acc = new QOSAccumulate();
					intEff.addAll(acc.pairwise_acc(ce, v.jsEFF, kb, true));

				}
			}
			_result = new LinkedHashSet<JSONEFFECT>();
			_result.addAll(intEff);
		}

		return _result;
	}
	// //////////////////////////////////////////////
	// Below are pairwise accumulation functions///
	// //////////////////////////////////////////////

}
