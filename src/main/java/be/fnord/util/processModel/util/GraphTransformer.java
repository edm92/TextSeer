package be.fnord.util.processModel.util;

import be.fnord.util.processModel.Edge;
import be.fnord.util.processModel.Graph;
import be.fnord.util.processModel.Trace;
import be.fnord.util.processModel.Vertex;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.FloydWarshallShortestPaths;

import java.util.LinkedList;
import java.util.TreeSet;

/**
 * Transformation functions for graphs
 * TODO Fix the templating system, I've written over it to produce code very quickly, if you can figure out what the
 * templating system is please note it here. I've forgotten.
 *
 * @author Evan Morrison edm92@uowmail.edu.au http://www.fnord.be
 *         Apache License, Version 2.0, Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */
public class GraphTransformer {
    public static boolean __DEBUG = a.e.__DEBUG;
    public static boolean __INFO = a.e.__INFO;

    public LinkedList<Trace> createTraces(LinkedList<Graph<Vertex, Edge>> models) {
        LinkedList<Trace> results = new LinkedList<Trace>();
        for (Graph<Vertex, Edge> g : models) {

            results.addAll(createTrace(g));

        }
        return results;
    }


    /**
     * Create Traces
     */
    public LinkedList<Trace> createTrace(Graph<Vertex, Edge> g) {
        LinkedList<Trace> __results = new LinkedList<Trace>();

        // All input models should be decision free models, so we need to first check for parallel gateways.
        // Break down the model into sections of parallel and non parallel paths
        // Then combine each non-parallel path with OCP instances of the parallel path.
        Trace startTrace = new Trace(g);
        Vertex currentNode;
        if (g.trueStart != null) {
            currentNode = g.trueStart;
            __results.add(startTrace);
            createTrace(g, startTrace, currentNode, g.trueEnd);
        }

        // Transform all traces into OCP's
        LinkedList<Trace> _results = new LinkedList<Trace>();
        for (Trace t : __results) {
            _results.addAll(t.processSubTraces(a.e.DEFAULT_TRACE_TYPE));
        }
        ;
        // Dedupe
        LinkedList<Trace> results = removeDupesFromTraces(_results);

        return results;
    }

    private void createTrace(Graph<Vertex, Edge> g,
                                    Trace currentTrace,
                                    Vertex currentNode, Vertex compareEnd) {

        currentTrace.addTraceNode(currentNode);    // Add the current node to the trace
        // If we are at the end, return the trace.
        if (currentNode.toString().compareTo(compareEnd.toString()) == 0) {
            return;    // No need to add anything
        }

        // More than one node then we split here
        // Should be a parallel
        if (g.outDegreeOf(currentNode) > 1 && currentNode.isAND) {
//			results.remove(currentTrace);
            if (__DEBUG)
                a.e.println("Splitting into parallel at " + currentNode + " until " + currentNode.corresponding);
            Trace subTrace = new Trace(g);
            currentTrace.removeTraceNode(currentNode);
            currentTrace.addTraceNode(subTrace);
            Vertex next = null;
            for (Edge e : g.outgoingEdgesOf(currentNode)) {
                Vertex newNode = g.getEdgeTarget(e);
                Trace newTrace = new Trace(g);
                newTrace.isTrace = true;
                newTrace.addTraceNode(currentNode);
                subTrace.addTraceNode(newTrace);
                createTrace(g, newTrace, newNode, currentNode.corresponding);
                next = g.getNext(currentNode.corresponding);

            }
            if (next != null)
                createTrace(g, currentTrace, next, compareEnd);
        } else
            for (Edge e : g.outgoingEdgesOf(currentNode)) {
                Vertex newNode = g.getEdgeTarget(e);
                currentTrace.addTraceEdge(e);
                createTrace(g, currentTrace, newNode, compareEnd);
            }

        return;
    }


    public LinkedList<Trace> removeDupesFromTraces(LinkedList<Trace> duped) {
        if (a.e.DEFAULT_DEDUPING_LEVEL == a.e.NO_DEDUPING) return duped;

        LinkedList<Trace> deDuped = new LinkedList<Trace>();
        LinkedList<String> hashedDecisionFreeGraphs = new LinkedList<String>();

        for (Trace g : duped) {
            String key = hashDecisionFreeGraph(g);

            if (!hashedDecisionFreeGraphs.contains(key)) {
                hashedDecisionFreeGraphs.add(key);
                deDuped.add(g);
            }
            //else a.e.println("Found duped graph " + key);
        }

        return deDuped;

    }

