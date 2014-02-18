import be.fnord.util.processModel.Edge;
import be.fnord.util.processModel.Graph;
import be.fnord.util.processModel.Trace;
import be.fnord.util.processModel.Vertex;
import be.fnord.util.processModel.util.GraphChecker;
import be.fnord.util.processModel.util.GraphLoader;
import be.fnord.util.processModel.util.GraphTransformer;

import java.util.LinkedList;

/**
 * This is an example of the decision free graph generation procedure. This will break down process models into instance
 * level chunks that have no decision points.
 * <p/>
 * The following example uses Jung to decision free processes.
 * Documented in Morrison et. al. 2014.
 * <p/>
 * Example
 * Got a trace: Trace<_3Start Event1,_8Parallel Gateway,_7Task,_6Para Exclusive Gateway,_27Para XOR Choice 1,_59Exclusive Gateway,_29Parallel Gateway Join,_11End Event3>
 * Got a trace: Trace<_3Start Event1,_8Parallel Gateway,_7Task,_6Para Exclusive Gateway,_31Para XOR Choice 2,_59Exclusive Gateway,_29Parallel Gateway Join,_11End Event3>
 * Got a trace: Trace<_38Start Event3,_39Task2,_40Inclusive Gateway,_43User Task,_47Inclusive Gateway2,_50End Event>
 * Got a trace: Trace<_38Start Event3,_39Task2,_40Inclusive Gateway,_45Service Task,_47Inclusive Gateway2,_50End Event>
 * Got a trace: Trace<_4Start Event2,_34Call Activity,_9Do More activities with the DB,_12Make a choice,_14Choice 1,_25End Event 2>
 * Got a trace: Trace<_4Start Event2,_34Call Activity,_9Do More activities with the DB,_12Make a choice,_16Choice 2 with reply from DB,_22End Event>
 * Got a trace: Trace<_53Start Event4,_37Sub-Process,_52Boundary Event,_56End Event5>
 * Got a trace: Trace<_53Start Event4,_37Sub-Process,_55End Event4>
 *
 * @author Evan Morrison edm92@uowmail.edu.au http://www.fnord.be
 *         Apache License, Version 2.0, Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */
public class DecisionFreeGraphConversion {

    public static void main(String[] args) {
        /////////////////////////////////////////
        /* Initialize core app. */
        new a.e();  //
        /////////////////////////////////////////
        //// Real start of program below	/////
        /////////////////////////////////////////

//		Graph<Vertex,Edge> g1 = GraphLoader.loadModel("models/MultiGateTest.bpmn20.xml", a.e.DONT_SAVE_MESSAGES_AND_PARTICIPANTS);
        Graph<Vertex, Edge> g1 = GraphLoader.loadModel("models/Model1.bpmn20.xml", a.e.DONT_SAVE_MESSAGES_AND_PARTICIPANTS);
//		System.out.println("G1-" + g1);
        GraphChecker gc = new GraphChecker();
        if (!gc.CheckEventsAndGateways(g1)) a.e.println("Issue checking events and gateways");

        LinkedList<Graph<Vertex, Edge>> _decisionless = GraphTransformer.makeDecisionFree(g1);
        LinkedList<Graph<Vertex, Edge>> decisionless = GraphTransformer.removeDupesFromDecisionFreeGraphs(_decisionless);

        for (Graph<Vertex, Edge> g : decisionless) {
            GraphChecker gcc = new GraphChecker();
            boolean isgood = gcc.CheckGraph(g);
//			a.e.println("Decision Free Graph: " + g);
//			a.e.println("Checking if well formed results in a return of : " + isgood);
            if (isgood) {
                // Create some traces
                LinkedList<Trace> traces = GraphTransformer.createTrace(g);
                for (Trace trace : traces) {
                    a.e.println("Got a trace: " + trace.toString());
                }
                g.toView();
            }
        }
        return;

    }


}
