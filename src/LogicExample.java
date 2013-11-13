import java.util.LinkedHashSet;

import be.fnord.util.logic.Accumulate;
import be.fnord.util.logic.WFF;




// Long effect to play with 
// Effect e5 = new Effect(" (~a | ~b | c) & ~(~c | d) & (~c | e) & (a & b -> c) & (~e | ~f | g) & ((c & d) -> e) & (c & f -> g) & (a | d) & (f & g) & (b | ~a) & (p -> q) & ((l & m) -> p) & ((b & l) -> m) & ((a & p) -> l) & ((a & b) -> l)");



public class LogicExample {

	public static void main(String[] Args){
		// Example of Effect Accumulation
		long start = System.currentTimeMillis();
		// Create new effect scenarios
		WFF e1 = new WFF("~(a & b) | ~c");			// Used to demonstrate accumulation
		WFF e2 = new WFF("(a & b) -> ~c");			// Used to demonstrate accumulation
//

		WFF e3 = new WFF("(p -> q) & (m -> (p | q))");		
		WFF e4 = new WFF("m -> q");
		
		System.out.println("(p->q), (m-> p V q)} |= (m->q)? " +  e3.eval(e4));
		WFF e5 = new WFF("(p -> q) & (m -> (p | q)) ^ ~(m -> q)");
		System.out.println("(p->q), (m-> p V q)} U ~(m->q)? " +  e5.issat());
		// Check if consistent
		System.out.println("Checking if " + e1 + " is consistent : " + e1.issat());
		System.out.println("Checking if " + e1 + " entails " + e2 + " : " + e1.entails(e2));
		
		// Accumulation class is a non-static class
		Accumulate acc = new Accumulate();
		// Do accumulation
		LinkedHashSet<WFF> resultEffects = acc.pairwise_acc(e1,e2, "~e", true);
		
		// Display Results 
		for(WFF e: resultEffects){
			System.out.println("Effect scenario resulting from acc: " + e);
			System.out.println("Checking if " + e + " is consistent : " + e.issat());

		}
		
		WFF e6 = new WFF("Teste2");
		WFF e7 = new WFF("(a & b)");
		
		resultEffects = acc.pairwise_acc(e6,e7, "((a & b) -> (~Teste2))", true);
		
		// Display Results 
		for(WFF e: resultEffects){
			System.out.println("Effect scenario resulting from acc: " + e);
			System.out.println("Checking if " + e + " is consistent : " + e.issat());

		}
		
		long end = System.currentTimeMillis();
		System.out.println("Execution time was "+(end-start)+" ms.");

		
	}

}
