import java.util.HashSet;
import java.util.LinkedList;

import be.fnord.util.logic.DefaultReasoner;
import be.fnord.util.logic.defaultLogic.DefaultRule;
import be.fnord.util.logic.defaultLogic.RuleSet;
import be.fnord.util.logic.defaultLogic.WorldSet;


public class DefaultLogicExample {

	public static void main(String[] args){
		example1();
		example2();
		example3();
		example4();
		example5();
	}
	
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
			a.e.println("\t Ext:" + c);
		}
	}
	
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
		
		a.e.println("Possible Extensions");
		for(String c : extensions){			
			a.e.println("\t Ext:" + c);
		}
	}
	
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
			a.e.println("\t Ext:" + c);
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
//		myRules.addRule(rule3);
		
		DefaultReasoner loader = new DefaultReasoner(myWorld, myRules);
		HashSet<String> extensions = loader.getPossibleScenarios();
		
		a.e.println("Given the world: \n\t" + myWorld.toString() + "\n And the rules \n\t" + myRules.toString() );
		
		a.e.println("Possible Extensions");
		for(String c : extensions){			
			a.e.println("\t Ext:" + c);
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
			a.e.println("\t Ext:" + c);
		}
	}
	
}
