package be.fnord.util.QUAL.Prefs;

import be.fnord.util.QUAL.Preferences;
import be.fnord.util.QUAL.Ranges;
import be.fnord.util.QUAL.Ranges.type;

public class MAX_COST extends Preferences<Float>{
	type range = Ranges.type.POS_REAL_NUM;
	static float top = Ranges.INFTY;
	
	public MAX_COST(){
		super();
	}
	
	public static boolean isBetter(float a, float b){
		return a >= b;
	}
	
	public static void main(String [] args){
		MAX_COST m = new MAX_COST();
		
		if(m.top >= m.top) a.e.println("Hello world" + m.top);  
	}
}


//PoSet<String> ex1 = new PoSet<String>(Set("abc", "def", "ab", "defgh")) { 
//	 public boolean _le_(String a, String b) { 
//	 return b.indexOf(a) >= 0; 
//	 } 
//	 }; 