import java.util.HashSet;
import be.fnord.util.logic.DefaultReasoner;
import be.fnord.util.logic.defaultLogic.DefaultRule;
import be.fnord.util.logic.defaultLogic.RuleSet;
import be.fnord.util.logic.defaultLogic.WorldSet;

/**
 * Default logic examples. 
 Most of the examples here are from  R. Reiter (1980). A logic for default reasoning. Artificial Intelligence, 13:81-132.
 Notes on output format - if you see the string eeee anywhere in the output take it as an EMPTY set or value.
 Default rules are written a:b ==> c  where a is the prerequisite, b is justification and c is conclusion.
------------------------------------------------------------------------------------------------------------------------------

	I have made every attempt to test this reasoner; however, have not documented the decision proceedure yet so please take 
	results with a grain of salt. 

 * @author Evan Morrison edm92@uowmail.edu.au http://www.fnord.be
 * Apache License, Version 2.0, Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 *
 */
public class DefaultLogicExample {

	public static void main(String[] args){
		long start = System.currentTimeMillis();
		a.e.println("Default logic examples. \n Most of the examples here are from  R. Reiter (1980). A logic for default reasoning. Artificial Intelligence, 13:81-132.\n Notes on output format - if you see the string eeee anywhere in the output take it as an EMPTY set or value.\n Default rules are written a:b ==> c  where a is the prerequisite, b is justification and c is conclusion.\n------------------------------------------------------------------------------------------------------------------------------\n\n");
		
		example1();
		a.e.println("--------------------------------------------------------------");
		example2();
		a.e.println("--------------------------------------------------------------");
		example3();
		a.e.println("--------------------------------------------------------------");
		example4();
		a.e.println("--------------------------------------------------------------");
		example5();
		a.e.println("--------------------------------------------------------------");
		example6();
		a.e.println("--------------------------------------------------------------");
		example7();
		a.e.println("--------------------------------------------------------------");
		example8();
		a.e.println("--------------------------------------------------------------");
		example9();
		a.e.println("--------------------------------------------------------------");
		example10();
		a.e.println("--------------------------------------------------------------");
		example11();
		a.e.println("--------------------------------------------------------------");
		example12();
		a.e.println("--------------------------------------------------------------");
		example13();
		
		
		long end = System.currentTimeMillis();
		System.out.println("Execution time was "+(end-start)+" ms.");

	}
	
	// Example 2.11 commitment to assumptions 
		// J. P. Delgrande, T. Schaub, and W. K. Jackson (1994). Alternative approaches to default logic. Artificial Intelligence, 70:167-237.
	/**
	 * Output
		Given the world: 
			
		 And the rules 
			[([]):(B) ==> (C)] , [([]):(~B) ==> (D)] , [([]):(~C) ==> (E)] , [([]):(~C) ==> (E)]
		Possible Extensions
			 Ext: Th(W U (C & D))
		A family may decide to do one of two things, go to the beach or to a movie (C or D) on the weekend, depending on whether it is sunny (B) or not.

	 */
	 	public static void example13(){
		WorldSet myWorld = new WorldSet();	// Empty World
			myWorld.addFormula(a.e.EMPTY_FORMULA);
			
			
			DefaultRule rule1 = new DefaultRule();
			rule1.setPrerequisite(a.e.EMPTY_FORMULA);
			rule1.setJustificatoin("B");
			rule1.setConsequence("C");
			
			DefaultRule rule2 = new DefaultRule();
			rule2.setPrerequisite(a.e.EMPTY_FORMULA);
			rule2.setJustificatoin("~B");
			rule2.setConsequence("D");
			
			DefaultRule rule3 = new DefaultRule();
			rule3.setPrerequisite(a.e.EMPTY_FORMULA);
			rule3.setJustificatoin("~C");
			rule3.setConsequence("E");
			
			DefaultRule rule4 = new DefaultRule();
			rule4.setPrerequisite(a.e.EMPTY_FORMULA);
			rule4.setJustificatoin("~D");
			rule4.setConsequence("F");
			
			RuleSet myRules = new RuleSet();
			myRules.addRule(rule1);
			myRules.addRule(rule2);
			myRules.addRule(rule3);
			myRules.addRule(rule3);
			
			DefaultReasoner loader = new DefaultReasoner(myWorld, myRules);
			HashSet<String> extensions = loader.getPossibleScenarios();
			
			a.e.println("Given the world: \n\t" + myWorld.toString() + "\n And the rules \n\t" + myRules.toString() );
			
			a.e.println("Possible Extensions");
			for(String c : extensions){			
				a.e.println("\t Ext: Th(W U (" + c + "))");	// Added for Graham
			}
			a.e.println("A family may decide to do one of two things, go to the beach or to a movie (C or D) on the weekend, depending on whether it is sunny (B) or not.");

		}
	
