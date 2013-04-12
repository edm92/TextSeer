package be.fnord.util.processModel.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * Class & methods were made to generate XML Schema's for the JSON input of BPMAI 
 * Remove soon
 * @deprecated Only use for generating XML schema
 * @author Evan Morrison edm92@uowmail.edu.au http://www.fnord.be
 * Apache License, Version 2.0, Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 *
 */
public class SchemaBuilder {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		boolean processLine = false;
		Set<String> elements = new HashSet<String>();
		int lineNo = 0;
	    BufferedReader reader = null;
		try {
			reader = new BufferedReader( new FileReader ("models/reader.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		if(reader == null) return ;
	    String         line = null;
	    StringBuilder  stringBuilder = new StringBuilder();
	    String         ls = System.getProperty("line.separator");

	    try {
			while( ( line = reader.readLine() ) != null ) {
				lineNo++;
				if(line.contains("\"properties\"")){
					processLine = true;
				}else if(line.contains("}") && processLine == true){

					processLine = false;
				}else if(line.contains("{") && processLine == true){
					System.out.println("Found another opener at line " + lineNo);
				}else if(processLine == true){
					String newLine[] = line.trim().split(":");
					String processed = "";
					if(newLine[0] != null && newLine[0].length() > 0){
						processed = newLine[0].replaceAll("\"", "");
					}
					//System.out.println("Processed : " + processed);
					if(processed.length() > 0)
						elements.add(processed);
				}
				
			    //stringBuilder.append( line );

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	    for(String s: elements){
	    	String newCodeLine = "public String " + s + ";" + ls;
	    	stringBuilder.append( newCodeLine );
	    }
	    System.out.println("Results:" + ls + stringBuilder.toString());
	}

	
}
