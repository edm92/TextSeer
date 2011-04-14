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

package std.prover;

import java.util.LinkedList;
import java.util.List;

import processBuilding.ScenarioBuilder;

import textSeer.Model.Graph;
import textSeer.Model.Vertex;

public class PairwiseChecker {
	public static String SOURCEFILE = std.string.endl + "Error in: std.prover.PairwiseChecker.java" + std.string.endl;
	public static boolean DEBUG = false; // std.string.debug;	// Hopefully true when you edit :P
	public static void debug(String msg){
		if(DEBUG)
			std.calls.debug_(msg + SOURCEFILE);
	}
	
	public boolean isConsistent = false;
	
	public PairwiseChecker(Graph g){
		if(g.allNodes != null && g.allNodes.size() > 0)
			isConsistent = ReverseListAccumulateBack(g.allNodes.get(g.allNodes.size()-1), g, new Graph(), 1);
		else{
			std.calls.showResult("No reverseListAccumulation in pairwisechecker");
		}
			
	}
	
	
	public boolean ReverseListAccumulateBack(Vertex v, Graph g, Graph reverse, int counter){
		reverse.ScenarioAddNode(v);
		//std.calls.showResult("Trying Scenario: " + ScenarioBuilder.graphString(reverse));
		std.prover.makeInput.createInput(g);				
		if(std.prover.Run.exec()){
			if(counter >= g.allNodes.size() ) return true;
			return ReverseListAccumulateBack(g.allNodes.get(g.allNodes.size()-(++counter)), g, new Graph(), counter);
		}{
			/* 
			 * ACC detect FUNCTION GOES HERE!!!
			 */
			List<Vertex> badNodes = new LinkedList<Vertex>();
			for(int j = g.allNodes.size()-1; j >= 0; j--){
				
				Vertex spare = g.allNodes.get(j);
				//std.calls.display("repeat" + g.allNodes.size() + std.string.endl);
				std.prover.makeInput.createInput(g, spare);
				if(std.prover.Run.exec()){
					//reverse.allNodes.add(spare);
					if(v == null) continue;
					if(spare == null) continue;
					if(v.IE == null) continue;
					if(spare.IE == null) continue;
					if(v.IE.toValue().compareTo(spare.IE.toValue()) != 0)
					badNodes.add(spare);
				}
				
			}
			for(Vertex d:badNodes){
				debug("Return is " + std.calls.popupquery("Found an inconsistent node:" + std.string.endl +
						"New node:" + d.name + std.string.endl +
						"  with effects: " + d.IE.toValue() + std.string.endl +
						"It is conflicting with " + ScenarioBuilder.graphString(reverse) + std.string.endl +
						"Would you like to Acc?"
						, "Acc Query"));
			}
			
			
		}
		
		
		return false;
	}
	

}
