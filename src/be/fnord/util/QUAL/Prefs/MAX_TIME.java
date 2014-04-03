package be.fnord.util.QUAL.Prefs;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import be.fnord.util.QUAL.Prefs.Ranges.type;

/**
 * Using Joda time to implement durations / periods
 * http://en.wikipedia.org/wiki/ISO_8601#Durations 
 * @author edm92
 *
 */
public class MAX_TIME extends Preferences<Float> implements PREF_FUNC{
	type range = Ranges.type.TIME;
	static String bot = Ranges.LNGTIME;
	static String top = Ranges.SHRTTIME;
	
	public MAX_TIME(){
		super();
	}
	
	public static void main(String [] args){
		MAX_TIME m = new MAX_TIME();
		a.e.println("isBetter(PT1M,PT2M) = " + m.compare("PT1M","PT2M"));
		a.e.println("isBetter(PT2M,PT1M) = " + m.compare("PT2M","PT1M"));
		a.e.println("Combine(PT2M,PT1M) = " + m.combine("PT2M","PT1M"));
		if(m.compare(bot,top)) a.e.println("Bot rules" + top);
		if(m.compare(top,bot)) a.e.println("Top rules" + top);
		
		m.Convert("P10D");
	}
	
	public DateTime Convert(String t){
		if(t.length() < 1) t = top;
		DateTime dt = DateTime.now();
		dt = dt.plus(Period.parse(t));
		return dt;
	}
	
	public String Convert(int t){
		String _result = Ranges.LNGTIME;
		
		return _result;	
	}
	
	public <T> boolean compare(T aa, T bb){
		if(aa == null || bb == null) return false;
		if( !aa.getClass().equals(DateTime.class) ){
			return false;
		}			
		return (Convert((String)bb)).isBefore(Convert((String)aa));
	}
	

	

	@Override
	public <T> String combine(T aa, T bb) {
		if(aa == null || bb == null) return "";
		if( !aa.getClass().equals(String.class) ){
			return aa.toString();
		}			
		
		DateTime dt = DateTime.now();
		DateTime _result = Convert((String)aa);
		_result = _result.plus(Period.parse((String) bb));
		DateTime _df = _result.minus(dt.getMillis());
		
		Period result = new Period(_df.getMillis());
//		System.out.println("Result = " + result);
		return result.toString();
	}
	
	
	
	
}


//PoSet<String> ex1 = new PoSet<String>(Set("abc", "def", "ab", "defgh")) { 
//	 public boolean _le_(String a, String b) { 
//	 return b.indexOf(a) >= 0; 
//	 } 
//	 }; 