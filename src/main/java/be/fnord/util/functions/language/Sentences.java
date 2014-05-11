package be.fnord.util.functions.language;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

import a.e.WORD_MATCH_STRENGTH;
import be.fnord.util.functions.Poset.Pair;
import edu.stanford.nlp.util.logging.RedwoodConfiguration;

/**
 * 
 * Functions for comparing a set of sentences (aka process traces)
 * 
 * @author edm92
 *
 */
public class Sentences {
	public static final double IMPORTANCE_OF_SENTENCES = 1;	// Not used yet
	public static final boolean __DEBUG = false;
	static boolean  RANGECALC = false; // Set to true to get a ratio from 0-1
	
	public int currentRange = 0;
	public enum ALG {
		SIMPLE_COMPARE_ALG,
		SIMPLE_COMPARE_WITH_MANYTOONE;
	}
	
//	LinkedList<String> sentences = new LinkedList<String>();
	
	@SuppressWarnings("unchecked")
	public  <T> LinkedList<String> List(T... elements) {
		LinkedList<String> sentences = (LinkedList<String>) new LinkedList<T>(Arrays.asList(elements));
		return sentences; 
	} 

	public static void main(String[] args) {
		
		Sentences s = new Sentences();
		
		LinkedList<String> Proc1 = s.List(
				"task1", "task two");
//				"My kingdom for a horse!", 
//				"My kingdom for a cow", 
//				"This is a first sentence.",
//				"This is a second one.", 
//				"My horse kingdom!"
//				);
		
		LinkedList<String> Proc2 = s.List(
				"task two", "task one");
//				"My kingdom for a horsey!", 
//				"My kingdom for a cowey", 
//				"This is my sentence number one.",
//				"This is my sentence number two.", 
//				"My horsey kingdom!"
//				);
		
		Pair<String,String> myNewSentences = 
				s.CreateStringOfTwoSetsOfSentences(Proc1, Proc2, 
						ALG.SIMPLE_COMPARE_ALG, 
						WORD_MATCH_STRENGTH.EXACT.MATCH_NUMBER);
//		a.e.println("Results: ");
		a.e.incIndent();
//		a.e.println(myNewSentences.toString());
		a.e.decIndent();
		
		int numChar = myNewSentences.extra ;
		
		for(char i: myNewSentences.getFirst().toCharArray())
			for(char j: myNewSentences.getSecond().toCharArray()){
				if(i == j) numChar--;
			}
		if(numChar < 1) numChar = 1;
		DamerauLevenshtein d = new DamerauLevenshtein(myNewSentences.getFirst(),myNewSentences.getSecond());
		double sim = (double)d.getSimilarity() ;
		
		if(RANGECALC)
			sim = 1 - ( sim / (double)numChar);
		
		
//		a.e.println("Result : " + sim );
		
			
	}
	
	public HashMap<String, String> cleanedString = new HashMap<String,String>();
	public HashMap<String, LinkedList<String>> charMapping = new HashMap<String,LinkedList<String>>();
	public HashMap<String, String> revCharMapping = new HashMap<String, String>();
	
