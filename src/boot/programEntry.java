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

import gui.Splash;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.LinkedList;

import processBuilding.*;

// Don't forget to configure std.strings for your system (will need to implement config file in the future).

import javax.swing.SwingUtilities;

//import std.prover.PairwiseChecker;
//import textSeer.Model.Graph;

import std.prover.PairwiseChecker;
import textSeer.Model.Graph;

/***
 * For Examples see exampleUsage.java
 * @author Evan Morrison (edm92@uow.edu.au)
 *
 */

public class programEntry {
	public static String SOURCEFILE = std.string.endl + "Error in: boot.programEntry.java" + std.string.endl;
	public static boolean DEBUG = true; //std.string.debug;	// Hopefully true when you edit :P
	public static void debug(String msg){
		if(DEBUG)
			std.calls.debug_(msg + SOURCEFILE);
	}
	
	
	public GuiEntry inst;
	public std.Timer t;
	public ScenarioBuilder myProcessBuilder;
	
	// Models
	process myProcess ;
	process myProcess2 ;
	
	/***
	 * First construct your models here. Then fill in the Processing function for 
	 * experiments. Finally use the showResults function to display your output.
	 */

	
	public void ModelConstruction(){
		// Create process models here
		//myProcess = processBuilding.randomProcessGenerator.generateProcess(5,5);  
		//myProcess = processBuilding.LoadExternal.loadFile("newpkg1.xpdl");// myProcess.name = "P1";
		//myProcess2 = processBuilding.LoadExternal.loadFile("newpkg2.xpdl"); //myProcess2.name = "P2";
		LinkedList<Graph> processes = (LinkedList<Graph>) std.DB.openGraph();
		//debug("process size : " + processes.size());
		myProcess = new process();
		myProcess.structure = processes.get(0);
		myProcess2 = new process();
		myProcess2.structure = processes.get(1);
		
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
		

//		std.calls.showResult("#################################");
//		// Process 2
//		myProcessBuilder = new ScenarioBuilder(myProcess2.structure);
//		myProcessBuilder.BuildScenarioLabels();	// this will build all scenario labels.
//		for(Graph g: myProcessBuilder.parentEffects){
//			//std.calls.showResult("Trying Scenario: " + ScenarioBuilder.graphString(g));
//			PairwiseChecker scenarioChecker = new PairwiseChecker(g);
//			if(scenarioChecker.isConsistent){
//				ScenarioBuilder.redoGraph(g);
//				myProcessBuilder.processEffects.add(g);				
//				myProcess2.endEffectScenarios.add(g);
//			}else{
//				std.calls.showResult("Inconsistent Scenario: " + ScenarioBuilder.graphString(g));
//			}
//		}
//		
//		std.calls.showResult("Process " + myProcess2.name + " has the following consistent end effect scenarios" + std.string.endl);
//		
//		for(Graph g: myProcessBuilder.processEffects){
//			std.calls.showResult(ScenarioBuilder.graphString(g));
//			std.calls.showResult("CE:" + ScenarioBuilder.cummulativeEffect(g));
//		}

//		std.DB.saveGraph(myProcess.structure);
//		std.DB.saveGraph(myProcess2.structure);
		
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
	
	public static void combinate(process process1, process process2){
		LinkedList<Graph> Scenarios = 
			processBuilding.composition.Make.SeqCompEff(process1.endEffectScenarios, process1.endEffectScenarios);
		
		process compProcess= new process();
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
			//std.calls.showResult("CE:" + ScenarioBuilder.cummulativeEffect(g));
		}
	}
	
	public void ShowResults(){
		// Select which results to show here
		std.calls.showResult("-----------------------" + std.string.endl + "Completed Processing" + std.string.endl + "-----------------------" + std.string.endl);
		long overTime = 0;
		
		std.calls.showResult("Total overtime: " + overTime + " ms");
		
		//std.calls.showResult(myProcessBuilder.resultString());
		std.calls.showResult("----------------" + std.string.endl + "Processing Time:" + t.toString() + std.string.endl);
		
	}
	
	
	class ProcessingThread implements Runnable {

		Thread runner;
		public ProcessingThread() {
		}
		public ProcessingThread(String threadName) {
			runner = new Thread(this, threadName); // (1) Create a new thread.
			
			//runner.start(); // (2) Start the thread.
		}
		public void run() {
			//Display info about this particular thread
			//System.out.println(Thread.currentThread());
			ModelConstruction();
			Process();
			ShowResults();
		}
	}
	
	
	
	public void TextSeer(){
		

		Thread thread2 = null;
		//Start the threads
		showSplash();
		
		// This threading is to make sure that the window doesn't freeze while processing
		// large ammounts of data.
		try {
			boolean t2IsAlive = true;
			thread2 = new Thread(new ProcessingThread(), "thread2");
			thread2.start();
			
			do {
				
				if (t2IsAlive && !thread2.isAlive()) 
				{
					t2IsAlive = false;
					//System.out.println("Finished Processing.");
				}else{
					//System.out.println("Processing Model.");
				}
				Thread.sleep(100);
			} while (t2IsAlive );
			hideSplash();
		} catch (Exception e) {
		}
		
	}

	
	
	
	
	
	public static Splash mywindow ;
	
	public void initSplash(){
		mywindow = new Splash();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    int y = (screenSize.height/2) - (430/2);
	    int x = (screenSize.width/2) - (500/2);
		mywindow.setLocation(x, y);
		mywindow.setSize( new Dimension( 500, 430 ) );
	}
	
	public void showSplash(){		
		mywindow.setVisible( true );
	}
	
	public void hideSplash(){
		//std.calls.display("Hide");
		mywindow.setVisible( false);
	}
	
	
	
/*
 * Boot up the gui display
 */	
	private programEntry thisEntry;
	
	public programEntry(){
		thisEntry = this;
		initSplash();
		showSplash();
		std.calls.init();
		inst = new GuiEntry(thisEntry);
		inst.setLocationRelativeTo(null);
		inst.setVisible(true);
		
		TextSeer();
		//closeProcessingSplash();
		
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Blah
				// put the previous code in here if you want to squeeze every last cycle out
				// of the CPU. You may also want to take the processing steps from TextSeer()
				// out of the threaded container
			}
		});
	}
	

}
