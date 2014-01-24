
package be.fnord.util.logic;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.merriampark.Gilleland.CombinationGenerator;

import be.fnord.util.functions.OCP.PartitionListElement;

import orbital.logic.imp.Formula;
import orbital.logic.imp.Logic;
//import orbital.logic.sign.ParseException;
import orbital.moon.logic.resolution.ClausalSet;
import orbital.moon.logic.resolution.Clause;
import orbital.moon.logic.resolution.DefaultClausalFactory;
import be.fnord.util.logic.WFF;
import be.fnord.util.processModel.Trace;
import be.fnord.util.processModel.Vertex;

/**
 * The following class is used for accumulation. I have tried to keep most
 * variables transient for distributed processing.
 * 
 * @author edm92
 */
public class Accumulate implements Serializable {

	private static final long serialVersionUID = 1L;
	private transient Formula formula;
	protected transient static Logger logger = Logger.getLogger("WFFFunction");
	private int currentDepth = 0;
	private int targetClauses = 0;

	boolean distro = false;

	// ///////////////////////////////////////////////////////
	// Below are functions for accumulating along a trace ///
	// ///////////////////////////////////////////////////////

	public LinkedHashSet<WFF> trace_acc(Trace src, String kb) {

		LinkedHashSet<WFF> currentEff = new LinkedHashSet<WFF>();
		currentEff.add(new WFF(a.e.EMPTY_EFFECT));
		for (Vertex v : src.getNodes()) {
			LinkedHashSet<WFF> intEff = new LinkedHashSet<WFF>();
			intEff.addAll(currentEff);

			for (WFF ce : currentEff) {
				if (!v.getWFF().isEmpty()) {
					intEff.remove(ce);
					Accumulate acc = new Accumulate(); // Needed because of
														// semi-static variables
					intEff.addAll(acc.pairwise_acc(ce, v.getWFF(), kb, true));

				}
			}
			currentEff = new LinkedHashSet<WFF>();
			currentEff.addAll(intEff);
		}

		return currentEff;
	}

//	// ///////////////////////////////////////////////////////
//	// Below are functions for accumulating along a trace  for Semantic Tracing///
//	// ///////////////////////////////////////////////////////
//
//	public LinkedHashSet<WFF> trace_acc_4st(Trace src, String kb) {
//
//		LinkedHashSet<WFF> currentEff = new LinkedHashSet<WFF>();
//		currentEff.add(new WFF(a.e.EMPTY_EFFECT));
//		for (Vertex v : src.getNodes()) {
//			LinkedHashSet<WFF> intEff = new LinkedHashSet<WFF>();
//			intEff.addAll(currentEff);
//
//			for (WFF ce : currentEff) {
//				if (!v.getWFF().isEmpty()) {
//					intEff.remove(ce);
//					Accumulate acc = new Accumulate(); // Needed because of
//														// semi-static variables
//					intEff.addAll(acc.pairwise_acc(ce, v.getWFF(), kb, true));
//
//				}
//			}
//
//			currentEff = new LinkedHashSet<WFF>();
//			currentEff.addAll(intEff);
//			a.e.println("CumulativeEffect = " + intEff); // for SematicTracing
//															// test
//			System.out.println("Begin test for spliting");
//
//			String tmp_intEff;
//			tmp_intEff = intEff.toString().replaceAll("\\[", "").replaceAll("\\]","");
//			String[] strList = tmp_intEff.split(",");
//			for(String r : strList){
//				System.out.println(r);
//				//assign the value to a vertex in the Graph
//				
//			}
//			System.out.println("End test for spliting");
//			System.out.println();
//		}
//
//		return currentEff;
//	}

	// //////////////////////////////////////////////
	// Below are pairwise accumulation functions///
	// //////////////////////////////////////////////

	public LinkedHashSet<WFF> pairwise_acc(
		WFF source, WFF target, String KB, boolean distrobute) {

		distro = distrobute;
		return pairwise_acc(source, target, KB);
	}

	private LinkedHashSet<WFF> pairwise_acc(WFF source, WFF target, String KB) {

		currentDepth = numClauses(source) - 1;
		targetClauses = numClauses(target);
		LinkedHashSet<WFF> results = pairwise_acc_old(source, target, KB);
		// System.err.println("Explored: " + explored.size() + " WFFs");
		return results;
	}

