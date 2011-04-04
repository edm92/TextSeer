package boot;

import processBuilding.*;

// Don't forget to configure std.strings for your system (will need to implement config file in the future).

import javax.swing.SwingUtilities;

import std.prover.PairwiseChecker;
import textSeer.Model.Graph;
import textSeer.Model.functions.ScenarioBuilder;

public class programEntry {
	public GuiEntry inst;
	public std.Timer t;
	public ScenarioBuilder myProcessBuilder;
	
	// Models
	process myProcess ;
	process myProcess2 ;
	
	public void ModelConstruction(){
		// Create process models here
		//myProcess = processBuilding.randomProcessGenerator.generateProcess(5);  
		myProcess = processBuilding.LoadExternal.loadFile(std.string.openFile);// myProcess.name = "P1";
		myProcess2 = processBuilding.LoadExternal.loadFile("newpkg2.xpdl"); //myProcess2.name = "P2";
		
		std.calls.showResult("Loading process models and figuring out end effect scenarios");
		
		// Lets try some process building
		
		// Process 1
		std.calls.showResult("#################################");
		myProcessBuilder = new ScenarioBuilder(myProcess.structure);
		myProcessBuilder.BuildScenarioLabels();	// this will build all scenario labels.
		for(Graph g: myProcessBuilder.parentEffects){
			//std.calls.showResult("Trying Scenario: " + ScenarioBuilder.graphString(g));
			PairwiseChecker scenarioChecker = new PairwiseChecker(g);
			if(scenarioChecker.isConsistent){
				ScenarioBuilder.redoGraph(g);
				myProcessBuilder.processEffects.add(g);				
				myProcess.endEffectScenarios.add(g);
			}else{
				std.calls.showResult("Inconsistent Scenario: " + ScenarioBuilder.graphString(g));
			}
		}
		
		std.calls.showResult("Process " + myProcess.name + " has the following consistent end effect scenarios" + std.string.endl);
		
		for(Graph g: myProcessBuilder.processEffects){
			std.calls.showResult(ScenarioBuilder.graphString(g));
			std.calls.showResult("CE:" + ScenarioBuilder.cummulativeEffect(g));
		}
		

		std.calls.showResult("#################################");
		// Process 2
		myProcessBuilder = new ScenarioBuilder(myProcess2.structure);
		myProcessBuilder.BuildScenarioLabels();	// this will build all scenario labels.
		for(Graph g: myProcessBuilder.parentEffects){
			//std.calls.showResult("Trying Scenario: " + ScenarioBuilder.graphString(g));
			PairwiseChecker scenarioChecker = new PairwiseChecker(g);
			if(scenarioChecker.isConsistent){
				ScenarioBuilder.redoGraph(g);
				myProcessBuilder.processEffects.add(g);				
				myProcess2.endEffectScenarios.add(g);
			}else{
				std.calls.showResult("Inconsistent Scenario: " + ScenarioBuilder.graphString(g));
			}
		}
		
		std.calls.showResult("Process " + myProcess2.name + " has the following consistent end effect scenarios" + std.string.endl);
		
		for(Graph g: myProcessBuilder.processEffects){
			std.calls.showResult(ScenarioBuilder.graphString(g));
			std.calls.showResult("CE:" + ScenarioBuilder.cummulativeEffect(g));
		}
		
		std.calls.showResult("-----------------------" + std.string.endl + "Starting Processing" + std.string.endl + "-----------------------" + std.string.endl);
	}
	
	public void Process(){
		// Do your experiments here
		t = new std.Timer();
		t.reset();
		t.start();
		// First start the timer 
		
		
		
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

		//std.calls.showResult(myProcess.structure.toString());
		
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
