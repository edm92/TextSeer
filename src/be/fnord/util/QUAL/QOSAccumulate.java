package be.fnord.util.QUAL;

import java.util.LinkedHashSet;
import org.apache.log4j.Logger;


import be.fnord.util.logic.WFF;
import be.fnord.util.processModel.Trace;
import be.fnord.util.processModel.Vertex;

public class QOSAccumulate {
    private static final long serialVersionUID = 1L;

    protected transient static Logger logger = Logger.getLogger("QUALAccumulation");

	public static void main(String[] args) {

	}
	
	
	 public LinkedHashSet<JSONEFFECT> trace_acc(Trace src, String kb) {
		 LinkedHashSet<JSONEFFECT> _result = new LinkedHashSet<JSONEFFECT>();
		 

		 _result.add(new JSONEFFECT());
	        for (Vertex v : src.getNodes()) {
	            LinkedHashSet<JSONEFFECT> intEff = new LinkedHashSet<JSONEFFECT>();
	            intEff.addAll(_result);

	            for (JSONEFFECT ce : _result) {
	                if (!(v.jsEFF == null)) {
	                    intEff.remove(ce);
	                    QOSAccumulate acc = new QOSAccumulate(); 
	                    intEff.addAll(acc.pairwise_acc(ce, v.jsEFF, kb, true));
	                    
	                }
	            }
	            _result = new LinkedHashSet<JSONEFFECT>();
	            _result.addAll(intEff);
	        }

		 
		 return _result;		 
	 }
    // //////////////////////////////////////////////
    // Below are pairwise accumulation functions///
    // //////////////////////////////////////////////

    public LinkedHashSet<JSONEFFECT> pairwise_acc(
    		JSONEFFECT source, JSONEFFECT target, String KB, boolean distrobute) {
    	// Do distribution stuff here
        return pairwise_acc(source, target, KB);
    }

    private LinkedHashSet<JSONEFFECT> pairwise_acc(JSONEFFECT source, JSONEFFECT target, String KB) {
    	
		return source.pairwise_acc(source, target, KB);
    	
    }


}
