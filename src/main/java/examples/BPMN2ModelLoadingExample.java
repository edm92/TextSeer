package examples;


import be.fnord.util.processModel.Edge;
import be.fnord.util.processModel.Graph;
import be.fnord.util.processModel.Vertex;
import be.fnord.util.processModel.util.GraphChecker;
import be.fnord.util.processModel.util.GraphLoader;

/**
 * @author Evan Morrison edm92@uowmail.edu.au http://www.fnord.be
 *         Apache License, Version 2.0, Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */
public class BPMN2ModelLoadingExample {


    public static void main(String[] args) {
        /////////////////////////////////////////
        /* Initialize core app. */
        new a.e();  //
        /////////////////////////////////////////
        //// Real start of program below	/////
        /////////////////////////////////////////
        GraphLoader gLoader = new GraphLoader();
        Graph<Vertex, Edge> g1 = gLoader.loadModel("models/Benefits Administration - Ongoing.bpmn20.xml");
        System.out.println("G1-" + g1);
        GraphChecker g1Checker = new GraphChecker();
        System.out.println("G1 Test: " + g1Checker.CheckGraph(g1));

        System.out.println("Doc = " + g1.documentation );

/* 2014-03-25 Commented during updated
        Graph<Vertex, Edge> g1 = GraphLoader.loadModel("models/proc1.bpmn20.xml");
        System.out.println("G1-" + g1);

        Graph<Vertex, Edge> g2 = GraphLoader.loadModel("models/proc2.bpmn20.xml");
        System.out.println("G2-" + g2);

        // Outputs True if Strongly connected
        // and other syntactic checks are cleared
        GraphChecker g1Checker = new GraphChecker();
        GraphChecker g2Checker = new GraphChecker();
        System.out.println("G1 Test: " + g1Checker.CheckGraph(g1));
        System.out.println("G2 Test: " + g2Checker.CheckGraph(g2));
*/

        // The jbpt algorithms appear mangled for their DirectedGraphAlgorithms, to use these functions
        // we need to recreate all edges with a new interface. Something to do later

//		System.out.println("Trying to load a json file attached to the jbptTests");
//		Graph<Vertex,Edge> g3 = altJSON2Process.convert("988654311_rev1.json", altJSON2Process.readFile("models/a.s00000112__s00003260.tpn_0.json"));
//		System.out.println("Displaying JSON file form jbptTests:" + g3.toString());

//		// Loading the BMI json files: 
//		Graph<Vertex,Edge> g4 = BMIJSON2Graph.convert("1582472422_rev18.json", altJSON2Process.readFile("models/1582472422_rev18.json"));
//		System.out.println("Displaying JSON based model:" + g4.toString());
//		System.out.println("G4 Test: " + g2Checker.CheckGraph(g4));


        return;

    }


}
