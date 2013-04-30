import java.util.HashSet;


import au.edu.dsl.dlab.processtools.Edge;
import au.edu.dsl.dlab.processtools.Effect;
import au.edu.dsl.dlab.processtools.Graph;
import au.edu.dsl.dlab.processtools.Vertex;
import au.edu.dsl.dlab.processtools.scenario.ComputeScenario;


public class ScenarioExample {

	/**
	 * The following example shows how to compute effect scenarios for a process model.
	 */
	public static void main(String[] args) {
		
		Graph<Vertex, Edge> myProcess = new Graph<Vertex, Edge>();
		Vertex t1 = new Vertex("Task 1"); t1.addEffect("a");
		Vertex t2 = new Vertex("Task 2"); t2.addEffect("c");
		Vertex t3 = new Vertex("Task 3"); t3.addEffect("d");
		Vertex t4 = new Vertex("Task 4"); t4.addEffect("");
		Vertex t5 = new Vertex("Task 5"); t5.addEffect("");
		Vertex t6 = new Vertex("Task 6"); t6.addEffect("");
		Vertex t7 = new Vertex("Task 7"); t7.addEffect("");
		
		Vertex g5 = new Vertex("Gate 5"); g5.addEffect(""); g5.makeXORGate();
		Vertex g6 = new Vertex("Gate 6"); g6.addEffect(""); g6.makeANDGate();
		Vertex g7 = new Vertex("Gate 7"); g7.addEffect(""); g7.makeANDGate();
		Vertex g8 = new Vertex("Gate 8"); g8.addEffect(""); g8.makeXORGate();
		
		Vertex g1 = new Vertex("Gate 1"); g1.addEffect(""); g1.makeXORGate(); g1.setCorresponding(g8);
		Vertex g2 = new Vertex("Gate 2"); g2.addEffect("b"); g2.makeANDGate(); g2.setCorresponding(g6);
		Vertex g3 = new Vertex("Gate 3"); g3.addEffect("c"); g3.makeANDGate(); g3.setCorresponding(g7);
		Vertex g4 = new Vertex("Gate 4"); g4.addEffect(""); g4.makeXORGate(); g4.setCorresponding(g5);
		
		
		myProcess.addVertex(t1);
		myProcess.addVertex(t2);
		myProcess.addVertex(t3);
		myProcess.addVertex(t4);
		myProcess.addVertex(t5);
		myProcess.addVertex(t6);
		myProcess.addVertex(t7);
		
		myProcess.addVertex(g1);
		myProcess.addVertex(g2);
		myProcess.addVertex(g3);
		myProcess.addVertex(g4);
		myProcess.addVertex(g5);
		myProcess.addVertex(g6);
		myProcess.addVertex(g7);
		myProcess.addVertex(g8);
		
		myProcess.addEdge(t1,g1);
		myProcess.addEdge(g1,g2);
		myProcess.addEdge(g2,t2);
		myProcess.addEdge(g2,t3);
		myProcess.addEdge(t2,g6);
		myProcess.addEdge(t3,g6);
		myProcess.addEdge(g6,g8);
		
		myProcess.addEdge(g8,t7);
		
		myProcess.addEdge(g1,g3);
		myProcess.addEdge(g3,t4);
		myProcess.addEdge(g3,g4);
		myProcess.addEdge(t4,g7);
		myProcess.addEdge(g4,t5);
		myProcess.addEdge(g4,t6);
		myProcess.addEdge(t5,g5);
		myProcess.addEdge(t6,g5);
		myProcess.addEdge(g5,g7);
		myProcess.addEdge(g7,g8);

		HashSet<Effect> finalEffects ;
		////////////////////////////////////////
		////// Either 
		////////////////////////////////////////
		finalEffects = ComputeScenario.makeCleanSave(myProcess, "(c -> ~(a & b))");
		
		
		for(Effect effect: finalEffects)
			System.out.println("EffectScenario: " + effect);
		

	}

}
