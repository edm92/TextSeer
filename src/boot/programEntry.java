package boot;

// Don't forget to configure std.strings for your system (will need to implement config file in the future).

import javax.swing.SwingUtilities;

import std.prover.PairwiseChecker;
import textSeer.Model.Effect;
import textSeer.Model.Gateway;
import textSeer.Model.Graph;
import textSeer.Model.Predicate;
import textSeer.Model.SequenceEdge;
import textSeer.Model.Vertex;
import textSeer.Model.functions.ScenarioBuilder;

public class programEntry {
	public GuiEntry inst;
	public std.Timer t;
	public ScenarioBuilder myProcessBuilder;
	
	// Models
	public Graph process1, process2;
	
	public void ModelConstruction(){
		// Create process models here
		process1 = new Graph();
		process2 = new Graph();
		
		// Process 1
		// a11 -> a12 -> a13
		
		Effect e1 = new Effect(); Effect e2 = new Effect();
		e1.addPredicate(new Predicate("a(x) & b(x)", true));	// Effect of P
		e2.addPredicate(new Predicate("c(x)", true));	// Effect of Q
		
		Vertex a11 = new Vertex(process1, "a11");
		a11.addEffect(e1);	// can only add 1 effect (need to add predicates otherwise)
		
		Vertex a12 = new Vertex(process1, "a12");
		a12.addEffect(e2);
		
		Vertex a13 = new Vertex(process1, "a13");
		
		SequenceEdge e11 = new SequenceEdge(process1, "e11");
		SequenceEdge e12 = new SequenceEdge(process1, "e12");
		
		e11.addSource(a11);
		e11.addTarget(a12);
		e12.addSource(a12);
		e12.addTarget(a13);
		
		// Edges should be finalized. These finalize calls propagate details through the model
		// this way search and processing speed should be more optimal.
		// Process structure is assumed a priori 
		e11.finalize();
		e12.finalize();
		
		// This is done to ensure that all elements of the graph are made correctly. Time consuming
		// user input == fast program output :D
		process1.addEdge(e11);
		process1.addEdge(e12);
		
		process1.addNode(a11);
		process1.addNode(a12);
		process1.addNode(a13);
		
		process1.finalize();
		
		
		// Lets make a second process, process 2 with a gateway
		/*						-> A22				-> A25
		 *      		A21  ->			-> A24 -> 			-> A27		->
		 * 		A31 ->			-> A23				-> A26						A33
		 * 				
		 * 				   	 -> A32 									->
		 * 
		 */
		Vertex a31 = new Vertex(process2, "a31");
		Vertex a32 = new Vertex(process2, "a32");
		a32.addEffect(e2);
		Vertex a33 = new Vertex(process2, "a33");
		
		Vertex a21 = new Vertex(process2, "a21");
		Vertex a22 = new Vertex(process2, "a22");
		Vertex a23 = new Vertex(process2, "a23");
		Vertex a24 = new Vertex(process2, "a24");
		
		Vertex a25 = new Vertex(process2, "a25");
		//a25.addEffect(e2);
		Vertex a26 = new Vertex(process2, "a26");
		Vertex a27 = new Vertex(process2, "a27");
		
		a31.addEffect(e1);
		a23.addEffect(e1);
		
		Gateway g31 = new Gateway(process2, "g31");
		g31.type = Gateway.gatetype.XOR;
		Gateway g32 = new Gateway(process2, "g32");
		g32.type = Gateway.gatetype.XOR;
		
		Gateway g21 = new Gateway(process2, "g21");
		g21.type = Gateway.gatetype.AND;
		Gateway g22 = new Gateway(process2, "g22");
		g22.type = Gateway.gatetype.AND;
		Gateway g23 = new Gateway(process2, "g23");
		g23.type = Gateway.gatetype.XOR;
		Gateway g24 = new Gateway(process2, "g24");
		g24.type = Gateway.gatetype.XOR;

		
		SequenceEdge e31 = new SequenceEdge(process2, "e31");
		
		SequenceEdge e32 = new SequenceEdge(process2, "e32");
		SequenceEdge e33 = new SequenceEdge(process2, "e33");
		SequenceEdge e34 = new SequenceEdge(process2, "e34");
		SequenceEdge e35 = new SequenceEdge(process2, "e33");
		SequenceEdge e36 = new SequenceEdge(process2, "e34");
		
		SequenceEdge e21 = new SequenceEdge(process2, "e21");
		SequenceEdge e22 = new SequenceEdge(process2, "e22");
		SequenceEdge e23 = new SequenceEdge(process2, "e23");
		SequenceEdge e24 = new SequenceEdge(process2, "e24");
		SequenceEdge e25 = new SequenceEdge(process2, "e25");
		
		SequenceEdge e26 = new SequenceEdge(process2, "e26");
		
		SequenceEdge e27 = new SequenceEdge(process2, "e27");
		SequenceEdge e28 = new SequenceEdge(process2, "e28");
		SequenceEdge e29 = new SequenceEdge(process2, "e29");
		SequenceEdge e210 = new SequenceEdge(process2, "e210");
		SequenceEdge e211 = new SequenceEdge(process2, "e211");
		SequenceEdge e212 = new SequenceEdge(process2, "e212");
		
		e31.addSource(a31);
		e31.addTarget(g31);
		e32.addSource(g31);
		e32.addTarget(a21);
		e33.addSource(g31);
		e33.addTarget(a32);
		e34.addSource(a27);
		e34.addTarget(g32);
		e35.addSource(a32);
		e35.addTarget(g32);
		e36.addSource(g32);
		e36.addTarget(a33);
		
		e21.addSource(a21);
		e21.addTarget(g21);
		e22.addSource(g21);
		e22.addTarget(a22);
		e23.addSource(g21);
		e23.addTarget(a23);
		e24.addSource(a22);
		e24.addTarget(g22);
		e25.addSource(a23);
		e25.addTarget(g22);
		e26.addSource(g22);
		e26.addTarget(a24);
		
		e27.addSource(a24);
		e27.addTarget(g23);
		e28.addSource(g23);
		e28.addTarget(a25);
		e29.addSource(g23);
		e29.addTarget(a26);
		e210.addSource(a25);
		e210.addTarget(g24);
		e211.addSource(a26);
		e211.addTarget(g24);
		e212.addSource(g24);
		e212.addTarget(a27);
		
		e31.finalize();
		e32.finalize();
		e33.finalize();
		e34.finalize();
		e35.finalize();
		e36.finalize();
		
		e21.finalize();
		e22.finalize();
		e23.finalize();
		e24.finalize();
		e25.finalize();
		e26.finalize();
		e27.finalize();
		e28.finalize();
		e29.finalize();
		e210.finalize();
		e211.finalize();
		e212.finalize();
		
		process2.addEdge(e31);
		process2.addEdge(e32);
		process2.addEdge(e33);
		process2.addEdge(e34);
		process2.addEdge(e35);
		process2.addEdge(e36);
		
		process2.addEdge(e21);
		process2.addEdge(e22);
		process2.addEdge(e23);
		process2.addEdge(e24);
		process2.addEdge(e25);
		process2.addEdge(e26);
		
		process2.addEdge(e27);
		process2.addEdge(e28);
		process2.addEdge(e29);
		process2.addEdge(e210);
		process2.addEdge(e211);
		process2.addEdge(e212);
		
		process2.addNode(a31);
		process2.addNode(a32);
		process2.addNode(a33);
		
		process2.addNode(a21);
		process2.addNode(a22);
		process2.addNode(a23);
		process2.addNode(a24);
		process2.addNode(a25);
		process2.addNode(a26);
		process2.addNode(a27);
		process2.addNode(g21);
		process2.addNode(g22);
		process2.addNode(g23);
		process2.addNode(g24);
		
		process2.addNode(g31);
		process2.addNode(g32);
		process2.finalize();
		
		// Lets try some process building
		myProcessBuilder = new ScenarioBuilder(process2);
		myProcessBuilder.BuildScenarioLabels();	// this will build all scenario labels.
		for(Graph g: myProcessBuilder.parentEffects){
			//std.calls.showResult("Trying Scenario: " + ScenarioBuilder.graphString(g));
			PairwiseChecker scenarioChecker = new PairwiseChecker(g);
			if(scenarioChecker.isConsistent){
				//std.calls.showResult(std.string.prover9sucess + std.string.endl);
				myProcessBuilder.processEffects.add(g);
			}else{
				//std.calls.showResult(std.string.prover9failed + std.string.endl);
				std.calls.showResult("Inconsistent Scenario (need to implement Acc for this): " + ScenarioBuilder.graphString(g));
			}
		}
		
		// Make up a scenario label to test for consistency
		
		// Create the nessisary prover9 files
		// Need to pass through some functions here
		std.calls.showResult("-----------------------" + std.string.endl + "Starting Processing" + std.string.endl + "-----------------------" + std.string.endl);
	}
	
