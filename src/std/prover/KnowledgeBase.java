/**
 * TextSeer, development prototype for ProcessSeer
    Copyright (C) 2011 Evan Morrison

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * @author Evan Morrison (edm92@uow.edu.au)
 *
 */



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
			std.calls.debug_(msg + SOURCEFILE);
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