	public Pair<String,String> CreateStringOfTwoSetsOfSentences(
			
			LinkedList<String> Proc1, 
			LinkedList<String> Proc2, 
			ALG ALG_TO_RUN, double fixNumber){
		double MIN_MATCH_SENTENCE_SCORE = fixNumber;
//		HashMap<String, LinkedList<String>> result = new HashMap<String, LinkedList<String>> ();
		
		LinkedList<String> cleanProc1 = new LinkedList<String>();
		LinkedList<String> cleanProc2 = new LinkedList<String>();
		
		for(String str: Proc1){
			String cleaned = Clean(str);
			cleanProc1.add(cleaned);
		};
		for(String str: Proc2){
			String cleaned = Clean(str);
			cleanProc2.add(cleaned);		
//			a.e.println(cleaned);
		}
			
		
		
		
		for(String str: cleanProc1)
			cleanedString.put(str, Proc1.get(cleanProc1.indexOf(str)));
		for(String str: cleanProc2){
			cleanedString.put(str, Proc2.get(cleanProc2.indexOf(str)));
		}
		
//		sentences.addAll(Proc1);
//		sentences.addAll(Proc2);
//		a.e.println("Comparing " + cleanProc1 + " vs " + cleanProc2);
		HashMap<String, LinkedList<SimSet>> hm = DoSimCheck(cleanProc1,cleanProc2, MIN_MATCH_SENTENCE_SCORE);
		hm = fixTwoProcSimSet(hm, cleanProc1, cleanProc2);
		
		HashMap<String, String> simple = null;
		if(ALG_TO_RUN == ALG.SIMPLE_COMPARE_ALG)				
				simple = simpleMatchClosest(hm, cleanProc1 , cleanProc2 , !INCLUDE_MANY_TO_ONE_MATCHES);
		else
			if(ALG_TO_RUN == ALG.SIMPLE_COMPARE_WITH_MANYTOONE)
				simple = simpleMatchClosest(hm, cleanProc1 , cleanProc2 ,  INCLUDE_MANY_TO_ONE_MATCHES);
		
		
		for(String str: simple.keySet()){
			
			if(!revCharMapping.containsKey(str)){
				LinkedList<String> newStuff = new LinkedList<String>();
				
				String chr = "";
				String alt = simple.get(str);
				if(str.trim().length() < 1) break;
				if(alt.trim().length() < 1) break;
				
				if(revCharMapping.containsKey(alt))
					chr = revCharMapping.get(alt);
				else chr = a.e.RANDOM_RANGE[currentRange++];
				
				if(charMapping.containsKey(chr))
					newStuff = charMapping.get(chr);
				
				newStuff.add(str);
//				a.e.println("Testing " + str + " and " + alt + " " + chr);
//				a.e.println(revCharMapping.toString());
				if(newStuff.size() > 0){
					charMapping.put(chr, newStuff);
					revCharMapping.put(str, chr);
					if(!revCharMapping.containsKey(alt)){
						charMapping.get(chr).add(alt);
						revCharMapping.put(alt, chr);
					}
				}
			}else{
				String alt = simple.get(str);
				a.e.err("If you see this, I couldn't get this to work to test it - it is in Sentences.java");
				if(!revCharMapping.containsKey(alt)){					
					String chr = revCharMapping.get(str);
					
					charMapping.get(chr).add(alt);
					revCharMapping.put(alt, chr);
				}					
			}
		}
		
		// Add any left overs
		for(String str:  cleanedString.keySet()){
//			a.e.println("Testing " + str);
			if(!revCharMapping.containsKey(str)){
				LinkedList<String> newStuff = new LinkedList<String>();
				String chr = a.e.RANDOM_RANGE[currentRange++];
				
				newStuff.add(str);
				if(newStuff.size() > 0){
					revCharMapping.put(str, chr);
					charMapping.put(chr, newStuff);
				}
			}
		}
		
		LinkedList<String> removeList = new LinkedList<String>();
		for(String s: charMapping.keySet()){
			if(charMapping.containsKey(s) && charMapping.get(s).size() < 1)
				removeList.add(s);
		}	for(String s: removeList) {
			a.e.println("Trying like hell to remove : " + s);
			charMapping.remove(s);
		}
				
		
		if(__DEBUG)
		a.e.println("Char mapping  = " + charMapping.toString());
		
		// Create reverse sentences
		String _result1 = "";
		String _result2 = "";
		for(String str: cleanProc1){
			if(revCharMapping.containsKey(str))
				_result1 += revCharMapping.get(str);
			else{
				a.e.err("Funny stuff in sentences.java " + str);
			}
		}
		for(String str: cleanProc2){
			if(revCharMapping.containsKey(str))
				_result2 += revCharMapping.get(str);
			else{
				a.e.err("Funny stuff in sentences.java " + str);
			}
		}
		if(__DEBUG){
//			a.e.println("Str : " + _result1);
//			a.e.println("Str : " + _result2);
		}
		
		Pair<String, String> _result = new Pair<String,String>(_result1, _result2);
//		LinkedList<String> removeList = new LinkedList<String>();
//		for(String str : simple.keySet()){
//			if(simple.get(str).trim().length() < 2) removeList.add(str);
//		}
//		for(String s : removeList) simple.remove(s);
			
		if(__DEBUG){
			for(String str : hm.keySet()){
				System.out.println("Result " + str + "=" + hm.get(str));
			}
//			a.e.println("Applying similarity algorithms");
//			a.e.incIndent();			
//			for(String str : simple.keySet()){
//				a.e.println(str + " ~~ " + simple.get(str));
//			}		
//			a.e.decIndent();
//			a.e.println(_result.toString());
		};
		
		
		_result.extra = currentRange;
		return _result;
	}
	
