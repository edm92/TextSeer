
import au.edu.dsl.dlab.strategy.strategy;


public class StrategyDBTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		config.Settings.init();	
		strategy.LoadFromDB();
	    for(strategy s: strategy.allStrategies.values()){
	    	System.out.println(s);
	    }

	}

}
