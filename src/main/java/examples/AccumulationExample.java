package examples;

import java.util.LinkedHashSet;
import java.util.LinkedList;

import be.fnord.util.QUAL.JSONEFFECT;
import be.fnord.util.QUAL.QOSAccumulate;
import be.fnord.util.logic.EffectAccumulate;
import be.fnord.util.logic.WFF;
import be.fnord.util.processModel.Edge;
import be.fnord.util.processModel.Graph;
import be.fnord.util.processModel.Trace;
import be.fnord.util.processModel.Vertex;
import be.fnord.util.processModel.util.GraphChecker;
import be.fnord.util.processModel.util.GraphLoader;
import be.fnord.util.processModel.util.GraphTransformer;

/**
 * The accumulation example will demonstrate the effect accumulation process
 * over a set of models
 * <p/>
 * <p/>
 * 
 * Notes on the accumulation process
 * 
 * Example Output (using normal effects): Effect scenario resulting from acc: c
 * & a Checking if c & a is consistent : true Effect scenario resulting from
 * acc: c & b Checking if c & b is consistent : true Effect scenario resulting
 * from acc: b & ~c & a Checking if b & ~c & a is consistent : true
 * ---------------------------------------------------------------- Example
 * Output (using QOS + effects): Effect scenario resulting from acc: ~B & C & A
 * Checking if ~B & C & A is consistent : true QOS Based Effect scenario
 * resulting from acc: QOS:Cost = 40.0; TIME = PT1H30M0.023S; SKILL = HIGH;
 * EFFECT:A; CONSTRAINT: A; GOAL: A; RESOURCE: A; Effect scenario resulting from
 * acc: ~B & ~C & A Checking if ~B & ~C & A is consistent : true QOS Based
 * Effect scenario resulting from acc: QOS:Cost = 40.0; TIME = PT1H20M; SKILL =
 * HIGH; EFFECT:~C; CONSTRAINT: ~C; GOAL: ~C; RESOURCE: ~C;
 * 
 *
 * @author Evan Morrison edm92@uowmail.edu.au http://www.fnord.be Apache
 *         License, Version 2.0, Apache License Version 2.0, January 2004
 *         http://www.apache.org/licenses/
 */
public class AccumulationExample {

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
				// a.e.println(_traces.toString());
				traces.addAll(_traces);
			}

		}

		return traces;
	}

	public static void main(String[] args) {
		// Setup the environment
		new a.e();
		Vertex.TO_STRING_WITH_WFFS = true;

		// Store our traces in a list

		LinkedList<Trace> traces = loadModel("models/Model1.bpmn20.xml"); // Loading
																			// a
																			// model
																			// and
																			// convert
																			// into
																			// a
																			// set
																			// of
																			// traces
		LinkedList<Trace> traces2 = loadModel("models/Benefits Administration - Ongoing.bpmn20.xml"); // Loading
																										// a
																										// model
																										// and
																										// convert
																										// into
																										// a
																										// set
																										// of
																										// traces

		// Iterate through each trace.
		a.e.println("Trying to compute accumulation on process without QOS");
		a.e.incIndent();
		if (traces != null) {
			showAccumulationForTrace(traces);
		}
		a.e.decIndent();
		a.e.println("Trying to compute accumulation on process with QOS");
		a.e.incIndent();
		if (traces2 != null) {
			showAccumulationForTrace(traces2);
		}

		a.e.decIndent();

	}

	public static void showAccumulationForTrace(LinkedList<Trace> traces) {
		EffectAccumulate acc = new EffectAccumulate();
		QOSAccumulate acc2 = new QOSAccumulate();

		for (Trace t : traces) {

			// a.e.println("New Model");
			// for(Vertex v: t.getNodes()){
			// a.e.println("Vertex effect " + v.getWFF());
			// }
			LinkedHashSet<WFF> _ee = acc.trace_acc(t, "((a & b) -> ~c)");
			LinkedHashSet<JSONEFFECT> _ee2 = acc2.trace_acc(t, "");
			// Display Results
			for (WFF e : _ee) {
				if (e.isEmpty()) {
					continue;
				}
				WFF newE = new WFF(e.getClosure()); // Clean up the result a
													// little bit
				a.e.println("Effect scenario resulting from acc: " + newE);
				a.e.println("\tChecking if " + newE + " is consistent : "
						+ newE.issat());

			}
			// Display Results
			for (JSONEFFECT e : _ee2) {
				if (e.isEmpty()) {
					continue;
				}
				a.e.println(e.Name + " " + e.Type);
				a.e.println("QOS Based Effect scenario resulting from acc: "
						+ e);
			}
		}
	}

}