	public HashMap<String, LinkedList<SimSet>> fixTwoProcSimSet(HashMap<String, LinkedList<SimSet>> _in,
			LinkedList<String> proc1, LinkedList<String> proc2){
		HashMap<String, LinkedList<SimSet>> result = new HashMap<String, LinkedList<SimSet>>();
		
		for(String str: proc1){
			if(_in.containsKey(str)){
				LinkedList<SimSet> current = _in.get(str);
				LinkedList<SimSet> _newCurrent = new LinkedList<SimSet>(); 
				for(SimSet sims : current){
					if(sims.sent1.compareToIgnoreCase(str) == 0){
						if(!proc1.contains(sims.sent2)){
							_newCurrent.add(sims);
						}
					}
				}
				result.put(str, _newCurrent);
			}
		}
		for(String str: proc2){
			if(_in.containsKey(str)){
				LinkedList<SimSet> current = _in.get(str);
				LinkedList<SimSet> _newCurrent = new LinkedList<SimSet>(); 
				for(SimSet sims : current){
					if(sims.sent1.compareToIgnoreCase(str) == 0){
						if(!proc2.contains(sims.sent2)){
							_newCurrent.add(sims);
						}
					}
				}
				result.put(str, _newCurrent);
			}
		}

		
		return result;
	}
	

	
	/**
	 * Finds the best matches and just returns them
	 * @param _in
	 * @param MANYTOONE Give either INCLUDE_MANY_TO_ONE_MATCHES or !INCLUDE_MANY_TO_ONE_MATCHES
	 * @return
	 */
	public HashMap<String, String> simpleMatchClosest(HashMap<String, LinkedList<SimSet>> _in,
			LinkedList<String> _sentences1,
			LinkedList<String> _sentences2,
			boolean MANYTOONE){
		HashMap<String,String> _result = new HashMap<String,String>();
		HashMap<String,Double> _dresult = new HashMap<String,Double>();
		HashMap<String,String> _Bresult =  new HashMap<String,String>();
		
		LinkedList<String> taken = new LinkedList<String>();
		for(String key : _in.keySet()){
			double dBest = 0;
			String sBest = "";
			for(SimSet compared : _in.get(key)){
				if(!MANYTOONE && !taken.contains(compared.sent2))
				if(compared.score > dBest){
					dBest = compared.score;
					sBest = compared.sent2;
				}
			}
			if(_result.containsKey(key) || _result.containsKey(sBest)){
//				a.e.println("Found overlap " + key + " " + sBest); // todo Fix here
				taken.add(sBest);
				_Bresult.put(key, sBest);
			}else{
				taken.add(sBest);
				_result.put(key, sBest);
				_dresult.put(key, dBest);
			}
		}
		if(MANYTOONE)		
			return _Bresult;
		
		
		LinkedList<String> remove = new LinkedList<String>();
		HashMap<String, String> add = new HashMap<String, String>();
		
		// Lets make sure that we can go backwards and forwards
		for(String key : _result.keySet()){
			if(_result.containsKey(_result.get(key))){
				String alt = _result.get(key);
				if(_dresult.get(key) < _dresult.get(alt)){
					remove.add(key);
					add.put(key, alt);
				}else{
					remove.add(alt);
					add.put(alt, key);
				}
			}
		}
		for(String s: remove){
			_result.remove(s);
		}
		_result.putAll(add);
		remove = new LinkedList<String>();
		// Remove any stragglers
		for(String s : _result.keySet()){
			if(_result.containsKey(_result.get(s))){
				remove.add(s);
			}
		}
		for(String s: remove)
			_result.remove(s);
		
		remove = new LinkedList<String>();
		for(String s: _result.keySet()){
			if(s.trim().length() < 2) remove.add(s);
			if(_result.get(s).trim().length() < 2) remove.add(s);
		}
		for(String s: remove)
			_result.remove(s);
		
		
		return _result;
		
	}
	
	

	
	class SimSet{
		public String sent1 = "";
		public String sent2 = "";
		public double score = 0;
		
