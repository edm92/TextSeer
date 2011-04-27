package processBuilding.viewer;

import processBuilding.process;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;


public class jungLayout {
	process viewProc;
	
	
	public jungLayout(process viewme){
		init();
		viewProc = viewme;
		
	}
	
	public void init(){
	}
	
	public void loadGraph(){
		 Graph<Integer, String> g = new SparseMultigraph<Integer, String>();
	}
	
}
