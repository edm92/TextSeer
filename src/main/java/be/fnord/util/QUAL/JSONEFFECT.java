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
		Name = "";
		Type = "";
		QOS = new Qos();
		EFFECT = new String[] {};
		CONSTRAINT = new String [] {};
		GOAL = new String [] {};
		RESOURCE = new String []{};
		
	}
	
	public String toDSTring(){
		String _result = "";
		_result = "QOS:" + QOS + "; EFFECT:";
		for(String s : EFFECT) _result += s + ",";
		if(EFFECT.length > 0) _result = _result.substring(0,_result.length()-1);
		_result += ";\n "+ a.e.dent() + "CONSTRAINT: ";
		for(String s : CONSTRAINT) _result += s + ",";
		if(CONSTRAINT.length > 0) _result = _result.substring(0,_result.length()-1);				
		_result += ";\n"+ a.e.dent() +"GOAL: ";
		for(String s : GOAL) _result += s + ",";
		if(GOAL.length > 0) _result = _result.substring(0,_result.length()-1);
		_result += ";\n" + a.e.dent() + "RESOURCE: ";
		for(String s : GOAL) _result += s + ",";
		if(GOAL.length > 0) _result = _result.substring(0,_result.length()-1);
		_result += ";";
		return _result;
	}
	
	public String toString(){
		String _result = "";
		_result += "(" +Name + " " + Type + ")";
		_result = "QOS:" + QOS + "; EFFECT:";
		for(String s : EFFECT) _result += s + ",";
		if(EFFECT.length > 0) _result = _result.substring(0,_result.length()-1);
		_result += "; CONSTRAINT: ";
		for(String s : CONSTRAINT) _result += s + ",";
		if(CONSTRAINT.length > 0) _result = _result.substring(0,_result.length()-1);
		_result += "; GOAL: ";
		for(String s : GOAL) _result += s + ",";
		if(GOAL.length > 0) _result = _result.substring(0,_result.length()-1);
		_result += "; RESOURCE: ";
		for(String s : GOAL) _result += s + ",";
		if(GOAL.length > 0) _result = _result.substring(0,_result.length()-1);
		_result += ";";
		return _result;
	}

}
