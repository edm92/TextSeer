package be.fnord.util.logic;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.merriampark.Gilleland.CombinationGenerator;

import be.fnord.util.functions.OCP.PartitionListElement;
import be.fnord.util.processModel.Effect;

import orbital.logic.imp.Formula;
import orbital.logic.imp.Logic;
//import orbital.logic.sign.ParseException;
import orbital.moon.logic.resolution.ClausalSet;
import orbital.moon.logic.resolution.Clause;
import orbital.moon.logic.resolution.DefaultClausalFactory;

/**
 * The following class is used for accumulation. I have tried to keep most variables transient for distributed processing. 
 * @author edm92
 *
 */
public class Accumulate implements Serializable{
	private static final long serialVersionUID = 1L;
	private transient Formula formula;
	protected transient static Logger logger = Logger.getLogger("EffectFunction");
	private int currentDepth = 0;
	private int targetClauses = 0;
	
	boolean distro = false;

	public LinkedHashSet<Effect> pairwise_acc(Effect source, Effect target, String KB, boolean distrobute){
		distro = distrobute;
		return pairwise_acc(source, target, KB);
	}
	
	private LinkedHashSet<Effect> pairwise_acc(Effect source, Effect target, String KB){
		currentDepth = numClauses(source)-1;
		targetClauses = numClauses(target); 
		LinkedHashSet<Effect> results = pairwise_acc_old(source, target, KB);
		//System.err.println("Explored: " + explored.size() + " effects");
		return results;
	}
	
