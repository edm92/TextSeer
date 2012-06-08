package au.edu.dsl.dlab.processtools.scenario;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import orbital.logic.imp.Formula;
import orbital.logic.imp.Logic;
import orbital.moon.logic.resolution.ClausalSet;
import orbital.moon.logic.resolution.Clause;
import orbital.moon.logic.resolution.DefaultClausalFactory;

import org.apache.log4j.Logger;
import org.jgrapht.GraphPath;

import config.Settings;
import au.edu.dsl.dlab.processtools.Edge;
import au.edu.dsl.dlab.processtools.Effect;
import au.edu.dsl.dlab.processtools.Graph;
import au.edu.dsl.dlab.processtools.Vertex;
import au.edu.dsl.dlab.processtools.logic.Accumulate;
import au.edu.dsl.dlab.processtools.logic.ClassicalLogicS;
import au.edu.dsl.dlab.processtools.parser.ModelLoader;
import au.edu.dsl.dlab.processtools.parser.effectReadWrite;


public class ComputeScenario<T extends Vertex, V extends Edge> {
	protected transient static Logger logger = Logger.getLogger("ComputeScenario");
	public static boolean CLEAN_OVER_ENTAILED_EFFECTS = true;	// If one effect scenario entails another in the same process then remove it.
	private transient static Formula formula;

	
	public static HashSet<Effect> makeCleanSave(Graph<Vertex, Edge> inputGraph, String inputKnowledge){
		// If we've already computed the answer then don't worry, be happy :D
		
		if(!Settings.RECOMPUTE_SCENARIOS) 
		if(inputGraph.effects != null && inputGraph.effects.size() >0 ) { saveGraph(inputGraph); return inputGraph.effects; };
		//System.err.println("Recompute Scenarios: " + Settings.RECOMPUTE_SCENARIOS);
		
		// Fix the input knowledgebase to be representative of the graph
		String tst = inputGraph.fixKnowledge(inputKnowledge);

		
		// Otherwise lets compute properly.
		ComputeScenario<Vertex,Edge> scenario = new ComputeScenario<Vertex,Edge>();
		HashSet<Effect> finalEffects = scenario.compute(inputGraph, tst);
		finalEffects = cleanScenarios(inputGraph, finalEffects);
		// Save to graph effects
		inputGraph.effects = finalEffects;
		saveGraph(inputGraph);
		Vertex.TO_STRING_WITH_EFFECTS = true;
		//System.err.println("returning" + finalEffects);
		return finalEffects;
	}
	
	
	private static void saveGraph(Graph<Vertex, Edge> inputGraph) {
		if(ModelLoader.INUSE && inputGraph.filename != null && inputGraph.filename.length() > 0 ){
			//System.err.println("Saving");
			effectReadWrite.writeEffect(Settings._repository + Settings.s + inputGraph.filename + ".acc" , inputGraph.effects);
		}
		
	}

	@SuppressWarnings("unchecked")
	public static HashSet<Effect> cleanScenarios(Graph<Vertex,Edge> inputGraph, HashSet<Effect> cleanMe){
		HashSet<Effect> cleaned = new HashSet<Effect>();
		HashSet<String> clean = new HashSet<String>();
		HashSet<Effect> removed = new HashSet<Effect>();
		HashSet<String> remove = new HashSet<String>();
		
		// Remove eeee from effects
		for(Effect e : cleanMe){
			try{
				Logic logic = new ClassicalLogicS();
				formula = (Formula) logic.createExpression( e.getFormula());
		        Formula result = ClassicalLogicS.Utilities.conjunctiveForm(formula, true);
		        DefaultClausalFactory myFacts = new DefaultClausalFactory();
		        ClausalSet myClauses =  myFacts.asClausalSet(result);
	        	String newForm = "";
	        	for(Clause cls :(Set<Clause>) myClauses){
	        		if(cls.toString().contains("eeee")) continue;
					newForm += cls.toString() + " & ";
					newForm = newForm.replaceAll("\\[", "(");
					newForm = newForm.replaceAll("\\]",")");
					newForm = newForm.replaceAll(",", "|");
					
				}
	        	if(newForm.length() > 3) newForm = newForm.substring(0, newForm.length() -3);
	        	if(newForm.length() > 0){
	        		cleaned.add(new Effect(newForm));
	        		//System.err.println("Adding " + newForm);
	        	}
	        	removed.add(e);
			}catch(Exception error){
				error.printStackTrace();
			}
		}
		for(Effect e: removed){
			cleanMe.remove(e);
		}
		for(Effect e: cleaned){
			cleanMe.add(e);
		}
		removed = new HashSet<Effect>();
		cleaned = new HashSet<Effect>();
		
		// Clean duplicate entailing effects
		if(CLEAN_OVER_ENTAILED_EFFECTS){
			for(Effect effect: cleanMe){
				for(Effect testAgainst: cleanMe){
					if(!remove.contains(effect.getID()) && !remove.contains(testAgainst.getID()))
					if(effect.getID().compareTo(testAgainst.getID()) != 0){
						if(effect.entails(testAgainst) && testAgainst.entails(effect)){
							remove.add(testAgainst.getID());
							removed.add(testAgainst);
						}
					}
				}
			}
			for(Effect effect: removed){
				cleanMe.remove(effect);
			}
		}
		
		// Try to remove brackets and duplicate characters
		try{
			for(Effect effect: cleanMe){
				Logic logic = new ClassicalLogicS();
				if(effect.getFormula().length() < 1) continue;
		        formula = (Formula) logic.createExpression(effect.getFormula());
		        String f = formula.toString();
		        String[] chucks = f.split("&");
		        HashSet<String> dupRem = new HashSet<String>();
		        for(String chunk : chucks){
		        	dupRem.add(chunk.trim());
		        }
		        
		        if(dupRem.size() > 1){
		        	String newForm = "";
		        	for(String ele : dupRem)
		        		newForm += ele.trim() + " & ";
		        		        		
	        		if(newForm.length() > 3) newForm = newForm.substring(0, newForm.length() - 3).trim(); // Remove final &
	        		Effect newEffect = new Effect(newForm);
	        		if(!clean.contains(formulaHash(newEffect.getFormula())))
	        		if(newEffect.entails(effect)){
	        			cleaned.add(newEffect);
	        			clean.add(formulaHash(newEffect.getFormula()));
	        		}else{
	        			cleaned.add(effect);
	        			clean.add(formulaHash(effect.getFormula()));
	        		}
		        	
		        }
			}
		}catch(Exception e){
			logger.error("Error shrinking effect in getMaxSubsets():"+ e);
			e.printStackTrace();
		}
		
		removed = new HashSet<Effect>();
		// Remap symbols to original
		for(Effect cleanToRevert : cleaned){
			String revertedEffect = inputGraph.revertResults(cleanToRevert.getFormula());
			//System.err.println("Reverting: " + cleanToRevert.getFormula() + " to " + revertedEffect);
			removed.add(new Effect(revertedEffect));
		}
		
		return removed;
	}
	