    /**
     * Remove duplicate decision free graphs.
     *
     * @param duped
     * @return
     */
    public LinkedList<Graph<Vertex, Edge>> removeDupesFromDecisionFreeGraphs(LinkedList<Graph<Vertex, Edge>> duped) {

        if (a.e.DEFAULT_DEDUPING_LEVEL == a.e.NO_DEDUPING) return duped;

        LinkedList<Graph<Vertex, Edge>> deDuped = new LinkedList<Graph<Vertex, Edge>>();
        LinkedList<String> hashedDecisionFreeGraphs = new LinkedList<String>();

        for (Graph<Vertex, Edge> g : duped) {
            String key = hashDecisionFreeGraph(g);

            if (!hashedDecisionFreeGraphs.contains(key)) {
                hashedDecisionFreeGraphs.add(key);
                deDuped.add(g);
            }
            else if(__DEBUG) a.e.println("Found duped graph " + key);
        }

        return deDuped;
    }


    /**
     * Create a hash for a graph. Function can be configured by setting DEDUPING_LEVEL in a.e class.
     * Not a great method and could use some improvement in the future
     *
     * @param in
     * @return String representing a hash of a particular graph based on the edges and nodes.
     */
    public String hashDecisionFreeGraph(Graph<Vertex, Edge> in) {
        String result = "";
        TreeSet<String> vertices = new TreeSet<String>();
        TreeSet<String> edges = new TreeSet<String>();
        for (Vertex v : in.vertexSet()) {
            vertices.add((v.toString().length() > 3 ? v.toString().substring(0, v.toString().length()) : v.toString().substring(0, 4)).trim());
        }
        for (Edge e : in.edgeSet()) {
            edges.add((e.id.toString().length() > 3 ? e.id.toString().substring(0, e.id.toString().length()) : e.id.toString().substring(0, 4)).trim());
        }

        for (String s : vertices.descendingSet())
            result += s;

        if (a.e.DEFAULT_DEDUPING_LEVEL == a.e.SIMPLE_DEDUPING)
            for (String s : edges.descendingSet())
                result += s;

//        a.e.println("Results = " + result); 
        return result;
    }


