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
	
	public List<Graph> processEffects;
	
	public List<Graph> parentEffects;

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
		processEffects = new LinkedList<Graph>();
	}
	
	public void BuildScenarioLabels(){
		// This function will co-ordinate a crawl through a process model
		// to capture all possible end effect scenarios

		
		
		ProcessScan(null,parent);
		
		a.s.writeGUI("Output off Build Scenario = " + graphString(parent));
		a.s.writeGUI("###############################");
		
		
		
	}
	
	public List<Graph> ProcessScan(Vertex n, Graph g){
		List<Graph> results = new LinkedList<Graph>();;
		
		
		if(n == null && g.startNodes != null){
			// No input given, then lets use the start nodes for the process.
			for(Vertex v: g.startNodes){
				List<Graph> myresults = ProcessNode(v,g);
				mergeList(myresults, results);	// Copy results to results which is what we return.
			}
		}else if(n != null){
			// Not exactly sure why we'd do this, but I imagine it's a consistency check for something
			for(Vertex v: n.outNodes)
				for(SequenceEdge w: v.outEdges){
					if(!v.outNodes.contains( w.target))
						v.outNodes.add(w.target);
				}
			// Process each outgoing node of given nodes
			for(Vertex v: n.outNodes){				
				List<Graph> myresults = ProcessNode(v,g);
				mergeList(myresults, results);		// Copy results to results which is what we return.
			};
		}

		// Some sort of output thing
		parentEffects = results;
		return results;
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
	
	public void MarkParallelGraph(Vertex start, Graph g, Vertex end, Graph subGraph){
		if(start.id == end.id){
			subGraph.ScenarioAddNode(start);
			return;
		}
		if(start.outNodes != null && start.outNodes.size() > 0)
		for(Vertex n:start.outNodes){
			MarkParallelGraph(n, g, end, subGraph);
		}
		if(!subGraph.allNodes.contains(start))
			subGraph.ScenarioAddNode(start);
		
	}
	
	public List<Graph> ProcessNode(Vertex n, Graph g){
		List<Graph> results = new LinkedList<Graph>();
		if(n.outNodes != null && n.outNodes.size() > 0){
			if(CheckParallel(n, g)){
				//a.s.writeConsole("New parallel branch " + n.name);
				Vertex v = GetParallelClose(n,g,0);
				
				Graph pg = new Graph();
				MarkParallelGraph(n,g,v, pg);
				
				List<Graph> subResults = ProcessScan(n,pg);
				if(subResults.size() < 1){
					subResults.add(pg);
				}
				
				mergeList(subResults, results);
				for(Vertex t: v.outNodes){					
					List<Graph> newsubResults = ProcessNode(t,g);	
					mergeResultsList(t, newsubResults, results);
				}
				
				
				//return results;
				
			}else{	// XOR
				if(n.getClass().equals(Gateway.class) && n.outNodes.size() > 1){
					if(((Gateway)n).type == Gateway.gatetype.XOR ){
						//a.s.writeConsole("For " + n.name + " outnodes = " + n.outNodes.size());
						for(Vertex v: n.outNodes){
							List<Graph>  subResults = ProcessScan(v,g);
							mergeList(subResults, results);							
						}
						
						}}
						else	// Non XOR			
				for(Vertex v: n.outNodes){
					if(g.allNodes.contains(v)){
						List<Graph>  subResults = ProcessNode(v,g);
						
						mergeList(subResults, results);
						
					}else{	// Fall off the edge					
						//debug("End of branch +" + v.name);
						return new LinkedList<Graph>();
						
					}
				}
				
			}
			//a.s.writeConsole("results size = " + results.size());
			for(Graph m: results){
				a.s.writeConsole("Adding " + n.toString());
				m.ScenarioAddNode(n);
			}
		}else{
			Graph m = new Graph();
			m.ScenarioAddNode(n);
			results.add(m);
		};
		return results;
	}
	
	public static void  mergeList(List<Graph> in, List<Graph> out){
		
		for(Graph g: in)
			if(out!=null)
				out.add(g);
			else
			{
				out = new LinkedList<Graph>();
				out.add(g);
			}
	}
	
	public static void  mergeResultsList(Vertex t, List<Graph> in, List<Graph> out){
		LinkedList<Graph> removal = new LinkedList<Graph>();
		LinkedList<Graph> putback = new LinkedList<Graph>();
		
		if(out != null)
		for(Graph g: out){
			g.finalize();
			LinkedList<Vertex> rm = new LinkedList<Vertex>();
			for(Vertex v: g.endNodes){
				if(!v.outNodes.contains(t))
					v.outNodes.add(t);
				if(!t.inNodes.contains(v))
					t.inNodes.add(v);
				rm.add(v);
			}
			for(Vertex v: rm){
				g.endNodes.remove(v);
			}
			g.allNodes.add(t);
			
			g.finalize();
			LinkedList<Graph> additions = new LinkedList<Graph>();
			additions.add(g);
			if(in.size() > 1){
				// Make some copies
				Graph newOut = new Graph();
				newOut.copy(g);
				additions.add(newOut);
			}				
			// Simply tack the two graphs together
			for(Graph b: additions){
				if(in !=null){
					Graph gg = in.get(0);
					for(Vertex v: gg.allNodes){
						if(!b.allNodes.contains(v))
							b.allNodes.add(v);
					}
					for(SequenceEdge v: gg.edges){
						if(!b.edges.contains(v))
							b.edges.add(v);
					}
				};
				putback.add(b);
			}
			removal.add(g);
		}
		for(Graph g: removal){
			out.remove(g);
		}
		for(Graph g: putback){
			out.add(g);
		}
		
	}
	
	public String showOutput(){
		String returnResult = "";
		if(parentEffects == null){
			debug("Null effects in showOutput()");
			returnResult = "Null Effects";
		}else{
			for(Graph g: parentEffects){
				returnResult += std.string.endl + "Scenario: ";
				for(Vertex v: g.allNodes){
					returnResult += v.name + "[] ";
				}
			}
		}
		
		
		
		return returnResult;
	}
	
	public static String cummulativeEffect(Graph g){
		String returnResult = "";
		//returnResult += std.string.endl + "";
		for(Vertex v: g.allNodes){
			returnResult += (v.IE==null || v.IE.toValue().length() < 3?"":(v.IE.toValue()+" & "));
		}
		
		if(returnResult.length() > 3){
			returnResult = returnResult.substring(0, returnResult.length()-3);
		}
		
		return returnResult;
	}

	public static void redoGraph(Graph g){
		
		LinkedList<Vertex> mine = (LinkedList<Vertex>) g.allNodes;
		LinkedList<Vertex> replacement = new LinkedList<Vertex>();
		Iterator<Vertex> i = mine.descendingIterator();
		while(i.hasNext()){
			Vertex n = i.next();
			
			replacement.add(n);
					
		}
		g.allNodes = replacement;
	}
	
	public static String graphString(Graph g){
		String returnResult = "";
		
		
		for(Vertex v: g.allNodes){
			if(v.outNodes.size() > 0)
				for(Vertex w: v.outNodes){
					returnResult +=  v.name + "["+(v.IE==null?"":v.IE.toValue())+"] -> " + w.name + "["+(v.IE==null?"":v.IE.toValue())+"]" + a.s.endl;
				}
		}
		
		if(returnResult.length() > "-> ".length()) returnResult = returnResult.substring(0, returnResult.length()-"-> ".length());
		//returnResult = new StringBuffer(returnResult).reverse().toString();
		returnResult = std.string.endl + "Scenario: " + returnResult;
		return returnResult;
	}
	
	
	
}
