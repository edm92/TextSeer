package examples;

import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedList;

import a.e.SIM_RESULT;
import a.e.WORD_MATCH_STRENGTH;
import be.fnord.util.functions.Poset.Pair;
import be.fnord.util.functions.language.Sentences.ALG;
import be.fnord.util.processModel.util.Distance;

/**
 * The following class demonstrates the use of the NLP similarity measure.
 * @author Evan Morrison edm92@uowmail.edu.au http://www.fnord.be Apache
 *         License, Version 2.0, Apache License Version 2.0, January 2004
 *         http://www.apache.org/licenses/
 *
 */
public class ProcessDistanceMeasureExample {
	/**
	 * Java doc
	 * @param args test
	 */
	public static void main(final String[] args) {
		// ///////////////////////////////////////
		/* Initialize core app. */
		new a.e(); //
		// ///////////////////////////////////////
		// // Real start of program below /////
		// ///////////////////////////////////////
		/**
		 * Set Magic Numbers :D
		 */
		a.e.PENALTY_FOR_EXTRA_TRACE = 0.1; // .1;
		a.e.PENALTY_FOR_EXTRA_LETTER = 1.1; // .1;
		// a.e.WHOLE_NUMBER = false;

		ProcessDistanceMeasureExample pc = new ProcessDistanceMeasureExample();
		String directory = "models/sim";

		File[] files = pc.finder(directory); // load the entire models root
		LinkedList<Pair<String, String>> compare = pc.makeComparable(files,
				directory);
		Pair<String, String> mostSim = null;
		double best = 1000;

		for (Pair<String, String> p : compare) {
			// a.e.err("Trying " + p.getFirst() + " and " + p.getSecond());
			Distance dc = new Distance();
			double distance = dc.computeDistance(p.getFirst(), p.getSecond(),
					ALG.SIMPLE_COMPARE_ALG, WORD_MATCH_STRENGTH.EXACT,
					SIM_RESULT.WHOLE_NUMBER);
			if (distance < best && distance != 0) {
				best = distance;
				mostSim = p;
			}
			a.e.println("Compared: " + p.toString() + " resulting in simscore="
					+ distance);
		}

		a.e.println("-------------------");
		a.e.println("Best match " + mostSim + " scored " + best);

	}

	private File[] finder(String dirName) {
		File dir = new File(dirName);

		return dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".bpmn20.xml");
			}
		});

	}

	private Pair<String, String> makeAlph(String myNewString, String myOldString) {
		Pair<String, String> p = null;
		if (myNewString.compareTo(myOldString) < 0) {
			p = new Pair<String, String>(myNewString, myOldString);
		} else {
			p = new Pair<String, String>(myOldString, myNewString);
		}

		return p;

	}

	private LinkedList<Pair<String, String>> makeComparable(File[] files,
			String dir) {
		LinkedList<Pair<String, String>> result = new LinkedList<Pair<String, String>>();

		for (File f : files) {
			for (File g : files) {
				String myNewString = dir + "/" + g.getName();
				String myOldString = dir + "/" + f.getName();
				if (myNewString.compareTo(myOldString) != 0) {
					Pair<String, String> p = makeAlph(myNewString, myOldString);
					if (!result.contains(p)) {
						result.add(p);
					}
				}
			}
		}
		// for(Pair<String,String> p : result)
		// a.e.println(p.toString());
		return result;
	}

}
