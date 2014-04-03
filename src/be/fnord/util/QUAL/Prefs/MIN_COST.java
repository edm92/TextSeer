package be.fnord.util.QUAL.Prefs;

import be.fnord.util.QUAL.Prefs.Ranges.type;

public class MIN_COST extends Preferences<Float> implements PREF_FUNC{
	type range = Ranges.type.POS_REAL_NUM;
	static float top = 0;
	static float bot = Ranges.INFTY;
	
	public MIN_COST(){
		super();
	}
		
	public <T> boolean compare(T aa, T bb){
		double _a = 0;
		double _b = 0; 
		if(aa == null || bb == null) return false;
		
		if( aa.getClass().equals(String.class) ){
			if(aa.toString().trim().length() < 1) return false;
			if(bb.toString().trim().length() < 1) return true;
			try{
				String _aa = (String)aa;
				String _bb = (String)bb;
				_aa = _aa.replaceAll("\\$", "").trim();
				_aa = _aa.replaceAll(",", "").trim();
				_aa = _aa.replaceAll("c", "").trim();
				
				_bb = _bb.replaceAll("\\$", "").trim();
				_bb = _bb.replaceAll(",", "").trim();
				_bb = _bb.replaceAll("c", "").trim();
				if(_aa.length() < 1) return false;
				if(_bb.length() < 1) return true;
				_a = Double.parseDouble((String) _aa);
				_b = Double.parseDouble((String) _bb);
				
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
		
		return _a <= _b;
	}
	
	public static void main(String [] args){
		MIN_COST m = new MIN_COST();
		a.e.println("isBetter($10,$20.50) = " + m.compare("$10","$20.50"));
		a.e.println("isBetter($10.01,$10) = " + m.compare("$10.01","10"));
		a.e.println("Combine($10.25,20.75) = " + m.combine("$10.25","20.75"));
		if(m.compare(bot,top)) a.e.println("Bot rules " + top);
		if(m.compare(top,bot)) a.e.println("Top rules " + top);
	}

	@Override
	public <T> String combine(T aa, T bb) {
		double _a = 0;
		double _b = 0; 
		if( aa.getClass().equals(String.class) ){
			try{
				String _aa = (String)aa;
				String _bb = (String)bb;
				_aa = _aa.replaceAll("\\$", "").trim();
				_aa = _aa.replaceAll(",", "").trim();
				_aa = _aa.replaceAll("c", "").trim();
				
				_bb = _bb.replaceAll("\\$", "").trim();
				_bb = _bb.replaceAll(",", "").trim();
				_bb = _bb.replaceAll("c", "").trim();
				if(_aa.length() < 1) return _bb;
				if(_bb.length() < 1) return _aa;
				_a = Double.parseDouble((String) _aa);
				_b = Double.parseDouble((String) _bb);
			}catch(Exception e){
				a.e.err("error in input " + e);
			}
		}else
		if( aa.getClass().equals(Integer.class) ){
			_a = (double) ((Integer)aa + .0);
			_b = (double) ((Integer)bb + .0);
		}else if(aa.getClass().equals(Float.class)){
			_a = (Double)aa;
			_b = (Double)bb;
		}else if(aa.getClass().equals(Double.class)){
			_a = (Double)aa;
			_b = (Double)bb;
		}	
		return ((_a + _b)) + "";
	}
}


//PoSet<String> ex1 = new PoSet<String>(Set("abc", "def", "ab", "defgh")) { 
//	 public boolean _le_(String a, String b) { 
//	 return b.indexOf(a) >= 0; 
//	 } 
//	 }; 