	// Example 2.11 commitment to assumptions 
	// J. P. Delgrande, T. Schaub, and W. K. Jackson (1994). Alternative approaches to default logic. Artificial Intelligence, 70:167-237.
/**
 * Output
		Given the world: 
			
		 And the rules 
			[([]):(B) ==> (C)] , [([]):(~B) ==> (D)]
		Possible Extensions
			 Ext: Th(W U (C & D))
 */
 	public static void example12(){
	WorldSet myWorld = new WorldSet();	// Empty World
		myWorld.addFormula(a.e.EMPTY_FORMULA);
		
		
		DefaultRule rule1 = new DefaultRule();
		rule1.setPrerequisite(a.e.EMPTY_FORMULA);
		rule1.setJustificatoin("B");
		rule1.setConsequence("C");
		
		DefaultRule rule2 = new DefaultRule();
		rule2.setPrerequisite(a.e.EMPTY_FORMULA);
		rule2.setJustificatoin("~B");
		rule2.setConsequence("D");
		
		RuleSet myRules = new RuleSet();
		myRules.addRule(rule1);
		myRules.addRule(rule2);
		
		DefaultReasoner loader = new DefaultReasoner(myWorld, myRules);
		HashSet<String> extensions = loader.getPossibleScenarios();
		a.e.println("This example is titled Commitment to assumptions");
		a.e.println("Given the world: \n\t" + myWorld.toString() + "\n And the rules \n\t" + myRules.toString() );
		
		a.e.println("Possible Extensions");
		for(String c : extensions){			
			a.e.println("\t Ext: Th(W U (" + c + "))");	// Added for Graham
		}

	}
	
	// Example 2.10 continued From 
	// J. P. Delgrande, T. Schaub, and W. K. Jackson (1994). Alternative approaches to default logic. Artificial Intelligence, 70:167-237.
/**
 * Output
		Given the world: 
			((A) & (B -> C))
		 And the rules 
			[(A):(B) ==> (B)] , [(A):(~B) ==> (~B)] , [(~C):(C) ==> (C)]
		Possible Extensions
			 Ext: Th(W U (B))
 */
 	public static void example11(){
	WorldSet myWorld = new WorldSet();	// Empty World
		myWorld.addFormula("((A) & (B -> C))");
		
		
		DefaultRule rule1 = new DefaultRule();
		rule1.setPrerequisite("A");
		rule1.setJustificatoin("B");
		rule1.setConsequence("B");
		
		DefaultRule rule2 = new DefaultRule();
		rule2.setPrerequisite("A");
		rule2.setJustificatoin("~B");
		rule2.setConsequence("~B");
		
		DefaultRule rule3 = new DefaultRule();
		rule3.setPrerequisite("~C");
		rule3.setJustificatoin("C");
		rule3.setConsequence("C");

		RuleSet myRules = new RuleSet();
		myRules.addRule(rule1);
		myRules.addRule(rule2);
		myRules.addRule(rule3);
		
		DefaultReasoner loader = new DefaultReasoner(myWorld, myRules);
		HashSet<String> extensions = loader.getPossibleScenarios();
		
		a.e.println("Given the world: \n\t" + myWorld.toString() + "\n And the rules \n\t" + myRules.toString() );
		
		a.e.println("Possible Extensions");
		for(String c : extensions){			
			a.e.println("\t Ext: Th(W U (" + c + "))");	// Added for Graham
		}

	}
	
