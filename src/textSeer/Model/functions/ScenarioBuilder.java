package textSeer.Model.functions;

import java.util.LinkedList;
import java.util.List;
import textSeer.Model.Gateway;
import textSeer.Model.Graph;
import textSeer.Model.Vertex;


// The following function takes as input a process model and returns a collection of scenarios
// which are straight through flows of the process (used for accumulation).

public class ScenarioBuilder {
	public static String SOURCEFILE = std.string.endl + "Error in: textSeer.Model.functions.ScenarioBuilder.java" + std.string.endl;
	public static boolean DEBUG = std.string.debug;	// Hopefully true when you edit :P
	public static void debug(String msg){
		if(DEBUG)
			std.calls.display(msg + SOURCEFILE);
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
		
	}
	
	public List<Graph> ProcessScan(Vertex n, Graph g){
		List<Graph> results = new LinkedList<Graph>();
		if(n == null && g.startNodes != null){
			for(Vertex v: g.startNodes){
				List<Graph> myresults = ProcessNode(v,g);
				mergeList(myresults, results);
			}
		}

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
		if(CheckParallel(n,g))
			++openGateways;
		if(CheckParallelClose(n,g))
			--openGateways;

		if(n.outNodes != null && n.outNodes.size() > 0 && openGateways > 0)
			for(Vertex v: n.outNodes){
				GetParallelClose(v,g,openGateways);
			}
		
		return n;
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
		
	}
	
	public List<Graph> ProcessNode(Vertex n, Graph g){
		List<Graph> results = new LinkedList<Graph>();
		if(n.outNodes != null && n.outNodes.size() > 0){
			if(CheckParallel(n, g)){
				Vertex v = GetParallelClose(n,g,0);
				Graph pg = new Graph();
				MarkParallelGraph(n,g,v, pg);
				List<Graph> subResults = ProcessScan(n,pg);
				mergeList(subResults, results);
				Vertex t= v.outNodes.get(0);
				subResults = ProcessNode(t,g);	
				mergeList(subResults, results);
				//return results;
			}else
			for(Vertex v: n.outNodes){
				if(g.allNodes.contains(v)){
					List<Graph> subResults = ProcessNode(v,g);				
					mergeList(subResults, results);
				}else{	// Fall off the edge
					debug("Should get here in ProcessNode");
					return new LinkedList<Graph>();
				}
			}
			for(Graph m: results){
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

	
	public static String graphString(Graph g){
		String returnResult = "";
		returnResult += std.string.endl + "Scenario: ";
		for(Vertex v: g.allNodes){
			returnResult += v.name + "["+(v.IE==null?"":v.IE.toValue())+"] ";
		}
		
		return returnResult;
	}
	
	
	
}
