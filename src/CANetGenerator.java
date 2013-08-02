import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.List;

import be.fnord.components.dataconnection.myConnection;
import au.edu.dsl.dlab.processtools.Edge;

import au.edu.dsl.dlab.processtools.Graph;
import au.edu.dsl.dlab.processtools.Vertex;
import au.edu.dsl.dlab.processtools.parser.bpmn.visualize;


public class CANetGenerator {

	public static TreeMap<String, String> patientID = new TreeMap<String, String>(); // PaitnetID, Patient Name
	public static TreeMap<String, LinkedList<String>> patientCase = new TreeMap<String, LinkedList<String>>(); // PatientID, patient task history 
	public static int counter = 0;
	public static void main(String [] args){
	    myConnection newInstance = new myConnection();
	    boolean test1 = newInstance.makeConnection("activiti", "root", "");
	    
	    // Get Patients / Process References
	    
	    ResultSet rs = newInstance.executeQuery("SELECT * FROM ACT_HI_VARINST WHERE NAME_ like 'patientName';");
	    LinkedList<String[]> dat = (LinkedList<String[]>) newInstance.getMetaDataResults(rs);
	    TreeMap<String, List<Object>> das = newInstance.getDataResults(rs);	    
	    TreeMap<String, String> patientReference = new TreeMap<String, String>();
	    for(String key:das.keySet()){
	            int i = 0; 
	            String patientName = "";
	            String processReference = "";
	            for(Object val : das.get(key)){
	            	if(++i == 2) processReference = val.toString();
	            	if(i == 11) patientName = val.toString();
	            }
	            patientReference.put(processReference, patientName);
	    }
	    
	    // For each patient, get any associated process models. 
	    
	    // Get process from database 
	    for(String procID : patientReference.keySet()){
	    	String name = patientReference.get(procID) ;
	    	System.out.println( a.e.endl + "Processes for patient " + name);	    	
	    	String sql = "SELECT * FROM ACT_HI_ACTINST WHERE PROC_INST_ID_ = '"+procID+"'  ORDER BY START_TIME_ ASC;";
		    rs = newInstance.executeQuery(sql);
		    dat = (LinkedList<String[]>) newInstance.getMetaDataResults(rs);
		    das = newInstance.getDataResults(rs);
		    
		    String processName = "";
		    
		    LinkedList<String> tasks = new LinkedList<String>();
		    for(String key:das.keySet()){
		            int i = 0; 
		            
		            for(Object val : das.get(key)){
		            	if(++i == 8) tasks.add(val.toString());
		            	if(i == 2) processName = val.toString();
		            }
		            
		    }
		    System.out.println(tasks.toString());
		    System.out.println("Process is " + processName);
		    if(patientID.values().contains(name)){
		    	// If the patient already exists, ask if this process is related to their 
		    	// existing history
		    	System.out.println("It appears that " + name + " has already been following some processes, would you like to associate this flow with his history?");
		    	  //  open up standard input
		    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		    	String reply = "no";
			      //  read the username from the command-line; need to use try/catch with the
			      //  readLine() method
			      try {
			         reply = br.readLine();
			      } catch (IOException ioe) {
			         System.out.println("IO error trying to read your name!");
			         System.exit(1);
			      }
			      if(reply.toLowerCase().contains("y")){
			    	  System.out.println("Adding to your patient history.");
			    	  String pID = "";
			    	  for(String s: patientID.keySet()) if(patientID.get(s).compareTo(name) == 0) pID = s;
			    	  LinkedList<String> history = patientCase.get(pID);
			    	  history.addAll(tasks);
			    	  patientCase.remove(pID);
			    	  patientCase.put(pID, history);
			    	  System.out.println("Updated case history for " + name + " is " + history);
			      }else{
			    	  System.out.println("Okay.");
			    	  // Store this patient record in a treemap
				    	String ID = name+'#'+counter;
				    	patientID.put(ID, name);
				    	patientCase.put(ID, tasks);
			      }
			      
		    	
		    }else{
		    	// Store this patient record in a treemap
		    	String ID = name+'#'+counter;
		    	patientID.put(ID, name);
		    	patientCase.put(ID, tasks);
		    }
	    }
	    
	    for(LinkedList<String> t : patientCase.values()){
	    	Graph<Vertex, Edge> myProcess = new Graph<Vertex, Edge>();
	    	Vertex prev = null;
	    	
	    	for(String task: t){
	    			Vertex t1 = new Vertex(task);
	    			myProcess.addVertex(t1);
	    			if(prev != null){
	    				myProcess.addEdge(prev,t1);
	    				prev = t1;
	    			};
	    			prev = t1;
	    		}
	    	visualize<Vertex,Edge> myViewer = new visualize<Vertex,Edge>();
			myViewer.showModel(myProcess);
		}
	    
	    
	    //END OF GENERATION
	    boolean test4 = (newInstance.closeConnection());
	}
}
