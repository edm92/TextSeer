package be.fnord.util.functions.language;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

import be.fnord.util.functions.Poset.Pair;

import edu.stanford.nlp.util.logging.RedwoodConfiguration;


public class Sentences {
	public static final double MIN_MATCH_SENTENCE_SCORE = 0.01; // 
	
	LinkedList<String> sentences = new LinkedList<String>();
	
	@SuppressWarnings("unchecked")
	public  <T> LinkedList<String> List(T... elements) {
		sentences = (LinkedList<String>) new LinkedList<T>(Arrays.asList(elements));
		return sentences; 
	} 

	/**
	 * 
	 * Stemming from http://preciselyconcise.com/apis_and_installations/snowball_stemmer.php
	 * @param args
	 */
	public static void main(String[] args) {
		
		Sentences s = new Sentences();
		HashMap<String, LinkedList<SimSet>> hm = s.DoSimCheck(s.List("My kingdom for a horse!", "My kingdom for a cow", "This is a first sentence.",
				"This is a second one.", "My horse kingdom!"
				));
		
		for(String str : hm.keySet()){
			System.out.println("Result " + str + "=" + hm.get(str));
		}
		a.e.println("Applying similarity algorithms");
		HashMap<String, String> simple = s.simpleMatchClosest(hm);
		for(String str : simple.keySet()){
			a.e.println(str + " ~~ " + simple.get(str));
		}
		

		
	}
	
	/**
	 * Finds the best matches and just returns them
	 * @param _in
	 * @return
	 */
	public HashMap<String, String> simpleMatchClosest(HashMap<String, LinkedList<SimSet>> _in){
		HashMap<String,String> _result = new HashMap<String,String>();
		HashMap<String,Double> _dresult = new HashMap<String,Double>();
		
		for(String key : _in.keySet()){
			double dBest = 0;
			String sBest = "";
			for(SimSet compared : _in.get(key)){
				if(compared.score > dBest){
					dBest = compared.score;
					sBest = compared.sent2;
				}
			}
			if(_result.containsKey(key) || _result.containsKey(sBest)){
				a.e.println("Found overlap " + key + " " + sBest); // todo Fix here
			}else{
				_result.put(key, sBest);
				_dresult.put(key, dBest);
			}
		}
		LinkedList<String> remove = new LinkedList<String>();
		HashMap<String,String> add = new HashMap<String, String>();
		
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
	
	public HashMap<String, LinkedList<SimSet>> DoSimCheck(LinkedList<String> _sentences){
		sentences = _sentences;
		HashMap<String, LinkedList<SimSet>> SimSents = new HashMap<String, LinkedList<SimSet>>();

		String _a = "";
		for(String __str : sentences){
			_a = Clean(__str);
	    for(String _str : sentences){
	    	if(__str.compareToIgnoreCase(_str) == 0) continue;
	    	//if(!started && _a.length() < 1) { _a = _str ; started = true; continue;};
	    	String _b = _str;
	    	
	    	// Process pair _a and _b
		    String __a = _a;
		    String __b = Clean(_b);
		    
	    	//init Map
	    	if(!SimSents.containsKey(__a))
	    		SimSents.put(__a, new LinkedList<SimSet>());

		    
		    //a.e.println("__a = " + __a + " ; b = " + __b);
		    double result = SentenceSim(__a, __b);
		    if(result >= MIN_MATCH_SENTENCE_SCORE){ // Save
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
	    
	    
	    return SimSents;
	}
	
	
	/** 
	 * Evan's sentence similarity wizard. 
	 * @param _a
	 * @param _b
	 * @return
	 */
	public double SentenceSim(String _a, String _b){
//		a.e.println("Comparing " + _a + " and " + _b);
		double cumulativeSim = 0;
		
		WordSim sim = new WordSim();
		ArrayList<String> _aList = Tokenize(_a);
		ArrayList<String> _bList = Tokenize(_b);
		int words = ((_aList.size() > 0 ? _aList.size() : 1) + (_bList.size() > 0 ? _bList.size() : 1)) / 2;
		for(String WFFL : _aList)	// Word from first list 
			for(String WFSL : _bList){ // Word from second list
				double currentBest = sim.getSim(WFFL,WFSL);
//				a.e.println("Result of " + WFFL + " vs. " + WFSL + " = " + currentBest);
				if(currentBest > 0)
					cumulativeSim += currentBest;
			}
		
//		a.e.println("Worst case " + cumulativeSim + " words " + words);
		
		return cumulativeSim / words ;
	}
	
	public String Clean(String in){
		String _result = "";
		for(String s : Tokenize(in))
			_result += s + " ";
		if(_result.length()>0) _result = _result.substring(0, _result.length() -1);
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

}

