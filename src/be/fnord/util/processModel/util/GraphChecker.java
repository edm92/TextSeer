package be.fnord.util.processModel.util;

import be.fnord.util.processModel.Edge;
import be.fnord.util.processModel.Graph;
import be.fnord.util.processModel.Vertex;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.alg.StrongConnectivityInspector;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.UUID;

/**
 * The following class is used to check the well formedness of a graph. The main method CheckGraph will also modify the graph as
 * best as possible to 'fix' any issues that would cause other functions to barf. After loading a graph it should be run through
 * the Check class.
 *
 * @author Evan Morrison edm92@uowmail.edu.au http://www.fnord.be
 *         Apache License, Version 2.0, Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */
public class GraphChecker {
    public static boolean __DEBUG = a.e.__DEBUG;
    public static boolean __INFO = a.e.__INFO;

    public static final int MAX_TIMES_TRY_FIX = 1;    // Cut off point for fixing gateway issues
    public static final int MAX_PATH_LENGTH = 100;    // Cut off point for processing gateway fixes


    private int NumberFixed = 0;
    LinkedList<String> startNodes = new LinkedList<String>();
    LinkedList<String> endNodes = new LinkedList<String>();


    /**
     * Test if gateways in a process model appear to be processable.
     *
     * @param g
     * @return
     */
    private boolean testGateways(Graph<Vertex, Edge> g) {
        int openAND = 0;
        int closeAND = 0;
        for (Vertex v : g.vertexSet()) {
            // Count number of gateways first
            if (v.isGateway) {
                if (v.getCorresponding() == null && !v.isXOR) {    // Check if we labeled a corresponding gateway
                    if (__DEBUG) a.e.println("Didn't find a corresponding gate for " + v.toString());
                    return false;
                } else {
                    if (!v.isXOR) // ignore corresponding XOR's, we'll throw them out anyway.
                        if (v.getCorresponding().getCorresponding().id.toString().compareTo(v.id.toString()) != 0) {
                            if (__DEBUG)
                                a.e.println("Corresponding gate has been associated to something else " + v.toString() + "; " + v.getCorresponding().toString());
                            if (__DEBUG)
                                a.e.println("Corresponding gate has been associated to something else " + v.getCorresponding().toString() + "; " + v.getCorresponding().getCorresponding().toString());
                            return false;
                        }
                }
                if (g.inDegreeOf(v) > 1) closeAND++;
                if (g.outDegreeOf(v) > 1) openAND++;

            }
        }
        if (openAND == closeAND) {
            return true;
        }
        return false;
    }