	public void Process(){
		// Do your experiments here
		t = new std.Timer();
		t.reset();
		t.start();
		// First start the timer 
		
		std.calls.showResult("Process has the following consistent end effect scenarios" + std.string.endl);
		
		for(Graph g: myProcessBuilder.processEffects){
			std.calls.showResult(ScenarioBuilder.graphString(g));
		}
		
		// Run Prover9
		//std.calls.showResult("Running Prover9 on process to check consistency over process scenarios:");
		
//		std.calls.showResult(myProcessBuilder.showOutput());
//		if(myProcessBuilder.parentEffects != null){
//			for(Graph g:myProcessBuilder.parentEffects){
//				std.calls.showResult("Trying Scenario: " + ScenarioBuilder.graphString(g));
//				std.prover.makeInput.createInput(g);				
//				if(std.prover.Run.exec()){
//					std.calls.showResult(std.string.prover9sucess + std.string.endl);
//				}else
//					std.calls.showResult(std.string.prover9failed + std.string.endl);
//
//			}
//		}


		
		// Stop the timer when finished to ensure experiment results are timed
		t.end();
	}
	
	public void ShowResults(){
		// Select which results to show here
		std.calls.showResult("-----------------------" + std.string.endl + "Completed Processing" + std.string.endl + "-----------------------" + std.string.endl);
		//std.calls.showResult(process2.toString() + std.string.endl);
		//std.calls.showResult(myProcessBuilder.resultString());
		std.calls.showResult("----------------" + std.string.endl + "Processing Time:" + t.toString() + std.string.endl);
		
	}
	
	
	
	
	public void TextSeer(){
		ModelConstruction();
		Process();
		ShowResults();
	}

	
	
	
	
	
	
	
	
	
	
	
	
/*
 * Boot up the gui display
 */	
	private programEntry thisEntry;
	
	public programEntry(){
		thisEntry = this;
		//std.calls.display("Welcome to Text Seer");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				std.calls.init();
				inst = new GuiEntry(thisEntry);
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
				TextSeer();
			}
		});
	}
	

}
