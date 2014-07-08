package be.fnord.util.logic;

import java.util.HashSet;
import java.util.LinkedList;

public class AbductionReasoner {
	public static final boolean _VERBOSE = false;
	public static final int MAX_GUESSES = 100; // Doesn't really do much
	public int CURRENT_GUESS = 0;
	LinkedList<String> facts = new LinkedList<String>();

	String kb = "";
	String observations = "rt";
	public boolean SOLUTION_FOUND = false;

	public AbductionReasoner() {
		String _kb = "((dr & cf) -> ~rt) & ((dr & ~cf) -> rt)"; // could (cf ->
																// nr) & (~cf ->
																// ~nr)
		LinkedList<String> _facts = new LinkedList<String>();
		_facts.add("di");
		_facts.add("dr");
		_facts.add("cf");
		_facts.add("~cf");
		_facts.add("rt");
		_facts.add("~rt");

		String _observations = "rt";
		_facts.remove("rt");

		_AbductionReasoner(_kb, _facts, _observations);
	}

	public AbductionReasoner(String _kb, LinkedList<String> _facts,
			String _observations) {
		_AbductionReasoner(_kb, _facts, _observations);
	}

	public void _AbductionReasoner(String _kb, LinkedList<String> _facts,
			String _observations) {
		kb = _kb;
		facts = _facts;
		observations = _observations;
	}

	public HashSet<String> findMinimal(LinkedList<String> input) {
		LinkedList<String> best = new LinkedList<String>();
		HashSet<String> all = new HashSet<String>();
		boolean replacements = false;
		for (String s : input) {
			all.add(s);
			// Loop through each solution and see if it entails other solutions
			for (String t : input) {
				if (s.compareTo(t) != 0) {
					WFF e1 = new WFF(s);
					WFF e2 = new WFF(t);
					if (e1.entails(e2) && !e2.entails(e1)) {
						best.add(t);
						replacements = true;
					}
				}
			}
		}
		if (replacements) {
			return findMinimal(best);
		}

		return all;
	}

	public LinkedList<String> getFacts() {
		return facts;
	}

	public String getKb() {
		return kb;
	}

	public String getObservations() {
		return observations;
	}

	public LinkedList<String> getPossibleScenarios() {

		LinkedList<String> possibleScenarios = new LinkedList<String>();
		for (String fact : facts) {
			if (_VERBOSE) {
				System.out.println("Trying the base fact : " + fact);
			}
			if (CURRENT_GUESS > MAX_GUESSES) {
				break;
			}
			LinkedList<String> trueFacts = new LinkedList<String>();
			testFact(kb, facts, observations, new LinkedList<String>(), fact,
					trueFacts);
			possibleScenarios.addAll(trueFacts);
		}
		return possibleScenarios;
	}

	/**
	 * Function takes a guess of possible actions that could result in the
	 * observation.
	 *
	 * @param kb
	 * @param guess
	 * @param observation
	 * @return
	 */
	public boolean makeGuess(String kb, String guess, String observation) {
		if (CURRENT_GUESS++ > MAX_GUESSES) {
			return false;
		}
		String possible_answer = "(" + kb + ")";
		if (guess != null && guess.length() > 0) {
			possible_answer += "& (" + guess + ")";
		}

		WFF observationWFF = new WFF(observation);
		WFF answerWFF = new WFF(possible_answer);
		boolean result = false;
		if (answerWFF.issat()) {
			result = answerWFF.entails(observationWFF);
		}
		if (_VERBOSE) {
			System.out.println(possible_answer + " isSat? " + result);
		}
		return result;
	}

	public void setFacts(LinkedList<String> facts) {
		this.facts = facts;
	}

	public void setKb(String kb) {
		this.kb = kb;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public boolean testFact(String kb, LinkedList<String> facts,
			String observations, LinkedList<String> existing, String newFact,
			LinkedList<String> trueFacts) {

		String _newFact = unrollList(existing, newFact);

		// Test to see if we could resolve with this fact
		if (makeGuess(kb, _newFact, observations)) {
			trueFacts.add(_newFact);
			SOLUTION_FOUND = true;
			return true;
		}
		existing.add(newFact);

		// Recursive checking
		for (String fact : facts) {
			if (!existing.contains(fact)) {
				testFact(kb, facts, observations, existing, fact, trueFacts);
				if (existing.contains(fact)) {
					existing.remove(fact);
				}
			}
		}

		if (CURRENT_GUESS > MAX_GUESSES) {
			return false;
		}

		return false;
	}

	/**
	 * Unroll a list into a propositional sentence.
	 *
	 * @param list
	 * @return
	 */
	public String unrollList(LinkedList<String> list, String newFact) {
		String _new = "";
		for (String s : list) {
			if (s.length() > 0) {
				_new += "( " + s + " ) & ";
			}
		}
		if (newFact.length() > 0) {
			_new += "( " + newFact + " ) ";
		} else if (_new.length() > 0) {
			_new = _new.substring(0, _new.length() - " & ".length());
		}
		return _new;
	}

}
