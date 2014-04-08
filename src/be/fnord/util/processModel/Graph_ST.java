
package be.fnord.util.processModel;

import org.apache.log4j.Logger;
import org.jgrapht.graph.DefaultDirectedGraph;

/**
 * The Graph for semantic tracing
 *
 * @param <v> vertex
 * @param <e> edge
 * @author Xiong Wen (xw926@uowmail.edu.au)
 */
public class Graph_ST<v extends Vertex_ST, e extends Edge_ST> extends DefaultDirectedGraph<Vertex_ST, Edge_ST> implements org.jgrapht.Graph<Vertex_ST, Edge_ST> {

	private static final long serialVersionUID = -7342427886299612645L;

	public static boolean __DEBUG = a.e.__DEBUG;

    protected transient static Logger logger = Logger.getLogger("GraphClass");
    public static boolean SKIP_EMPTY_WFF_PATHS = true;
    public static final int MAX_PATH_LENGTH = 100; // Set higher if your graph
    // isn't working out

    public Graph_ST(Class<Edge_ST> arg0) {

        super((Class<? extends Edge_ST>) arg0);
    }

    public Graph_ST() {

        super(Edge_ST.class);
    }


}
