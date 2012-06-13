import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import au.edu.dsl.dlab.strategy.strategy;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteConstants;
import com.almworks.sqlite4java.SQLiteStatement;


public class StrategyDBTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Logger.getLogger("com.almworks.sqlite4java").setLevel(Level.OFF);
		SQLiteConnection db = new SQLiteConnection(new File("models/strategy.sqlite"));
		SQLiteStatement st = null;
		 try {
		    db.open(true);
		    
		    st = db.prepare("SELECT * FROM Mainstrategy");
	   
		    String ColumnNames[] = new String[5];
	    	 // you can get column count and names before or after st.step()
	    	  for (int i = 0; i < st.columnCount(); i++) {
	    	    String columnName = st.getColumnName(i);
	    	    ColumnNames[i] = columnName;
	    	    //System.out.println("columnName:" + columnName);
	    	  }
	    	  while (st.step()) {
	    		  //System.out.println("New Strategy-------------");
	    		  strategy newStrategy = new strategy(st.columnString(0), st.columnString(1), 
	    				  st.columnString(2), st.columnString(3), st.columnString(4));
	    		  //System.out.println(":" + newStrategy);
	    	  }
	    } catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(st!= null)
				st.dispose();
	    }
	    db.dispose();
	    
	    for(strategy s: strategy.allStrategies.values()){
	    	System.out.println(s);
	    }

	}

}
