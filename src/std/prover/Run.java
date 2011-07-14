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

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Run {
	public static String SOURCEFILE = std.string.endl + "Error in: std.prover.Run.java" + std.string.endl;
	public static boolean DEBUG = false; // std.string.debug;	// Hopefully true when you edit :P
	public static void debug(String msg){
		if(DEBUG)
			std.calls.debug_(msg + SOURCEFILE);
	}
	
	
	public static boolean MULTIFLAG = false;

	public Run(){
		//exec();
	}
	
	public static boolean exec(){
		Boolean result = false;
		//System.err.println(std.string.prover9ExecString);
	     try {

             Runtime rt = Runtime.getRuntime();
             //Process pr = rt.exec("cmd /c dir");
             Process pr = rt.exec(std.string.prover9ExecString);
             
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
            	 result = false;
            	 debug("Can't see a result for one consistency check; Most likely an inconsistency (need to double check these ones)" + std.string.endl);
             }
             

         } catch(Exception e) {
        	 if(!MULTIFLAG){
        		 debug(e.toString());
        		 //e.printStackTrace();
        		 MULTIFLAG = true;
        	 };
             //e.printStackTrace();
         }
         return result;
	}
	
}
