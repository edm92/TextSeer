package be.fnord.util.QUAL;

import java.util.HashSet;

import be.fnord.util.QUAL.Prefs.MAX_COST;
import be.fnord.util.QUAL.Prefs.MAX_SKILL;
import be.fnord.util.QUAL.Prefs.MAX_TIME;
import be.fnord.util.QUAL.Prefs.MIN_COST;
import be.fnord.util.QUAL.Prefs.MIN_SKILL;
import be.fnord.util.QUAL.Prefs.MIN_TIME;
import be.fnord.util.QUAL.Prefs.PREF_FUNC;

/**
 * Created by edm92 on 25/03/2014. TODO add an isempty command
 */
public class Qos {
	/**
	 * Test Functions
	 *
	 */
	public static void main(String[] args) {
		JSONEFFECT _a = new JSONEFFECT();
		JSONEFFECT _b = new JSONEFFECT();
		Qos n = new Qos();
		_b.GOAL = new String[] { "MINPRICE", "MINTIME", "MINSKILL" };

		_a.QOS.COST = "$10";
		_a.QOS.TIME = "PT10M";
		_a.QOS.SKILL = "MED";

		_b.QOS.COST = "$20";
		_b.QOS.TIME = "PT20M";
		_b.QOS.SKILL = "HIGH";

		n = n.pairwise_acc(_a.QOS, _b.QOS, _a, _b);
		a.e.println(n.toString());
	}

	public String COST;
	public String SKILL;
	public String TIME;

	public String UTILITY;

	public Qos() {
		COST = "";
		TIME = "";
		SKILL = "";
		UTILITY = "";
	}

	public Qos copy() {
		Qos _result = new Qos();

		_result.COST = COST;
		_result.TIME = TIME;
		_result.SKILL = SKILL;
		_result.UTILITY = UTILITY;

		return _result;
	}

	public String getCOST() {
		return COST;
	}

	public String getSKILL() {
		return SKILL;
	}

	public String getTIME() {
		return TIME;
	}

	// Pairwise Acc
	public Qos pairwise_acc(Qos source, Qos target, JSONEFFECT _src,
			JSONEFFECT _trg) {
		Qos _result = new Qos();

		// Process the goals to check which objective functions we're using
		HashSet<String> goals = new HashSet<String>();
		for (String s : _src.GOAL) {
			goals.add(s);
		}
		for (String s : _trg.GOAL) {
			goals.add(s);
		}

		// Handle Cost Accumulation
		PREF_FUNC costPref = null;
		if (goals.contains("MINPRICE")) {
			costPref = new MIN_COST();
		} else {
			costPref = new MAX_COST();
		}

		// Handle Time Preferences
		PREF_FUNC timePref = null;
		if (goals.contains("MINTIME")) {
			timePref = new MIN_TIME();
		} else {
			timePref = new MAX_TIME();
		}

		// Handle Skill Accumulation
		PREF_FUNC skillPref = null;
		if (goals.contains("MINSKILL")) {
			skillPref = new MIN_SKILL();
		} else {
			skillPref = new MAX_SKILL();
		}

		// Handle Utility Accumulation
		PREF_FUNC utilityPref = null;
		utilityPref = new MAX_COST();

		_result.COST = costPref.combine(source.COST, target.COST);
		_result.TIME = timePref.combine(source.TIME, target.TIME);
		_result.SKILL = skillPref.combine(source.SKILL, target.SKILL);
		_result.UTILITY = utilityPref.compare(source.UTILITY, target.UTILITY) ? source.UTILITY
				: target.UTILITY;

		return _result;
	}

	public void setCOST(String cOST) {
		COST = cOST;
	}

	public void setSKILL(String sKILL) {
		SKILL = sKILL;
	}

	public void setTIME(String tIME) {
		TIME = tIME;
	}

	@Override
	public String toString() {
		return "Cost = " + COST + "; TIME = " + TIME + "; SKILL = " + SKILL;
	}

	public boolean isEmpty() {
		if(COST != null && COST.length() > 0)
			return false;
		if(TIME != null && TIME.length() > 0)
			return false;
		if(SKILL != null && SKILL.length() > 0)
			return false;
		if(UTILITY != null && UTILITY.length() > 0)
			return false;
		return true;
	}
}
