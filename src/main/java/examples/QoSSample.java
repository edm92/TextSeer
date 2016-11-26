package examples;

import java.util.LinkedHashSet;
import java.util.LinkedList;

import be.fnord.util.QUAL.JSONEFFECT;
import be.fnord.util.QUAL.QOSAccumulate;
import be.fnord.util.processModel.Trace;
import be.fnord.util.processModel.Vertex;


/**
 * Sample of a QOS chain being accumulated across a process model. 
 * @author e
 *
 */
public class QoSSample {

	public static void main(String [] args){
		// Load model
		// Setup the environment
		new a.e();
		
		Vertex.TO_STRING_WITH_WFFS = true;

		// Store our traces in a list

		LinkedList<Trace> traces = QOSAccumulate.loadModel("models/QuickModel.bpmn"); // Loading
		String kb = "";
		
		
		// For each output trace. 
		for(Trace t: traces){
			// Accumulate trace
			LinkedHashSet<JSONEFFECT> last = null;
			
			JSONEFFECT _lst = null;
			int step = 0;
			
			a.e.println("Trace : " + t.toString());
			a.e.incIndent();
			
			for (Vertex v : t.getNodes()) {
				
				JSONEFFECT j = new JSONEFFECT(v.getWFF().getFormula());
				// Show the vertex and current Effect
//						a.e.println(v.name + " : " + j.toString());
				if(!j.isEmpty()){
					if(last== null){
						if(_lst == null)
							_lst = j;
						else{
							LinkedHashSet<JSONEFFECT> __n = QOSAccumulate.pairwise_acc(_lst, j ,kb);
							if(last == null) last = __n;
							
						}
					}else
					if(last != null){
						LinkedHashSet<JSONEFFECT> _res = QOSAccumulate.pairwise_acc(last, j, kb);
						last = _res;
						
					};
					
					if(last != null){
						for(JSONEFFECT l : last)
							a.e.println("Step " + step + ":" + l.QOS.toString());
					}
					
					
				}
				++step;
			}
			
			a.e.decIndent();
		}
	}
	
}
