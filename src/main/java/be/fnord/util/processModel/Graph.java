package be.fnord.util.processModel;

import be.fnord.util.processModel.visual.jungViewer;
import org.apache.log4j.Logger;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.util.*;

/**
 * @param <v>
 * @param <e>
 * @author Evan Morrison edm92@uowmail.edu.au http://www.fnord.be
 *         Apache License, Version 2.0, Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */
public class Graph<v extends Vertex, e extends Edge> extends DefaultDirectedGraph<Vertex, Edge> implements org.jgrapht.Graph<Vertex, Edge> {
    public static boolean __DEBUG = a.e.__DEBUG;

    protected transient static Logger logger = Logger.getLogger("GraphClass");
    public static boolean SKIP_EMPTY_WFF_PATHS = true;
    public static final int MAX_PATH_LENGTH = 100; // Set higher if your graph isn't working out


//    public HashSet<WFF> WFFs; // Deleted on 1/4
    public String filename = "";
    public String documentation = "";
    //	public String name = "";
//	public String id = "";
    public String QOS = "";
    private transient UUID ID;

    public String getID() {
        return ID.toString();
    }

    ;
    public TreeMap<String, String> WFFMap = new TreeMap<String, String>();
    public int addition = 0;
    public char currentWFF = 'a';
//	public t trueStart;
//	public t trueEnd;

    // List of all graphs
    public static TreeMap<String, Graph<Vertex, Edge>> allGraphs = new TreeMap<String, Graph<Vertex, Edge>>();
    //	public static TreeMap<String, Trace<Vertex,Edge>> traceRef = new TreeMap<String,Trace<Vertex,Edge>>();
    // Subgraph of this graph
    public LinkedList<String> subGraphs = new LinkedList<String>();
    // Subtrace of this graph
    public LinkedList<String> subTraces = new LinkedList<String>();

    public List<Vertex> getEnds() {
        ends = new LinkedList<Vertex>();
        for (Vertex v : this.vertexSet()) {
            if (this.outDegreeOf(v) == 0) {
                ends.add(v);
                this.trueEnd = v;
            }
        }
        return ends;
    }

    public List<Vertex> getStarts() {
        starts = new LinkedList<Vertex>();
        for (Vertex v : this.vertexSet()) {
            if (this.inDegreeOf(v) == 0) {
                this.trueStart = v;
                starts.add(v);
            }
        }
        return starts;
    }

    public Vertex getNext(Vertex current) {
        if (this.edgesOf(current).size() > 0) {
            for (Edge e : this.edgesOf(current)) {
                if (e.source == current) return e.target;
            }
        }
        return null;
    }

    /**
     * Copy a graph into the Graph structure
     *
     * @param g
     */
    public void copyInGraph(Graph<Vertex, Edge> g) {
        if (this.subGraphs.contains(g.name)) return;

        for (Vertex v : g.vertexSet())
            this.addV(v);
        for (Edge e : g.edgeSet())
            this.addE(e);
        this.subGraphs.add(g.name);
        if (!allGraphs.containsKey(g.name))
            allGraphs.put(g.name, g);
    }

    /**
     * Copy a graph from the start vertex.
     * 
     */
    public void copyInGraph(Graph<Vertex, Edge> g, Vertex start) {
        copyInGraph(g, start, null);
        return;
    }

    /**
     * Copy a graph from the start vertex to an end vertex.
     */
    public void copyInGraph(Graph<Vertex, Edge> g, Vertex start, Vertex end) {
        if (this.subGraphs.contains(g.name)) return;

        HashSet<String> vToAdd = new HashSet<String>();
        HashSet<String> eToAdd = new HashSet<String>();
        Vertex current = start;
        searchAndAdd(vToAdd, eToAdd, current, g, end);

        for (String vID : vToAdd) {
            Vertex copy = g.vertexRef.get(g.vertexIDRef.get(vID)).makeCopy();
            this.addV(copy);
        }
        for (String eID : eToAdd) {
            Edge copy = g.edgeRef.get(eID).makeCopy();
            if (!this.containsVertex(copy.source)) {
                this.addV(copy.source);
            }
            if (!this.containsVertex(copy.target)) {
                this.addV(copy.target);
            }
            this.addE(copy);
        }

    }

    /**
     * Trawl through the graph one node at a time looking forward and building up hashsets of nodes and edges.
     *
     * @param vToAdd
     * @param eToAdd
     * @param current
     * @param g
     */
    public void searchAndAdd(HashSet<String> vToAdd, HashSet<String> eToAdd, Vertex current, Graph<Vertex, Edge> g, Vertex end) {
//		System.out.println("Called searchAndAdd with " + current.toString() );
        for (Edge e : g.outgoingEdgesOf(current)) {
            eToAdd.add(e.toString());
            Edge foundEdge = g.edgeRef.get(e.toString());
            if (foundEdge == null) {
                if (__DEBUG) a.e.println("Found an edge from " + e.toString() + " but counldn't find it in the graph");
                g.addE(e);
                foundEdge = g.edgeRef.get(e.toString());
            }
            Vertex Target = foundEdge.target;
//			a.e.println("Making edge from " + current.toString() + " to " + Target.toString());
            if (Target.toString().compareTo(current.toString()) != 0) {
                if (end != null) {
                    if (Target.toString().compareTo(end.toString()) == 0)
                        continue;
                }
                vToAdd.add(g.edgeRef.get(e.toString()).target.id);
                searchAndAdd(vToAdd, eToAdd, g.edgeRef.get(e.toString()).target, g, end);
            }
        }

    }

