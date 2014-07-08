package be.fnord.util.logic.defaultLogic;

import java.util.LinkedList;

import be.fnord.util.logic.WFF;

import com.merriampark.Gilleland.CombinationGenerator;

// Todo add in extra functions for add remove rules

public class RuleSet {

	//
	public LinkedList<String> addedConsequence = new LinkedList<String>();

	private LinkedList<DefaultRule> rules = new LinkedList<DefaultRule>();

	public void addRule(DefaultRule _rule) {
		rules.add(_rule);
	}

	public LinkedList<String> applyRules(LinkedList<String> possibleExtensions,
			WFF world) {
		LinkedList<String> _extensions = new LinkedList<String>();
		// System.out.println("Applyig rules to the world " + world + " ==== " +
		// world.getClosure().trim());
		// System.out.println("!!-------------------------------------------------------");
		// System.out.println(possibleExtensions);
		for (String possExtension : possibleExtensions) {

			LinkedList<String> _consequences = new LinkedList<String>();

			WFF currentExtension = new WFF(possExtension);
			WFF assertion = new WFF("(" + world.getFormula() + ") & ("
					+ possExtension + ")");

			boolean overall = true;
			for (DefaultRule d : rules) {
				// a.e.println("Trying: " + currentExtension.getFormula());
				// Test if prerequisite is fired
				WFF preq = new WFF(d.getPrerequisite());
				WFF just = new WFF(d.getJustificatoin());
				WFF cons = new WFF(d.getConsequence());
				boolean prereqisite_fired = assertion.eval(preq);
				// System.out.println(assertion.getFormula() + "  |=  " +
				// preq.getFormula() + " ===" + prereqisite_fired);

				if (prereqisite_fired) {
					boolean just_consistent = assertion.isConsistent(just
							.getFormula());
					if (just_consistent) {
						// Test if consequence is entailed by extension
						boolean test_consequence_entailed_by_extension = currentExtension
								.entails(cons);
						// System.out.println(currentExtension.getFormula() +
						// " |= " + d.getConsequence() + " = " +
						// test_consequence_entailed_by_extension );

						if (!test_consequence_entailed_by_extension) {
							overall = false;
						}
						_consequences.add(currentExtension.getFormula());
					}
				} else {

					//
					//
					// if(!preqCon.isConsistent(currentExtension.getFormula())){
					// System.out.println("false");
					// }
				}
			}
			// System.out.println("_consequences:" + _consequences);
			// System.out.println("~~-------------------------------------------------------");
			if (overall) {
				// Loop through the rules again looking for inconsistencies
				for (DefaultRule d : rules) {
					WFF preq = new WFF(d.getPrerequisite());
					// WFF just = new WFF(d.getJustificatoin());
					WFF cons = new WFF(d.getConsequence());
					boolean prereqisite_fired = assertion.entails(preq);
					// boolean just_consistent =
					// assertion.isConsistent(just.getFormula());

					if (!prereqisite_fired) {
						WFF preqCon = new WFF("(" + d.getPrerequisite()
								+ ") & (" + d.getJustificatoin() + ")");
						boolean test_consequence_entailed_by_extension = currentExtension
								.entails(cons);
						if (test_consequence_entailed_by_extension) {
							if (!preqCon.isConsistent(currentExtension
									.getFormula())) {
								overall = false;
							}
						}
					}
				}
				if (!overall) {
					break;
				}
				// We create a deductive closure of the extension and all of the
				// consequences
				String talliedCons = "";
				for (String c : _consequences) {
					talliedCons = talliedCons + " " + a.e.AND + " " + c;
				}
				if (talliedCons.length() > 0) {
					talliedCons = talliedCons.substring(
							(" " + a.e.AND + " ").length(),
							talliedCons.length());
				}
				WFF wffCons = new WFF(talliedCons);

				String extString = currentExtension.getClosure().trim();
				String conString = wffCons.getClosure().trim();
				// System.out.println("ext =" + extString);
				if (extString.compareTo(conString) == 0) {
					_extensions.add(possExtension);
				}
			}
		}

		return _extensions;
	}