		public String toString(){
			return "[(" + sent1 + ", " + sent2 + ")=" + score + "]";
		}
	}
	
	public HashMap<String, LinkedList<SimSet>> DoSimCheck(LinkedList<String> _sentencesA, LinkedList<String> _sentencesB, Double MATCH_SCORE){
//		sentences = new LinkedList<String>();
//		sentences.addAll(_sentencesA);
		
		HashMap<String, LinkedList<SimSet>> SimSents = new HashMap<String, LinkedList<SimSet>>();

		String _a = "";
		for(String __str : _sentencesA){
			_a = Clean(__str);
	    for(String _str : _sentencesB){
	    	if(__str.compareToIgnoreCase(_str) == 0) continue;
	    	//if(!started && _a.length() < 1) { _a = _str ; started = true; continue;};
	    	String _b = _str;
	    	
	    	// Process pair _a and _b
		    String __a = _a;
		    String __b = Clean(_b);
		    
	    	//init Map
	    	if(!SimSents.containsKey(__a))
	    		SimSents.put(__a, new LinkedList<SimSet>());

		    

		    double result = SentenceSim(__a, __b, MATCH_SCORE);
//		    a.e.println("__a = " + __a + " ; b = " + __b + " = " + result);
		    if(result >= MATCH_SCORE){ // Save
		    	SimSet ss = new SimSet();
		    	ss.sent1 = __a; ss.sent2 = __b;
		    	ss.score = result;
		    	LinkedList<SimSet> LLS = SimSents.get(_a);
		    	boolean breaker = false;
		    	if(LLS != null){
		    	for(SimSet sims : LLS)
		    		if(sims.sent2.compareToIgnoreCase(__b) == 0) breaker = true;
		    	if(!breaker) LLS.add(ss);
		    	}
		    	else{
		    		LLS = new LinkedList<SimSet>(); LLS.add(ss);
		    		SimSents.put(__a, LLS);
		    	}
		    	
		    }
//		    a.e.println("Result of comparing remaining = " + SentenceSim(__a,__b) + " (" + __a + ", " + __b +")");
		    
		    //_a = __b;
	    }};
	    
	    LinkedList<String> remove = new LinkedList<String>();
	    for(String s: SimSents.keySet()){
	    	if(SimSents.get(s) == null || SimSents.get(s).size() <1)
	    		remove.add(s);
	    }for(String s: remove) SimSents.remove(s);
	    
	    return SimSents;
	}
	
	
	/** 
	 * Evan's sentence similarity wizard. 
	 * @param _a
	 * @param _b
	 * @return
	 */
	public double SentenceSim(String _a, String _b, double MATCH_SCORE){
//		a.e.println("Comparing " + _a + " and " + _b);
		double cumulativeSim = 0;
		
		WordSim sim = new WordSim();
		ArrayList<String> _aList = Tokenize(_a);
		ArrayList<String> _bList = Tokenize(_b);
//		double words = ((_aList.size() > 0 ? _aList.size() : 1) + (_bList.size() > 0 ? _bList.size() : 1)) / 2;
		for(String WFFL : _aList)	// Word from first list 
			for(String WFSL : _bList){ // Word from second list
				double currentBest = sim.getSim(WFFL,WFSL);
//				a.e.println("Result of " + WFFL + " vs. " + WFSL + " = " + currentBest);
//				a.e.println("Match score = " + MATCH_SCORE);
				if(currentBest > MATCH_SCORE)
					cumulativeSim += currentBest;
			}
		
//		a.e.println("Worst case " + cumulativeSim + " words " + words);
		
		
		return cumulativeSim;// / IMPORTANCE_OF_SENTENCES ;
	}
	
