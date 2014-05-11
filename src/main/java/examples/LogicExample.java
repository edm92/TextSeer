package examples;
import be.fnord.util.logic.EffectAccumulate;
import be.fnord.util.logic.WFF;

import java.util.LinkedHashSet;


/**
 * The following code demonstrates the logic engine powered mainly by Orbital Library
 * <p/>
 * <p/>
 * Example Output
 * (p->q), (m-> p V q)} |= (m->q)? true
 * (p->q), (m-> p V q)} U ~(m->q)? true
 * Checking if ~(a & b) | ~c is consistent : true
 * Checking if ~(a & b) | ~c entails (a & b) -> ~c : true
 * Effect scenario resulting from acc: (~(a & b) | ~c) & ((a & b) -> ~c)
 * Checking if (~(a & b) | ~c) & ((a & b) -> ~c) is consistent : true
 * Effect scenario resulting from acc: (a & b)
 * Checking if (a & b) is consistent : true
 * Execution time was 424 ms.
 *
 * @author Evan Morrison edm92@uowmail.edu.au http://www.fnord.be
 *         Apache License, Version 2.0, Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */


// Long effect to play with 
// Effect e5 = new Effect(" (~a | ~b | c) & ~(~c | d) & (~c | e) & (a & b -> c) & (~e | ~f | g) & ((c & d) -> e) & (c & f -> g) & (a | d) & (f & g) & (b | ~a) & (p -> q) & ((l & m) -> p) & ((b & l) -> m) & ((a & p) -> l) & ((a & b) -> l)");
public class LogicExample {

    
    public static void main(String[] args) {
        // Example of Effect Accumulation
        long start = System.currentTimeMillis();
        // Create new effect scenarios
        WFF e1 = new WFF("~(a & b) | ~c");            // Used to demonstrate accumulation
        WFF e2 = new WFF("(a & b) -> ~c");            // Used to demonstrate accumulation
        WFF cl = new WFF("(B & (~A | ~C | ~E) & C & ~E & Z & (Z | ~A) & (A & C) & (D | A | ~C) & A)");
        WFF c2 = new WFF("(( B & (A -> Z) & (C -> (D | A)) & ((A & C) -> ~E) ) & (C & A))");
        a.e.println("Closure of " + cl + "=\n\t\t" + cl.getClosure());
        a.e.println("Is consistent? = " + cl.isConsistent());
        if(true)
        return;
//

        WFF e3 = new WFF("(p -> q) & (m -> (p | q))");
        WFF e4 = new WFF("m -> q");

        System.out.println("(p->q), (m-> p V q)} |= (m->q)? " + e3.eval(e4));
        WFF e5 = new WFF("(p -> q) & (m -> (p | q)) ^ ~(m -> q)");
        System.out.println("(p->q), (m-> p V q)} U ~(m->q)? " + e5.issat());
        // Check if consistent
        System.out.println("Checking if " + e1 + " is consistent : " + e1.issat());
        System.out.println("Checking if " + e1 + " entails " + e2 + " : " + e1.entails(e2));

        // Accumulation class is a non-static class
        EffectAccumulate acc = new EffectAccumulate();
        // Do accumulation
        LinkedHashSet<WFF> resultEffects = acc.pairwise_acc(e1, e2, "~e", true);

        // Display Results
        for (WFF e : resultEffects) {
            System.out.println("Effect scenario resulting from acc: " + e);
            System.out.println("Checking if " + e + " is consistent : " + e.issat());

        }

        WFF e6 = new WFF("Teste2");
        WFF e7 = new WFF("(a & b)");

        resultEffects = acc.pairwise_acc(e6, e7, "((a & b) -> (~Teste2))", true);

        // Display Results
        for (WFF e : resultEffects) {
            System.out.println("Effect scenario resulting from acc: " + e);
            System.out.println("Checking if " + e + " is consistent : " + e.issat());

        }

        long end = System.currentTimeMillis();
        System.out.println("Execution time was " + (end - start) + " ms.");


    }

}
