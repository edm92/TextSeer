package au.edu.dsl.dlab.strategy;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.log4j.Logger;

import au.edu.dsl.dlab.processtools.Effect;

public class strategy {
	protected transient static Logger logger = Logger.getLogger("StrategyClass");
	
	private static final long serialVersionUID = 1L;
	public static TreeMap<String, strategy> allStrategies = new TreeMap<String,strategy>();
	public String id = "";
	public String name = "";
	public String formal = "";
	public String type = "";
	public LinkedList<String> parents = new LinkedList<String>();
	private transient UUID ID;

	public strategy(){ID = UUID.randomUUID();}
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
	
}
