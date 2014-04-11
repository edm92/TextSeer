package be.fnord.util.functions.language;


import java.util.ArrayList;
import java.util.Scanner;


public class Sentences {

	/**
	 * 
	 * Stemming from http://preciselyconcise.com/apis_and_installations/snowball_stemmer.php
	 * @param args
	 */
	public static void main(String[] args) {
		String _a = "This is a first sentence.";
	    String _b = "This is a second one.";

	    Sentences s = new Sentences();
//	    a.e.println(s.Tokenize(_a) + "");
//	    a.e.println(s.Tokenize(_b) + "");
//	                
	    String __a = s.Clean(_a);
	    String __b = s.Clean(_b);
	    
	    a.e.println("__a = " + __a + " ; b = " + __b);
//	    System.out.println("Similarity between the sentences\n-"+__a+"\n-"+__b+"\n is: " + c.getResult());
	     

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
		StanfordLemmatizer sl = new StanfordLemmatizer();
		
		String _in = "";
		for(String s: sl.lemmatize(in))
			_in += s + " ";
		if(_in.length() > 1) _in = _in.substring(0,_in.length()-1);
		a.e.println("Starting with "+_in);
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
		        System.out.println(_word);
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

