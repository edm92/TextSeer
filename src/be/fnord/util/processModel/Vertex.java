package be.fnord.util.processModel;

import be.fnord.util.logic.WFF;
import be.fnord.util.processModel.util.GraphLoader;

import java.util.LinkedList;
import java.util.Random;
import java.util.UUID;

/**
 * @author Evan Morrison edm92@uowmail.edu.au http://www.fnord.be
 *         Apache License, Version 2.0, Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */
public class Vertex extends Graph<Vertex, Edge> {
    private static final long serialVersionUID = 1L;
    public String name = "";
    public int type = 0;
    public String id = "";
    public boolean isSplit = false;
    public boolean isJoin = false;
    public boolean isSubstructural = false;
    public boolean isAND = false;
    public boolean isXOR = false;
    public boolean isOR = false;
    public boolean isGateway = false;
    public boolean isSubprocess = false;
    public boolean isTrace = false;

    private String WFF = "";
    private WFF immWFF = new WFF();
    private WFF cumWFF = new WFF(); // Include cumulative effects

    public Vertex corresponding = null;
    public LinkedList<String> boundaryRefs = new LinkedList<String>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return GraphLoader.getType(type);
    }

    public void setType(int type) {
        this.type = type;
    }

    public Vertex() {
        this(UUID.randomUUID().toString());
    }

    public Vertex(String _name) {
        this(_name, GraphLoader.Task);
    }

    public Vertex(String _name, int _type) {
        super();
        this.name = _name;
        this.type = _type;
    }

    public Vertex getCorresponding() {
        return corresponding;
    }

    public void setCorresponding(Vertex corresponding) {
        if (corresponding.corresponding != null || corresponding.isXOR) return;
        this.corresponding = corresponding;
        this.corresponding.corresponding = this;
    }

    public WFF getWFF() {
        immWFF.setFormula(WFF);
        return immWFF;
    }

    ;

    public void setWFF(String _WFF) {
        WFF = _WFF;
    }

    public void addWFF(String _WFF) {
        if (WFF.length() > 0) {
            WFF = "( " + WFF + " ) " + a.e.AND + " ( " + _WFF + " )";
        } else setWFF(_WFF);
    }


    public static boolean TO_STRING_WITH_WFFS = false;
    public static boolean ADD_RANDOM_WFFS = false;

    public enum _TYPE {NODE, GATEWAY, SUBPROCESS, PLACE, TRANSISTION}

    ;

    public enum _GTYPE {XOR, AND, AAND}

    ;
    //	public String name;
//	public String id;
//	private String WFF = "";
//	private WFF immWFF = new WFF();
//	public _TYPE type;
    public _GTYPE gtype;
    private UUID ID;
    public static int MAX_RANDOM_PREDICATES = 5;
    public static int MAX_RANDOM_PREDICATE_RANGE = 3;
    public static String RANDOM_RANGE[] = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
            "l", "m", "n"};

    //	public boolean isGate(){return type == _TYPE.GATEWAY;}
//	public boolean isActivity(){return type == _TYPE.NODE;}
    public boolean isXOR() {
        return gtype == _GTYPE.XOR;
    }

    public boolean isAND() {
        return gtype == _GTYPE.AND;
    }

    //	public void makeGate() { makeXORGate(); }
