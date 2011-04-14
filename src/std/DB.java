package std;

import java.util.LinkedList;
import java.util.List;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;

import textSeer.Model.Graph;

public class DB {
	public static String SOURCEFILE = std.string.endl + "Error in: std.DB.java" + std.string.endl;
	public static boolean DEBUG = true; //std.string.debug;	// Hopefully true when you edit :P
	public static void debug(String msg){
		if(DEBUG)
			std.calls.debug_(msg + SOURCEFILE);
	}
	
	public static ODB odb = null;
	
	public static boolean openDB(){
        odb = ODBFactory.open(std.string.database);
        if(odb != null) return true;
        return false;
	}
	
	
	public static boolean closeDB(){
		if (odb != null) {
            // Close the database
            odb.close();
        }
		return true;
	}
	
	public static boolean saveGraph(Graph g){
		if(g == null) return false;
		openDB();
		
		
		odb.store(g);
		
		
		closeDB();
		
		return true;
	}
	
	public static List<Graph> openGraph(){
		LinkedList<Graph> myList = new LinkedList<Graph>();
		
		openDB();
		IQuery query = new CriteriaQuery(Graph.class);
		//query.orderByAsc("id");
 
		Objects<Graph> Graphs = odb.getObjects(query);
 
		System.out.println("\nGraphs ordered by id asc");
 
		//int i = 1;
		// display each object
		do{
			Graph g = Graphs.next();
			//debug("Got a graph" + g.ID);
			if(g != null){
				//System.out.println((i++) + "\t: " + Graphs.next().toString());
				myList.add(g);
			}
		}while (Graphs.hasNext());
		closeDB();
 
        return myList;
	}
}