    public TreeMap<String, Graph<v, e>> processPools = new TreeMap<String, Graph<v, e>>();
    public LinkedList<String> existingVertices = new LinkedList<String>();
    public TreeMap<String, Vertex> vertexRef = new TreeMap<String, Vertex>();
    public TreeMap<String, String> vertexIDRef = new TreeMap<String, String>();
    public LinkedList<String> existingEdges = new LinkedList<String>();
    public TreeMap<String, Edge> edgeRef = new TreeMap<String, Edge>();
    public boolean isEmpty = false;
    public String id = UUID.randomUUID().toString();
    public String name = id.toString();

    public Vertex trueStart = null;    // Start Node
    public Vertex trueEnd = null;    // End Node
    public LinkedList<Vertex> starts = new LinkedList<Vertex>();
    public LinkedList<Vertex> ends = new LinkedList<Vertex>();

    public Graph<v, e> copyGraph(Graph<v, e> in) {
        Graph<v, e> copy = new Graph<v, e>(Edge.class);
        for (Vertex v : in.vertexSet()) {
            copy.addV(v);
        }
        for (Edge e : in.edgeSet()) {
            copy.addE(e);
        }
        return copy;
    }

    public String toString() {
        String result = a.e.dent() +
                "g[{" + "NODES:" + a.e.endl;
        a.e.incIndent();
        for (Vertex t : this.vertexSet()) {
            result += a.e.dent() + t.toString() + a.e.endl;
        }
        result += "}{EDGES: " + a.e.endl;

        for (Edge e : this.edgeSet()) {
            result += a.e.dent() + e.toString() + a.e.endl;
        }
        a.e.decIndent();
        if (processPools.size() > 0) {
            result += a.e.dent() + "Pools:(" + a.e.endl;
            a.e.incIndent();
            for (String key : processPools.keySet()) {
                result += processPools.get(key).toString();
            }
            a.e.decIndent();
            result += ")[End of Pools]" + a.e.endl;
        }

        return result + "}]" + a.e.endl;
    }

    public boolean addV(Vertex myV) {
        if (!existingVertices.contains(myV.toString())) {


            existingVertices.add(myV.toString());
            vertexRef.put(myV.toString(), myV);
            vertexIDRef.put(myV.id, myV.toString());
            return this.addVertex((Vertex) myV);
        }
        return false;
    }

    public boolean addP(Graph<v, e> pool) {
        processPools.put(pool.name, pool);

        return true;
    }

    public boolean removeV(Vertex myV) {
        existingVertices.remove(myV.toString());
        vertexRef.remove(myV.toString());

        // Remove associated edges
        LinkedList<Edge> removeList = new LinkedList<Edge>();
        for (Edge e : this.edgesOf(myV))
            removeList.add(e);
        for (Edge e : removeList)
            this.removeEdge(e);

        this.removeVertex(myV);
        return true;
    }


    public boolean addE(Edge myE) {
        if (!existingEdges.contains(myE.toString())) {
            existingEdges.add(myE.toString());
            edgeRef.put(myE.toString(), myE);
            Vertex src = (Vertex) ((be.fnord.util.processModel.Edge) myE).getSource();
            Vertex trg = (Vertex) ((be.fnord.util.processModel.Edge) myE).getTarget();


            if (src == null) {
                if (__DEBUG) a.e.println("src node not found ");
                return false;
            }
            if (trg == null) {
                if (__DEBUG) a.e.println("trg node not found ");
                return false;
            }



            if (__DEBUG && a.e.__HIGHDETAILS) a.e.println(src.name + " " + this.inDegreeOf(src));
            if (__DEBUG && a.e.__HIGHDETAILS) a.e.println(trg.toString() + " ");
            return this.addEdge(src, trg, myE);
        }
        return false;
    }

    public boolean removeE(Edge myE) {
        existingEdges.remove(myE.toString());
        edgeRef.remove(myE.toString());
        this.removeEdge(myE);
        return true;
    }

    public Graph(Class<Edge> arg0) {
        super((Class<? extends Edge>) arg0);
    }

    public Graph() {
        super(Edge.class);
    }

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    public void toView() {
        jungViewer jt = new jungViewer();
        jt.displayGraph((Graph<Vertex, Edge>) this, this.name);
    }


}