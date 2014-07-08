package be.fnord.util.processModel;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.UUID;

import be.fnord.util.QUAL.JSONEFFECT;
import be.fnord.util.logic.WFF;
import be.fnord.util.processModel.util.GraphLoader;

import com.google.gson.Gson;

/**
 * @author Evan Morrison edm92@uowmail.edu.au http://www.fnord.be Apache
 *         License, Version 2.0, Apache License Version 2.0, January 2004
 *         http://www.apache.org/licenses/
 */
public class Vertex extends Graph<Vertex, Edge> {
	public enum _GTYPE {
		AAND, AND, XOR
	}

	public enum _TYPE {
		GATEWAY, NODE, PLACE, SUBPROCESS, TRANSISTION
	}

	public static boolean ADD_RANDOM_WFFS = false;
	public static int MAX_RANDOM_PREDICATE_RANGE = 3;
	public static int MAX_RANDOM_PREDICATES = 5;
	public static String RANDOM_RANGE[] = { "a", "b", "c", "d", "e", "f", "g",
			"h", "i", "j", "k", "l", "m", "n" };
	private static final long serialVersionUID = 1L;
	public static boolean TO_STRING_WITH_WFFS = false;

	/**
	 * This function is a utility function used to generate the random range.
	 */
	public static void CREATE_RANDOM_RANGE() {
		RANDOM_RANGE[0] = "a";
		RANDOM_RANGE = new String[17577];
		for (int i = 0; i < 26; i++) {
			for (int j = 0; j < 26; j++) {
				for (int k = 0; k < 26; k++) {
					RANDOM_RANGE[26 * 26 * i + 26 * j + k] = ""
							+ Character.toChars(i + 97)[0]
							+ Character.toChars(j + 97)[0]
							+ Character.toChars(k + 97)[0];
					// System.out.println((26*26*i) + (26*j) + k + " = " +
					// RANDOM_RANGE[(26*26*i) + (26*j) + k] );
				}

			}
		}
	}

	// public WFF getWFF(){immWFF.setFormula(WFF); return immWFF;};
	public static Vertex createNew() {
		return new Vertex();
	}

	/**
	 * This function generates a random WFF scenario based on the starting
	 * random seeds.
	 *
	 * @return A new WFF scenario
	 */
	public static String generateWFFs() {
		boolean used[] = new boolean[MAX_RANDOM_PREDICATES]; // Need better
																// implementation
																// of this
		String returnWFF = new String();
		Random randomGenerator = new Random();
		String predicate = "";
		int numPredicates = randomGenerator.nextInt(MAX_RANDOM_PREDICATES);
		for (int i = 0; i < numPredicates; i++) {
			int ChoosePredicate = randomGenerator
					.nextInt(MAX_RANDOM_PREDICATE_RANGE);
			if (!used[ChoosePredicate]) {
				used[ChoosePredicate] = true;
				predicate += RANDOM_RANGE[ChoosePredicate] + " & ";
			}
		}
		if (predicate.length() > 3) {
			predicate = predicate.substring(0, predicate.length() - 3); // Remove
																		// final
																		// &
			returnWFF += predicate + " & ";
		}
		if (returnWFF.length() > 3) {
			returnWFF = returnWFF.substring(0, returnWFF.length() - 3);
		}

		return returnWFF;

	}

	public LinkedList<String> boundaryRefs = new LinkedList<String>();
	public Vertex corresponding = null;

	// public void makeGate() { makeXORGate(); }
	// public void makeActivity() { type = _TYPE.NODE; }
	// public void makeXORGate() { type = _TYPE.GATEWAY; gtype = _GTYPE.XOR; }
	// public void makeANDGate() { type = _TYPE.GATEWAY; gtype = _GTYPE.AND; }
	// public void makeAANDGate() { type = _TYPE.GATEWAY; gtype = _GTYPE.AAND; }
	public Vertex CorrespondingVertex = null;
	public LinkedHashSet<WFF> cumWFF = new LinkedHashSet<WFF>(); // Include
																	// cumulative
																	// effects
	// public String name;
	// public String id;
	// private String WFF = "";
	// private WFF immWFF = new WFF();
	// public _TYPE type;
	public _GTYPE gtype;
	public int height = 0;

	public String id = "";
	private UUID ID;

	private WFF immWFF = new WFF();

	public boolean isAND = false;

	public boolean isGateway = false;

	public boolean isJoin = false;

	public boolean isOR = false;

	public boolean isSplit = false;

	public boolean isSubprocess = false;

