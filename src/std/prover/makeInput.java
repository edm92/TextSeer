package std.prover;
import java.io.*;
import java.util.Iterator;

import textSeer.Model.Graph;
import textSeer.Model.Vertex;


public class makeInput {
	private static String totalScenario = "";
	
	public static void createInput(Graph g)
	{ 
		createInput(g,null);
	}
	
	public static void createInput(Graph g, Vertex avoid)
	  {
		totalScenario = "";
	      try{
	    // Create file 
	    FileWriter fstream = new FileWriter(std.string.prover9Input);
	        BufferedWriter out = new BufferedWriter(fstream);
	    out.write("assign(max_seconds, "+std.string.prover9MaxTime+")." + std.string.endl);
	    out.write("formulas(sos)." + std.string.endl);
	    // Knowledge base goes here
	    out.write("\t all x ( (a(x) & b(x)) -> -c(x) )." + std.string.endl);
	    out.write("\t all x ( c(x) -> -(a(x) & b(x)) )." + std.string.endl);
	    
	    // Each immediate effect here
	    out.write(makeImmediateEffects(g, avoid));
	    
	    out.write("end_of_list." + std.string.endl);
	    out.write("formulas(goals)." + std.string.endl);
	    
	    // Test new knowledge conjunction here
	    //out.write("\t exists x (a(x) & b(x)). " + std.string.endl);
	    if(totalScenario.length() > 1){
	    	totalScenario = "\t exists x (" + totalScenario.substring(0,totalScenario.length()-3) + ")." + std.string.endl;
	    	out.write(totalScenario);
	    }else{
	    	out.write("\t $T. " + std.string.endl);
	    }
	    // Finalize
	    out.write("end_of_list." + std.string.endl);
	    
	    //Close the output stream
	    out.close();
	    }catch (Exception e){//Catch exception if any
	      std.calls.display("MakeInputer CreateIntput Error: " + e.getMessage());
	    }
	  }
	
	
	
	public static String makeImmediateEffects(Graph g, Vertex avoid){
		String returnString = "";
		
		String StratString = "\t exists x ";

		Iterator<Vertex> i = g.allNodes.iterator();
			while(i.hasNext()){
				Vertex current = (i.next());
				if(avoid == current) continue;
				if(current.IE != null ) {
					if(current.IE.effects.isEmpty()){ std.calls.showResult("Effects are empty");}
					else {
						returnString += StratString + "(" + current.IE.toValue() + ")." + std.string.endl;
						totalScenario += current.IE.toValue() + " & "; 
					}
					

				}

			}
		
		
		
		return returnString;
	}
		
}



/*
assign(max_seconds, 1).

formulas(sos).

%	exists x learned(x, prover9). 
%	all x (learned(x, prover9) -> knows(x, prover9)).
%	learned(Evan, prover9).

	
	%exists x -b(x).
	exists x (a(x) & b(x)).
	exists x c(x).
	
	all x ( (a(x) & b(x)) -> -c(x) ).
	all x ( c(x) -> -(a(x) & b(x)) ).


	
end_of_list.

formulas(goals).
	
	exists x (a(x) & b(x) & c(x)). 

	%$T.
	%knows(Evan, prover9).

end_of_list.
*/