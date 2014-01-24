/**
 * 
 */

package be.fnord.util.logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

import be.fnord.util.processModel.Trace;
import be.fnord.util.processModel.Vertex;
import be.fnord.util.processModel.Graph_ST;
import be.fnord.util.processModel.Vertex_ST;
import be.fnord.util.processModel.Edge_ST;
import be.fnord.util.logic.WFF;


/**
 * This class is for semantic accumulation for a BPMN model.
 *  
 * @author Xiong Wen (xw926@uowmail.edu.au)
 *
 */
public class Accumulate_4ST extends Accumulate {

	private static final long serialVersionUID = 1L;
	private static final boolean _DEBUG = false; // a.e.__DEBUG;

	/**
	 * @param args
	 */
	// ///////////////////////////////////////////////////////
	// Below are functions for accumulating along a trace for Semantic
	// Tracing///
	// ///////////////////////////////////////////////////////

	
	/**
	 * Accumulate the effects for a given trace from a BPMN model under certain knowledge base
	 * @param src the input trace 
	 * @param kb knowledge base 
	 * @param gST the graph for storing semantic vertices and edges
	 * @return gST the graph with effect scenarios and the corresponding links stored
	 */
	public Graph_ST <Vertex_ST, Edge_ST> trace_acc(
		Trace src, String kb, Graph_ST<Vertex_ST, Edge_ST> gST) {
		//The following vList is a 2-dimension array for storing the candidate vertices for the graph
		ArrayList<ArrayList<Vertex_ST>> vList = new ArrayList<ArrayList<Vertex_ST>>();
		
		LinkedHashSet<WFF> currentEff = new LinkedHashSet<WFF>(); //current effect
		currentEff.add(new WFF(a.e.EMPTY_EFFECT));
		for (Vertex v : src.getNodes()) { //traverse nodes in the given trace
			LinkedHashSet<WFF> intEff = new LinkedHashSet<WFF>();
			//store the temporary effect for building the link in the graph
			ArrayList<Vertex_ST> tmpV = new ArrayList<Vertex_ST>(); 
			intEff.addAll(currentEff);
			
			if(_DEBUG){
				System.out.println("\nvertex id: " + v.id);
				System.out.println("vertex task: " + v.name);
				System.out.println("immediate effect: " + v.getWFF());
			}
			for (WFF ce : currentEff) {
				if (!v.getWFF().isEmpty()) {
					intEff.remove(ce);
					Accumulate_4ST acc = new Accumulate_4ST(); // Needed because of
														// semi-static variables
					intEff.addAll(acc.pairwise_acc(ce, v.getWFF(), kb, true));

				}
			}
			
			//if it is not the first/last Vertex, then store the Vertex to the Graph, then Edge
			if (src.getNodes().get(src.getNodes().size() - 1).equals(v) || src.getNodes().get(0).equals(v)) {
				continue; // don't print the last accumulative effect, it would be a repetition; 
				         //don't print the first accumulative effect neither, because it is empty
			}
			else {
				for (WFF e: intEff){
//					System.out.println(e);
//					gST.addVertex(new Vertex_ST(v.name, v.getWFF().toString(), e.toString()) );
					tmpV.add(new Vertex_ST(v.name, v.getWFF().toString(), e.toString()) ); //add candidate vertex to ArrayList
					
				}
				
			}
			
			currentEff = new LinkedHashSet<WFF>();
			currentEff.addAll(intEff);
			
			vList.add(tmpV);
			
		}

//		 return currentEff;
		
//		//print out the ArrayList with Vertex_ST stored
//		for(ArrayList<Vertex_ST> level: vList){
//			System.out.println("###Task " + (vList.indexOf(level) + 1) + "###" );
//			for(Vertex_ST node: level){
//				System.out.println("\n---Begin a node in the graph---");
////				System.out.println("ID: " + node.id);
//				System.out.println("taskName: " + node.taskName);
//				System.out.println("immediate effect: " + node.immWFF);
//				System.out.println("cummulative effect: " + node.esWFF);
//				System.out.println("---End a node in the graph----\n");
//			}
//			
//		}
		

		//add vertices to Graph_ST
		for(int i = 0; i < vList.size(); i++){
			for(int j = 0; j < vList.get(i).size(); j++){
				gST.addVertex(vList.get(i).get(j));
//				if ( (i != 0) &&)
				if(_DEBUG){
					System.out.println("\n---Begin a node in the graph---");
					System.out.println("ID: " + vList.get(i).get(j).id);
					System.out.println("taskName: " + vList.get(i).get(j).taskName);
					System.out.println("immediate effect: " + vList.get(i).get(j).immWFF);
					System.out.println("cummulative effect: " + vList.get(i).get(j).esWFF);
					System.out.println("---End a node in the graph----\n");
				}
			}
			
		}
		
		//build links between effect scenario
		for(int i = 1; i < vList.size(); i++){
			for(int j = 0; j < vList.get(i).size(); j++){
				for(int x = 0; x < vList.get(i - 1).size(); x++) {
					
					Accumulate_4ST accCheck = new Accumulate_4ST(); 
					
//					System.out.println("source: " + (new WFF(vList.get(i-1).get(x).esWFF)));
//					System.out.println("target: " + vList.get(i).get(j).immWFF);
					
					LinkedHashSet<WFF> effCheckList = accCheck.pairwise_acc(new WFF(vList.get(i-1).get(x).esWFF), new WFF(vList.get(i).get(j).immWFF), kb, true);
					
//					System.out.println("begin test acc");
//					System.out.println("x= " + x);
//					System.out.println("i= " + i);
//					System.out.println("j= " + j);
//					for(WFF e : effCheckList){
//						System.out.println(e);
//					}
//					System.out.println("end test acc");
//					System.out.println("current es: " + vList.get(i).get(j).esWFF);

					if( containsES(effCheckList, vList.get(i).get(j).esWFF) ){
						Vertex_ST vSource = vList.get(i-1).get(x);
						Vertex_ST vTarget = vList.get(i).get(j);
						gST.addEdge(vSource, vTarget);
						
//						System.out.println("####edge: " + vSource.esWFF + " linked to " + vTarget.esWFF);
						
					}
				}

			}
			
		}
		
		
		return gST;

	}
	
	
	/**
	 * Check if the effect scenario of target Vertex is in the accumulated effect set,
	 * if it is in the checkList, then return true, otherwise, return false.
	 * @param checkList the list with effect scenarios
	 * @param vTarget_esWFF the given effect scenarios
	 * @return true / false
	 */
	boolean containsES(LinkedHashSet<WFF> checkList, String vTarget_esWFF ){
		for(WFF e: checkList) {
			if(e.toString().equals(vTarget_esWFF))
				return true;
		}
		return false;
	}

}
