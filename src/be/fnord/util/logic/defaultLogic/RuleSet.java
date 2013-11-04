package be.fnord.util.logic.defaultLogic;

import java.util.LinkedList;

import be.fnord.util.logic.WFF;

import com.merriampark.Gilleland.CombinationGenerator;

// Todo add in extra functions for add remove rules

public class RuleSet {

	private LinkedList<DefaultRule> rules = new LinkedList<DefaultRule>();

	public LinkedList<String> getAllConsequences(WFF w){
		LinkedList<String> _result = new LinkedList<String>();
		
		LinkedList<String> base = new LinkedList<String>();
		for(DefaultRule r : rules){
			base.add(r.getConsequence());
		}
//		System.out.println(base + " " + base.size());
		
		for(int j = 1; j <= base.size() ; j++){
			CombinationGenerator comb = new CombinationGenerator(base.size(), j);
			
			while(comb.hasMore()){
				
				int[] ar = comb.getNext();
				String newFormula = "";			
				for(int i: ar){			
					newFormula = newFormula + " " + a.e.AND + " " + base.get(i);
				}
				
				if(newFormula.length() > 0) newFormula = newFormula.substring((" " + a.e.AND + " ").length() , newFormula.length());
				_result.add(newFormula);
			}
		};
		
		_result = removeInconsistent(_result, w);
		return _result;
	}
	
	public void addRule(DefaultRule _rule){
		rules.add(_rule);
	}
	
	public LinkedList<DefaultRule> getRules() {
		return rules;
	}

	public void setRules(LinkedList<DefaultRule> rules) {
		this.rules = rules;
	}

	// Remove all inconsistent consequences
	public LinkedList<String> removeInconsistent(LinkedList<String> consequences, WFF world) {
		LinkedList<String> _newconsequences = new LinkedList<String>();
		
		for(String s: consequences){
			WFF w = new WFF(s); // Create a new well formed formula
			
			if(w.isConsistent(world.getFormula())){
				_newconsequences.add(s);
			}else{
//				System.out.println("Removing inconsistent consequence " + s);
			}
		}
		
		return _newconsequences;
	}
	
	// 
	public LinkedList<String> applyRules(LinkedList<String> possibleExtensions, WFF world){
		LinkedList<String> _extensions = new LinkedList<String>();
//		System.out.println("Applyig rules to the world " + world);
		for(String possExtension : possibleExtensions){
			LinkedList<String> _consequences = new LinkedList<String>();
			WFF currentExtension = new WFF(possExtension); 
			
			// So long as one rule fires then we store the consequences of the rule
			boolean overall = false;
			for(DefaultRule d : rules){
				boolean results = testRule(currentExtension, world, d);
				if(results) overall = true;
				if(results)
					_consequences.add(d.getConsequence());

			}
			
			if(overall){
				// We create a deductive closure of the extension and all of the consequences
				String talliedCons = "";
				for(String c : _consequences){
					talliedCons = talliedCons + " " + a.e.AND + " " + c;
				}
				if(talliedCons.length() > 0) talliedCons = talliedCons.substring((" " + a.e.AND + " ").length() , talliedCons.length());
				WFF wffCons = new WFF(talliedCons);
				
				String extString = currentExtension.getClosure().trim();
				String conString = wffCons.getClosure().trim();
//				System.out.println("ext =" + extString);
				if(extString.compareTo(conString) == 0)				
					_extensions.add(possExtension);
			}
		}
		
		return _extensions;
	}
	
	public boolean testRule(WFF ext, WFF world, DefaultRule d){
		WFF prec = new WFF(d.getPrerequisite());
//		System.out.println("World is " + world + " prec is " + prec);
		
		if(world.entails(prec)){
			
			// Good start, our prerequisite is true
			WFF just = new WFF(d.getJustificatoin());
			if(just.eval(ext)){
				// It's okay to consider the extension, lets test it for entailment
				WFF cons = new WFF(d.getConsequence());
//				System.err.println(ext + " " + cons);
				if(ext.isConsistent(cons.getFormula()))
				if(ext.entails(cons)) return true;
			}
		}
		
		return false;

	}
		
}
