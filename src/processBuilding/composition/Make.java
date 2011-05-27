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


package processBuilding.composition;

import java.util.LinkedList;
import java.util.List;

import processBuilding.process;

import textSeer.Model.Gateway;
import textSeer.Model.Graph;
import textSeer.Model.SequenceEdge;
import textSeer.Model.Vertex;

public class Make {
	public static String SOURCEFILE = std.string.endl + "Error in: processBuilding.composition.Make.java" + std.string.endl;
	public static boolean DEBUG = std.string.debug;	// Hopefully true when you edit :P
	public static void debug(String msg){
		if(DEBUG)
			std.calls.debug_(msg + SOURCEFILE);
	}

	
	
	public static LinkedList<Graph> ParCompEff(List<Graph> endEffectScenarios,
			List<Graph> endEffectScenarios2) {
		process p = new process();
		LinkedList<Graph> myScenarios = new LinkedList<Graph>();
		
		FixGraph(endEffectScenarios);
		FixGraph(endEffectScenarios2);
		
		Graph g = new Graph();
		Gateway gateOpen = new Gateway(g);
		g.ScenarioAddNode(gateOpen);
		gateOpen.type = Gateway.gatetype.AND; gateOpen.name = "ParallelProcess_" + gateOpen.name ; 
		Gateway gateClose = new Gateway(g);
		gateClose.type = Gateway.gatetype.AND;gateClose.name  = "ParallelProcessClose_" + gateClose.name ;
		g.ScenarioAddNode(gateClose);
		
		
		for(Graph gg1: endEffectScenarios)
			for(Graph gg2: endEffectScenarios2){
				makePara(gg1,g,gateOpen,gateClose);
				makePara(gg2,g,gateOpen,gateClose);
				FixGraph(g);
				
				p.structure = g;
				myScenarios.add(g);
				//std.calls.display(g.toString());
				
//				ScenarioBuilder myProcessBuilder = new ScenarioBuilder(p.structure);
//				myProcessBuilder.BuildScenarioLabels();	// this will build all scenario labels.
//				for(Graph great: myProcessBuilder.parentEffects){
//					//std.calls.showResult("Trying Scenario: " + ScenarioBuilder.graphString(g));
//					PairwiseChecker scenarioChecker = new PairwiseChecker(great);
//					if(scenarioChecker.isConsistent){
//						ScenarioBuilder.redoGraph(great);
//						myProcessBuilder.processEffects.add(great);				
//						p.endEffectScenarios.add(great);
//					}else{
//						//std.calls.showResult("Inconsistent Scenario: " + ScenarioBuilder.graphString(great));
//					}
//				}
//				
//				for(Graph swap:p.endEffectScenarios){
//					myScenarios.add(swap);
//				}
			}
	return myScenarios;
		
		
	}
	