	/**
	 * Do pairwise accumulation and return the set of maximally consistent WFFs
	 * that result from accumulation
	 * 
	 * @param source
	 *            starting WFF
	 * @param target
	 *            new WFF to accumulate with
	 * @param KB
	 *            existing knowledgebase
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private LinkedHashSet<WFF> pairwise_acc_old(
		WFF source, WFF target, String KB) {

		if (!source.eval(target, KB)) { // Not sat so compute maxSubsets
			try {
				LinkedHashSet<WFF> results = new LinkedHashSet<WFF>();
				LinkedHashSet<String> cleanedResults =
					new LinkedHashSet<String>();
				do {
					// System.err.println("depth : " + currentDepth);
					Set<WFF> correctDepthResults =
						makeCorrectDepthResults(source);
					// System.err.println("Partitions: " + correctDepthResults);
					for (WFF newResult : correctDepthResults) {
						// System.out.println("Trying " + newResult);
						LinkedHashSet<WFF> returnPairResults;
						returnPairResults =
							rec_pair_acc(newResult, target, KB, distro);

						// for(WFF e : returnPairResults)
						// System.out.println(currentDepth + ") Returned on " +
						// e );
						results.addAll(returnPairResults);
					}
					if (results != null && results.size() > 0)
						break;
					currentDepth--;
					if (currentDepth < 0)
						break;
				}
				while (currentDepth > 0);
				// for(WFF e : results) System.err.println(currentDepth +
				// ") Working on " + e);
				LinkedHashSet<WFF> newResults = new LinkedHashSet<WFF>();
				int maxSize = 0;
				for (WFF e : results) {
					int number = numClauses(e);
					if (number > maxSize)
						maxSize = number;
				}
				maxSize += targetClauses;
				for (WFF e : results) {
					Logic logic = new ClassicalLogicS();
					// System.err.println("Trying " + e.getFormula());
					formula =
						(Formula) logic.createExpression("(" + e.getFormula() +
							") & (" + target.getFormula() + ")");
					Formula result =
						ClassicalLogicS.Utilities.conjunctiveForm(formula, true);
					DefaultClausalFactory myFacts = new DefaultClausalFactory();
					ClausalSet myClauses = myFacts.asClausalSet(result);
					if (myClauses.size() < maxSize)
						continue;
					String newForm = "";
					for (Clause cls : (Set<Clause>) myClauses) {
						newForm += cls.toString() + " & ";
						newForm = newForm.replaceAll("\\[", "(");
						newForm = newForm.replaceAll("\\]", ")");
						newForm = newForm.replaceAll(",", "|");
					}
					if (newForm.length() > 3)
						newForm = newForm.substring(0, newForm.length() - 3); // Remove
																				// final
																				// &
					// WFF resultWFF = new WFF(newForm);
					cleanedResults.add(newForm);
					// newResults.add(resultWFF);

				}
				for (String newEff : cleanedResults)
					newResults.add(new WFF(newEff));
				if (newResults.size() < 1)
					newResults.add(target);
				return newResults;
			}
			catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		else {
			WFF resultingScenario =
				new WFF(source.getEffect(target.getFormula(), KB));
			LinkedHashSet<WFF> returnList = new LinkedHashSet<WFF>();
			returnList.add(resultingScenario);
			return returnList;
		}

	}

	@SuppressWarnings("unchecked")
	private Set<WFF> makeCorrectDepthResults(WFF source) {

		HashSet<WFF> results = new HashSet<WFF>();
		try {
			Logic logic = new ClassicalLogicS();
			if (source.getFormula().length() < 1) {
				return results;
			};
			formula = (Formula) logic.createExpression(source.getFormula());
			Formula result =
				ClassicalLogicS.Utilities.conjunctiveForm(formula, true);
			DefaultClausalFactory myFacts = new DefaultClausalFactory();
			ClausalSet myClauses = myFacts.asClausalSet(result);

			PartitionListElement<Clause> reducedWFF =
				new PartitionListElement<Clause>();
			for (Clause cls : (Set<Clause>) myClauses) {
				reducedWFF.add(cls);
			}

			int[] indices;
			CombinationGenerator x =
				new CombinationGenerator(reducedWFF.size(), currentDepth);
			StringBuffer combination;
			HashSet<String> myWFF = new HashSet<String>();
			while (x.hasMore()) {
				combination = new StringBuffer();
				indices = x.getNext();
				for (int i = 0; i < indices.length; i++) {
					combination.append(reducedWFF.get(indices[i]));
				}
				String mynewWFF = combination.toString();
				myWFF.add(mynewWFF.replaceAll("\\]\\[", "\\],\\["));
			}

			for (String formString : myWFF) {
				String newForm = formString;
				newForm = newForm.replaceAll("\\[", "(");
				newForm = newForm.replaceAll("\\],", ") & ");
				newForm = newForm.replaceAll("\\]", ")");
				newForm = newForm.replaceAll(",", " |");

				// System.out.println("Made new WFF at depth " + currentDepth +
				// " = " + newForm);
				results.add(new WFF(newForm));
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return results;
	}

	private HashSet<String> explored = new HashSet<String>();

	@SuppressWarnings("unchecked")
	private LinkedHashSet<WFF> rec_pair_acc(
		WFF sourceWFF, WFF targetWFF, String KB, boolean Loop) {

		LinkedHashSet<WFF> resultsList = new LinkedHashSet<WFF>();

		if (sourceWFF.eval(targetWFF, KB)) {
			resultsList.add(sourceWFF);
			resultsList.add(targetWFF);
			// System.err.println("Eval sucess" + sourceWFF);
			return resultsList;
		}
		else {
			// System.err.println("Eval of " + sourceWFF + " and " + targetWFF +
			// " with " + KB + " failed!");
		}

		LinkedHashSet<WFF> nextLevelList = new LinkedHashSet<WFF>();
		try {
			// Compute the next group of smaller clauses
			Logic logic = new ClassicalLogicS();
			formula = (Formula) logic.createExpression(sourceWFF.getFormula());
			Formula result =
				ClassicalLogicS.Utilities.conjunctiveForm(formula, true);
			DefaultClausalFactory myFacts = new DefaultClausalFactory();
			ClausalSet myClauses = myFacts.asClausalSet(result);
			if (myClauses.size() <= 1) {
				resultsList.add(targetWFF);
				return resultsList;
			} // return the target and still no matches
			for (Clause cl : (Set<Clause>) myClauses) { // For each clause in
														// current WFF, remove
														// an element
				String newForm = "";
				for (Clause cls : (Set<Clause>) myClauses) {
					if (cls.toString().compareTo(cl.toString()) != 0) {
						// System.err.println("Working on " + cls.toString());
						newForm += cls.toString() + " & ";
						newForm = newForm.replaceAll("\\[", "(");
						newForm = newForm.replaceAll("\\]", ")");
						newForm = newForm.replaceAll(",", "|");
					}
				}
				if (newForm.length() > 3)
					newForm = newForm.substring(0, newForm.length() - 3); // Remove
																			// final
																			// &
				WFF newWFF = new WFF(newForm);
				if (newWFF.eval(targetWFF, KB))
					resultsList.add(newWFF);
				else {
					// System.err.println("Adding " + newWFF +
					// " to nextLevellist");
					nextLevelList.add(newWFF);
				}
			}
		}
		catch (Exception e) {
			logger.error("Error shrinking WFF in rec_pair_acc():" + e);
			e.printStackTrace();
		}
		if (resultsList.size() < 1 && Loop) {
			// for(WFF reduced: nextLevelList){
			// System.err.println("Looping false " + reduced);
			// resultsList.addAll(rec_pair_acc(reduced, targetWFF, KB, false));
			// }

			// if(resultsList.size() < 1){
			for (WFF newSource : nextLevelList) {
				if (!explored.contains(newSource.getFormula())) {
					if (numClauses(newSource) == currentDepth) {
						// System.err.println("Trying new source("
						// +numClauses(newSource)+ "): " + newSource);
						explored.add(newSource.getFormula());
						resultsList.addAll(rec_pair_acc(
							newSource, targetWFF, KB, distro));
					}
				}

			}
			// }

		}
		return resultsList;
	}

	private int numClauses(WFF eff) {

		try {
			Logic logic = new ClassicalLogicS();
			if (eff.getFormula().length() < 1)
				return 0;
			formula = (Formula) logic.createExpression(eff.getFormula());
			Formula result =
				ClassicalLogicS.Utilities.conjunctiveForm(formula, true);
			DefaultClausalFactory myFacts = new DefaultClausalFactory();
			ClausalSet myClauses = myFacts.asClausalSet(result);
			eff.clauses = myClauses.size();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return eff.clauses;
	}

	// try {
	//
	// Logic logic = new ClassicalLogicS();
	// Formula formula = (Formula) logic.createExpression(e5.formulaText);
	// Formula result = ClassicalLogicS.Utilities.conjunctiveForm(formula,
	// true);
	// DefaultClausalFactory myFacts = new DefaultClausalFactory();
	// ClausalSet myClauses = myFacts.asClausalSet(result);
	// System.out.println("Output:" + myClauses + ";" + result);
	// } catch (IllegalArgumentException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (ParseException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }

	// @SuppressWarnings("unchecked")
	// private LinkedHashSet<WFF> getMaxSubsets(WFF sourceWFF, WFF targetWFF,
	// String KB){
	// // Get num elements in source WFF
	// LinkedHashSet<WFF> newWFFs = new LinkedHashSet<WFF>();
	// try{
	// Logic logic = new ClassicalLogicS();
	// formula = (Formula) logic.createExpression(sourceWFF.getFormula());
	// Formula result = ClassicalLogicS.Utilities.conjunctiveForm(formula,
	// true);
	// DefaultClausalFactory myFacts = new DefaultClausalFactory();
	// ClausalSet myClauses = myFacts.asClausalSet(result);
	// for(Clause cl :(Set<Clause>) myClauses){
	// String newForm = "";
	// for(Clause cls :(Set<Clause>) myClauses){
	// if(cls.toString().compareTo(cl.toString()) != 0){
	// //System.err.println("Working on " + cls.toString());
	// newForm += cls.toString() + " & ";
	// newForm = newForm.replaceAll("\\[", "(");
	// newForm = newForm.replaceAll("\\]",")");
	// newForm = newForm.replaceAll(",", "|");
	// }
	// }
	// if(newForm.length() > 3) newForm = newForm.substring(0, newForm.length()
	// - 3); // Remove final &
	// WFF newWFF = new WFF(newForm);
	// if(newWFF.eval(targetWFF, KB)) newWFFs.add(newWFF);
	//
	// }
	// }catch(Exception e){
	// logger.error("Error shrinking WFF in getMaxSubsets():"+ e);
	// e.printStackTrace();
	// }
	//
	// return newWFFs;
	// }

}