	// Example 2.9 continued From 
	// J. P. Delgrande, T. Schaub, and W. K. Jackson (1994). Alternative approaches to default logic. Artificial Intelligence, 70:167-237.
/**
 * Output
		Given the world: 
			(A | B)
		 And the rules 
			[([]):(A) ==> (A)] , [((A | B)):(~A) ==> (~A)]
		Possible Extensions
			 Ext: Th(W U (~A))
			 Ext: Th(W U (A))
		Though in Delgrande's paper the solutions is : Th(W U {A}) and Th(W U {~A, B}).
 */
 	public static void example10(){
	WorldSet myWorld = new WorldSet();	// Empty World
		myWorld.addFormula("(A | B)");
		
		
		DefaultRule rule1 = new DefaultRule();
		rule1.setPrerequisite(a.e.EMPTY_FORMULA);
		rule1.setJustificatoin("A");
		rule1.setConsequence("A");
		
		DefaultRule rule2 = new DefaultRule();
		rule2.setPrerequisite("(A | B)");
		rule2.setJustificatoin("~A");
		rule2.setConsequence("~A");

		RuleSet myRules = new RuleSet();
		myRules.addRule(rule1);
		myRules.addRule(rule2);
		
		DefaultReasoner loader = new DefaultReasoner(myWorld, myRules);
		HashSet<String> extensions = loader.getPossibleScenarios();
		
		a.e.println("Given the world: \n\t" + myWorld.toString() + "\n And the rules \n\t" + myRules.toString() );
		
		a.e.println("Possible Extensions");
		for(String c : extensions){			
			a.e.println("\t Ext: Th(W U (" + c + "))");	// Added for Graham
		}
		
		a.e.println("Though in Delgrande's paper the solutions is : Th(W U {A}) and Th(W U {~A, B}).");
	}

	// Example 2.9 From 
		// J. P. Delgrande, T. Schaub, and W. K. Jackson (1994). Alternative approaches to default logic. Artificial Intelligence, 70:167-237.
	/**
	 * Output
		Given the world: 
			
		 And the rules 
			[([]):(A & ~B) ==> (A)] , [([]):(~A & B) ==> (~A)]
		Possible Extensions
			 Ext: Th(W U (~A))
			 Ext: Th(W U (A))
	 */
	 	public static void example9(){
		WorldSet myWorld = new WorldSet();	// Empty World
			myWorld.addFormula(a.e.EMPTY_FORMULA);
			
			
			DefaultRule rule1 = new DefaultRule();
			rule1.setPrerequisite(a.e.EMPTY_FORMULA);
			rule1.setJustificatoin("A & ~B");
			rule1.setConsequence("A");
			
			DefaultRule rule2 = new DefaultRule();
			rule2.setPrerequisite(a.e.EMPTY_FORMULA);
			rule2.setJustificatoin("~A & B");
			rule2.setConsequence("~A");
	
			RuleSet myRules = new RuleSet();
			myRules.addRule(rule1);
			myRules.addRule(rule2);
			
			DefaultReasoner loader = new DefaultReasoner(myWorld, myRules);
			HashSet<String> extensions = loader.getPossibleScenarios();
			
			a.e.println("Given the world: \n\t" + myWorld.toString() + "\n And the rules \n\t" + myRules.toString() );
			
			a.e.println("Possible Extensions");
			for(String c : extensions){			
				a.e.println("\t Ext: Th(W U (" + c + "))");	// Added for Graham
			}
		}

