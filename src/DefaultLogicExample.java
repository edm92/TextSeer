import java.util.HashSet;
import java.util.LinkedList;

import be.fnord.util.logic.DefaultReasoner;
import be.fnord.util.logic.defaultLogic.DefaultRule;
import be.fnord.util.logic.defaultLogic.RuleSet;
import be.fnord.util.logic.defaultLogic.WorldSet;


public class DefaultLogicExample {

public static void main(String[] args){
		
		WorldSet myWorld = new WorldSet();
		myWorld.addFormula("B");		// We think that x is a bird
		myWorld.addFormula("(C -> (D | A))");		// Penguins can not fly
		myWorld.addFormula("((A & C) -> ~E)"); // X is a Penguin
		myWorld.addFormula(a.e.EMPTY_EFFECT); // X is a Penguin
		
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
		
		a.e.println("Possible Extensions");
		for(String c : extensions){			
			a.e.println("\t Ext:" + c);
		}
	}
	
}
