package au.edu.dsl.dlab.strategy;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteStatement;

//import java.util.HashSet;
//import org.apache.log4j.Logger;
//import au.edu.dsl.dlab.processtools.Effect;
//import com.almworks.sqlite4java.SQLiteConnection;
//import com.almworks.sqlite4java.SQLiteConstants;
//import com.almworks.sqlite4java.SQLiteStatement;

/**
 * NOTE there is a hard limit of 1000 strategies coded into : fixReferences
 */

public class strategy {
	protected transient static Logger logger = Logger.getLogger("StrategyClass");
	
	private static final long serialVersionUID = 1L;
	public static TreeMap<String, strategy> allStrategies = new TreeMap<String,strategy>();
	public String id = "";
	public String name = "";
	public String formal = "";
	public String type = "";
	public LinkedList<String> parents = new LinkedList<String>();

	public strategy(){
		}
	
	public strategy(String _id, String _name, String _formal, String _type, String _parents){
		super();
		id = _id;
		name = _name;
		formal = _formal;
		type = _type;
		allStrategies.put(id, this);
		
		if(_parents == null) return;
		StringTokenizer st = new StringTokenizer(_parents, ",");
		while (st.hasMoreTokens()) {
			parents.add(st.nextToken());
		}
	}
	
	public static void fixReferences(){
		for(String key1 : allStrategies.keySet()){
			strategy s = allStrategies.get(key1);
			if(s.formal.contains("{") && s.formal.contains("}")){
				//_fixReferences();
			}
		}
		
	}
	
	public static void _fixReferences(){
		try{
			HashSet<strategy> replace = new HashSet<strategy>();
			for(String key1 : allStrategies.keySet()){
				strategy s = allStrategies.get(key1);
//				while(s.formal.contains("{") && s.formal.contains("}")){
					int i = indexOfFirstContainedCharacter(s.formal, "{");
					int j = indexOfFirstContainedCharacter(s.formal, "}");
					if (j - i > 0 && j-i < 4) { // Hard limit of 1000 strategies
						String replaceID = s.formal.substring(i+1, j).trim();
						System.out.println("Trying " + replaceID);
						if(allStrategies.containsKey(replaceID)){
							s.formal = s.formal.replaceAll("{" + replaceID + "}", "( " + allStrategies.get(replaceID) +" )" );
						}else{
							throw new Exception("invalid key - " + replaceID);
						}
					}
//				}
				replace.add(s);
			}
			for(strategy s : replace){
				allStrategies.remove(s.id);
				allStrategies.put(s.id,s);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static int indexOfFirstContainedCharacter(String s1, String s2) {
		  Set<Character> set = new HashSet<Character>();
		  for (int i=0; i<s2.length(); i++) {
		    set.add(s2.charAt(i)); // Build a constant-time lookup table.
		  }
		  for (int i=0; i<s1.length(); i++) {
		    if (set.contains(s1.charAt(i))) {
		      return i; // Found a character in s1 also in s2.
		    }
		  }
		  return -1; // No matches.
		}

	
	public String toString(){
		String result = "Strategy("+type+"):{"+id+"}" + name + ":" + formal;
		if(parents.size() > 0){
			result += " -- Parents --";
			for(String s : parents){
				if(allStrategies.get(s) != null)
					result += "\n\t" + allStrategies.get(s);
			}
		}
		return result;
	}
	
	public static void LoadFromDB(){
		// "models/strategy.sqlite"
		System.out.println("Loading strategies from SQLDB - "+ config.Settings.StrategyDB);
		LoadFromDB(config.Settings.StrategyDB); // Location is set in base.properties file
	}
	public static void LoadFromDB(String strategyDB){
		
		Logger.getLogger("com.almworks.sqlite4java").setLevel(Level.OFF);
		SQLiteConnection db = new SQLiteConnection(new File(strategyDB));
		SQLiteStatement st = null;
		 try {
		    db.open(true);
		    
		    st = db.prepare("SELECT * FROM Mainstrategy");
	   
		    String ColumnNames[] = new String[5];
	    	  for (int i = 0; i < st.columnCount(); i++) {
	    	    String columnName = st.getColumnName(i);
	    	    ColumnNames[i] = columnName;
	    	  }
	    	  while (st.step()) {
	    		  strategy newStrategy = new strategy(st.columnString(0), st.columnString(1), 
	    				  st.columnString(2), st.columnString(3), st.columnString(4)); 
	    		  if(newStrategy == null) throw new Exception("Strategy not created");
	    	  }
	    } catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(st!= null)
				st.dispose();
	    }
	    db.dispose();
	    fixReferences();
	}
	
}