	public static void makePara(Graph p1, Graph g, Vertex gateOpen, Vertex gateClose){

			String previous ="";
			FixGraph(p1);
			for(Vertex v:p1.startNodes){
				if(previous.equals(v.name)) continue; 	// remove duplicates
				previous = v.name;
				//m.E.writeConsole("Adding vertex open: " + v.name + m.E.endl);
				SequenceEdge e1 = new SequenceEdge(g);
				e1.addSource(gateOpen);
				e1.addTarget(v);
				g.ScenarioAddEdge(e1);
				e1.finalize();				
			}
			previous ="";
			//m.E.writeConsole("End Nodes size = "  + p1.endNodes.size() + p1.endNodes.toString());
			for(Vertex v:p1.endNodes){
				if(previous.equals(v.name)) continue; 	// remove duplicates
				previous = v.name;
				//m.E.writeConsole("Adding vertex close: " + v.name + m.E.endl);
				SequenceEdge e1 = new SequenceEdge(g);
				e1.addSource(v);
				e1.addTarget(gateClose);
				e1.finalize();
				g.ScenarioAddEdge(e1);
			}
			
			for(Vertex v:p1.allNodes){
				if(p1.startNodes.contains(v) || p1.endNodes.contains(v)) continue;
				g.ScenarioAddNode(v);
				for(Vertex ov: v.outNodes){
					boolean switcher = false;
					for(SequenceEdge e: v.parent.edges){
						if(e.source == v && e.target == ov){
							g.ScenarioAddEdge(e);
							switcher = true;
						}						
					}
					if(!switcher){
						SequenceEdge e = new SequenceEdge(g);
						e.source = v;
						e.target = ov;
						e.finalize();
						g.ScenarioAddEdge(e);
					}
				}
			}
//			for(SequenceEdge e: p1.edges){
//				g.addEdge(e);
//			}
			
			
			g.finalize();
		
	}
	
	
	public static LinkedList<Graph> SeqCompEff(List<Graph> endEffectScenarios,
			List<Graph> endEffectScenarios2) {
		
		LinkedList<Graph> myScenarios = new LinkedList<Graph>();
		
		FixGraph(endEffectScenarios);
		FixGraph(endEffectScenarios2);
			
		/** TODO:
		 *  This section needs to consider cases where there are multiple start and multiple end events
		 */
		int counter = 0;
		for(Graph p1 : endEffectScenarios){
			
			for(Graph p2: endEffectScenarios2){
				Graph newScenario = new Graph(p1);
				FixGraph(newScenario);
				//std.calls.showResult("Computing with  " + ScenarioBuilder.graphString(newScenario));
				
				if(newScenario.endNodes.size() > 0 && newScenario.endNodes.get(0) != null){
					if(p2.startNodes.size() > 0 ){					
						
						newScenario.endNodes.get(0).outNodes.add(p2.startNodes.get(0));
						SequenceEdge joinEdge = new SequenceEdge(newScenario);
						joinEdge.name ="GenJoinEdge"+p1.ID + p2.ID + counter++;
						// Dodgy hack, need to find out why the end and start nodes of this process are the same
						joinEdge.addSource(p1.allNodes.get(p1.allNodes.size()-1));
						
						joinEdge.addTarget(p2.startNodes.get(0));
						newScenario.ScenarioAddEdge(joinEdge);
						//p2.startNodes.get(0).inNodes.add(newScenario.endNodes.get(0));
						//p2.startNodes.remove(0);
						newScenario.endNodes.remove(0);
						int k = 0; int l = 1;
						for(Vertex v : p2.allNodes){
							//std.calls.showResult("Adding " + v.name);
							newScenario.ScenarioAddNode(v);
							if(l < p2.allNodes.size()){							
								SequenceEdge newGenEdge = new SequenceEdge(newScenario);
				        		newGenEdge.addSource(p2.allNodes.get(k));
				        		newGenEdge.addTarget(p2.allNodes.get(l));
				        		newGenEdge.name = p2.ID + "MakeGenEdge" + k + l;
				        		newScenario.ScenarioAddEdge(newGenEdge);
				        		k++; l++;
							};
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
		
		LinkedList<Vertex> removal = new LinkedList<Vertex>();
		LinkedList<Vertex> used = new LinkedList<Vertex>();
		LinkedList<String> names = new LinkedList<String>();	names.add("");
		
		for(Vertex v: p2.startNodes){ if(v.inNodes != null && v.inNodes.size() == 0 &&!used.contains(v)) used.add(v); };
		
		for(Vertex v:used) if(names.contains(v.name)) removal.add(v);		
		for(Vertex v: removal) used.remove(v);
			
		p2.startNodes = new LinkedList<Vertex>(); for(Vertex v:used) p2.startNodes.add(v);
		removal = new LinkedList<Vertex>();
		used = new LinkedList<Vertex>();
		names = new LinkedList<String>();	names.add("");
		for(Vertex v: p2.endNodes){ if(v.outNodes != null && v.outNodes.size() == 0 && !used.contains(v)) used.add(v); };

		for(Vertex v:used) if(names.contains(v.name)) removal.add(v);		
		for(Vertex v: removal) used.remove(v);
		
		p2.endNodes = new LinkedList<Vertex>(); 
		for(Vertex v:used) {p2.endNodes.add(v);};// m.E.writeConsole("Added " + v.name + " to end nodes");};
		
		
		
			for(Vertex v: p2.allNodes){
				if(v.inNodes == null || v.inNodes.size() < 1){
					p2.startNodes.add(v);
//					std.calls.showResult("Adding " + v.name + " to start nodes"); 
				}else{
//					std.calls.showResult(" Not Adding " + v.name + " to start nodes");
				}
				if(v.outNodes == null || v.outNodes.size() < 1){
					p2.endNodes.add(v);
				}
			}
//			if(p2.edges.size() < 1){
//	        	int k = 0;
//	        	for(int l = 1; l < p2.allNodes.size(); l++){
//	        		SequenceEdge newGenEdge = new SequenceEdge(p2);
//	        		newGenEdge.addSource(p2.allNodes.get(k));
//	        		newGenEdge.addTarget(p2.allNodes.get(l));
//	        		newGenEdge.name = p2.ID + "MakeGenEdge" + k + l;
//	        		p2.ScenarioAddEdge(newGenEdge);
//	        		k++;
//	        	}
//	        	}
	}

}