    public boolean CheckEventsAndGateways(Graph<Vertex, Edge> g) {
        LinkedList<Vertex> multiOuts = new LinkedList<Vertex>();
        LinkedList<String> unconnectedNodes = new LinkedList<String>();

        // Find the start and end nodes, make sure we know what they are
        // Hunt out any boundaries and create an edge to them
        for (Vertex v : g.vertexSet()) {
            // Check if start node
            if (g.inDegreeOf(v) == 0) {
                startNodes.add(v.toString());
                v.type = GraphLoader.StartEvent;
            }
            // Check if end node
            if (g.outDegreeOf(v) == 0) {
                endNodes.add(v.toString());
                v.type = GraphLoader.EndEvent;
            }
            // Check if there is a boundary and create an edge between the node and the boundary
            if (v.boundaryRefs.size() > 0) {
                for (String ref : v.boundaryRefs) {
                    if (g.vertexIDRef.containsKey(ref)) {
                        // Create an edge between the two
                        String NAME = g.vertexIDRef.get(ref);
                        Vertex v2 = g.vertexRef.get(NAME);
                        Edge e = new Edge(v, v2);
                        g.addE(e);
                    }
                }
            }
            // Find tasks with multi outgoing edges and create XOR gateway
            if (g.outDegreeOf(v) > 1 && !v.isGateway) {
                multiOuts.add(v);
            }
            // Convert OR's to and/XORS -- Analyst note, should not model with OR's
            if (v.type == GraphLoader.InclusiveGateway) {
                v.type = GraphLoader.ExclusiveGateway;
                v.isOR = false;
                v.isXOR = true;
                v.isGateway = true;
            }
        }
        for (Vertex v : multiOuts) {
            Vertex v2 = new Vertex("subGate" + UUID.randomUUID(), GraphLoader.ExclusiveGateway);
            v2.isSubstructural = true;
            v2.isGateway = true;
            v2.isXOR = true;
            g.addV(v2);
            LinkedList<Edge> removeListEdges = new LinkedList<Edge>();
            for (Edge e : g.outgoingEdgesOf(v)) {
                removeListEdges.add(e);
                Edge ee = new Edge(v2, e.getTarget());
                g.addE(ee);
            }
            for (Edge e : removeListEdges) {
                g.removeE(e);
            }
            Edge e = new Edge(v, v2);
            g.addE(e);
        }
        // Go through list looking for elements in both
        for (String s : startNodes) {
            for (String e : endNodes) {
                if (s.compareTo(e) == 0) {
                    unconnectedNodes.add(s);
                }
            }
        }
        // Remove these elements
        for (String u : unconnectedNodes) {
            startNodes.remove(u);
            endNodes.remove(u);
            g.removeVertex(g.vertexRef.get(u));
            g.vertexRef.remove(u);
            g.existingVertices.remove(u);
            if (__INFO) a.e.println("Removed unconnected node: " + u);
        }

        // Check if we have at least one start and end
        if (!(startNodes.size() > 0 && endNodes.size() > 0)) return false;

        HashSet<Vertex> fixDoubles = new HashSet<Vertex>();
        HashSet<Vertex> fixXORs = new HashSet<Vertex>();
        // Test all non-gateway nodes for only one entry and one exit
        for (Vertex v : g.vertexSet()) {
            if (!(v.isAND || v.isXOR || v.isOR)) {
                if (g.inDegreeOf(v) > 1 || g.outDegreeOf(v) > 1) {
                    if (__DEBUG)
                        a.e.println("Vertex " + v.name + " has a large in/out degree for a non-gateway, converting to XOR");
                    fixXORs.add(v);
                    // removed return false
                }
            } else {    // Ensure that gateways are either a split or a join
                if (g.inDegreeOf(v) > 1 && g.outDegreeOf(v) > 1) {
                    if (__DEBUG) a.e.println("Gateway " + v.name + " is used as both a split and a join.");
                    fixDoubles.add(v);
                    // removed return false
                }
            }
        }
        for (Vertex v : fixXORs) {
            if (g.outDegreeOf(v) > 1) {
                Vertex v2 = new Vertex("newXORGate-" + UUID.randomUUID(), GraphLoader.ExclusiveGateway);
                v2.isXOR = true;
                v2.isGateway = true;
                v2.isSubstructural = true;
                g.addV(v2);

                LinkedList<Edge> removeListEdges = new LinkedList<Edge>();
                for (Edge e : g.outgoingEdgesOf(v)) {
                    Edge ee = new Edge(v2, e.getTarget());
                    ee.setName(e.name);
                    removeListEdges.add(e);
                    g.addE(ee);
                }
                for (Edge e : removeListEdges)
                    g.removeE(e);
                g.addE(new Edge(v, v2));
                if (g.inDegreeOf(v) > 1 && g.outDegreeOf(v) > 1) {
                    fixDoubles.add(v);
                }
            }
            if (g.inDegreeOf(v) > 1) {
                Vertex v2 = new Vertex("newXORGate-" + UUID.randomUUID(), GraphLoader.ExclusiveGateway);
                v2.isXOR = true;
                v2.isGateway = true;
                v2.isSubstructural = true;
                g.addV(v2);
                LinkedList<Edge> removeListEdges = new LinkedList<Edge>();
                for (Edge e : g.incomingEdgesOf(v)) {
                    Edge ee = new Edge(e.getSource(), v2);
                    ee.setName(e.name);
                    removeListEdges.add(e);
                    g.addE(ee);
                }
                for (Edge e : removeListEdges) {
                    g.removeE(e);
                }
                g.addE(new Edge(v2, v));
                if (g.inDegreeOf(v) > 1 && g.outDegreeOf(v) > 1) {
                    fixDoubles.add(v);
                }
            }
        }

        for (Vertex v : fixDoubles) {
            Vertex v2 = new Vertex("newXORGate-" + UUID.randomUUID(), v.type);
            g.addV(v2);
            v2.isXOR = true;
            v2.isGateway = true;
            v2.isSubstructural = true;
            for (Edge e : g.outgoingEdgesOf(v)) {
                g.removeE(e);
                e.setSource(v2);
                g.addE(e);
            }
            g.addE(new Edge(v, v2));
        }

        // Fix splits and joins labels
        for (Vertex v : g.vertexSet()) {
            if (g.inDegreeOf(v) > 1) v.isJoin = true;
            if (g.outDegreeOf(v) > 2) v.isSplit = true;
            if (v.isSplit && v.isJoin) {
                a.e.println("GraphChecker.CheckEventsAndGateways failed to fix splits and joins on " + v.toString());
                return false;
            }
        }


        return true;
    }

