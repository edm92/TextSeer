package au.edu.dsl.dlab.processtools.parser;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;

import config.Settings;

import au.edu.dsl.dlab.processtools.Edge;
import au.edu.dsl.dlab.processtools.Effect;
import au.edu.dsl.dlab.processtools.Graph;
import au.edu.dsl.dlab.processtools.Vertex;
import au.edu.dsl.dlab.processtools.parser.bpmn.Activiti;


public class ModelLoader{ // implements DataProvider{ // Implement for JPPF 
	private static final long serialVersionUID = 1L;
	public static boolean INUSE = false;
	public TreeMap<String, Graph<Vertex,Edge>> models;
	public TreeMap<String, String> KB;	// Extend to TreeMap<String, LinkedList<String>> for complex expressions
	
	public TreeMap<String, HashSet<Effect>> EFFECTS;	// Extend to TreeMap<String, LinkedList<String>> for complex expressions
	public LinkedList<String> alreadyInDB ;
	
	public ModelLoader(){
		init();
	}
	
	public void init(){
		INUSE = true;
		EFFECTS = new TreeMap<String, HashSet<Effect>>();
		Settings.init();	// Make sure we're okay to use our settings.
		Settings.MODELS = this;
		models = new TreeMap<String, Graph<Vertex,Edge>>();
		alreadyInDB = new LinkedList<String>();
		KB = new TreeMap<String, String>();
		load();
	}
	
	public Set<String> getModelIDs(){
		return models.keySet();
	}
	
	public Graph<Vertex,Edge> getModel(String id){
		if(models.containsKey(id))
			return models.get(id);
		else
			return null;
	}
	
	public void load(){
		
		String[] children;
		if(Settings.LoadFromRepo){
			children = getFilesInRepo();
			if(children == null ) { System.err.println("Unable to load repository."); return; };
			for(String s: children){
				if(s.length() > 10)
				if(s.substring(s.length() - 10, s.length()).compareToIgnoreCase("bpmn20.xml") == 0){
					//System.err.println("Loaded" + Settings._repository + Settings.s + s);
					Activiti newLoader = new Activiti();
					Graph<Vertex,Edge> t = newLoader.loadModel( Settings._repository + Settings.s + s);
					
					if(t != null){
						t.filename = s;
						models.put(t.name, t);
					}
						
				}else if(s.substring(s.length() - 4, s.length()).compareToIgnoreCase(".acc") == 0){
					HashSet<Effect> accEff = effectReadWrite.readEffect(Settings._repository + Settings.s + s);
					if(EFFECTS == null) System.err.println("Here");
					EFFECTS.put(s, accEff);
				}else{
					if(s.substring(s.length() - 7, s.length()).compareToIgnoreCase(".kb.txt") == 0){
						String kb = KBReader.readKB(Settings._repository + Settings.s + s);
						KB.put(s, kb);
					}else{
						if(s.substring(s.length() - 5, s.length()).compareToIgnoreCase("pnml.xml") == 0){
							//tsGraph t = 
							// Petri-net parser goes here
//								SigPNMLParser.PARSEmain( a.s._repository + a.s.s + s, s);
//							if(t != null)
//							models.put(t._LABEL, t);
						}
					}
				}
			}
			
		}
		
		// Add document effects
		for(String id: models.keySet()){
			if(EFFECTS!= null)
			if(EFFECTS.containsKey(id)){
				models.get(id).effects = EFFECTS.get(id);
			}
		}
		
		// Database work has been removed below
		
//		if(Settings.LoadFromDB){
//			 ODB odb = null;
//			  try{		        
//				  odb = ODBFactory.open(a.s.mainDB);
//				  IQuery query = new CriteriaQuery(tsGraph.class);
//				  Objects<tsGraph> objects = odb.getObjects(query);
//				  while(objects.hasNext()){
//					  tsGraph current = objects.next();
//					  
//					  if(!models.containsKey(current._LABEL)){
//						  models.put(current._LABEL, current);
//					  }else{
//						  alreadyInDB.add(current._LABEL);
//					  }
//				  }
//			     } finally{
//			         if(odb!=null){
//			          // Close the database
//			          odb.close();
//			         }        
//			    }
//		}
		
//		if(a.s.StoreInDB){
//			ODB odb = null;
//			 
//	        try {
//	            // Open the database
//	            odb = ODBFactory.open(a.s.mainDB);
//	            for(String s: models.keySet()){
//	            	if(!alreadyInDB.contains(s)){
//	            		 // Store the object
//	    	            odb.store(models.get(s));
//	            	}
//	            }
//	           
//	        } finally {
//	            if (odb != null) {
//	                // Close the database
//	                odb.close();
//	            }
//	        }
//		}		
	}
	
	
	public String getKB(String modelName){
		//a.s.dr("Passed back " + initKB(modelName));
		return initKB(modelName);
	}
	
	public String initKB(){
		String resultString ="";
		if(KB.containsKey("main.kb.txt")){
			resultString = KB.get("main.kb.txt"); // Load first
			return resultString;
		}
		
		return resultString;
	}
	
	public String initKB(String modelName){
		String resultString = initKB();
		if(KB.containsKey(modelName)){
			resultString += " ^ (" + KB.get(modelName) + ")";
		}		
		return resultString;
	}
	
	public String initKB(String modelName, String currentKB){
		String resultString = currentKB;
		if(KB.containsKey(modelName)){
			resultString += " ^ (" + KB.get(modelName) + ")";
		}		
		return resultString;
	}
	
	public String initKB(String[] models){
		String resultString = initKB();
		for(String s: models ){
			resultString = initKB(s, resultString);
		}
		return resultString;
	}
	
	public String initKB(String[] models, String currentKB){
		String resultString = currentKB;
		for(String s: models ){
			resultString = initKB(s, resultString);
		}
		return resultString;
	}
	
	public String[] getFilesInRepo(){
		//System.err.println("Trying " + Settings._repository);
		File dir = new File(Settings._repository);
		String[] children = dir.list();
		if (children == null) {
		    // Either dir does not exist or is not a directory
		} else {
		    for (int i=0; i<children.length; i++) {
		        // Get filename of file or directory
		        String filename = children[i]; if(filename != null);	// Supressing Warnings
		        
		    }
		}

		// It is also possible to filter the list of returned files.
		// This example does not return any files that start with `.'.
		FilenameFilter filter = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return !name.startsWith(".");
		    }
		};
		children = dir.list(filter);


		// The list of files can also be retrieved as File objects
		File[] files = dir.listFiles(); if(files != null); // Supress warning

		// This filter only returns directories
		FileFilter fileFilter = new FileFilter() {
		    public boolean accept(File file) {
		        return file.isDirectory();
		    }
		};
		files = dir.listFiles(fileFilter);
		
		return children;
	}

	public Object getValue(Object arg0) throws Exception {
		if(arg0 != null && arg0.getClass().getCanonicalName().contains("String"))
			return models.get((String) arg0);
		return null;
	}


	@SuppressWarnings("unchecked")
	public void setValue(Object arg0, Object arg1) throws Exception {
		if(arg0 != null && arg0.getClass().getCanonicalName().contains("String") &&
				arg1 != null && arg1.getClass().getCanonicalName().contains("Graph") )
		{
			Graph<Vertex,Edge> g = (Graph<Vertex,Edge>) arg1;
			String s = (String) arg0;
			if(models.containsKey(s))
				models.remove(s);
			models.put(s, g);
		}
	}
	
}