	public LinkedList<String> generateExtensions(
			LinkedList<String> possibleExtensions, WFF world) {
		LinkedList<String> _extensions = new LinkedList<String>();
		// System.out.println("Applyig rules to the world " + world + " ==== " +
		// world.getClosure().trim());

		for (String possExtension : possibleExtensions) {
			LinkedList<String> _consequences = new LinkedList<String>();
			WFF currentExtension = new WFF(possExtension);

			// So long as one rule fires then we store the consequences of the
			// rule
			boolean overall = false;
			for (DefaultRule d : rules) {
				// a.e.println("Trying: " + currentExtension.getFormula());
				boolean results = testRule(currentExtension, world, d);
				if (results) {
					overall = true;
				}
				if (results) {
					// Recurse over rules with consequence as a fact

					if (addedConsequence.contains(possExtension)
							|| world.entails(currentExtension)) {
						// Do nothing just add it
						_consequences.add(d.getConsequence());
					} else {
						addedConsequence.add(possExtension);
						WFF backup = world;
						world = new WFF(world.getFormula() + " " + a.e.AND
								+ " (" + d.getConsequence() + ")");
						LinkedList<String> _newCons = generateExtensions(
								possibleExtensions, world);
						LinkedList<String> __newCons = new LinkedList<String>();
						for (String s : _newCons) {
							s = s + " " + a.e.AND + "(" + d.getConsequence()
									+ ")";
							__newCons.add(s);
						}
						_consequences.addAll(__newCons);
						world = backup;
					}

				}

			}

			if (overall) {
				// We create a deductive closure of the extension and all of the
				// consequences
				String talliedCons = "";
				for (String c : _consequences) {
					talliedCons = talliedCons + " " + a.e.AND + " " + c;
				}
				if (talliedCons.length() > 0) {
					talliedCons = talliedCons.substring(
							(" " + a.e.AND + " ").length(),
							talliedCons.length());
				}
				WFF wffCons = new WFF(talliedCons);

				String extString = currentExtension.getClosure().trim();
				String conString = wffCons.getClosure().trim();
				// System.out.println("ext =" + extString);
				if (extString.compareTo(conString) == 0) {
					_extensions.add(possExtension);
				}
			}
		}

		return _extensions;
	}

	public LinkedList<String> getAllConsequences(WFF w) {
		LinkedList<String> _result = new LinkedList<String>();

		LinkedList<String> base = new LinkedList<String>();
		for (DefaultRule r : rules) {
			base.add(r.getConsequence());
		}
		// System.out.println(base + " " + base.size());

		for (int j = 1; j <= base.size(); j++) {
			CombinationGenerator comb = new CombinationGenerator(base.size(), j);

			while (comb.hasMore()) {

				int[] ar = comb.getNext();
				String newFormula = "";
				for (int i : ar) {
					newFormula = newFormula + " " + a.e.AND + " " + base.get(i);
				}

				if (newFormula.length() > 0) {
					newFormula = newFormula
							.substring((" " + a.e.AND + " ").length(),
									newFormula.length());
				}
				_result.add(newFormula);
			}
		}
		;

		_result = removeInconsistent(_result, w);

		// Get closure
		LinkedList<String> __result = new LinkedList<String>();
		for (String s : _result) {
			WFF cl = new WFF(s);
			String r = cl.getClosure();
			__result.add(r);
		}

		return __result;
	}

	public LinkedList<DefaultRule> getRules() {
		return rules;
	}

	// Remove all inconsistent consequences
	public LinkedList<String> removeInconsistent(
			LinkedList<String> consequences, WFF world) {
		LinkedList<String> _newconsequences = new LinkedList<String>();

		for (String s : consequences) {
			WFF w = new WFF(s); // Create a new well formed formula

			if (w.isConsistent(world.getFormula())) {
				_newconsequences.add(s);
			} else {
				// System.out.println("Removing inconsistent consequence " + s);
			}
		}

		return _newconsequences;
	}

	public void setRules(LinkedList<DefaultRule> rules) {
		this.rules = rules;
	}

	public boolean testRule(WFF ext, WFF _world, DefaultRule d) {
		WFF prec = new WFF(d.getPrerequisite());
		WFF world = new WFF(_world.getFormula() + " & " + ext.getFormula());
		if (world.entails(prec)) {
			// Good start, our prerequisite is true

			WFF just = new WFF(d.getJustificatoin());
			if (just.eval(ext)) {

				// It's okay to consider the extension, lets test it for
				// entailment
				WFF cons = new WFF(d.getConsequence());
				// System.err.println(ext + " " + cons);
				if (ext.isConsistent(cons.getFormula())) {
					if (ext.entails(cons)) {
						// System.out.println("Testing extension " +
						// ext.getFormula());
						// System.out.println("\tWorld is " + world +
						// " prec is " + prec);
						// System.out.println("\tRule is " + d.toString());
						return true;
					}

				}
			}
		} else {
			// System.out.println("Failed on " );
			// System.out.println("\tWorld is " + world + " prec is " + prec);
		}

		return false;
	}

	@Override
	public String toString() {
		String s = "";
		for (DefaultRule r : rules) {
			s += r.toString() + " , ";
		}
		if (s.length() > 1) {
			s = s.substring(0, s.length() - 3);
		}
		return s;
	}

	// public HashSet<String> getLongestExtensions(HashSet<String> _ext){
	//
	// // Fix the bug of not getting C & F and C & E & F by splitting the string
	// // i.e. Tokens = s.split("&");
	//
	//
	// HashSet<String> _result = new HashSet<String>();
	// HashSet<String> _remove = new HashSet<String>();
	// _result.add(a.e.EMPTY_FORMULA);
	// boolean updated = false;
	// for(String e : _ext){
	// for(String s: _ext){
	//
	// if(s.compareTo(e) != 0 && s.contains(e)){
	// updated = true;
	// _remove.add(e);
	// }else{
	// _result.add(s);
	// }
	// }
	// }
	// _result.remove(a.e.EMPTY_FORMULA);
	// for(String s: _remove){
	// _result.remove(s);
	// }
	// if(updated){
	//
	// HashSet<String> _newResult = getLongestExtensions(_result);
	//
	//
	// return _newResult;
	// }
	// return _result;
	// }

}
