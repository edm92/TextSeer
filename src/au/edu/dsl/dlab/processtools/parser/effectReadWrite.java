package au.edu.dsl.dlab.processtools.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashSet;

import au.edu.dsl.dlab.processtools.Effect;




public class effectReadWrite{
	private static final long serialVersionUID = 1L;

	public static boolean writeEffect(String filename, HashSet<Effect> effects){
		try{
			  // Create file 
			  FileWriter fstream = new FileWriter(filename);
			  BufferedWriter out = new BufferedWriter(fstream);
			  String output = "";
			  for(Effect e: effects){
				  output += e.getFormula() + "###";
			  }
			  if(output.length() > 3)
				  output = output.substring(0,output.length()-3);
			  out.write(output);
			  //Close the output stream
			  out.close();
			  }catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
			  }
		return true;
	}
	
	public static HashSet<Effect> readEffect(String filename){
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
			  //System.out.println (strLine); 
				  resultString += strLine;
			  }
			  //Close the input stream
			  in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
		 return convertStringToEffect(resultString);
	}
	
	public static HashSet<Effect> convertStringToEffect(String input){
		String tmp[] = input.split("###");
		HashSet<Effect> results = new HashSet<Effect>();
		for(String s:tmp){
			results.add(new Effect(s));
		}
		return results;
	}
	
}
