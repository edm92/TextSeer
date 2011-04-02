package boot;

import processBuilding.*;

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
	
	
	public void ModelConstruction(){
		// Create process models here
		process myProcess = processBuilding.LoadExternal.loadFile("newpkg1.xpdl"); 
		
		
		// Lets try some process building
		myProcessBuilder = new ScenarioBuilder(myProcess.structure);
		myProcessBuilder.BuildScenarioLabels();	// this will build all scenario labels.
		for(Graph g: myProcessBuilder.parentEffects){
			//std.calls.showResult("Trying Scenario: " + ScenarioBuilder.graphString(g));
			PairwiseChecker scenarioChecker = new PairwiseChecker(g);
			if(scenarioChecker.isConsistent){
				myProcessBuilder.processEffects.add(g);
			}else{
				std.calls.showResult("Inconsistent Scenario (need to implement Acc for this): " + ScenarioBuilder.graphString(g));
			}
		}
		

		
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
