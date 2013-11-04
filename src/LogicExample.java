import java.util.LinkedHashSet;

import be.fnord.util.logic.Accumulate;
import be.fnord.util.processModel.Effect;



// Long effect to play with 
// Effect e5 = new Effect(" (~a | ~b | c) & ~(~c | d) & (~c | e) & (a & b -> c) & (~e | ~f | g) & ((c & d) -> e) & (c & f -> g) & (a | d) & (f & g) & (b | ~a) & (p -> q) & ((l & m) -> p) & ((b & l) -> m) & ((a & p) -> l) & ((a & b) -> l)");



public class LogicExample {

	public static void main(String[] Args){
		// Example of Effect Accumulation
		long start = System.currentTimeMillis();
		// Create new effect scenarios
		Effect e1 = new Effect("~(a & b) | ~c");			// Used to demonstrate accumulation
		Effect e2 = new Effect("(a & b) -> ~c");			// Used to demonstrate accumulation
//

//		Effect e3 = new Effect("(p -> q) & (m -> (p | q))");			// Used to demonstrate accumulation
//		Effect e4 = new Effect("m -> q");
		Effect e3 = new Effect("B & (C -> (D | A)) & ((A & C) -> ~E) & eeee & (A) & (C)");
		Effect e4 = new Effect("(D & A)");
		
		System.out.println("(p->q), (m-> p V q)} |= (m->q)? " +  e3.eval(e4));
/*		Effect e5 = new Effect("(p -> q) & (m -> (p | q)) ^ ~(m -> q)");
		System.out.println("(p->q), (m-> p V q)} U ~(m->q)? " +  e5.issat());
		// Check if consistent
		System.out.println("Checking if " + e1 + " is consistent : " + e1.issat());
		System.out.println("Checking if " + e1 + " entails " + e2 + " : " + e1.entails(e2));
		
		// Accumulation class is a non-static class
		Accumulate acc = new Accumulate();
		// Do accumulation
		LinkedHashSet<Effect> resultEffects = acc.pairwise_acc(e1,e2, "~e", true);
		
		// Display Results 
		for(Effect e: resultEffects){
			System.out.println("Effect scenario resulting from acc: " + e);
			System.out.println("Checking if " + e + " is consistent : " + e.issat());

		}
*/		
		long end = System.currentTimeMillis();
		System.out.println("Execution time was "+(end-start)+" ms.");

		
	}

}