	// Example 2.3 From 
		// A Logic for Default Reasoning, R. Reiter -- R. Reiter (1980). A logic for default reasoning. Artificial Intelligence, 13:81-132.
	/**
	 * Output
			Applyig rules to the world Effect:eeee & eeee ==== eeee
			Given the world: 
				
			 And the rules 
				[([]):(C) ==> (~D)] , [([]):(D) ==> (~C)]
			Possible Extensions
				 Ext: Th(W U (~D))
				 Ext: Th(W U (~C))

	 */
	 	public static void example8(){
		WorldSet myWorld = new WorldSet();	// Empty World
			myWorld.addFormula(a.e.EMPTY_FORMULA);
			
			
			DefaultRule rule1 = new DefaultRule();
			rule1.setPrerequisite(a.e.EMPTY_FORMULA);
			rule1.setJustificatoin("C");
			rule1.setConsequence("~D");
			
			DefaultRule rule2 = new DefaultRule();
			rule2.setPrerequisite(a.e.EMPTY_FORMULA);
			rule2.setJustificatoin("D");
			rule2.setConsequence("~C");
	
			RuleSet myRules = new RuleSet();
			myRules.addRule(rule1);
			myRules.addRule(rule2);
			
			DefaultReasoner loader = new DefaultReasoner(myWorld, myRules);
			HashSet<String> extensions = loader.getPossibleScenarios();
			
			a.e.println("Given the world: \n\t" + myWorld.toString() + "\n And the rules \n\t" + myRules.toString() );
			
			a.e.println("Possible Extensions");
			for(String c : extensions){			
				a.e.println("\t Ext: Th(W U (" + c + "))");	// Added for Graham
			}
		}
		
	// Example 2.2 From 
	// A Logic for Default Reasoning, R. Reiter -- R. Reiter (1980). A logic for default reasoning. Artificial Intelligence, 13:81-132.
/**
 * Output
		Applyig rules to the world Effect:eeee & eeee ==== eeee
		Given the world: 
			
		 And the rules 
			[([]):(C) ==> (~D)] , [([]):(D) ==> (~E)] , [([]):(E) ==> (~F)]
		Possible Extensions
			 Ext: Th(W U (~D & ~F))
 */
 	public static void example7(){
	WorldSet myWorld = new WorldSet();	// Empty World
		myWorld.addFormula(a.e.EMPTY_FORMULA);
		
		
		DefaultRule rule1 = new DefaultRule();
		rule1.setPrerequisite(a.e.EMPTY_FORMULA);
		rule1.setJustificatoin("C");
		rule1.setConsequence("~D");
		
		DefaultRule rule2 = new DefaultRule();
		rule2.setPrerequisite(a.e.EMPTY_FORMULA);
		rule2.setJustificatoin("D");
		rule2.setConsequence("~E");
	
		DefaultRule rule3 = new DefaultRule();
		rule3.setPrerequisite(a.e.EMPTY_FORMULA);
		rule3.setJustificatoin("E");
		rule3.setConsequence("~F");
		
			
		RuleSet myRules = new RuleSet();
		myRules.addRule(rule1);
		myRules.addRule(rule2);
		myRules.addRule(rule3);

		
		DefaultReasoner loader = new DefaultReasoner(myWorld, myRules);
		HashSet<String> extensions = loader.getPossibleScenarios();
		
		a.e.println("Given the world: \n\t" + myWorld.toString() + "\n And the rules \n\t" + myRules.toString() );
		
		a.e.println("Possible Extensions");
		for(String c : extensions){			
			a.e.println("\t Ext: Th(W U (" + c + "))");	// Added for Graham
		}
	}
	
		// Example 2.1 From 
		// A Logic for Default Reasoning, R. Reiter -- R. Reiter (1980). A logic for default reasoning. Artificial Intelligence, 13:81-132.
	/**
	 * Output
			Applyig rules to the world Effect:eeee & (B -> (~A & ~C)) ==== (~C | ~B) & eeee & (~A | ~B)
			Given the world: 
				(B -> (~A & ~C))
			 And the rules 
				[([]):(A) ==> (A)] , [([]):(B) ==> (B)] , [([]):(C) ==> (C)]
			Possible Extensions
				 Ext: Th(W U (B))
				 Ext: Th(W U (A & C))
	 */
	 	public static void example6(){
		WorldSet myWorld = new WorldSet();	// Empty World
			myWorld.addFormula("(B -> (~A & ~C))");
			
			
			DefaultRule rule1 = new DefaultRule();
			rule1.setPrerequisite(a.e.EMPTY_FORMULA);
			rule1.setJustificatoin("A");
			rule1.setConsequence("A");
			
			DefaultRule rule2 = new DefaultRule();
			rule2.setPrerequisite(a.e.EMPTY_FORMULA);
			rule2.setJustificatoin("B");
			rule2.setConsequence("B");
		
			DefaultRule rule3 = new DefaultRule();
			rule3.setPrerequisite(a.e.EMPTY_FORMULA);
			rule3.setJustificatoin("C");
			rule3.setConsequence("C");
			
				
			RuleSet myRules = new RuleSet();
			myRules.addRule(rule1);
			myRules.addRule(rule2);
			myRules.addRule(rule3);

			
			DefaultReasoner loader = new DefaultReasoner(myWorld, myRules);
			HashSet<String> extensions = loader.getPossibleScenarios();
			
			a.e.println("Given the world: \n\t" + myWorld.toString() + "\n And the rules \n\t" + myRules.toString() );
			
			a.e.println("Possible Extensions");
			for(String c : extensions){			
				a.e.println("\t Ext: Th(W U (" + c + "))");	// Added for Graham
			}
		}
	