	public String Clean(String in){
		
		
		// Remove first section
		if(in.contains("_")){
			in = in.trim();
			in = in.substring(in.indexOf("_") +1, in.length());
			in = in.trim();
			if(in.substring(0,1).matches("[0-9]")) in = in.substring(1, in.length());
			if(in.length() > 0)
			if(in.substring(0,1).matches("[0-9]")) in = in.substring(1, in.length());
			if(in.length() > 0)
			if(in.substring(0,1).matches("[0-9]")) in = in.substring(1, in.length());
			in = in.trim();
		}
		
		String _result = "";
		
		String[] words = in.split(" ");
		in = "";
		for(String str: words){
			str = str.replaceAll("(\\b)(\\d)", "$2 ");
			str = str.replaceAll("(\\d)(\\b)", " $1");
			
			str = str.replaceAll("1", " one ");
			str = str.replaceAll("2", " two ");
			str = str.replaceAll("3", " three ");
			str = str.replaceAll("4", " four ");
			str = str.replaceAll("5", " five ");
			str = str.replaceAll("6", " six ");
			str = str.replaceAll("7", " seven ");
			str = str.replaceAll("8", " eight ");
			str = str.replaceAll("9", " nine ");
			str = str.replaceAll("0", " zero ");
			str = str.replaceAll("  ", " ");
			str = str.trim();
			in += str + " ";
		}
		in = in.trim();
		
		
		for(String s : Tokenize(in))
			_result += s + " ";
		//if(_result.length()>0) _result = _result.substring(0, _result.length() -1);
		_result = _result.trim();
		

		return _result;
		
	}
	
	@SuppressWarnings("resource")
	public ArrayList<String> Tokenize(String in){
		ArrayList<String> tokens = new ArrayList<String>();
		//create a new instance of PorterStemmer
//		PorterStemmer stemmer = new PorterStemmer();
		
		 // Turn off Stanford loggin -- Thanks to http://stackoverflow.com/questions/21851217/how-to-shutdown-stanford-corenlp-redwood-logging
    	// shut off the annoying intialization messages
    	RedwoodConfiguration.empty().capture(System.err).apply();
		StanfordLemmatizer sl = new StanfordLemmatizer();
    	RedwoodConfiguration.empty().apply();
		
		String _in = "";
		for(String s: sl.lemmatize(in))
			_in += s + " ";
		if(_in.length() > 1) _in = _in.substring(0,_in.length()-1);
//		a.e.println("Starting with "+_in);
		Stopwords stops = new Stopwords();
		Scanner tokenize = new Scanner(_in);
		while (tokenize.hasNext()) {
			//set the word to be stemmed
			String word = tokenize.next();
			word = word.replaceAll("[^\\p{L}\\p{Z}]","").trim();
//		    stemmer.setCurrent(word);
		    
		    //call stem() method. stem() method returns boolean value. 
//		    if(stemmer.stem())
			if(word.length() > 0)
		    {
		        //If stemming is successful obtain the stem of the given word
		    	String _word = word ;//stemmer.getCurrent();
//		        System.out.println(_word);
		        if(!stops.isStopword(_word))
		        	tokens.add(_word);
		    }
		    
		}		
		
		return tokens;
	}
	
	public String removeIrrelevant(String in){
		String out = in.toLowerCase().trim();
		return out;
	}

//	public double MIN_MATCH_SENTENCE_SCORE = WORD_MATCH_STRENGTH.EXACT.MATCH_NUMBER;
	private static final boolean INCLUDE_MANY_TO_ONE_MATCHES = false;
}



