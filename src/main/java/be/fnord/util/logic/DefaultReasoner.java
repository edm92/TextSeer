package be.fnord.util.logic;

import java.util.HashSet;
import java.util.LinkedList;

import be.fnord.util.logic.defaultLogic.DefaultRule;
import be.fnord.util.logic.defaultLogic.RuleSet;
import be.fnord.util.logic.defaultLogic.WorldSet;

public class DefaultReasoner {
	public static void main(String[] args) {

		WorldSet myWorld = new WorldSet();
		myWorld.addFormula("bird_x"); // We think that x is a bird
		myWorld.addFormula("(penguin_x " + a.e.IMP + " " + a.e.NOT + "flies_x)"); // We
																					// think
																					// that
																					// x
																					// can
																					// fly
		myWorld.addFormula("penguin_x");

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
		HashSet<String> extensions = loader.getPossibleScenarios();
		a.e.println("Possible Extensions");
		for (String c : extensions) {
			a.e.println("\t Ext:" + c);
		}
	}

	public RuleSet rules;

	public WorldSet world;

	public DefaultReasoner() {
		WorldSet myWorld = new WorldSet();
		myWorld.addFormula("bird_x"); // We think that x is a bird
		myWorld.addFormula("(penguin_x " + a.e.IMP + " " + a.e.NOT + "flies_x)"); // We
																					// think
																					// that
																					// x
																					// can
																					// fly
		myWorld.addFormula("penguin_x");

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

		_DefaultReasoner(myWorld, myRules);
	}

	public DefaultReasoner(WorldSet myWorld, RuleSet myRules) {
		_DefaultReasoner(myWorld, myRules);
	}

	public void _DefaultReasoner(WorldSet _world, RuleSet _rules) {
		world = _world;
		rules = _rules;
	}

	// Process world and rules
	public HashSet<String> getPossibleScenarios() {
		// WorldSet _results = new WorldSet();

		// First step - make a WFF of the world
		WFF myWorld = new WFF(world.getWorld());
		WFF myOriginalWorld = new WFF(world.getWorld());

		// Take the full set of consequences
		LinkedList<String> consequences = rules.getAllConsequences(myWorld);

		// Apply the rules againt the consequences to test the results
		LinkedList<String> extensions = rules.generateExtensions(consequences,
				myWorld);

		// Take the max strings
		LinkedList<String> validExtensions = rules.applyRules(extensions,
				myOriginalWorld);
		HashSet<String> _validExtensions = new HashSet<String>();
		for (String s : validExtensions) {
			WFF _s = new WFF(s);
			_validExtensions.add(_s.getClosure());
		}

		// HashSet<String> maxLenExtensions =
		// rules.getLongestExtensions(_validExtensions);

		// Return the result
		return _validExtensions;
	}

}
