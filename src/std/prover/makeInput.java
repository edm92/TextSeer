package std.prover;
import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

import std.extern.CombinationGenerator;
import textSeer.Model.Effect;
import textSeer.Model.Graph;
import textSeer.Model.Predicate;
import textSeer.Model.Vertex;


public class makeInput {
	public static String SOURCEFILE = std.string.endl + "Error in: std.prover.makeInput.java" + std.string.endl;
	public static boolean DEBUG = std.string.debug;	// Hopefully true when you edit :P
	public static void debug(String msg){
		if(DEBUG)
			std.calls.display(msg + SOURCEFILE);
	}
	
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
	    //out.write("\t all x ( (a(x) & b(x)) -> -c(x) )." + std.string.endl);
	    //out.write("\t all x ( c(x) -> -(a(x) & b(x)) )." + std.string.endl);
	    out.write(KnowledgeBase.getKnowledgeBase());
	    
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
	   // debug("Finsihed");
	    }catch (Exception e){//Catch exception if any
	      debug("MakeInputer CreateIntput Error: " + e.getMessage());
	    }
	  }
	
	
	
	public static String makeImmediateEffects(Graph g, Vertex avoid){
		String returnString = "";
		
		String StratString = "\t exists x ";
		
		Effect e = new Effect();
		
		
		Iterator<Vertex> i = g.allNodes.iterator();
			while(i.hasNext()){
				Vertex current = (i.next());
				if(avoid == current) continue;
				if(current.IE != null ) {
					if(current.IE.effects.isEmpty()){ debug("Effects are empty");}
					else {
						returnString += StratString + "(" + current.IE.toValue() + ")." + std.string.endl;
						totalScenario += current.IE.toValue() + " & ";
						for(Object s:current.IE.effects.keySet()){
							e.addPredicate(current.IE.effects.get(s));
							//debug("added: " + current.IE.effects.get(s).toValue() + " - size:" +e.effects.size() + " name:" +current.IE.effects.get(s).name );
						}
							
					}
					

				}

			}
			
		LinkedList<Effect> totalEffects = makeAllEffectCombinations(e);
		
		// Need to make this all permutations
		
		try{
			for(Effect comboEffect:totalEffects){
				boolean globalEffects = true;
				
				if(KnowledgeBase.rules!= null)
					for(String s:KnowledgeBase.rules){
						StringTokenizer st = new StringTokenizer(s, " &>");
						int l = st.countTokens();
					     while (st.hasMoreTokens()) {
					    	 String r = st.nextToken();
					    	 if(r.contains("-")) r = r.replaceAll("-","");
					    	// std.calls.display("Checking : " + comboEffect.toValue());

					    	 for(String eff:comboEffect.effects.keySet()){
					    	
						    	//std.calls.display("R = " + r + "; effect = " + e.effects.get(eff).toValue());
								if(e.effects.get(eff).toValue().contains(r)){
									l--;
									//std.calls.display("In the result is r = " +r + " ;" + e.effects.get(eff).toValue() + " l = " + l);
								}
						
					    	 }
					     }
				    	// std.calls.display("L = " + l);
				    	 if(l == 0) {
					    	 globalEffects = false;
					    	 
					     }

					     
					}
					
				
				if(globalEffects)
				{
					if(totalScenario != null && totalScenario.length() >3){
						returnString += (StratString + "(" + comboEffect.toValue()  + ")." + std.string.endl);
					}
					
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		//std.calls.showResult("WTF:::::::: " + returnString);
		
		
		
		return returnString;
	}
	
	
	public static LinkedList<Effect> makeAllEffectCombinations(Effect e){
		
		LinkedList<Effect>totalResult = new LinkedList<Effect>();
		try{
		Predicate[] elements = new Predicate[e.effects.size()];
		int j = 0;
		//debug("size of e " + e.effects.size());
		
		for(Predicate p : e.effects.values()){
			elements[j++] = p;
			//debug("adding " + p.toValue());
		}
		
		int[] indices;
		for(int k = 2; k <= e.effects.size(); k++){
		CombinationGenerator x = new CombinationGenerator (elements.length,k);
		while (x.hasMore ()) {
			  //std.calls.display("Combo: ");
				Effect current = new Effect();
			  indices = x.getNext ();
			  for (int i = 0; i < indices.length; i++) {
				  
				  //std.calls.display((elements[indices[i]].toValue()));
				  current.addPredicate( elements[indices[i]]);
			  }
			  totalResult.add(current);
			  //debug("combo: " + current.toValue());
			 
			  
			}
		};
		
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		//debug("Finished");
		
		return totalResult;
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