    /**
     * Check the graph for good structures, fix some if possible and remove the rest
     * This function will add a substructural start and end node to the process which means that the process will only end
     * up with a single start and a single end.
     *
     * @return True if good structured model, false if badly structured model beyond repair
     */
    public boolean CheckGraph(Graph<Vertex, Edge> g) {
        // Fix the gateways and boundaries first
        if (!CheckEventsAndGateways(g)) return false;

        // This includes a parallel gateway for splitting multiplestarts
        if (startNodes.size() > 1) {
            Vertex newStart = new Vertex("newStart-" + UUID.randomUUID(), GraphLoader.ParallelGateway);
            newStart.isAND = true;
            newStart.isSplit = true;
            newStart.isSubstructural = true;
            newStart.corresponding = null;
            g.addV(newStart);
            g.trueStart = newStart;
            for (String s : startNodes) {
                // Create edge to each node
                Edge newEdge = new Edge(newStart, g.vertexRef.get(s));
                g.addE(newEdge);
            }
        } else g.trueStart = g.vertexRef.get(startNodes.get(0));

        if (endNodes.size() > 1) {
            Vertex newEnd = new Vertex("newEnd-" + UUID.randomUUID(), GraphLoader.ParallelGateway);
            newEnd.isAND = true;
            newEnd.isJoin = true;
            newEnd.isSubstructural = true;
            g.addV(newEnd);
            g.trueEnd = newEnd;

            if (g.trueStart.type == GraphLoader.ParallelGateway) g.trueStart.setCorresponding(newEnd);
            for (String s : startNodes) {
                // Create edge to each node
                Edge newEdge = new Edge(g.vertexRef.get(s), newEnd);
                g.addE(newEdge);
            }
        } else g.trueEnd = g.vertexRef.get(endNodes.get(0));
        // Do cleanup (add in missing gateway to start of process)
        if (g.trueEnd.type == GraphLoader.ParallelGateway && g.trueEnd.corresponding == null) {
            Vertex newStartGate = new Vertex("newStartGate-" + UUID.randomUUID(), GraphLoader.ParallelGateway);
            newStartGate.isAND = true;
            newStartGate.isSplit = true;
            newStartGate.isSubstructural = true;
            newStartGate.setCorresponding(g.trueEnd);
            g.addV(newStartGate);
            // Replace edges from the start node to vertices and recreate from the new start node gateway
            LinkedList<Edge> removeList = new LinkedList<Edge>();
            for (Edge e : g.outgoingEdgesOf(g.trueStart)) {
                Edge newEdge = new Edge(newStartGate, e.getTarget());
                g.addE(newEdge);
                removeList.add(e);
            }
            for (Edge e : removeList) {
                g.removeE(e);
            }
            // Add and edge from the start to the new Gateway
            Edge newEdge = new Edge(g.trueStart, newStartGate);
            g.addE(newEdge);
        }
        // Add in missing gateway to the end of process
        if (g.trueStart.type == GraphLoader.ParallelGateway && g.trueStart.corresponding == null) {
            Vertex newEndGate = new Vertex("newEndGate-" + UUID.randomUUID(), GraphLoader.ParallelGateway);
            newEndGate.isAND = true;
            newEndGate.isSplit = true;
            newEndGate.isSubstructural = true;
            newEndGate.setCorresponding(g.trueStart);
            g.addV(newEndGate);
            // Replace edges from the start node to vertices and recreate from the new start node gateway
            LinkedList<Edge> removeList = new LinkedList<Edge>();
            for (Edge e : g.incomingEdgesOf(g.trueEnd)) {
                Edge newEdge = new Edge(e.getSource(), newEndGate);
                g.addE(newEdge);
                removeList.add(e);
            }
            for (Edge e : removeList) {
                g.removeE(e);
            }
            // Add and edge from the start to the new Gateway
            Edge newEdge = new Edge(newEndGate, g.trueEnd);
            g.addE(newEdge);
        }


        // Create copy of graph and connect the ends to the starts
        Graph<Vertex, Edge> copy = g.copyGraph(g);
        for (String s : startNodes) {
            for (String e : endNodes) {
                Edge newEdge = new Edge(g.vertexRef.get(e), g.vertexRef.get(s));
                copy.addE(newEdge);
            }
        }
        // Check if each node is reachable
        StrongConnectivityInspector<Vertex, Edge> sci =
                new StrongConnectivityInspector<Vertex, Edge>(copy);
        if (!sci.isStronglyConnected()) return false;

        // Check if there are correct gateways
        fixGateways(g); // See if we can fix the gateways first then test them
        if (!testGateways(g)) return false;

        // Set start and end nodes:
        g.getStarts();
        g.getEnds();

        return true;

    }

