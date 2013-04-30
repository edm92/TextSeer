package a;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * General utilities and global flags
 * @author Evan Morrison edm92@uowmail.edu.au http://www.fnord.be
 * Apache License, Version 2.0, Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 * 
 */
public class e {
	public static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public static boolean __DEBUG = false;
	public static boolean __INFO  = true;
	public static boolean __FATAL = true;
	public static boolean __HIGHDETAILS = false;
	
	public static boolean __LOGGER = true;
	
	// Process Model loading 
	public static final int NO_FLAGS = 0;
	public static final int DONT_SAVE_MESSAGES_AND_PARTICIPANTS= 1;		// Don't bother with message flows between pools or between actors
	
	// Trace processing 
	public static final int SIMPLE_TRACES = 0;			// Simple trace processing, don't both with order constrained permutations
	public static final int FULL_TRACES = 1;			// Compute ALL traces. 
	
	public static final int AGGRESSIVE_DEDUPING = 1;	// Don't consider edges, if you find that edges are being removed then change to simple
	public static final int SIMPLE_DEDUPING = 2; 		// Consider edges, this edges up leaving lots of duplicates. 
														// See GraphTransformer for implementation to see ifyou can make it better?
	public static final int NO_DEDUPING = 3;
	
	public static int DEDUPING_LEVEL = AGGRESSIVE_DEDUPING; // See above

	
	public static final int FATAL = 5;
	public static final int DEBUG = 3;
	public static final int INFO  = 1;
	public static final int OFF   = 0;
	
	static final int __DEFAULTDISPLAY = INFO;
	public static String endl = System.getProperty("line.separator");
	public static String tab  = "\t";
	
	public static int indent = 0;
	
	public static void incIndent(){ ++indent; }
	public static void decIndent(){ --indent; if(indent < 0) indent = 0;}
	
	public static String dent() { String result = ""; for(int i = 0; i < indent; i++) { result += tab; }; return result; }
	
	public static String write(String msg)   { return dent() + msg ; } 
	public static String writeln(String msg) { return write(msg) + endl; }
	public static String print(String msg)   { return print(msg, __DEFAULTDISPLAY); } 
	public static String println(String msg) { return println(msg, __DEFAULTDISPLAY); }
	public static String println(String msg, int displayLevel) { return print(msg + endl); }
	public static String print(String msg,   int displayLevel)   { String result = write(msg); log(result);  
		switch(__DEFAULTDISPLAY){
		case INFO:System.out.print(result); break;
		case DEBUG:
		case FATAL : System.err.print(result); break;
		}; return result; }
	
	
	public static void log(String msg){
		if(__LOGGER){switch(__DEFAULTDISPLAY){
			case INFO:	LOGGER.info(msg);break;
			case DEBUG:	LOGGER.warning(msg);break;
			case FATAL:	LOGGER.severe(msg);break;	}	}   }
	
	/**
	 * Init Function, not assumed to be executed
	 */
	static boolean __initDone = false;
	public e(){
		if(!__initDone){
			LOGGER.setLevel(Level.SEVERE);
			__initDone = true;
		}
	}
}
