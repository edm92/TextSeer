import java.util.LinkedList;

import be.fnord.util.logic.DefaultReasoner;
import be.fnord.util.logic.defaultLogic.DefaultRule;
import be.fnord.util.logic.defaultLogic.RuleSet;
import be.fnord.util.logic.defaultLogic.WorldSet;


public class DefaultLogicExample {

public static void main(String[] args){
		
		WorldSet myWorld = new WorldSet();
		myWorld.addFormula("bird_x");		// We think that x is a bird
		myWorld.addFormula("(penguin_x " + a.e.IMP + " " + a.e.NOT + "flies_x)");		// Penguins can not fly
		myWorld.addFormula("penguin_x"); // X is a Penguin
		
		DefaultRule rule1 = new DefaultRule();
		rule1.setPrerequisite("bird_x");
		rule1.setJustificatoin("flies_x");
		rule1.setConsequence("flies_x");
		
		DefaultRule rule2 = new DefaultRule();
		rule2.setPrerequisite("bird_x");
		rule2.setJustificatoin("penguin_x");
		rule2.setConsequence(a.e.NOT + "flies_x");
		
		DefaultRule rule3 = new DefaultRule();
		rule3.setPrerequisite("dog_x");
		rule3.setJustificatoin("cat_x");
		rule3.setConsequence(a.e.NOT + "monkey_x");
		
		
		RuleSet myRules = new RuleSet();
		myRules.addRule(rule1);
		myRules.addRule(rule2);
		myRules.addRule(rule3);
		
		
		DefaultReasoner loader = new DefaultReasoner(myWorld, myRules);
		LinkedList<String> extensions = loader.getPossibleScenarios();
		a.e.println("Possible Extensions");
		for(String c : extensions){			
			a.e.println("\t Ext:" + c);
		}
	}
	
}
