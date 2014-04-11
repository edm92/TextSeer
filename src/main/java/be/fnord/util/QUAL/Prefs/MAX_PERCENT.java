package be.fnord.util.QUAL.Prefs;

import be.fnord.util.QUAL.Prefs.Ranges.type;

public class MAX_PERCENT extends Preferences<Float> implements PREF_FUNC{
	type range = Ranges.type.POS_REAL_NUM;
	static double top = 1;
	static double bot = 0;
	
	public MAX_PERCENT(){
		super();
	}
	
	public <T> boolean compare(T aa, T bb){
		double _a = 0;
		double _b = 0; 
		if(aa == null || bb == null) return false;
		if( aa.getClass().equals(String.class) ){
			try{
				_a = Double.parseDouble((String) aa);
				_b = Double.parseDouble((String) bb);
			}catch(Exception e){
				a.e.err("error in input " + e);
			}
		}else
		if( aa.getClass().equals(Integer.class) ){
			_a = (double) ((Integer)aa + .0);
			_b = (double) ((Integer)bb + .0);
		}else if(aa.getClass().equals(Float.class)){
			_a = (double) ((Float)aa + .0);
			_b = (double) ((Float)bb + .0);
		}else if(aa.getClass().equals(Double.class)){
			_a = (Double)aa;
			_b = (Double)bb;
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

	
	public <T> String combine(T aa, T bb) {
		double _a = 0;
		double _b = 0; 
		if( aa.getClass().equals(String.class) ){
			try{
				_a = Double.parseDouble((String) aa);
				_b = Double.parseDouble((String) bb);
			}catch(Exception e){
				a.e.err("error in input " + e);
			}
		}else
		if( aa.getClass().equals(Integer.class) ){
			_a = (double) ((Integer)aa + .0);
			_b = (double) ((Integer)bb + .0);
		}else if(aa.getClass().equals(Float.class)){
			_a = (double) ((Float)aa + .0);
			_b = (double) ((Float)bb + .0);
		}else if(aa.getClass().equals(Double.class)){
			_a = (Double)aa;
			_b = (Double)bb;
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