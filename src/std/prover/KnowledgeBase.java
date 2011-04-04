package std.prover;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

public class KnowledgeBase {
	public static String SOURCEFILE = std.string.endl + "Error in: std.prover.KnowledgeBase.java" + std.string.endl;
	public static boolean DEBUG = std.string.debug;	// Hopefully true when you edit :P
	public static void debug(String msg){
		if(DEBUG)
			std.calls.display(msg + SOURCEFILE);
	}
	
	public static LinkedList<String> rules;

	@SuppressWarnings("deprecation")
	public static String getKnowledgeBase(){
		rules = new LinkedList<String>();
		String resultReturn ="";
	    File file = new File(std.string.knowledgeBase);
	    FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    DataInputStream dis = null;

	    try {
	      fis = new FileInputStream(file);

	      // Here BufferedInputStream is added for fast reading.
	      bis = new BufferedInputStream(fis);
	      dis = new DataInputStream(bis);

	      // dis.available() returns 0 if the file does not have more lines.
	      while (dis.available() != 0) {

	      // this statement reads the line from the file and print it to
	        // the console.
	    	  String rule = dis.readLine();
	    	  rules.add(rule);
	    	  //std.calls.showResult("added:" + rule);
	    	  rule = rule.replaceAll(">", "->");
	    	  resultReturn += "\t all x ( " + rule + " ). " +std.string.endl;
	    	  
	      }

	      // dispose all the resources after using them.
	      fis.close();
	      bis.close();
	      dis.close();

	    } catch (FileNotFoundException e) {
	      //e.printStackTrace();
	    	debug("Can't open knowledge base file.");
	    } catch (IOException e) {
	      //e.printStackTrace();
	    	debug("Can't open knowledge base file.");
	    }
	    
	    return resultReturn;
	  }

	
}