	private static String formulaHash(String input){
		char[] content = input.toCharArray();
    	java.util.Arrays.sort(content);
    	String sorted = new String(content);
    	return sorted;
	}
	
	
	public HashSet<Effect> compute(Graph<T,V> g, String KB){
		if(g.trueStart == null || g.trueEnd == null || g.trueStart == g.trueEnd){
			g.cleanup(false);	// Try to fix
		}
		if(g.trueStart == null || g.trueEnd == null || g.trueStart == g.trueEnd){
			logger.error("Start and/or end node not found -  make sure you added trueStart and trueEnd to your process");
			return new HashSet<Effect>();
		}
		Accumulate acc = new Accumulate();
		HashSet<Effect> processEffectScenarios = new HashSet<Effect>();
		for(GraphPath<T, V> gp : g.getPaths()){
        	T s = gp.getStartVertex();
        	HashSet<Effect> effects = new HashSet<Effect>();
        	effects.add(((Vertex)s).getEffect());
        	String currentPath = s + " -> "; 
        	for(V e : gp.getEdgeList()){
        		currentPath += "(" + e.getEffect() + "), "+ g.getEdgeTarget(e) + "(" + g.getEdgeTarget(e).getEffect() +") -> ";
        		 
        		HashSet<Effect> effectbuff = new HashSet<Effect>();
        		for(Effect se : effects){
        			HashSet<Effect> neweffects = new HashSet<Effect>();
        			neweffects = acc.pairwise_acc(se, ((Edge)e).getEffect() , KB , true);
        			if(neweffects == null) continue;
        			for(Effect ne : neweffects)
        				effectbuff.add(ne);
        		}
        		effects = new HashSet<Effect>(); for(Effect ce : effectbuff) effects.add(ce); effectbuff = new HashSet<Effect>();
        		for(Effect se : effects){
        			HashSet<Effect> neweffects = new HashSet<Effect>();
        			neweffects = acc.pairwise_acc(se, ((Vertex)g.getEdgeTarget(e)).getEffect(), KB, true);
        			for(Effect ne : neweffects)
        				effectbuff.add(ne);
        		}
        		effects = new HashSet<Effect>(); for(Effect ce : effectbuff) effects.add(ce); 
        	}
        	currentPath = currentPath.substring(0, currentPath.length() - 3);
//        	System.out.println("Path:" + currentPath);
//        	for(Effect effectScenario : effects){
//        		System.out.println("Effect of Path:" + effectScenario);
//        	}
        	for(Effect cpme : effects)
        		processEffectScenarios.add(cpme);
        	
        }
		
		// Cleanup
		LinkedList<Effect> removeList = new LinkedList<Effect>();
		for(Effect cleanme: processEffectScenarios){
			if(removeList.contains(cleanme)) continue;
			for(Effect cleanmetoo : processEffectScenarios){
				if(cleanme != cleanmetoo){
					if(cleanme.entails(cleanmetoo) && cleanmetoo.entails(cleanme)) {if(!removeList.contains(cleanmetoo)) removeList.add(cleanme); break;} 
				}
			}
		}
		
		for(Effect removals : removeList){
			processEffectScenarios.remove(removals);
		}
			
		
		return processEffectScenarios;
	}
}
