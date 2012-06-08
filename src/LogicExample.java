import java.util.LinkedHashSet;

import au.edu.dsl.dlab.processtools.Effect;
import au.edu.dsl.dlab.processtools.logic.Accumulate;

// Long effect to play with 
// Effect e5 = new Effect(" (~a | ~b | c) & ~(~c | d) & (~c | e) & (a & b -> c) & (~e | ~f | g) & ((c & d) -> e) & (c & f -> g) & (a | d) & (f & g) & (b | ~a) & (p -> q) & ((l & m) -> p) & ((b & l) -> m) & ((a & p) -> l) & ((a & b) -> l)");



public class LogicExample {
	public static void main(String[] Args){
		// Example of Effect Accumulation
		long start = System.currentTimeMillis();
		// Create new effect scenarios
		Effect e1 = new Effect("(clientregisteredinsystem | clientgivenguestpass) & (eeee)");			// Used to demonstrate accumulation
		Effect e2 = new Effect("a & b");			// Used to demonstrate accumulation

		// Accumulation class is a non-static class
		Accumulate acc = new Accumulate();

		LinkedHashSet<Effect> resultEffects = acc.pairwise_acc(e1,e2, "(clientgivenguestpass | clientregisteredinsystem) -> ~(a & b)", true);	
		
		
		
		// Display Results 
		for(Effect e: resultEffects){
			System.out.println("Effect scenario resulting from acc: " + e);

			// Entailment checking is built into effects
			System.out.println("Test if " + e1 + " entails " + e + ", result: " + e1.entails(e));

		}
		
		long end = System.currentTimeMillis();
		System.out.println("Execution time was "+(end-start)+" ms.");

		
	}

}
