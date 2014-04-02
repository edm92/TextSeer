package be.fnord.util.QUAL;

import java.util.HashSet;
import java.util.LinkedHashSet;

import be.fnord.util.QUAL.Prefs.MAX_COST;
import be.fnord.util.QUAL.Prefs.MIN_COST;
import be.fnord.util.QUAL.Prefs.PREF_FUNC;

/**
 * Created by edm92 on 25/03/2014.
 */
public class Qos {
	public String COST;
	public String TIME;
	public String SKILL;
	
	public Qos(){
		COST = "";
		TIME = "";
		SKILL = "";
	}
	
	public Qos copy(){
		Qos _result = new Qos();
		
		_result.COST = COST;
		_result.TIME = TIME;
		_result.SKILL = SKILL;
		
		return _result;
	}
	
	// Pairwise Acc 
	public Qos pairwise_acc(Qos source, Qos target, JSONEFFECT _src, JSONEFFECT _trg) {
		Qos _result = new Qos();
		HashSet<String> goals = new HashSet<String>();
		for(String s: _src.GOAL)
			goals.add(s);
		for(String s: _trg.GOAL)
			goals.add(s);
		PREF_FUNC msPref = null;
		if(goals.contains("MINPRICE"))
			msPref = new MIN_COST();
		else
			msPref = new MAX_COST();
		
		_result.COST = msPref.combine(source.COST, target.COST);
		
		return _result;
	}
	
	public String getCOST() {
		return COST;
	}
	public void setCOST(String cOST) {
		COST = cOST;
	}
	public String getTIME() {
		return TIME;
	}
	public void setTIME(String tIME) {
		TIME = tIME;
	}
	public String getSKILL() {
		return SKILL;
	}
	public void setSKILL(String sKILL) {
		SKILL = sKILL;
	}
	
}
