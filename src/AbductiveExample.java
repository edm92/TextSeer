import java.util.LinkedList;

import au.edu.dsl.dlab.processtools.Effect;


public class AbductiveExample {
	public static final boolean _VERBOSE = false;
	public static final int MAX_GUESSES = 100;
	public static int CURRENT_GUESS = 0; 
	public static boolean SOLUTION_FOUND = false;

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
		facts.remove("rt");
		
		// Expect -- dr & ~cf
		
		String guess = ""; 
		int guessNumber = 0;
		 
		
		// Start looping through the possible facts to brute force a possible diagnosis.
		LinkedList<String> possibleScenarios = new LinkedList<String>();
		for(String fact : facts){
			if(_VERBOSE) System.out.println("Trying the base fact : " + fact);
			if(CURRENT_GUESS > MAX_GUESSES) break; 
			LinkedList<String> trueFacts = new LinkedList<String>();
			testFact(kb, facts, observations, new LinkedList<String>(), fact, trueFacts);
			possibleScenarios.addAll(trueFacts);
		}
			
		/*******  output ********/
		System.out.println("Input KB = " + kb);
		System.out.println("Input Observations = " + observations);
		
		if(!SOLUTION_FOUND){
			System.out.println("After trying " + CURRENT_GUESS + " possible results couldn't find a theory to support the observation");
		}else{
			System.out.println("After trying " + CURRENT_GUESS + " I think the following are possible events that could have happened...");
			for(String hypthosis: possibleScenarios){
				System.out.println("\t " + hypthosis);
			}
		}
		
		

	}

	/**
	 * Unroll a list into a propositional sentence. 
	 * @param list
	 * @return
	 */
	public static String unrollList(LinkedList<String> list , String newFact){
		String _new = "";
		for(String s : list){
			if(s.length() > 0)
			_new += "( " + s + " ) & ";
		}
		if(newFact.length() > 0) _new += "( " + newFact + " ) "; else
		if(_new.length() > 0) _new = _new.substring(0, _new.length() - " & ".length()) ;
		return _new;
	}
	
	public static boolean testFact(String kb, LinkedList<String> facts, String observations, LinkedList<String> existing, String newFact, LinkedList<String> trueFacts){
		String _newFact = unrollList(existing , newFact) ;
		
		// Test to see if we could resolve with this fact
		if(makeGuess(kb , _newFact, observations)){
			trueFacts.add(_newFact);
			SOLUTION_FOUND = true;
			return true; 
		}
		existing.add(newFact);
		
		
		
		// Recursive checking
		for(String fact : facts){
			if(!existing.contains(fact)){
				testFact(kb, facts, observations, existing, fact, trueFacts);
				if(existing.contains(fact)) existing.remove(fact);
			}
		}
				
		if(CURRENT_GUESS > MAX_GUESSES) return false;
		
		return false;
	}
	
	
	/**
	 * Function takes a guess of possible actions that could result in the observation. 
	 * @param kb
	 * @param guess
	 * @param observation
	 * @return
	 */
	public static boolean makeGuess(String kb, String guess, String observation){
		if(CURRENT_GUESS++ > MAX_GUESSES) return false;
		String possible_answer = "(" + kb + ")";
		if(guess != null && guess.length() > 0){
			 possible_answer += "& (" + guess + ")";
		}
		
		Effect observationEffect = new Effect(observation);
		Effect answerEffect = new Effect(possible_answer);
		boolean result = false;
		if(answerEffect.issat())
			result = answerEffect.entails(observationEffect);
		
			
		
		if(_VERBOSE) System.out.println(possible_answer + " isSat? " +  result);
		
		
		return result;
	}

}
