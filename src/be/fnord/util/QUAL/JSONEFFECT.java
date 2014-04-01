package be.fnord.util.QUAL;

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
	public JSONEFFECT(){
		Name = "TestName";
		Type = "Activity";
		QOS = new Qos();
		EFFECT = new String[] {"A", "B","C"};
		CONSTRAINT = new String [] {"~A", "B", "C"};
		GOAL = new String [] {};
		RESOURCE = new String []{};
		
	}

}