	public boolean isSubstructural = false;

	public boolean isTrace = false;

	public boolean isXOR = false;

	;

	public JSONEFFECT jsEFF = null; // Extended effects

	public String name = "";

	public int type = 0;
	private String WFF = "";

	public int width = 0;

	;

	public int x = 0;

	;
	public int y = 0;

	public Vertex() {
		this(UUID.randomUUID().toString());
	}

	public Vertex(String _name) {
		this(_name, GraphLoader.Task);
	}

	public Vertex(String _name, int _type) {
		super();
		name = _name;
		type = _type;
	}

	public void addWFF(String _WFF) {
		if (WFF.length() > 0) {
			WFF = "( " + WFF + " ) " + a.e.AND + " ( " + _WFF + " )";
		} else {
			setWFF(_WFF);
		}
	}

	public Vertex getCorresponding() {
		return corresponding;
	}

	// public void setCorresponding(Vertex input){ CorrespondingVertex = input;
	// input.CorrespondingVertex = this; }
	@Override
	public String getID() {
		return ID.toString();
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return GraphLoader.getType(type);
	}

	;

	public WFF getWFF() {
		immWFF.setFormula(WFF);
		return immWFF;
	}

	public boolean isAND() {
		return gtype == _GTYPE.AND;
	}

	public boolean isSubprocess() {
		return isSubprocess;
	}

	// public boolean isGate(){return type == _TYPE.GATEWAY;}
	// public boolean isActivity(){return type == _TYPE.NODE;}
	public boolean isXOR() {
		return gtype == _GTYPE.XOR;
	}

	// TODO implement this at some point
	public Vertex makeCopy() {
		return this;
	}

	public void setCorresponding(Vertex corresponding) {
		if (corresponding.corresponding != null || corresponding.isXOR) {
			return;
		}
		this.corresponding = corresponding;
		this.corresponding.corresponding = this;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSubprocess(boolean isSubprocess) {
		this.isSubprocess = isSubprocess;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setWFF(String _WFF) {
		// New effects
		if (_WFF.contains("_JSONEFFECT")) {
			_WFF = _WFF.replaceAll("_JSONEFFECT", "").trim();
			Gson gson = new Gson();
			// a.e.println("Trying " + _WFF);
			jsEFF = gson.fromJson(_WFF, JSONEFFECT.class);
			WFF = jsEFF.getEffect();
		} else {
			WFF = _WFF;
		}
	}

	@Override
	public String toString() {
		String result = "";
		if (getWFF().getFormula().compareTo(a.e.EMPTY_EFFECT) != 0) {
			if (TO_STRING_WITH_WFFS) {
				result += "{" + getWFF().getFormula() + "}";
			}
		}
		if (isSubprocess) {
			if (boundaryRefs.size() > 0) {
				result += "[boundedEvents:";
				for (String s : boundaryRefs) {
					result += s + ",";
				}
				result = result.substring(0, result.length() - 1) + "]";
			}
		}
		return name + "(" + GraphLoader.getType(type) + result + ")";
	}

	/**
	 * Old Deprecated Functions
	 */
	/**
	 * Generic constructor will create a new vertex with a numbered name like
	 * 'node1'
	 */
	// public Vertex(){
	// this("Node" + generated);
	// }
	//
	// /**
	// * Construct a new vertex with a specific name
	// * @param newName
	// */
	// public Vertex(String newName){
	// name = newName;
	// type = _TYPE.NODE;
	// WFF = "";
	// if(ADD_RANDOM_WFFS){
	// generated++;
	// Random randomGenerator = new Random();
	// if( randomGenerator.nextInt(2) > 0)
	// addWFF(generateWFFs());
	// }
	// ID = UUID.randomUUID();
	// }
	//
	// public void addWFF(String _WFF){
	// //System.err.println("Adding WFF " + _WFF);
	// WFF = _WFF;
	// }
	//
	// /**
	// * Output String of a vertex. Set display of WFFs and types using
	// TO_STRING_WITH_WFF flag.
	// */
	// public String toString(){
	// String myType = "ACTIVITY";
	// if(type == _TYPE.GATEWAY){
	// myType = "[Gateway]";
	// if(gtype == _GTYPE.XOR){
	// myType += "[XOR]";
	// }else{
	// myType += "[AND]";
	// }
	// }
	//
	// return "V{" +name+ "}" + (TO_STRING_WITH_WFFS ? "{E:" +WFF+ "}{T:"
	// +myType+ "};" : "");
	// }

}
