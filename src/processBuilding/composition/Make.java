package processBuilding.composition;

import java.util.LinkedList;
import java.util.List;

import processBuilding.process;

import std.prover.PairwiseChecker;
import textSeer.Model.Effect;
import textSeer.Model.Gateway;
import textSeer.Model.Graph;
import textSeer.Model.Predicate;
import textSeer.Model.SequenceEdge;
import textSeer.Model.Vertex;
import textSeer.Model.functions.ScenarioBuilder;

public class Make {
	
	
	public static LinkedList<Graph> ParCompEff(List<Graph> endEffectScenarios,
			List<Graph> endEffectScenarios2) {
		process p = new process();
		LinkedList<Graph> myScenarios = new LinkedList<Graph>();
		
		FixGraph(endEffectScenarios);
		FixGraph(endEffectScenarios2);
			
		Graph g = new Graph();
		Gateway gateOpen = new Gateway(g);
		g.addNode(gateOpen);
		gateOpen.type = Gateway.gatetype.AND; gateOpen.name = "ParallelProcess_" + gateOpen.name ; 
		Gateway gateClose = new Gateway(g);
		gateClose.type = Gateway.gatetype.AND;gateClose.name  = "ParallelProcess_" + gateClose.name ;
		g.addNode(gateClose);
		
		makePara(endEffectScenarios,g,gateOpen,gateClose);
		makePara(endEffectScenarios2,g,gateOpen,gateClose);
		FixGraph(g);
		
		p.structure = g;
		std.calls.display(g.toString());
		
		ScenarioBuilder myProcessBuilder = new ScenarioBuilder(p.structure);
		myProcessBuilder.BuildScenarioLabels();	// this will build all scenario labels.
		for(Graph great: myProcessBuilder.parentEffects){
			//std.calls.showResult("Trying Scenario: " + ScenarioBuilder.graphString(g));
			PairwiseChecker scenarioChecker = new PairwiseChecker(great);
			if(scenarioChecker.isConsistent){
				ScenarioBuilder.redoGraph(great);
				myProcessBuilder.processEffects.add(great);				
				p.endEffectScenarios.add(great);
			}else{
				//std.calls.showResult("Inconsistent Scenario: " + ScenarioBuilder.graphString(great));
			}
		}
		
		for(Graph swap:p.endEffectScenarios){
			myScenarios.add(swap);
		}
		
		return myScenarios;
		
	}
	
	public static void makePara(List<Graph> endEffectScenarios, Graph g, Vertex gateOpen, Vertex gateClose){
		for(Graph p1 : endEffectScenarios){
			FixGraph(p1);
			for(Vertex v:p1.startNodes){
				SequenceEdge e1 = new SequenceEdge(g);
				e1.addSource(gateOpen);
				e1.addTarget(v);
				e1.finalize();
				g.addEdge(e1);
			}
			for(Vertex v:p1.endNodes){
				SequenceEdge e1 = new SequenceEdge(g);
				e1.addSource(v);
				e1.addTarget(gateClose);
				e1.finalize();
				g.addEdge(e1);
			}
			g.finalize();
		}
	}
	
	
	public static LinkedList<Graph> SeqCompEff(List<Graph> endEffectScenarios,
			List<Graph> endEffectScenarios2) {
		
		LinkedList<Graph> myScenarios = new LinkedList<Graph>();
		
		FixGraph(endEffectScenarios);
		FixGraph(endEffectScenarios2);
			
		
		
		for(Graph p1 : endEffectScenarios){
			
			for(Graph p2: endEffectScenarios2){
				Graph newScenario = new Graph(p1);
				FixGraph(newScenario);
				//std.calls.showResult("Computing with  " + ScenarioBuilder.graphString(newScenario));
				
				if(newScenario.endNodes.size() > 0 && newScenario.endNodes.get(0) != null){
					if(p2.startNodes.size() > 0 ){					
						
						newScenario.endNodes.get(0).outNodes.add(p2.startNodes.get(0));
						//p2.startNodes.get(0).inNodes.add(newScenario.endNodes.get(0));
						//p2.startNodes.remove(0);
						newScenario.endNodes.remove(0);
						for(Vertex v : p2.allNodes){
							//std.calls.showResult("Adding " + v.name);
							
							newScenario.ScenarioAddNode(v);
						}
						myScenarios.add(newScenario);
						//std.calls.showResult("Added " + ScenarioBuilder.graphString(newScenario));
			
						
					}else{
					}
					
				}else{
				}
			}
		}
		
		return myScenarios;
		
	}
	
	public static void FixGraph(List<Graph> endEffectScenarios){
		for(Graph p2: endEffectScenarios){
			FixGraph(p2);
		}
	}
	
	public static void FixGraph(Graph p2){
		if(p2.startNodes == null) p2.startNodes = new LinkedList<Vertex>();
		if(p2.endNodes == null) p2.endNodes = new LinkedList<Vertex>();
		
			for(Vertex v: p2.allNodes){
				if(v.inNodes == null || v.inNodes.size() < 1){
					p2.startNodes.add(v);
//					std.calls.showResult("Adding " + v.name + " to start nodes"); 
				}else{
//					std.calls.showResult(" Not Adding " + v.name + " to start nodes");
				}
				if(v.outNodes == null || v.inNodes.size() < 1){
					p2.endNodes.add(v);
				}
			}
		
	}

}
