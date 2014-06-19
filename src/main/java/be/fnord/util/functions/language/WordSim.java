package be.fnord.util.functions.language;

import java.util.ArrayList;
import java.util.List;

import be.fnord.util.functions.Poset.Pair;
import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.jawjaw.pobj.Synset;
import edu.cmu.lti.jawjaw.util.WordNetUtil;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.lexical_db.data.Concept;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.Path;

public class WordSim {
	private static RelatednessCalculator rc;
    
	private static boolean setup = false;
	public WordSim(){
		oneTimeSetUp();
	}
    public static void oneTimeSetUp() {
    	if(setup) return; setup = true;
            ILexicalDatabase db = new NictWordNet();
            rc = new Path(db);
    }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		WordSim sim = new WordSim();
		double currentBest = sim.getSim("hacker","programmer");
		a.e.println("Best score overall is " + currentBest);

	}
	
	/**
	 * Should return a score of similarity between 0 and 1.
	 * 1 is similar
	 * and 0 is not similar
	 * @param _a
	 * @param _b
	 * @return
	 */
	public double getSim(String _a, String _b){
		_a = _a.toLowerCase().trim();
		_b = _b.toLowerCase().trim();
		if(_a.compareTo(_b) == 0) return 1;
		
//		WordSim sim = new WordSim();
		double currentBest = 0;
		for(String s: types)
			try{
			Pair<List<Concept>,List<Concept>> mySyms = MakeSyns(_a, _b, s);
//			a.e.println(_a + " " + mySyms.getFirst());
			double score = rc.calcRelatednessOfSynset(mySyms.getFirst().get(0), mySyms.getSecond().get(0)).getScore();
			if(currentBest < score) currentBest = score;
//			a.e.println("Sim = " + rc.calcRelatednessOfSynset(mySyms.getFirst().get(1), mySyms.getSecond().get(0)).getScore()) ;
//			a.e.println("Sim = " + score +" " + currentBest + " " + _a + " " + _b + ":" + s);
			}catch(Exception e){
//				
				// Ignore
//				a.e.println("Error " + e );
			}
//		a.e.println("Best score overall is " + currentBest);
		return currentBest;
	}
	
	public Pair<List<Concept>,List<Concept>> MakeSyns(String _a, String _b, String _type){
		List<Concept> _n1Synsets = toSynsets(_a, _type);
		
		List<Concept> _n2Synsets = toSynsets(_b, _type);
//		a.e.println("_n1SynSet for " + _a + "(" + _type + ") is " + _n1Synsets.toString());
//		a.e.println("_n1SynSet for " + _b + "(" + _type + ") is " + _n2Synsets.toString());
		Pair<List<Concept>,List<Concept>> _result = new Pair<List<Concept>,List<Concept>>(_n1Synsets,_n2Synsets);
		return _result;
	}
	
	
	public static String N = "n";
	public static String V = "v";
	public static String A = "a";
	public static String R = "r";

	public static String [] types = {N,V,A,R};
	
	//init
	public String n1 = "cyclone";
	public String n2 = "hurricane";
	public List<Concept> n1Synsets = toSynsets(n1, N);
	public List<Concept> n2Synsets = toSynsets(n2, N);

	public final String v1 = "migrate";
	public final String v2 = "emigrate";
	public final List<Concept> v1Synsets = toSynsets(v1, V);
	public final List<Concept> v2Synsets = toSynsets(v2, V);

	public final String a1 = "huge";
	public final String a2 = "tremendous";
	public final List<Concept> a1Synsets = toSynsets(a1, A);
	public final List<Concept> a2Synsets = toSynsets(a2, A);

	public final String r1 = "eventually";
	public final String r2 = "finally";
	public final List<Concept> r1Synsets = toSynsets(r1, R);
	public final List<Concept> r2Synsets = toSynsets(r2, R);

	public final String n3 = "manuscript";
	public final String v3 = "write_down";
	public final List<Concept> n3Synsets = toSynsets(n3, N);
	public final List<Concept> v3Synsets = toSynsets(v3, V);

	public final String nv1 = "chat";
	public final String nv2 = "talk";

	private List<Concept> toSynsets( String word, String posText ) {
	        POS pos2 = POS.valueOf(posText); 
	        List<Synset> synsets = WordNetUtil.wordToSynsets(word, pos2);
	        List<Concept> concepts = new ArrayList<Concept>(synsets.size());
	        for ( Synset synset : synsets ) {
	                concepts.add( new Concept(synset.getSynset(), POS.valueOf(posText)) );
	        }
	        return concepts;
	}
}
