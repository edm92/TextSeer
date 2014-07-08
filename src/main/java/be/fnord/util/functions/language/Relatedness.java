package be.fnord.util.functions.language;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.jawjaw.pobj.Synset;
import edu.cmu.lti.jawjaw.util.Configuration;
import edu.cmu.lti.jawjaw.util.WordNetUtil;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.lexical_db.data.Concept;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

public class Relatedness {
	private static RelatednessCalculator rc;

	@BeforeClass
	public static void oneTimeSetUp() {
		ILexicalDatabase db = new NictWordNet();
		rc = new Path(db);
	}

	public String A = "a";

	public final String a1 = "huge";

	public final List<Concept> a1Synsets = toSynsets(a1, A);

	public final String a2 = "tremendous";

	public final List<Concept> a2Synsets = toSynsets(a2, A);

	public String N = "n";

	// init
	public String n1 = "cyclone";
	public List<Concept> n1Synsets = toSynsets(n1, N);
	public String n2 = "hurricane";
	public List<Concept> n2Synsets = toSynsets(n2, N);

	public final String n3 = "manuscript";
	public final List<Concept> n3Synsets = toSynsets(n3, N);
	public final String nv1 = "chat";
	public final String nv2 = "talk";

	public String R = "r";
	public final String r1 = "eventually";
	public final List<Concept> r1Synsets = toSynsets(r1, R);
	public final String r2 = "finally";

	public final List<Concept> r2Synsets = toSynsets(r2, R);
	public String V = "v";
	public final String v1 = "migrate";
	public final List<Concept> v1Synsets = toSynsets(v1, V);

	public final String v2 = "emigrate";
	public final List<Concept> v2Synsets = toSynsets(v2, V);
	public final String v3 = "write_down";
	public final List<Concept> v3Synsets = toSynsets(v3, V);

	{
		WS4JConfiguration.getInstance().setLeskNormalize(false);
		WS4JConfiguration.getInstance().setMFS(false);
		Configuration.getInstance().setMemoryDB(false);
	}

	/**
	 * Test method for
	 * {@link edu.cmu.lti.similarity.impl.Path#calcRelatedness(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testHappyPathOnSynsets() {
		// both japanese and english!, n and v

		// English pair
		assertEquals(0.5,
				rc.calcRelatednessOfSynset(n1Synsets.get(1), n2Synsets.get(0))
						.getScore(), 0.0001);
		assertEquals(0.125,
				rc.calcRelatednessOfSynset(n1Synsets.get(0), n2Synsets.get(0))
						.getScore(), 0.0001);

		assertEquals(0.5,
				rc.calcRelatednessOfSynset(v1Synsets.get(0), v2Synsets.get(0))
						.getScore(), 0.0001);
		assertEquals(0.25,
				rc.calcRelatednessOfSynset(v1Synsets.get(1), v2Synsets.get(0))
						.getScore(), 0.0001);
	}

	/**
	 * Test method for
	 * {@link edu.cmu.lti.similarity.RelatednessCalculator#calcRelatednessOfWords(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testHappyPathOnWords() {
		assertEquals(0.5, rc.calcRelatednessOfWords(n1, n2), 0.0001D);
		assertEquals(0.5, rc.calcRelatednessOfWords(v1, v2), 0.0001D);
	}

	/**
	 * Test method for
	 * {@link edu.cmu.lti.similarity.RelatednessCalculator#calcRelatednessOfWords(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testHappyPathOnWordsWithPOS() {
		assertEquals(0.3333, rc.calcRelatednessOfWords(nv1 + "#n", nv2 + "#n"),
				0.0001D);
		assertEquals(0.0000, rc.calcRelatednessOfWords(nv1 + "#n", nv2 + "#v"),
				0.0001D);
		assertEquals(0.0000, rc.calcRelatednessOfWords(nv1 + "#v", nv2 + "#n"),
				0.0001D);
		assertEquals(0.3333, rc.calcRelatednessOfWords(nv1 + "#v", nv2 + "#v"),
				0.0001D);
		assertEquals(0.0000,
				rc.calcRelatednessOfWords(nv1 + "#other", nv2 + "#other"),
				0.0001D);
	}

	@Test
	public void testOnSameSynsets() {
		a.e.println("Testing: " + n1Synsets.get(0) + " and " + n1Synsets.get(0)
				+ "");
		assertEquals(1,
				rc.calcRelatednessOfSynset(n1Synsets.get(0), n1Synsets.get(0))
						.getScore(), 0.0001);
	}

	@Test
	public void testOnUnknownSynsets() {
		assertEquals(0, rc.calcRelatednessOfSynset(null, n1Synsets.get(0))
				.getScore(), 0.0001);
		assertEquals(0, rc.calcRelatednessOfWords(null, n1), 0.0001);
		assertEquals(0, rc.calcRelatednessOfWords("", n1), 0.0001);
	}

	private List<Concept> toSynsets(String word, String posText) {
		POS pos2 = POS.valueOf(posText);
		List<Synset> synsets = WordNetUtil.wordToSynsets(word, pos2);
		List<Concept> concepts = new ArrayList<Concept>(synsets.size());
		for (Synset synset : synsets) {
			concepts.add(new Concept(synset.getSynset(), POS.valueOf(posText)));
		}
		return concepts;
	}
}
