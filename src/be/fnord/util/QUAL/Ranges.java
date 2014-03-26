package be.fnord.util.QUAL;

/**
 * Created by edm92 on 25/03/2014.
 */
public class Ranges {

    public enum type{
        POS_REAL_NUM,
        POS_NAT_NUM,
        NEG_REAL_NUM,
        NEG_NAT_NUM,
        PROBABILITY,
        HIGH_MED_LOW,
        OTHER
    };
    
    public enum hml{
    	HIGH("HIGH"), MED("MED"), LOW("LOW");
    	
    	hml type;
    	String _type = "HIGH";
    	hml(String _set){
    		_type = _set;
    		
    	}
    	
    	public String toString(){
    		return _type;
    	}
    	
    	public hml toType(String _set){
    		if(_set.compareTo("HIGH") == 0) type = hml.HIGH;
    		if(_set.compareTo("MED") == 0) type = hml.MED;
    		if(_set.compareTo("LOW") == 0) type = hml.LOW;
    		return type;
    	}
    	
    	
    };
    
    public static int INFTY = 99999999;	// Approximation :P

}
