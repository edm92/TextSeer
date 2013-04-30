package au.edu.dsl.dlab.processtools;

import java.util.Random;
import java.util.UUID;



/**
 * 
 * @author edm92
 * @deprecated This class has been moved to be.fnord.util.processModel
 */
public class Vertex {
	public static boolean TO_STRING_WITH_EFFECTS = false;
	public static boolean ADD_RANDOM_EFFECTS = false;
	public enum _TYPE { NODE, GATEWAY, SUBPROCESS, PLACE, TRANSISTION };
	public enum _GTYPE { XOR, AND, AAND };
	public String name;
	public String id;
	private String effect = "";
	private Effect immEffect = new Effect();
	public _TYPE type;
	public _GTYPE gtype;
	private static int generated = 1;
	private UUID ID;
	public static int MAX_RANDOM_PREDICATES = 5;
	public static int MAX_RANDOM_PREDICATE_RANGE = 3;
	public static String RANDOM_RANGE[] = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
		"l", "m", "n"};
	public boolean isGate(){return type == _TYPE.GATEWAY;}
	public boolean isActivity(){return type == _TYPE.NODE;}
	public boolean isXOR(){return gtype == _GTYPE.XOR;}
	public boolean isAND(){return gtype == _GTYPE.AND;}
	public void makeGate() { makeXORGate(); }
	public void makeActivity() { type = _TYPE.NODE; }
	public void makeXORGate() { type = _TYPE.GATEWAY; gtype = _GTYPE.XOR; }
	public void makeANDGate() { type = _TYPE.GATEWAY; gtype = _GTYPE.AND; }
	public void makeAANDGate() { type = _TYPE.GATEWAY; gtype = _GTYPE.AAND; }
	public Vertex CorrespondingVertex = null;
	public void setCorresponding(Vertex input){	CorrespondingVertex = input; input.CorrespondingVertex = this; }
	public String getID() { return ID.toString(); };
	public int height = 0;
	public int width = 0;
	public int x = 0;
	public int y = 0;
	public Effect getEffect(){immEffect.setFormula(effect); return immEffect;};
	public static Vertex createNew(){ return new Vertex(); }
	
	/**
	 * Generic constructor will create a new vertex with a numbered name like 'node1'
	 */
	public Vertex(){
		this("Node" + generated);
	}
	
	/**
	 * Construct a new vertex with a specific name
	 * @param newName
	 */
	public Vertex(String newName){
		name = newName;
		type = _TYPE.NODE;
		effect = "";
		if(ADD_RANDOM_EFFECTS){
			generated++;
			Random randomGenerator = new Random();
			if( randomGenerator.nextInt(2) > 0)
				addEffect(generateEffects());
		}
		ID = UUID.randomUUID();
	}
	
	public void addEffect(String _effect){
		//System.err.println("Adding effect " + _effect);
		effect = _effect;
	}
	
	/**
	 * Output String of a vertex. Set display of effects and types using TO_STRING_WITH_EFFECT flag.
	 */
	public String toString(){
		String myType = "ACTIVITY";
		if(type == _TYPE.GATEWAY){
			myType = "[Gateway]";
			if(gtype == _GTYPE.XOR){
				myType += "[XOR]";
			}else{
				myType += "[AND]";
			}
		}
		
		return "V{" +name+ "}" + (TO_STRING_WITH_EFFECTS ? "{E:" +effect+ "}{T:" +myType+ "};" : "");
	}
	
	
	
	
	/**
	 * This function generates a random effect scenario based on the starting random seeds. 
	 * @return A new effect scenario
	 */
	public static String generateEffects(){
		boolean used[] = new boolean[MAX_RANDOM_PREDICATES];	// Need better implementation of this
		String returnEffect = new String();
		Random randomGenerator = new Random();
		String predicate = "";
		int numPredicates = randomGenerator.nextInt(MAX_RANDOM_PREDICATES);
		for(int i = 0; i < numPredicates; i++){
			int ChoosePredicate = randomGenerator.nextInt(MAX_RANDOM_PREDICATE_RANGE);
			if(!used[ChoosePredicate]){
				used[ChoosePredicate] = true;
				predicate += RANDOM_RANGE[ChoosePredicate] + " & ";
			}
		}
		if(predicate.length() > 3){ 
			predicate = predicate.substring(0, predicate.length() - 3); // Remove final &
			returnEffect += predicate + " & " ;
		}
		if(returnEffect.length() > 3)
			returnEffect = returnEffect.substring(0, returnEffect.length() - 3);

		return returnEffect;

	}
	
	
	/**
	 * This function is a utility function used to generate the random range.
	 */
	public static void CREATE_RANDOM_RANGE(){
		RANDOM_RANGE[0] = "a";
		RANDOM_RANGE = new String[17577];
		for(int i = 0; i < 26; i++){
			for(int j = 0; j < 26; j++){
				for(int k = 0; k < 26; k++){
					RANDOM_RANGE[(26*26*i) + (26*j) + k] = "" + Character.toChars(i+97)[0] + Character.toChars(j+97)[0] + Character.toChars(k+97)[0];
					//System.out.println((26*26*i) + (26*j) + k + " = " + RANDOM_RANGE[(26*26*i) + (26*j) + k] );
				}

			}
		}
	}

}
