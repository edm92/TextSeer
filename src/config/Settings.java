package config;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import au.edu.dsl.dlab.processtools.parser.ModelLoader;
import au.edu.dsl.dlab.processtools.parser.bpmn.Activiti;


public class Settings {
	public static boolean RECOMPUTE_SCENARIOS = false;
	protected transient static Logger logger = Logger.getLogger("Settings");
	public static String tab = "\t";
	public static String t = "";
	public static int current_indent = 0;
	public static boolean _DEBUG = true;
	public static String sharredModelKey = "sharedModels";
	public static String jppgExecutionError = "ErrorExecutingJPPFTask";
	public static String l = System.getProperty("line.separator");
	public static String s = System.getProperty("file.separator");
	public static String _name = System.getProperty("user.name");
	public static String _home = System.getProperty("user.home");
	public static String _currentDir = System.getProperty("user.dir");
	public static enum OS { WIN, MAC, LIN, ALT };
	public static OS _SYS = OS.ALT;
	public static String _JTTP_ServerScript = "";
	public static String _JTTP_NodeScript = "";
	public static String _repository = "";
	public static boolean visualEnabled = false;
	public static transient TreeMap<String, String> PROP = new TreeMap<String,String>();
	public static String currentProp = "";
	
	
	public transient static ModelLoader MODELS = null; 
	
	public static boolean LoadFromDB = false;
	public static boolean StoreInDB = false;
	public static boolean LoadFromRepo = false;
	public static boolean _JPPF_EXEC = true;
	
	public static String mainDB = "base.db";	
	public static String propertiesBase = "base.properties";

	public static String makeNewProp(){
		String results = "";
		return results;
	}
	
	public static String get_t(){
		String tt = "";
		for(int i = 0; i < current_indent; i++){
			tt += tab;
		}
		t = tt;
		return tt;
	}
	public static boolean initialized = false;
	
	public static void increaseIndent(){ current_indent++; get_t();}
	public static void decreaseIndent(){ current_indent--; get_t();}
	
	public static void init(){
		if(initialized) {System.err.println("Init done"); return; }
		//System.err.println("Doing init");
		
		initialized=true;
		t = get_t();
		
		// Get OS if that is important
		if(isWindows()){
			_SYS = OS.WIN;
		}else if(isMac()){
			_SYS = OS.MAC;
		}else if(isUnix()){
			_SYS = OS.LIN;
		}else{
			_SYS = OS.ALT;
		}
	
		// Load main properites
		Properties props = new Properties();

        //try retrieve data from file
			try {
				props.load(new FileInputStream(propertiesBase));
				mainDB = props.getProperty("mainDB");
				_repository 		= props.getProperty("Repository");
				if(_repository.contains("$CURRENTDIR$")){
					_repository = _repository.replace("$CURRENTDIR$", _currentDir);
				}			
				visualEnabled = Activiti.PREPARE_VISUAL_MODELS = Integer.parseInt(props.getProperty("prepareVisual")) == 1 ? true : false;
				LoadFromRepo = Integer.parseInt(props.getProperty("loadFromRepo")) == 1 ? true : false;
				RECOMPUTE_SCENARIOS = Integer.parseInt(props.getProperty("recomputeScenarios")) == 1 ? true : false; 
				

//				LoadFromDB = Integer.parseInt(props.getProperty("loadFromDB")) == 1 ? true : false;
//				StoreInDB = Integer.parseInt(props.getProperty("storeInDB")) == 1 ? true : false;
//				LoadFromRepo = Integer.parseInt(props.getProperty("loadFromRepo")) == 1 ? true : false;
				_JPPF_EXEC = Integer.parseInt(props.getProperty("useJPPF")) == 1 ? true : false; 
			}catch(Exception e){
				e.printStackTrace();
				logger.error("Fatal Error: Problem loading properties - init():" +  e);
				System.exit(1);
			}
			
		//System.err.println("_repository:" + _repository);
			
	
		
	}
	
	
	
	
	public static String _toString(){
		String _r = "";
		_r += "File seperator : " + s + l + 
			"Username: " + _name + l +
			"HomeDir:" + _home + l +
			"CurrentDir: " + _currentDir + l + 
			"System: " + sysString() + l;
		
		return _r;	
	}
	
	public static String sysString(){
		if(_SYS == OS.WIN) return "Windows";
		if(_SYS == OS.MAC) return "Mac";
		if(_SYS == OS.LIN) return "Linux";
		return "Not known";
	}
	
	public static boolean isWindows(){
		 
		String os = System.getProperty("os.name").toLowerCase();
		//windows
	    return (os.indexOf( "win" ) >= 0); 
 
	}
 
	public static boolean isMac(){
 
		String os = System.getProperty("os.name").toLowerCase();
		//Mac
	    return (os.indexOf( "mac" ) >= 0); 
 
	}
 
	public static boolean isUnix(){
 
		String os = System.getProperty("os.name").toLowerCase();
		//linux or unix
	    return (os.indexOf( "nix") >=0 || os.indexOf( "nux") >=0);
 
	}
		
	
}