//	public void makeActivity() { type = _TYPE.NODE; }
//	public void makeXORGate() { type = _TYPE.GATEWAY; gtype = _GTYPE.XOR; }
//	public void makeANDGate() { type = _TYPE.GATEWAY; gtype = _GTYPE.AND; }
//	public void makeAANDGate() { type = _TYPE.GATEWAY; gtype = _GTYPE.AAND; }
    public Vertex CorrespondingVertex = null;

    //	public void setCorresponding(Vertex input){	CorrespondingVertex = input; input.CorrespondingVertex = this; }
    public String getID() {
        return ID.toString();
    }

    ;
    public int height = 0;
    public int width = 0;
    public int x = 0;
    public int y = 0;

    //	public WFF getWFF(){immWFF.setFormula(WFF); return immWFF;};
    public static Vertex createNew() {
        return new Vertex();
    }


    /**
     * This function generates a random WFF scenario based on the starting random seeds.
     *
     * @return A new WFF scenario
     */
    public static String generateWFFs() {
        boolean used[] = new boolean[MAX_RANDOM_PREDICATES];    // Need better implementation of this
        String returnWFF = new String();
        Random randomGenerator = new Random();
        String predicate = "";
        int numPredicates = randomGenerator.nextInt(MAX_RANDOM_PREDICATES);
        for (int i = 0; i < numPredicates; i++) {
            int ChoosePredicate = randomGenerator.nextInt(MAX_RANDOM_PREDICATE_RANGE);
            if (!used[ChoosePredicate]) {
                used[ChoosePredicate] = true;
                predicate += RANDOM_RANGE[ChoosePredicate] + " & ";
            }
        }
        if (predicate.length() > 3) {
            predicate = predicate.substring(0, predicate.length() - 3); // Remove final &
            returnWFF += predicate + " & ";
        }
        if (returnWFF.length() > 3)
            returnWFF = returnWFF.substring(0, returnWFF.length() - 3);

        return returnWFF;

    }


    /**
     * This function is a utility function used to generate the random range.
     */
    public static void CREATE_RANDOM_RANGE() {
        RANDOM_RANGE[0] = "a";
        RANDOM_RANGE = new String[17577];
        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < 26; j++) {
                for (int k = 0; k < 26; k++) {
                    RANDOM_RANGE[(26 * 26 * i) + (26 * j) + k] = "" + Character.toChars(i + 97)[0] + Character.toChars(j + 97)[0] + Character.toChars(k + 97)[0];
                    //System.out.println((26*26*i) + (26*j) + k + " = " + RANDOM_RANGE[(26*26*i) + (26*j) + k] );
                }

            }
        }
    }

    public String toString() {
        String result = "";
        if (this.getWFF().getFormula().compareTo(a.e.EMPTY_EFFECT) != 0) {
            result += "{" + this.getWFF().getFormula() + "}";
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
        return this.name + "(" + GraphLoader.getType(type) + result + ")";
    }

    public boolean isSubprocess() {
        return isSubprocess;
    }

    public void setSubprocess(boolean isSubprocess) {
        this.isSubprocess = isSubprocess;
    }

    // TODO implement this at some point
    public Vertex makeCopy() {
        return this;
    }


    /**
     * Old Deprecated Functions
     */
    /**
     * Generic constructor will create a new vertex with a numbered name like 'node1'
     */
//	public Vertex(){
//		this("Node" + generated);
//	}
//	
//	/**
//	 * Construct a new vertex with a specific name
//	 * @param newName
//	 */
//	public Vertex(String newName){
//		name = newName;
//		type = _TYPE.NODE;
//		WFF = "";
//		if(ADD_RANDOM_WFFS){
//			generated++;
//			Random randomGenerator = new Random();
//			if( randomGenerator.nextInt(2) > 0)
//				addWFF(generateWFFs());
//		}
//		ID = UUID.randomUUID();
//	}
//	
//	public void addWFF(String _WFF){
//		//System.err.println("Adding WFF " + _WFF);
//		WFF = _WFF;
//	}
//	
//	/**
//	 * Output String of a vertex. Set display of WFFs and types using TO_STRING_WITH_WFF flag.
//	 */
//	public String toString(){
//		String myType = "ACTIVITY";
//		if(type == _TYPE.GATEWAY){
//			myType = "[Gateway]";
//			if(gtype == _GTYPE.XOR){
//				myType += "[XOR]";
//			}else{
//				myType += "[AND]";
//			}
//		}
//		
//		return "V{" +name+ "}" + (TO_STRING_WITH_WFFS ? "{E:" +WFF+ "}{T:" +myType+ "};" : "");
//	}

}

