package be.fnord.util.QUAL.Prefs;

import java.util.Set;
import java.util.TreeSet;


import be.fnord.util.QUAL.Preferences;
import be.fnord.util.QUAL.Ranges;
import be.fnord.util.QUAL.Ranges.hml;

public class MAX_HIGH_MED_LOW extends Preferences{
	Ranges.type range = Ranges.type.HIGH_MED_LOW;
	static Ranges.hml top = Ranges.hml.HIGH;
	static Ranges.hml bot = Ranges.hml.LOW;
//	static PoSet<String> ex1 ;
	
	public MAX_HIGH_MED_LOW(){
		super();
		setup();
	}
	
	public void setup(){		
		TreeSet<String> s = new TreeSet<String>();
		s.add(hml.HIGH.toString());
		s.add(hml.MED.toString());
		s.add(hml.LOW.toString());
	
	}
	
	
	public static boolean isBetter(Ranges.hml a, Ranges.hml b){
//		return ex1._le_(a.toString(), b.toString());
		return true;
	}
	
	public static void main(String [] args){
		MAX_HIGH_MED_LOW m = new MAX_HIGH_MED_LOW();
		a.e.println("isBetter(bot,top) = " + isBetter(bot,top));
		a.e.println("isBetter(top,bot) = " + isBetter(top,bot));
		if(isBetter(bot,top)) a.e.println("Hello world" + top);  
	}



}