    /**
     * Search graph for corresponding gateways and attempt to fix small structural issues
     *
     * @param g input Graph
     * @return return true if no errors needed fixing and false if there were some errors found
     */
    @SuppressWarnings("unused")
    private boolean fixGateways(Graph<Vertex, Edge> g) {
        int openAND = 0;
        int closeAND = 0;
        boolean result = true;
        for (Vertex v : g.vertexSet()) {
            // Count number of gateways first
            if (v.type == GraphLoader.ParallelGateway) {
                if (g.inDegreeOf(v) > 1) closeAND++;
                if (g.outDegreeOf(v) > 1) openAND++;
            }
        }
        String msg = "";
        if (openAND == closeAND) {
            msg = "gateway count matches";
            // Add corresponding gates
            fixCorrespondingGates(g);
        } else {
            msg = "gateway count mismatch";

            if (closeAND < openAND)
                msg += " ; less closing AND gates than opening AND gates";
            else
                msg += " ; more closing AND gates than opening AND gates";
            fixLessCloseGates(g);
        }
        if (__DEBUG && false) a.e.println("msg:" + msg);    // This is for detailed debugging only
        if (++NumberFixed < MAX_TIMES_TRY_FIX) fixGateways(g);
        return result;
    }


    private void fixLessCloseGates(Graph<Vertex, Edge> g) {
        TreeMap<String, Integer> scoreTallyOut = new TreeMap<String, Integer>();
        TreeMap<String, Integer> scoreTallyIn = new TreeMap<String, Integer>();
        LinkedList<String> gateways = new LinkedList<String>();
        // Make paths through the graph
        KShortestPaths<Vertex, Edge> sp = new KShortestPaths<Vertex, Edge>(g, g.trueStart, MAX_PATH_LENGTH);
        for (GraphPath<Vertex, Edge> gp : sp.getPaths(g.trueEnd)) {
            // Walk along paths keeping score of open and close gateways
            Vertex start = gp.getStartVertex();
            Vertex end = gp.getEndVertex();
            Vertex current = start;
            //System.err.println("Viewing current path " + gp);
            //System.err.println("Starting with " + start);
            do {
                if (current == null) break;
                if (current.type == GraphLoader.ParallelGateway) {
                    if (g.inDegreeOf(current) > 1) {
                        scoreTallyIn.put(current.name, g.inDegreeOf(current));
                        gateways.add(current.name);
                    } else if (g.outDegreeOf(current) > 1) {
                        scoreTallyOut.put(current.name, g.outDegreeOf(current));
                        gateways.add(current.name);
                    }
                }
                current = next(current, gp, g);
            } while (current != null || current != end);


            // Now check along path
            String last = "";
            boolean first = true;
            for (String currentGate : gateways) {
                if (first) {
                    last = currentGate;
                    first = false;
                    continue;
                }
                ; // Skip first gateway

                if (!(scoreTallyOut.get(last) == null || scoreTallyIn.get(currentGate) == null)) {
                    if (scoreTallyOut.get(last) > scoreTallyIn.get(currentGate)) {
                        if (__INFO)
                            a.e.println("Found point of interest: " + last + ":" + scoreTallyOut.get(last) + " vs. " + currentGate + ":" + scoreTallyIn.get(currentGate));
                    }//else System.err.println("No point of interest: " + last + ":" + scoreTallyOut.get(last) + " vs. " + currentGate + ":" + scoreTallyIn.get(currentGate));
                }
                last = currentGate;
            }
        }
    }

