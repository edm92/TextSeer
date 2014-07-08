package be.fnord.util.functions.language;

import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class Sentiment {

	public static void main(String[] args) {
		Sentiment s = new Sentiment();
		String ss = "Turn the light off";
		a.e.println("Sentiment for " + ss + " is " + s.calculateSentiment(ss));
	}

	StanfordCoreNLP pipeline;

	public Sentiment() {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");

		pipeline = new StanfordCoreNLP(props);

	}

	public float calculateSentiment(String text) {

		float mainSentiment = 0;

		int longest = 0;
		Annotation annotation = pipeline.process(text);
		for (CoreMap sentence : annotation
				.get(CoreAnnotations.SentencesAnnotation.class)) {
			Tree tree = sentence
					.get(SentimentCoreAnnotations.AnnotatedTree.class);
			int sentiment = RNNCoreAnnotations.getPredictedClass(tree) - 2;
			String partText = sentence.toString();
			if (partText.length() > longest) {
				mainSentiment = sentiment;
				longest = partText.length();
			}

		}

		return mainSentiment;

	}

}
