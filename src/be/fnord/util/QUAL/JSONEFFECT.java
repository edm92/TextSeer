package be.fnord.util.QUAL;

import java.util.LinkedHashSet;

public class JSONEFFECT {
	public String Name = "";
	public String Type = "";
	
	public Qos QOS = new Qos();;
	
	public String [] EFFECT ;
	public String [] CONSTRAINT ;
	
	public String [] GOAL ;
	
	public String [] RESOURCE;
	
	public String getEffect(){
		String _result = "";
		if(EFFECT != null && EFFECT.length > 0)
		for(String s : EFFECT){
			if(s.length() > 0)
			_result += "( "+s+" ) & ";
		}
		if(_result.length() > 3) _result = _result.substring(0, _result.length() - 3);	
		
		return _result;
	}
	
	public boolean isEmpty(){
		if(Name.length() > 0 && Type.length() > 0) return false;
		
		return true;
	}
	
	// Accumulate 
	public LinkedHashSet<JSONEFFECT> pairwise_acc(JSONEFFECT source, JSONEFFECT target, String KB) {
		JSONEFFECT _result = target.copy();
		
		// Leave Name and TYPE
		_result.QOS = QOS.pairwise_acc(source.QOS, target.QOS, source, target);
    	
    	// Copy the final effect 
    	LinkedHashSet<JSONEFFECT> rs = new LinkedHashSet<JSONEFFECT>();
    	rs.add(_result);
		return rs;
	}
	
	// Return a copy of this effect
	public JSONEFFECT copy(){
		JSONEFFECT _result = new JSONEFFECT();
		
		_result.Name = Name;
		_result.Type = Type;
		_result.QOS = QOS.copy();
		_result.EFFECT 		= _cpStrArr(EFFECT);
		_result.CONSTRAINT 	= _cpStrArr(CONSTRAINT);
		_result.GOAL 		= _cpStrArr(GOAL);
		_result.RESOURCE 	= _cpStrArr(RESOURCE);
		
		return _result;
	}
	
	public String[] _cpStrArr(String [] input){
		String [] _result = new String [EFFECT.length];
		int i = 0;
		for(String s: EFFECT)
			_result[i++] = s;
		
		return _result;
		
	}
	
	public JSONEFFECT(){
		Name = "TestName";
		Type = "Activity";
		QOS = new Qos();
		EFFECT = new String[] {};
		CONSTRAINT = new String [] {};
		GOAL = new String [] {};
		RESOURCE = new String []{};
		
	}

}