	// From 
	// A Logic for Default Reasoning, R. Reiter -- R. Reiter (1980). A logic for default reasoning. Artificial Intelligence, 13:81-132.
 	public static void example5(){
	WorldSet myWorld = new WorldSet();	// Empty World
		myWorld.addFormula("Spouse_Fred_Mary & Hometown_Fred_Toronto & Employer_Bill_Mary & Hometown_Bill_Vancouver");
		myWorld.addFormula("Hometown_Mary_Vancouver -> ~Hometown_Mary_Toronto");
		
		DefaultRule rule1 = new DefaultRule();
		rule1.setPrerequisite("Spouse_Fred_Mary & Hometown_Fred_Toronto");
		rule1.setJustificatoin("Hometown_Mary_Toronto");
		rule1.setConsequence("Hometown_Mary_Toronto");
		
		DefaultRule rule2 = new DefaultRule();
		rule2.setPrerequisite("Employer_Bill_Mary & Hometown_Bill_Vancouver");
		rule2.setJustificatoin("Hometown_Mary_Vancouver");
		rule2.setConsequence("Hometown_Mary_Vancouver");
		
	
		
		RuleSet myRules = new RuleSet();
		myRules.addRule(rule1);
		myRules.addRule(rule2);

		
		DefaultReasoner loader = new DefaultReasoner(myWorld, myRules);
		HashSet<String> extensions = loader.getPossibleScenarios();
		
		a.e.println("Given the world: \n\t" + myWorld.toString() + "\n And the rules \n\t" + myRules.toString() );
		
		a.e.println("Possible Extensions");
		for(String c : extensions){			
			a.e.println("\t Ext: Th(W U (" + c + "))");	// Added for Graham
		}
	}
	
 	// Example 2.6 from 
 	// A Logic for Default Reasoning, R. Reiter -- R. Reiter (1980). A logic for default reasoning. Artificial Intelligence, 13:81-132.
	public static void example4(){
		WorldSet myWorld = new WorldSet();	// Empty World
		
		
		DefaultRule rule1 = new DefaultRule();
		rule1.setPrerequisite(a.e.EMPTY_FORMULA);
		rule1.setJustificatoin("A");
		rule1.setConsequence(a.e.NOT+ "A");
		
		
		RuleSet myRules = new RuleSet();
		myRules.addRule(rule1);
		
		DefaultReasoner loader = new DefaultReasoner(myWorld, myRules);
		HashSet<String> extensions = loader.getPossibleScenarios();
		
		a.e.println("Given the world: \n\t" + myWorld.toString() + "\n And the rules \n\t" + myRules.toString() );
		
		a.e.println("Possible Extensions ");
		for(String c : extensions){			
			a.e.println("\t Ext: Th(W U (" + c + "))");	// Added for Graham
		}
	}
	
