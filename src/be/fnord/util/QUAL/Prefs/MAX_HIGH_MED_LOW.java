package be.fnord.util.QUAL.Prefs;

import java.util.Set;
import java.util.TreeSet;


import be.fnord.collections.Pair;
import be.fnord.collections.Poset;

public class MAX_HIGH_MED_LOW extends Preferences implements PREF_FUNC{
    
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

	Ranges.type range = Ranges.type.HIGH_MED_LOW;
	static hml top = hml.HIGH;
	static hml bot = hml.LOW;
	Poset hmlSet; 
	
	public MAX_HIGH_MED_LOW(){
		super();
		setup();
	}
	
	
	
	public void setup(){		
		TreeSet<String> s = new TreeSet<String>();
		s.add(hml.HIGH.toString());
		s.add(hml.MED.toString());
		s.add(hml.LOW.toString());
		hmlSet = new Poset(); 
		hmlSet.List(
				P(hml.HIGH , hml.MED),
				P(hml.MED , hml.LOW)
				);

	
	}

	

	public static void main(String [] args){
		MAX_HIGH_MED_LOW m = new MAX_HIGH_MED_LOW();
		a.e.println("isBetter(bot,top) = " + m.compare(bot,top));
		a.e.println("isBetter(top,bot) = " + m.compare(top,bot));
		a.e.println("isBetter(top,MED) = " + m.compare(top,hml.MED));
		a.e.println("Combine(top,bot) = " + m.combine(top,bot));
		a.e.println("Combine(bot,top) = " + m.combine(bot,top));
	}

	static <T> Pair<T,T> P(T a, T b){
		return new Pair<T,T>(a,b);
	}



	@Override
	public <T> boolean compare(T a, T b) {
		return hmlSet.leq(a, b);		
	}



	@Override
	public <T> String combine(T a, T b) {
		if(hmlSet.leq(a, b)) return a.toString();
		else return b.toString();
	}

}