	/**
	 * Do pairwise accumulation and return the set of maximally consistent effects that result from accumulation
	 * @param source starting effect
	 * @param target new effect to accumulate with
	 * @param KB existing knowledgebase
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private LinkedHashSet<Effect> pairwise_acc_old(Effect source, Effect target, String KB){
     	if(!source.eval(target, KB)){ // Not sat so compute maxSubsets
	     	try{
	     		LinkedHashSet<Effect> results = new LinkedHashSet<Effect>();
	     		LinkedHashSet<String> cleanedResults = new LinkedHashSet<String>();
	     		do{
		     		//System.err.println("depth : " + currentDepth);
		     		Set<Effect> correctDepthResults = makeCorrectDepthResults(source);
		     		//System.err.println("Partitions: " + correctDepthResults);
		     		for(Effect newResult : correctDepthResults){
		     			//System.out.println("Trying " + newResult);
		     			LinkedHashSet<Effect> returnPairResults;
	     				returnPairResults = rec_pair_acc(newResult, target,  KB, distro);

		     			//for(Effect e : returnPairResults) System.out.println(currentDepth + ") Returned on " + e );
		     			results.addAll(returnPairResults);		     			
		     		}
		     		if(results != null && results.size() > 0) break;
		     		currentDepth--;
		     		if(currentDepth < 0) break;
	     		}while(currentDepth > 0);
	     		//for(Effect e : results) System.err.println(currentDepth + ") Working on " + e);
	     		LinkedHashSet<Effect> newResults = new LinkedHashSet<Effect>();
	     		int maxSize = 0;
	     		for(Effect e: results){
	     			int number = numClauses(e);	     			
	    	        if(number > maxSize) maxSize = number;
	     		}
	     		maxSize += targetClauses;
	     		for(Effect e: results){
	     			Logic logic = new ClassicalLogicS();
	     			//System.err.println("Trying " + e.getFormula());
     				formula = (Formula) logic.createExpression("(" + e.getFormula() + ") & (" + target.getFormula() + ")");
	    	        Formula result = ClassicalLogicS.Utilities.conjunctiveForm(formula, true);
	    	        DefaultClausalFactory myFacts = new DefaultClausalFactory();
	    	        ClausalSet myClauses =  myFacts.asClausalSet(result);
	    	        if(myClauses.size() < maxSize) continue;
    	        	String newForm = "";
    	        	for(Clause cls :(Set<Clause>) myClauses){
	    				newForm += cls.toString() + " & ";
	    				newForm = newForm.replaceAll("\\[", "(");
	    				newForm = newForm.replaceAll("\\]",")");
	    				newForm = newForm.replaceAll(",", "|");
	    			}
    	        	if(newForm.length() > 3) newForm = newForm.substring(0, newForm.length() - 3); // Remove final &
    	        	//Effect resultEffect = new Effect(newForm);
    	        	cleanedResults.add(newForm);
    	        	//newResults.add(resultEffect);
    	        
	     		}
	     		for(String newEff: cleanedResults)
	     			newResults.add(new Effect(newEff));
	     		if(newResults.size() < 1) newResults.add(target);
	     		return newResults;
	     	}catch(Exception e){
	     		e.printStackTrace();
	     		return null;
	     	}
     	}else{
     		Effect resultingScenario = new Effect(source.getEffect(target.getFormula(), KB));
     		LinkedHashSet<Effect> returnList = new LinkedHashSet<Effect>();
     		returnList.add(resultingScenario);
     		return returnList;
     	}
     	
	}
	
	@SuppressWarnings("unchecked")
	private Set<Effect> makeCorrectDepthResults(Effect source) {
		HashSet<Effect> results = new HashSet<Effect>();
		try{
			Logic logic = new ClassicalLogicS();
			if(source.getFormula().length() < 1){ return results; };
	        formula = (Formula) logic.createExpression(source.getFormula());
	        Formula result = ClassicalLogicS.Utilities.conjunctiveForm(formula, true);
	        DefaultClausalFactory myFacts = new DefaultClausalFactory();
	        ClausalSet myClauses =  myFacts.asClausalSet(result);
	        
	        
	        PartitionListElement<Clause> reducedEffect = new PartitionListElement<Clause>();
	    	for(Clause cls :(Set<Clause>) myClauses){
	    		reducedEffect.add(cls);
			}
	    	
	    	int[] indices;
	        CombinationGenerator x = new CombinationGenerator (reducedEffect.size(), currentDepth);
	        StringBuffer combination;
	        HashSet<String> myEffect = new HashSet<String>();
	        while (x.hasMore ()) {
	          combination = new StringBuffer ();
	          indices = x.getNext ();
	          for (int i = 0; i < indices.length; i++) {
	            combination.append(reducedEffect.get(indices[i]));
	          }
	          String mynewEffect = combination.toString();
	          myEffect.add(mynewEffect.replaceAll("\\]\\[", "\\],\\["));
	        }
	        
	    	for(String formString: myEffect){
	    		String newForm = formString;
				newForm = newForm.replaceAll("\\[", "(");
				newForm = newForm.replaceAll("\\],", ") & ");
				newForm = newForm.replaceAll("\\]", ")");
				newForm = newForm.replaceAll(","," |");
				
	    		//System.out.println("Made new effect at depth " + currentDepth + " = " + newForm);
	    		results.add(new Effect(newForm));
	    	}
	    	
	    	
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return results;
	}

	private HashSet<String> explored = new HashSet<String>();
	
	@SuppressWarnings("unchecked")
	private LinkedHashSet<Effect> rec_pair_acc(Effect sourceEffect,
			Effect targetEffect, String KB, boolean Loop) {
		LinkedHashSet<Effect> resultsList = new LinkedHashSet<Effect>();
	
		if(sourceEffect.eval(targetEffect,KB)){
			resultsList.add(sourceEffect);
			resultsList.add(targetEffect);
			//System.err.println("Eval sucess" + sourceEffect);
			return resultsList;
		}else{
			//System.err.println("Eval of " + sourceEffect + " and " + targetEffect + " with " + KB + " failed!");
		}
		
		LinkedHashSet<Effect> nextLevelList = new LinkedHashSet<Effect>();
		try{
			// Compute the next group of smaller clauses
			Logic logic = new ClassicalLogicS();
	        formula = (Formula) logic.createExpression(sourceEffect.getFormula());
	        Formula result = ClassicalLogicS.Utilities.conjunctiveForm(formula, true);
	        DefaultClausalFactory myFacts = new DefaultClausalFactory();
	        ClausalSet myClauses =  myFacts.asClausalSet(result);
	        if(myClauses.size() <= 1) {	resultsList.add(targetEffect); return resultsList;} // return the target and still no matches
	    	for(Clause cl :(Set<Clause>) myClauses){	// For each clause in current effect, remove an element
	    		String newForm = "";
	    		for(Clause cls :(Set<Clause>) myClauses){
	    			if(cls.toString().compareTo(cl.toString()) != 0){
	    				//System.err.println("Working on " + cls.toString());
	    				newForm += cls.toString() + " & ";
	    				newForm = newForm.replaceAll("\\[", "(");
	    				newForm = newForm.replaceAll("\\]",")");
	    				newForm = newForm.replaceAll(",", "|");
	    			}
	    		}
	    		if(newForm.length() > 3) newForm = newForm.substring(0, newForm.length() - 3); // Remove final &
	    		Effect newEffect = new Effect(newForm);
	    		if(newEffect.eval(targetEffect,KB))
	    			resultsList.add(newEffect);
	    		else{
	    			//System.err.println("Adding " + newEffect + " to nextLevellist");
	    			nextLevelList.add(newEffect);
	    		}
	    	}
		}catch(Exception e){
			logger.error("Error shrinking effect in rec_pair_acc():"+ e);
			e.printStackTrace();
		}
		if(resultsList.size() < 1 && Loop){
//			for(Effect reduced: nextLevelList){
//				System.err.println("Looping false " + reduced);
//				resultsList.addAll(rec_pair_acc(reduced, targetEffect, KB, false));
//			}
		
//			if(resultsList.size() < 1){
				for(Effect newSource: nextLevelList){
					if(!explored.contains(newSource.getFormula())){
						if(numClauses(newSource) == currentDepth){
							//System.err.println("Trying new source(" +numClauses(newSource)+ "): "  + newSource);
							explored.add(newSource.getFormula());
							resultsList.addAll(rec_pair_acc(newSource, targetEffect, KB, distro));
						}
					}
					
				}
//			}

		}
		return resultsList;
	}
	
	private int numClauses(Effect eff){
		try{
 			Logic logic = new ClassicalLogicS();
 			if(eff.getFormula().length() < 1) return 0;
	        formula = (Formula) logic.createExpression(eff.getFormula());
	        Formula result = ClassicalLogicS.Utilities.conjunctiveForm(formula, true);
	        DefaultClausalFactory myFacts = new DefaultClausalFactory();
	        ClausalSet myClauses =  myFacts.asClausalSet(result);
	        eff.clauses = myClauses.size(); 
		}catch(Exception e){
			e.printStackTrace();
		}		
		return eff.clauses;
	}
	
	
//	try {
//		
//		Logic logic = new ClassicalLogicS();
//		Formula formula = (Formula) logic.createExpression(e5.formulaText);
//		Formula result = ClassicalLogicS.Utilities.conjunctiveForm(formula, true);
//		DefaultClausalFactory myFacts = new DefaultClausalFactory();
//		ClausalSet myClauses =  myFacts.asClausalSet(result);
//		System.out.println("Output:" + myClauses + ";" + result);
//	} catch (IllegalArgumentException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} catch (ParseException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
	
//	@SuppressWarnings("unchecked")
//	private LinkedHashSet<Effect> getMaxSubsets(Effect sourceEffect, Effect targetEffect, String KB){
//		// Get num elements in source effect
//		LinkedHashSet<Effect> newEffects = new LinkedHashSet<Effect>();
//		try{
//			Logic logic = new ClassicalLogicS();
//	        formula = (Formula) logic.createExpression(sourceEffect.getFormula());
//	        Formula result = ClassicalLogicS.Utilities.conjunctiveForm(formula, true);
//	        DefaultClausalFactory myFacts = new DefaultClausalFactory();
//	        ClausalSet myClauses =  myFacts.asClausalSet(result);
//        	for(Clause cl :(Set<Clause>) myClauses){
//        		String newForm = "";
//        		for(Clause cls :(Set<Clause>) myClauses){
//        			if(cls.toString().compareTo(cl.toString()) != 0){
//        				//System.err.println("Working on " + cls.toString());
//        				newForm += cls.toString() + " & ";
//        				newForm = newForm.replaceAll("\\[", "(");
//        				newForm = newForm.replaceAll("\\]",")");
//        				newForm = newForm.replaceAll(",", "|");
//        			}
//        		}
//        		if(newForm.length() > 3) newForm = newForm.substring(0, newForm.length() - 3); // Remove final &
//        		Effect newEffect = new Effect(newForm);
//        		if(newEffect.eval(targetEffect, KB)) newEffects.add(newEffect);
//
//	        }
//		}catch(Exception e){
//			logger.error("Error shrinking effect in getMaxSubsets():"+ e);
//			e.printStackTrace();
//		}
//		
//		return newEffects;
//	}

	
}