	// Example 
	public static void example3(){
		WorldSet myWorld = new WorldSet();	// Empty World
		
		DefaultRule rule1 = new DefaultRule();
		rule1.setPrerequisite("A");
		rule1.setJustificatoin("ExPx");
		rule1.setConsequence("ExPx");
		
		DefaultRule rule2 = new DefaultRule();
		rule2.setPrerequisite(a.e.EMPTY_FORMULA);
		rule2.setJustificatoin("A");
		rule2.setConsequence("A");
		
		DefaultRule rule3 = new DefaultRule();
		rule3.setPrerequisite(a.e.EMPTY_FORMULA);
		rule3.setJustificatoin(a.e.NOT + "A");
		rule3.setConsequence(a.e.NOT + "A");
		
		
		RuleSet myRules = new RuleSet();
		myRules.addRule(rule1);
		myRules.addRule(rule2);
		myRules.addRule(rule3);
		
		DefaultReasoner loader = new DefaultReasoner(myWorld, myRules);
		HashSet<String> extensions = loader.getPossibleScenarios();
		
		a.e.println("Given the world: \n\t" + myWorld.toString() + "\n And the rules \n\t" + myRules.toString() );
		
		a.e.println("Possible Extensions");
		for(String c : extensions){			
			a.e.println("\t Ext: Th(W U (" + c + "))");	// Added for Graham
		}
	}
	
	public static void example2(){
		WorldSet myWorld = new WorldSet();
		myWorld.addFormula("bird_x");		// We think that x is a bird
		myWorld.addFormula("(penguin_x " + a.e.IMP + " " + a.e.NOT + "flies_x)");		// We think that x can fly
		myWorld.addFormula("penguin_x");
		
		DefaultRule rule1 = new DefaultRule();
		rule1.setPrerequisite("bird_x");
		rule1.setJustificatoin("flies_x");
		rule1.setConsequence("flies_x");
		
		DefaultRule rule2 = new DefaultRule();
		rule2.setPrerequisite("bird_x");
		rule2.setJustificatoin("penguin_x");
		rule2.setConsequence(a.e.NOT + "flies_x");
		
		RuleSet myRules = new RuleSet();
		myRules.addRule(rule1);
		myRules.addRule(rule2);
		
		DefaultReasoner loader = new DefaultReasoner(myWorld, myRules);
		HashSet<String> extensions = loader.getPossibleScenarios();
		
		a.e.println("Given the world: \n\t" + myWorld.toString() + "\n And the rules \n\t" + myRules.toString() );
		
		a.e.println("Possible Extensions");
		for(String c : extensions){			
			a.e.println("\t Ext: Th(W U (" + c + "))");	// Added for Graham
		}
	}
	
	public static void example1(){
		WorldSet myWorld = new WorldSet();
		
		myWorld.addFormula("B");		// B is true
		myWorld.addFormula("(C -> (D | A))");		// C implies either D or A or Both D and A
		myWorld.addFormula("((A & C) -> ~E)"); // A and C implies not E
		
		
		DefaultRule rule1 = new DefaultRule();
		rule1.setPrerequisite(a.e.EMPTY_EFFECT);
		rule1.setJustificatoin("A");
		rule1.setConsequence("A");
		
		DefaultRule rule2 = new DefaultRule();
		rule2.setPrerequisite("B");
		rule2.setJustificatoin("C");
		rule2.setConsequence("C");
		
		DefaultRule rule3 = new DefaultRule();
		rule3.setPrerequisite("(D & A)");
		rule3.setJustificatoin("E");
		rule3.setConsequence("E");
		
		DefaultRule rule4 = new DefaultRule();
		rule4.setPrerequisite("C & E");
		rule4.setJustificatoin("((~A) & (D | A))");
		rule4.setConsequence("F");
		
		RuleSet myRules = new RuleSet();
		myRules.addRule(rule1);
		myRules.addRule(rule2);
		myRules.addRule(rule3);
		myRules.addRule(rule4);
		
		
		DefaultReasoner loader = new DefaultReasoner(myWorld, myRules);
		HashSet<String> extensions = loader.getPossibleScenarios();
		
		a.e.println("Given the world: \n\t" + myWorld.toString() + "\n And the rules \n\t" + myRules.toString() );
		
		a.e.println("Possible Extensions");
		for(String c : extensions){			
			a.e.println("\t Ext: Th(W U (" + c + "))");	// Added for Graham
		}
	}
	
}
