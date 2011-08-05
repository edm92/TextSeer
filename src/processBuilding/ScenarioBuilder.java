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



package processBuilding;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import textSeer.Model.Gateway;
import textSeer.Model.Graph;
import textSeer.Model.Vertex;
import textSeer.Model.SequenceEdge;


// The following function takes as input a process model and returns a collection of scenarios
// which are straight through flows of the process (used for accumulation).

public class ScenarioBuilder {
	public static String SOURCEFILE = std.string.endl + "Error in: textSeer.Model.functions.ScenarioBuilder.java" + std.string.endl;
	public static boolean DEBUG = std.string.debug;	// Hopefully true when you edit :P
	public static void debug(String msg){
		if(DEBUG)
			std.calls.debug_(msg + SOURCEFILE);
	}
	
	public List<Graph> processScenarios;
	


	public int currentMarker = 0;
	public Graph parent;
	public Graph markedGraph = null;
	
	public ScenarioBuilder(Graph myParent){
		init();
		parent = myParent;
		
	}
	
	public ScenarioBuilder(Graph construction, Vertex next){
		init();
		parent = null;
		
	}
	
	public void init(){
		//markerPoint.push(0);

		processScenarios = new LinkedList<Graph>();
	}
	
	public void BuildScenarioLabels(){
		// This function will co-ordinate a crawl through a process model
		// to capture all possible end effect scenarios

		
		LinkedList<Graph> myScenarios = new LinkedList<Graph>();
		myScenarios.add(new Graph());
		processScenarios = ProcessScan(null, null,parent, myScenarios);
		
	
		
		
	}
	
	public void AddToScenarios(Vertex v, LinkedList<Graph> Scenarios){
		for(Graph g: Scenarios){
			g.ScenarioAddNode(v);
		}
	}

	public void AddToScenarios(Vertex v, Vertex u, LinkedList<Graph> Scenarios){
		if(Scenarios.size() < 1)
			Scenarios.add(new Graph());
		
		for(Graph g: Scenarios){
			g.ScenarioAddNode(v);
			for(SequenceEdge e: v.outEdges){
				if(!g.edges.contains(e))
					g.ScenarioAddEdge(e);
			}
			for(SequenceEdge e: u.inEdges){
				if(!g.edges.contains(e))
					g.ScenarioAddEdge(e);
			}
		}
	}
	
	public boolean CheckParallel(Vertex n, Graph g){
		if(n.getClass().equals(Gateway.class))
			if(((Gateway)n).type == Gateway.gatetype.AND)
				if(n.outNodes.size() > 1)
					return true;		
		return false;
	}
	
	public boolean CheckParallelClose(Vertex n, Graph g){
		if(n.getClass().equals(Gateway.class))
			if(((Gateway)n).type == Gateway.gatetype.AND)
				if(n.inNodes.size() > 1)
					return true;		
		return false;
	}
	
	
	public Vertex GetParallelClose(Vertex n, Graph g, int openGateways){
		Vertex close = n;
		if(CheckParallel(n,g))
			++openGateways;
		if(CheckParallelClose(n,g))
			--openGateways;

		if(n.outNodes != null && n.outNodes.size() > 0 && openGateways > 0)
			for(Vertex v: n.outNodes){
				close = GetParallelClose(v,g,openGateways);
			}
		
		return close;
	}

	
	public LinkedList<Graph> ProcessScan(Vertex start, Vertex end, Graph g, LinkedList<Graph> scenarios){
		
		// For each start node continue
		if(start == null){
			for(Vertex outnode: g.startNodes){
				scenarios = ProcessScan(outnode, end, g, scenarios);
			}
		}else{	// Check for terminal nodes
			if((end != null && start.name.compareTo(end.name) == 0) || start.outNodes.size() < 1){	// Start = End
				AddToScenarios(start, scenarios);
				return scenarios;
			}else	// Check for straight through nodes
			if(start.nodeType != std.type.Node.GATEWAY){
				// Assuming that there is only one outnode				
				scenarios = ProcessScan(start.outNodes.get(0), end, g, scenarios);
			}else	// Lets look at the XOR's 
			if(start.nodeType == std.type.Node.GATEWAY && start.subNodeType == std.type.GatewayType.XOR){
				// Duplicate scenario for each outgoing node
				LinkedList<LinkedList<Graph>> subScenarios = new LinkedList<LinkedList<Graph>>();
				
				for(Vertex v: start.outNodes){
					LinkedList<Graph> newScenarios = new LinkedList<Graph>();
					for(Graph sg : scenarios){
						Graph copySG = new Graph(sg);
						newScenarios.add(copySG);
					}
					newScenarios = ProcessScan(v, end, g, newScenarios);
					subScenarios.add(newScenarios);
				}
				scenarios = new LinkedList<Graph>();
				for(LinkedList<Graph> subList: subScenarios){
					for(Graph subGraph : subList){
						scenarios.add(subGraph);
					}
				}
			}else	// Parallel branches
				if(CheckParallel(start, g)){
					Vertex v = GetParallelClose(start,g,0);
					Vertex nextAfterClose = v.outNodes.get(0);	// Assuming only one node coming out of the close
					
					LinkedList<LinkedList<Graph>> subScenarios = new LinkedList<LinkedList<Graph>>();
					scenarios = ProcessScan(nextAfterClose, end, g, scenarios);
					for(Vertex outNodes: start.outNodes){
						LinkedList<Graph> futureScenarios = new LinkedList<Graph>();
						for(Graph src: scenarios)
							futureScenarios.add(new Graph(src));
						futureScenarios = ProcessScan(outNodes, v, g, futureScenarios);
						subScenarios.add(futureScenarios);
					}
					
					// MERGE PARALLEL BRANCHES
					LinkedList<Graph> parallelScenarios = mergeParallelBranches(subScenarios);
					scenarios = mergeScenarios(parallelScenarios, scenarios);
				}
		}
		
		if(start != null){
			a.s.writeConsole("Adding scenarios: " + start.name + " and ");
			a.s.writeConsole( start.outNodes.get(0).name);
			AddToScenarios(start, start.outNodes.get(0), scenarios);
		}
		return scenarios;
	}	
	
	public LinkedList<Graph> mergeScenarios(LinkedList<Graph> first, LinkedList<Graph> second){
		LinkedList<Graph> mergedScenario = new LinkedList<Graph>();
		for(Graph scene: first){
			for(Graph scene2 : second){
				Graph copy = new Graph(scene);
				copy.SequenceCopy(scene2);
				mergedScenario.add(copy);
			}
		}
		return mergedScenario;
	}
	
	public LinkedList<Graph> mergeParallelBranches(LinkedList<LinkedList<Graph>> possibleWorlds){
		LinkedList<Graph> result = new LinkedList<Graph>();
		LinkedList<Graph> second = new LinkedList<Graph>();
		if(possibleWorlds.size() > 2){	// Pairwise joining, shrink parallel processing down to two worlds at a time and mergebackwards
			LinkedList<LinkedList<Graph>> newWorlds = new LinkedList<LinkedList<Graph>>();
			for(int i = 1; i < possibleWorlds.size(); i++)
				newWorlds.add(possibleWorlds.get(i));
			second = mergeParallelBranches(newWorlds);
		}else
			second = possibleWorlds.get(1);
		
		for(Graph scene: possibleWorlds.get(0)){
			for(Graph scenesecond : second){
				Graph copy = new Graph(scene);
				copy.SequenceCopy(scenesecond);
				result.add(copy);
			}	
		}	
		return result;
	}
	
	
}
