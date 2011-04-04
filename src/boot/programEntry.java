package boot;

import gui.Splash;

import java.awt.Dimension;
import java.awt.Toolkit;

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
			
			
			
		} catch (Exception e) {
		}
		
	}

	
	
	
	
	
	
	
	
	
	
	
	
/*
 * Boot up the gui display
 */	
	private programEntry thisEntry;
	static Splash mywindow = new Splash();
	public programEntry(){
		thisEntry = this;
		//std.calls.display("Welcome to Text Seer");
		 // make the frame half the height and width
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    int y = (screenSize.height/2) - (430/2);
	    int x = (screenSize.width/2) - (500/2);
	   
		
		mywindow.setLocation(x, y);
		mywindow.setSize( new Dimension( 500, 430 ) );
		mywindow.setVisible( true );
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				std.calls.init();
				inst = new GuiEntry(thisEntry);
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
				
				TextSeer();
				//closeProcessingSplash();
				mywindow.close();
			}
		});
	}
	

}
