package std.prover;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Run {
	public static String SOURCEFILE = std.string.endl + "Error in: std.prover.Run.java" + std.string.endl;
	public static boolean DEBUG = false; // std.string.debug;	// Hopefully true when you edit :P
	public static void debug(String msg){
		if(DEBUG)
			std.calls.display(msg + SOURCEFILE);
	}
	
	
	public static boolean MULTIFLAG = false;

	public Run(){
		//exec();
	}
	
	public static boolean exec(){
		Boolean result = false;
	     try {

             Runtime rt = Runtime.getRuntime();
             //Process pr = rt.exec("cmd /c dir");
             Process pr = rt.exec(std.string.prover9ExecString);
             //std.calls.display(std.string.prover9ExecString);
             BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

             String line=null;
             
             
             while((line=input.readLine()) != null) {
                 //System.out.println(line);
            	 // std.calls.showResult(line + std.string.endl);
            	 if(line.contains(std.string.prover9sucess)){
            		 //std.calls.showResult(std.string.prover9sucess + std.string.endl);
            		 result = true;
            		 break;
            		 }
            	 else{
            			 if(line.contains(std.string.prover9failed)){
                    		 //
                    		 result = false;
                    		 break;}
            		 }
            	 
             }

             int exitVal = pr.waitFor();
             //std.calls.showResult("Exited with error code "+exitVal + std.string.endl);
             exitVal++; // only to remove warning
             if(!result && std.string.debug){
            	 debug("Can't see a result for one consistency check; Most likely an inconsistency (need to double check these ones)" + std.string.endl);
             }
             

         } catch(Exception e) {
        	 if(!MULTIFLAG){
        		 System.out.println(e.toString());
        		 MULTIFLAG = true;
        	 };
             //e.printStackTrace();
         }
         return result;
	}
	
}
