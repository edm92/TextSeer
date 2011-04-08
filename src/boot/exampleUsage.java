/**
 * TextSeer, development prototype for ProcessSeer
    Copyright (C) 2011 Evan Morrison

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * @author Evan Morrison (edm92@uow.edu.au)
 *
 */


package boot;

import java.util.LinkedList;

import processBuilding.process;
import std.prover.PairwiseChecker;
import textSeer.Model.Graph;
import textSeer.Model.functions.ScenarioBuilder;

public class exampleUsage {
	// Timer for timed output
	public std.Timer t;
	// ScenarioBuilder is used for generating scenario labels
	public ScenarioBuilder myProcessBuilder;
	
	// Models
	process myProcess ;
	process myProcess2 ;
	
	/***
	 * First construct your models here. Then fill in the Processing function for 
	 * experiments. Finally use the showResults function to display your output.
	 */
	
	
	// This example will load two files
	public void _FileLoading_ModelConstruction(){
		// Create process models here
		//myProcess = processBuilding.randomProcessGenerator.generateProcess(5);  
		myProcess = processBuilding.LoadExternal.loadFile("newpkg1.xpdl");// myProcess.name = "P1";
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
	
	/***
	 * Example of processing the loaded data. This will churn the process model through prover9 
	 */
	public void _Process(){
		// Do your experiments here
		t = new std.Timer();
		t.reset();
		t.start();
		// First start the timer 
		
		
		
		// Run Prover9
		//std.calls.showResult("Running Prover9 on process to check consistency over process scenarios:");
		
		std.calls.showResult(myProcessBuilder.showOutput());
		if(myProcessBuilder.parentEffects != null){
			for(Graph g:myProcessBuilder.parentEffects){
				std.calls.showResult("Trying Scenario: " + ScenarioBuilder.graphString(g));
				std.prover.makeInput.createInput(g);				
				if(std.prover.Run.exec()){
					std.calls.showResult(std.string.prover9sucess + std.string.endl);
				}else
					std.calls.showResult(std.string.prover9failed + std.string.endl);

			}
		}


		
		// Stop the timer when finished to ensure experiment results are timed
		t.end();
	}
	
	/***
	 * Self show output
	 */
	
	public void _ShowResults(){
		// Select which results to show here
		std.calls.showResult("-----------------------" + std.string.endl + "Completed Processing" + std.string.endl + "-----------------------" + std.string.endl);

		//std.calls.showResult(myProcess.structure.toString());
		
		//std.calls.showResult(myProcessBuilder.resultString());
		std.calls.showResult("----------------" + std.string.endl + "Processing Time:" + t.toString() + std.string.endl);
		
	}
	
	
	/***
	 * This is for generating process outputs
	 * this takes about 2 minutes to run through
	 */

	long[][] times = new long[10][20]; 
	public void __Process(){
		
		// First start the timer
		t = new std.Timer();
		t.reset();
		t.start();
		// Do your experiments here
		
		for(int j = 5; j < 10; j++){
		for(int i = 0 ; i < 20; i++){
			
			myProcess = processBuilding.randomProcessGenerator.generateProcess(j,10);
			
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
					//std.calls.showResult("Inconsistent Scenario: " + ScenarioBuilder.graphString(g));
				}
			}
			
			std.calls.showResult("Process " + myProcess.name + " has the following consistent end effect scenarios" + std.string.endl);
			
			for(Graph g: myProcessBuilder.processEffects){
				std.calls.showResult(ScenarioBuilder.graphString(g));
				std.calls.showResult("CE:" + ScenarioBuilder.cummulativeEffect(g));
			}
			t.end();
			times[j][i] = t.duration();
			t.reset();
			t.start();
			std.calls.display("Finished i = " + i);
		}
		std.calls.display("Finished j = " + j);
		}


		
		// Stop the timer when finished to ensure experiment results are timed
		t.end();
	}
	
	public void __ShowResults(){
		// Select which results to show here
		std.calls.showResult("-----------------------" + std.string.endl + "Completed Processing" + std.string.endl + "-----------------------" + std.string.endl);

		long overTime = 0;
		for(int j = 5; j < 10; j++){
			long totalTime = 0;
			for(long c: times[j]){
				totalTime += c;
			}
			overTime += totalTime;
			std.calls.showResult("Total running time for complexity at " + j +" number of tasks: " + totalTime + " ms, average process time is:" + totalTime/100 + " ms");
		}
		std.calls.showResult("Total overtime: " + overTime + " ms");
		//		std.calls.showResult(myProcess.structure.toString());
		
		//std.calls.showResult(myProcessBuilder.resultString());
		std.calls.showResult("----------------" + std.string.endl + "Processing Time:" + t.toString() + std.string.endl);
		
	}
	
	
	/**
	 * Testing out the sequence functions
	 * 
	 */
	public void ___Process(){
		
		// First start the timer
		t = new std.Timer();
		t.reset();
		t.start();
		// Do your experiments here
		
		process compProcess= new process();
		
		LinkedList<Graph> Scenarios = 
			processBuilding.composition.Make.SeqCompEff(myProcess.endEffectScenarios, myProcess2.endEffectScenarios);
		
		
		for(Graph great : Scenarios){
				//std.calls.showResult("Trying Scenario: " + ScenarioBuilder.graphString(g));
				PairwiseChecker scenarioChecker = new PairwiseChecker(great);
				if(scenarioChecker.isConsistent){
					//ScenarioBuilder.redoGraph(great);
					compProcess.endEffectScenarios.add(great);
					//std.calls.showResult("Consistent:" + ScenarioBuilder.graphString(great));
					
				}else{
					//std.calls.showResult("Inconsistent Scenario: " + ScenarioBuilder.graphString(great));
				}
		}
		for(Graph g: compProcess.endEffectScenarios){
			std.calls.showResult(ScenarioBuilder.graphString(g));
			std.calls.showResult("CE:" + ScenarioBuilder.cummulativeEffect(g));
		}
		
		// Stop the timer when finished to ensure experiment results are timed
		t.end();
	}

}