    /**
     * Create a set of decision free process model from an input model. This will split models to have only one start event etc.
     *
     * @param g Input graph
     * @return LinkedList<PGraph<Vertex,Edge>>
     */
    public LinkedList<Graph<Vertex, Edge>> makeDecisionFree(Graph<Vertex, Edge> g) {
        LinkedList<Graph<Vertex, Edge>> result = new LinkedList<Graph<Vertex, Edge>>();
        LinkedList<String> startEvents = new LinkedList<String>();
        LinkedList<String> joinGates = new LinkedList<String>();
        LinkedList<String> splitGates = new LinkedList<String>();

        boolean continuer = false;
        int startCounter = 0;
        int boundaryCounter = 0;
        // First Check for XOR's, boundaries and multiple starts
        for (Vertex v : g.vertexSet()) {
            if (v.isXOR) {
                if (g.inDegreeOf(v) > 1)    // Only add in joins
                    joinGates.add(v.id);
                if (g.outDegreeOf(v) > 1) {    // Only add in splits
                    splitGates.add(v.id);
                    continuer = true;
                }
            }

            if (v.type == GraphLoader.StartEvent) {

                if (!startEvents.contains(v.id)) {
                    startCounter++;
                    startEvents.add(v.id);
                }
            }
        }

        if (startCounter > 1) continuer = true;
        if (boundaryCounter > 1) continuer = true;

        if (!continuer) { // No XOR's or many starts found, nothing to do so lets add the graph to the return
            Graph<Vertex, Edge> pg = new Graph<Vertex, Edge>(Edge.class);
            pg.copyInGraph(g);
            result.add(pg);
            return result;
        }
        if (__DEBUG) {
            a.e.print(" Joins: ");
            for (String gateID : joinGates) {
                a.e.print(g.vertexRef.get(g.vertexIDRef.get(gateID)).toString() + ", ");
            }
            a.e.println("");
            a.e.print(" Splits: ");
            for (String gateID : splitGates) {
                a.e.print(g.vertexRef.get(g.vertexIDRef.get(gateID)).toString() + ", ");
            }
            a.e.println("");
        }
        // Handle Multiple Starts
        if (startCounter > 1) {
            LinkedList<Graph<Vertex, Edge>> corrected = new LinkedList<Graph<Vertex, Edge>>();
            for (String startID : startEvents) {
                Vertex startEvent = g.vertexRef.get(g.vertexIDRef.get(startID));

                // Make a new graph
                if (startEvent != null) {
                    Graph<Vertex, Edge> pg = new Graph<Vertex, Edge>(Edge.class);
                    pg.copyInGraph(g, startEvent);
                    if (__DEBUG) a.e.println("Processing " + startEvent.toString());
                    if (__DEBUG) a.e.println("Starting with " + pg.toString());
                    LinkedList<Graph<Vertex, Edge>> revised = makeDecisionFree(pg);
                    corrected.addAll(revised);
                    if (__DEBUG) a.e.println("Got: " + revised.toString());
                }
            }
            // Send each of the new subgraphs to the function again to deal with any and all XOR's
            for (Graph<Vertex, Edge> current : corrected) {
                LinkedList<Graph<Vertex, Edge>> revised = makeDecisionFree(current);

                //corrected.addAll(revised);
                result.addAll(revised);
            }
        }

        // XOR FOUND
        // Look for paths from each start event to our gate

        for (String startID : startEvents) {

            Vertex startEvent = g.vertexRef.get(g.vertexIDRef.get(startID));
            if (__DEBUG) a.e.println("Processing " + startEvent.toString());
            for (String gateID : splitGates) {
                // Small amount of error checking
                if (g.vertexIDRef.get(gateID) == null) {
                    if (__DEBUG) a.e.println("Can't find gateway " + gateID + " skipping fragmenting");
                    continue;
                }

                Vertex gateway = g.vertexRef.get(g.vertexIDRef.get(gateID));
                if (__DEBUG) a.e.println("Gateway: " + gateway.toString());

                FloydWarshallShortestPaths<Vertex, Edge> pather = new FloydWarshallShortestPaths<Vertex, Edge>(g);
                GraphPath<Vertex, Edge> gp = null;
                try {
                    gp = pather.getShortestPath(startEvent, gateway);
                } catch (Exception e) {
                    a.e.println("Error computing shortest path", a.e.FATAL);
                }
                if (gp != null) {
                    // Get vertex before the gate and list after the gate
                    Vertex predGate = null;
                    for (Edge pe : g.incomingEdgesOf(gateway)) {
                        predGate = pe.getSource();
                    }
                    LinkedList<Vertex> succGates = new LinkedList<Vertex>();
                    for (Edge pe : g.outgoingEdgesOf(gateway)) {
                        succGates.add(g.getEdgeTarget(pe));
                    }
                    // Remove the gateway

                    for (Vertex successor : succGates) {
                        // Now we have a path lets build a fragment
                        Graph<Vertex, Edge> pg = new Graph<Vertex, Edge>(Edge.class);
                        pg.copyInGraph(g, startEvent, gateway);
                        pg.removeV(gateway);
                        LinkedList<Graph<Vertex, Edge>> startFragment = makeDecisionFree(pg);
                        if (__DEBUG) a.e.println("222Made graph " + pg.toString());

                        Graph<Vertex, Edge> pg2 = new Graph<Vertex, Edge>(Edge.class);
                        pg2.copyInGraph(g, successor);
                        if (__DEBUG) a.e.println("Now working on " + pg2.toString());
                        LinkedList<Graph<Vertex, Edge>> endFragment = makeDecisionFree(pg2);
                        // Join the fragments together
                        // Replaced predGate with gateway to try to include gateway node
                        result.addAll(merge(startFragment, endFragment, predGate, gateway, successor));
                    }
                } else {
                    if (__DEBUG) a.e.println("GP was null for " + " from " + startEvent + " to " + gateway);
                }
            }
        }


        return result;
    }

    public LinkedList<Graph<Vertex, Edge>> merge(LinkedList<Graph<Vertex, Edge>> startFrags, LinkedList<Graph<Vertex, Edge>> endFrags, Vertex endOfPred, Vertex gateway, Vertex startOfSucc) {
        LinkedList<Graph<Vertex, Edge>> results = new LinkedList<Graph<Vertex, Edge>>();
        for (Graph<Vertex, Edge> predFrag : startFrags) {
            for (Graph<Vertex, Edge> succFrag : endFrags) {
                Graph<Vertex, Edge> pg = new Graph<Vertex, Edge>(Edge.class);
                pg.copyInGraph(predFrag);
                pg.copyInGraph(succFrag);
                pg.addV(endOfPred);
                pg.addV(gateway);
                pg.addV(startOfSucc);
                pg.addE(new Edge(endOfPred, gateway));
                pg.addE(new Edge(gateway, startOfSucc));
                results.add(pg);
//				a.e.println("Made a graph : " + pg.toString());
            }
        }
        return results;
    }


    public Graph<Vertex, Edge> seqComp(Graph<Vertex, Edge> lhs, Graph<Vertex, Edge> rhs) {
        Graph<Vertex, Edge> result = new Graph<Vertex, Edge>(Edge.class);


        return result;
    }


}
