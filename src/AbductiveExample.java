import java.util.HashSet;
import java.util.LinkedList;

import be.fnord.util.logic.AbductionReasoner;


/**
 * An abductive reasoner, will take in a knowledgebase, some possible actions effects (facts) and an observation. The reasoner
 * will then find possible actions that could be performed together. 
 * 
 * An extension to this is a planning problem, by using an accumlation function to add effects to each other. 
 * 
 * 
 * Output produced

Input KB = ((dr & cf) -> ~rt) & ((dr & ~cf) -> rt)
Input Observations = rt
After trying 103 guesses I think the following are possible events that could have happened...
	 ( di ) & ( dr ) & ( ~cf ) 
	 ( di ) & ( ~cf ) & ( dr ) 
	 ( dr ) & ( di ) & ( ~cf ) 
	 ( dr ) & ( ~cf ) 
The minimal scenarios are: 
	 ( dr ) & ( ~cf ) 

 * 
 * @author edm92
 *
 */

public class AbductiveExample {

	public static void main(String[] args) {
		// The following example demonstrates how abductive reasoning can be performed using textseer. 
		
		/******** Inputs ********/
		// Knowledgebase 
		// If drug dose reduce (dr) AND first occurance of CTC Grade 2 (cf) THEN no dose reduction (~rt)
		// If drug dose reduce (dr) AND not the first occurance of the CTC Grade 2 (~cf) THEN reduce dose by 25% (rt)
		
		String kb = "((dr & cf) -> ~rt) & ((dr & ~cf) -> rt)"; // could (cf -> nr) & (~cf -> ~nr)
		
		// Facts
		LinkedList<String> facts = new LinkedList<String>();
		facts.add("di");// Drug dose increase 
		facts.add("dr");// Drug dose reduce
		facts.add("cf");// First occurance of CTC grade 2
		facts.add("~cf");// Second occurance of CTC grade 2
		facts.add("rt"); // Drug dosage reduce by 25% 
		facts.add("~rt");// Drug dosage not reduced

		
		// Observations
		String observations ="rt"; // dose has been reduced by 25%
		// Remove observations from facts
		facts.remove("rt"); // <-- could write a signature based removal script (but won't, you're more than welcome to do this)
		
	
		AbductionReasoner myReason = new AbductionReasoner(kb, facts, observations);
		LinkedList<String> possibleScenarios = myReason.getPossibleScenarios();	// Compute all abductive scenarios
		HashSet<String> bestScenarios = myReason.findMinimal(possibleScenarios); // Find the minimal scenarios. 		
			
		/*******  output ********/
		///
		// Expecting the following dr & ~cf
		///
		System.out.println("Input KB = " + myReason.getKb());
		System.out.println("Input Observations = " + myReason.getObservations());
		
		if(!myReason.SOLUTION_FOUND){
			System.out.println("After trying " + myReason.CURRENT_GUESS + " guesses possible results couldn't find a theory to support the observation");
		}else{
			System.out.println("After trying " + myReason.CURRENT_GUESS + " guesses I think the following are possible events that could have happened...");
			for(String hypthosis: possibleScenarios){
				System.out.println("\t " + hypthosis);
			}
			System.out.println("The minimal scenarios are: ");
			for(String hypthosis: bestScenarios){
				System.out.println("\t " + hypthosis);
			}
		}
		
		

	}


}
