package au.edu.dsl.dlab.processtools.parser;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;


public class KBReader{
	private static final long serialVersionUID = 1L;

	public static String readKB(String filename){
		String resultString = "";
		 try {
			 FileInputStream fstream = new FileInputStream(filename);
			 // Get the object of DataInputStream
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String strLine;
			  //Read File Line By Line
			  while ((strLine = br.readLine()) != null)   {
			  // Print the content on the console
			  // System.out.println (strLine); 
				  resultString += strLine;
			  }
			  //Close the input stream
			  in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
		 return resultString;
	}
}