    private void fixCorrespondingGates(Graph<Vertex, Edge> g) {
        TreeMap<String, Integer> scoreTallyOut = new TreeMap<String, Integer>();
        TreeMap<String, Integer> scoreTallyIn = new TreeMap<String, Integer>();
        LinkedList<String> gateways = new LinkedList<String>();

        KShortestPaths<Vertex, Edge> sp = new KShortestPaths<Vertex, Edge>(g, g.trueStart, MAX_PATH_LENGTH);
        for (GraphPath<Vertex, Edge> gp : sp.getPaths(g.trueEnd)) {
            // Walk along paths keeping score of open and close gateways
            Vertex start = gp.getStartVertex();
            Vertex end = gp.getEndVertex();
            Vertex current = start;
            //System.err.println("Viewing current path " + gp);
            //System.err.println("Starting with " + start);
            do {
                if (current == null) break;
                if (current.type == GraphLoader.ParallelGateway) {
                    //scoreTallyIn.put(current.name, 0);
                    if (g.inDegreeOf(current) > 1) {
                        // gatway join
                        scoreTallyIn.put(current.name, g.inDegreeOf(current));
                        gateways.add(current.name);
                    } else if (g.outDegreeOf(current) > 1) {
                        scoreTallyOut.put(current.name, g.outDegreeOf(current));
                        gateways.add(current.name);
                    }
                }
                current = next(current, gp, g);
            } while (current != null || current != end);
        }

        loopyfix(gateways, scoreTallyIn, scoreTallyOut, g);

    }

    private void loopyfix(LinkedList<String> gateways,
                          TreeMap<String, Integer> scoreTallyIn,
                          TreeMap<String, Integer> scoreTallyOut,
                          Graph<Vertex, Edge> g) {
        if (gateways.size() < 1) return;
        String last = gateways.get(0);
        boolean starter = true;
        LinkedList<String> rmList = new LinkedList<String>();
        LinkedList<String> contList = new LinkedList<String>();
        for (String s : gateways) {
            if (starter) {
                starter = false;
                continue;
            }
            ;
            if (scoreTallyOut.get(last) != null && scoreTallyIn.get(s) != null)
                if (scoreTallyOut.get(last) > 1 && scoreTallyIn.get(s) > 1) {
                    Vertex pntSt = null;
                    Vertex pntEd = null;
                    for (Vertex v : g.vertexSet()) {
                        if (v.name.compareTo(last) == 0)
                            pntSt = v;
                    }
                    for (Vertex v : g.vertexSet()) {
                        if (v.name.compareTo(s) == 0)
                            pntEd = v;
                    }
                    if (pntSt != null && pntEd != null) {
                        //System.err.println("Setting correspoinding" + pntSt + "," + pntEd);
                        pntSt.setCorresponding(pntEd);
                        rmList.add(last);
                        rmList.add(s);
                    }
                }
            last = s;
        }
        for (String ss : gateways) {
            if (!rmList.contains(ss))
                contList.add(ss);
        }
        if (contList.size() > 1) loopyfix(contList, scoreTallyIn, scoreTallyOut, g);

    }

    private Vertex next(Vertex current, GraphPath<Vertex, Edge> gp, Graph<Vertex, Edge> g) {
        for (Edge e : g.outgoingEdgesOf(current)) {
            if (gp.getEdgeList().contains(e)) {
                return gp.getGraph().getEdgeTarget(e);
            }
        }
        return null;
    }

}
