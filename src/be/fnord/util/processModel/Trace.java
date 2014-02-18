package be.fnord.util.processModel;

import be.fnord.util.functions.OCP.OrderConstrainedPartitionList;
import be.fnord.util.functions.OCP.PartitionList;
import be.fnord.util.functions.OCP.PartitionListElement;
import be.fnord.util.functions.OCP.PartitionListItem;

import java.util.LinkedList;
import java.util.TreeMap;

/**
 * @param <v>
 * @param <e>
 * @author Evan Morrison edm92@uowmail.edu.au http://www.fnord.be
 *         Apache License, Version 2.0, Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

public class Trace extends Vertex {
    public boolean INCLUDE_EDGE = false;
    private static final long serialVersionUID = 1L;

    public Trace(String _name) {
        super(_name);
        isTrace = true;
    }

    public Trace(Graph<Vertex, Edge> g) {
        super();
        parentGraph = g;
        isTrace = true;
        allTraces.put(this.name, this);
    }

    public Graph<Vertex, Edge> parentGraph;

    public boolean isSubTrace = false;

    // List of all graphs
    public static TreeMap<String, Trace> allTraces = new TreeMap<String, Trace>();
    //public LinkedList<String> subTraces = new LinkedList<String>(); // Defined in Graph


    LinkedList<Vertex> nodes = new LinkedList<Vertex>();

    public LinkedList<Vertex> getNodes() {
        return nodes;
    }

    public void setNodes(LinkedList<Vertex> nodes) {
        this.nodes = nodes;
    }

    public LinkedList<Edge> getEdges() {
        return edges;
    }

    public void setEdges(LinkedList<Edge> edges) {
        this.edges = edges;
    }

    LinkedList<Edge> edges = new LinkedList<Edge>();
    public boolean parallel = false;

    // Need to trigger this when finished using toVectorArray!
    public static void reset() {
        successStores = new TreeMap<String, LinkedList<Vertex>>();
    }

    public void addTraceNode(Vertex vert) {
        nodes.add(vert);
    }

    public void addTraceEdge(Edge edge) {
        edges.add(edge);
    }

    public void removeTraceNode(Vertex vert) {
        if (nodes.contains(vert))
            nodes.remove(vert);
    }

    public void removeTraceEdge(Edge edge) {
        if (edges.contains(edge))
            edges.remove(edge);
    }

    public void addSubTrace(String t) {
        this.subTraces.add(t);
    }

    public Trace copy() {
        Trace result = new Trace(parentGraph);
        result.isTrace = true;
        for (String t : this.subTraces) {
            result.addSubTrace(t);
        }

        for (Vertex vert : nodes) {
            result.addTraceNode(vert);
        }
        for (Edge edge : edges) {
            result.addTraceEdge(edge);
        }
        return result;
    }

    public static int ten = 10;


    /**
     * Once we are happy with our trace, we will create subtraces which are the actual <v,v,v,v> traces
     *
     * @param SIMPLE_OR_FULL either SIMPLE_TRACES or FULL_TRACES
     * @return
     */
    public LinkedList<Trace> processSubTraces(int SIMPLE_OR_FULL) {
        LinkedList<Trace> results = new LinkedList<Trace>();
        LinkedList<LinkedList<Vertex>> vertexArrays = toVertexArray();
        for (LinkedList<Vertex> vertexArray : vertexArrays) {
            if (SIMPLE_OR_FULL == a.e.SIMPLE_TRACES) {
                results.add(processSimpleSubTraces(vertexArray));
            } else if (SIMPLE_OR_FULL == a.e.FULL_TRACES) {
                results.add(processFullSubTraces(vertexArray));
            }
        }
        return results;
    }

    // TODO Make this happen! Create this
    private Trace processFullSubTraces(LinkedList<Vertex> vertexArray) {
        return null;
    }

    private Trace processSimpleSubTraces(LinkedList<Vertex> vertexArray) {
        Trace newTrace = new Trace(parentGraph);

        this.addSubTrace(newTrace.name);
        newTrace.isSubTrace = true;
        Vertex previous = null;
        for (Vertex v : vertexArray) {
            newTrace.addTraceNode(v);

            // Add in the edges
            if (previous != null) {
                Edge e = this.parentGraph.getEdge(previous, v);
                newTrace.addTraceEdge(e);
            }

            previous = v;
        }
        return newTrace;
    }


    /**
     * toVertexArray should take as input a trace and then convert that trace into a list of verticies.
     * The actual return is a list of lists of vertices. We have multiple returns because for each array conduct an
     * order constrained permutation on parallel activities.
     *
     * @return
     */
    public LinkedList<LinkedList<Vertex>> toVertexArray() {
        // TODO Need to fix this
        Trace.reset();    // This is a really weird hack and will screw with parallel processing.
        // Needed because for some reason the processGroupsOfParallelNodes returns different results

        return _cleanVertexArray(_toVertexArray(KEEP_START_END_NODES_IN_TRACE));
    }

    public static final boolean IGNORE_START_END_NODES_IN_TRACE = true;
    public static final boolean KEEP_START_END_NODES_IN_TRACE = false;

    public static int num = 10;

    public LinkedList<LinkedList<Vertex>> _cleanVertexArray(LinkedList<LinkedList<Vertex>> input) {
        LinkedList<LinkedList<Vertex>> output = new LinkedList<LinkedList<Vertex>>();
        for (LinkedList<Vertex> currentTrace : input) {
            LinkedList<String> _existing = new LinkedList<String>();
            LinkedList<Vertex> _replacement = new LinkedList<Vertex>();

            for (Vertex v : currentTrace) {
                if (!_existing.contains(v.toString())) {
                    _existing.add(v.toString());
                    if (!v.isSubstructural)
                        _replacement.add(v);
                }
            }
            if (_replacement.size() > 0)
                output.add(_replacement);
        }

        return output;
    }

    public LinkedList<LinkedList<Vertex>> _toVertexArray(boolean SHOULD_IGNORE_START_END_NODES) {
        // For the main store of vertices
        LinkedList<LinkedList<Vertex>> mainResult = new LinkedList<LinkedList<Vertex>>();

        LinkedList<Vertex> newResult = new LinkedList<Vertex>();
        LinkedList<Vertex> currentLL = new LinkedList<Vertex>();

        num += 10;
        boolean inSquares = false;
        // Iterate through each node in the trace
        for (Vertex v : nodes) {
            // Check if the node is actually a trace trapped in a vertex body
            if (v.isTrace) {    // It is, so lets process it as a trace.
                // In square is used to group traces next to each other, i.e. <a, [<b,c>,<d,e>],f >
                // for the group in the square <b,c>,<d,e>, the group should be OCP'd together (to get the result:
                // <b,c,d,e>, <b,d,c,e>, <b,d,e,c>, <d,b,c,e> ... etc.
                if (!inSquares) {
                    inSquares = true;
                }
                Trace t = (Trace) v;    // We expand the this trace into a list of lists of elements.
                LinkedList<LinkedList<Vertex>> newvArray = t._toVertexArray(IGNORE_START_END_NODES_IN_TRACE);
                // Result should be a list of possible alternatives.
                // [<s, a,b, e>,<s, c,d, e>,<s, d,f, e>]

                for (LinkedList<Vertex> ele : newvArray) {
                    //a.e.println("Adding " + _num + " :: " + ele);
                    currentLL.addAll(ele);

                }
                //currentLL.addAll(newvArray);

            } else {
                if (inSquares) {
                    inSquares = false;
                    // Process LL's
//						a.e.print("1--");
                    LinkedList<Vertex> returnedResults = _processGroupsOfParallelNodes(currentLL);
                    newResult.addAll(returnedResults); // .substring(0, currentString.length()/2));
                }

                newResult.add(v);
            }

            if (v == nodes.getLast()) {
                if (inSquares) {
                    inSquares = false;
//						a.e.print("2--");
                    LinkedList<Vertex> returnedResults = _processGroupsOfParallelNodes(currentLL);
                    newResult.addAll(returnedResults); // .substring(0, currentString.length()/2));
                }
            }

        }
        mainResult.add(newResult);
        return mainResult;
    }

    /**
     * Weird Hack variable. Seems to be needed but would like to remove it.
     * Technically it speeds up processing a little, but will also screw with parallel processing
     */
    public static TreeMap<String, LinkedList<Vertex>> successStores = new TreeMap<String, LinkedList<Vertex>>();

    public LinkedList<Vertex> _processGroupsOfParallelNodes(LinkedList<Vertex> currentLL) {
        if (successStores.containsKey(currentLL.toString().trim())) {
            return successStores.get(currentLL.toString().trim());
        }


        LinkedList<LinkedList<Vertex>> newLL = new LinkedList<LinkedList<Vertex>>();
        LinkedList<LinkedList<Vertex>> newLL2 = new LinkedList<LinkedList<Vertex>>();
        newLL.add(currentLL);
        newLL2 = processGroupsOfParallelNodes(newLL);
        // If there is some weird computational error, recompute
        if (newLL2.size() == 0) {
            if (successStores.containsKey(currentLL.toString().trim()))
                return successStores.get(currentLL.toString().trim());
            return currentLL;
        }
//		a.e.println("Given currentLL I won! ;" + currentLL + ";");
        successStores.put(currentLL.toString().trim(), newLL2.getFirst());
        return newLL2.getFirst();
    }

    public LinkedList<LinkedList<Vertex>> processGroupsOfParallelNodes(LinkedList<LinkedList<Vertex>> currentLL) {
        Vertex start = currentLL.getFirst().getFirst();
        Vertex end = currentLL.getLast().getLast();
        LinkedList<LinkedList<Vertex>> newResult = new LinkedList<LinkedList<Vertex>>();


        for (LinkedList<Vertex> cc : currentLL) {
//			cc.remove(start);
//			cc.remove(end);

            LinkedList<Vertex> removeList = new LinkedList<Vertex>();
            PartitionListItem<Vertex> OCS = new PartitionListItem<Vertex>();
            PartitionListElement<Vertex> list = new PartitionListElement<Vertex>();
            //a.e.println("Gonna try working with the following:");
            a.e.incIndent();
            for (Vertex s : cc) {
                if (s == start) {
                    removeList.add(s);
                    list = new PartitionListElement<Vertex>();
                } else if (s == end) {
                    removeList.add(s);
                    OCS.add(list);
                } else {
                    //a.e.println(s.name);
                    list.add(s);
                }
            }
            for (Vertex s : removeList)
                cc.remove(s);
            a.e.decIndent();

            // We now should be able to create an OCP of the elements in CC
            int numIterations = 0;
            PartitionList<Vertex> OCPresults1 = null; // OrderConstrainedPartitionList.makePartitions( list);
            PartitionList<Vertex> OCPresults2 = null;
            PartitionList<Vertex> OCPresults3 = null;
            for (PartitionListElement<Vertex> _list : OCS) {
                switch (numIterations) {
                    case 0:
                        OCPresults1 = OrderConstrainedPartitionList.makePartitions(_list);
                        numIterations++;
                        break;
                    case 1:
                        OCPresults2 = OrderConstrainedPartitionList.makePartitions(_list);
                        OCPresults3 = OrderConstrainedPartitionList.joinSets(OCPresults1, OCPresults2);
                        // Shuffle back
                        OCPresults1 = OCPresults2;
                        OCPresults2 = OCPresults3;
                        numIterations++;
                        break;
                    case 2:
                    default:
                        OCPresults2 = OrderConstrainedPartitionList.makePartitions(_list);
                        OCPresults3 = OrderConstrainedPartitionList.joinPartitionedSets(OCPresults1, OCPresults2);
                        // Shuffle back
                        OCPresults1 = OCPresults2;
                        OCPresults2 = OCPresults3;
                        break;
                }
            }


            // Do this for each OCP
            if (OCPresults3 != null)
                for (PartitionListItem<Vertex> _item : OCPresults3) {
                    for (PartitionListElement<Vertex> _ele : _item) {
                        LinkedList<Vertex> newCC = new LinkedList<Vertex>();
                        newCC.push(start);
                        for (Vertex v : _ele) {
                            newCC.add(v);
                        }
                        newCC.add(end);
                        newResult.add(newCC);
                    }
                }


        }

//		a.e.println("returning results! : " + newResult);
        return newResult;
    }


    public String toString() {
        String result = "Trace<";

        // Changed for now, replace with functin below later TODO~!
        for (Vertex v : nodes) {
            result += v.name + ",";
        }
//		for(Vertex v : this.toVertexArray()){			
//			result += v.name + ",";
//			if(INCLUDE_EDGE){
//				if(i < edges.size())
//					result = result.substring(0, result.length() - 1) + "->(" + edges.get(i++) + ")->"; 
//			}
//
//		}
        if (nodes.size() > 1) result = result.substring(0, result.length() - 1);
        result += ">";

        return result;
    }
}
