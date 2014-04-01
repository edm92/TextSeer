package be.fnord.util.QUAL.Prefs;

import be.fnord.util.QUAL.Prefs.Ranges.type;

public class MAX_PERCENT extends Preferences<Float> implements PREF_FUNC{
	type range = Ranges.type.POS_REAL_NUM;
	static double top = 1;
	static double bot = 0;
	
	public MAX_PERCENT(){
		super();
	}
	
	public <T> boolean compare(T a, T b){
		double _a = 0;
		double _b = 0; 
		if(a == null || b == null) return false;
		if( a.getClass().equals(Integer.class) ){
			_a = (double) ((Integer)a + .0);
			_b = (double) ((Integer)b + .0);
		}else if(a.getClass().equals(Float.class)){
			_a = (double) ((Float)a + .0);
			_b = (double) ((Float)b + .0);
		}else if(a.getClass().equals(Double.class)){
			_a = (Double)a;
			_b = (Double)b;
		}	
		
		return _a >= _b;
	}
	
	public static void main(String [] args){
		MAX_PERCENT m = new MAX_PERCENT();
		a.e.println("isBetter(.1,.2) = " + m.compare(.1,.2));
		a.e.println("isBetter(.2,.1) = " + m.compare(.2,.1));
		a.e.println("Combine(.2,.1) = " + m.combine(.2,.1));
		if(m.compare(bot,top)) a.e.println("Bot rules" + top);
		if(m.compare(top,bot)) a.e.println("Top rules" + top);
	}

	@Override
	public <T> String combine(T a, T b) {
		double _a = 0;
		double _b = 0; 
		if( a.getClass().equals(Integer.class) ){
			_a = (double) ((Integer)a + .0);
			_b = (double) ((Integer)b + .0);
		}else if(a.getClass().equals(Float.class)){
			_a = (double) ((Float)a + .0);
			_b = (double) ((Float)b + .0);
		}else if(a.getClass().equals(Double.class)){
			_a = (Double)a;
			_b = (Double)b;
		}	
		double _com = ((_a * _b));
		return _com%top + "";
	}
}


//PoSet<String> ex1 = new PoSet<String>(Set("abc", "def", "ab", "defgh")) { 
//	 public boolean _le_(String a, String b) { 
//	 return b.indexOf(a) >= 0; 
//	 } 
//	 }; 