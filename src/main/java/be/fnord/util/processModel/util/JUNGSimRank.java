//package be.fnord.util.processModel.util;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//
//import edu.uci.ics.jung.graph.Graph;
//import edu.uci.ics.jung.graph.util.Pair;
//
//// From http://stackoverflow.com/questions/21640337/calculate-simrank-in-jung-graph
//
//public class JUNGSimRank {
//	/**
//	 * Compute the initial SimRank for the vertices of the given graph. This
//	 * initial SimRank for two vertices (a,b) is 0.0f when a != b, and 1.0f when
//	 * a == b
//	 *
//	 * @param <V>
//	 *            The vertex type
//	 * @param g
//	 *            The graph
//	 * @return The SimRank, as a map from pairs of vertices to their similarity
//	 */
//	private static <V> Map<Pair<V>, Float> computeInitialSimRank(Graph<V, ?> g) {
//		Map<Pair<V>, Float> R0 = new LinkedHashMap<Pair<V>, Float>();
//		for (V a : g.getVertices()) {
//			for (V b : g.getVertices()) {
//				Pair<V> ab = new Pair<V>(a, b);
//				if (a.equals(b)) {
//					R0.put(ab, 1.0f);
//				} else {
//					R0.put(ab, 0.0f);
//				}
//			}
//		}
//		return R0;
//	}
//
//	/**
//	 * Compute the SimRank for the vertices of the given graph.
//	 *
//	 * @param <V>
//	 *            The vertex type
//	 * @param g
//	 *            The graph
//	 * @return The SimRank, as a map from pairs of vertices to their similarity
//	 */
//	private static <V> Map<Pair<V>, Float> computeSimRank(Graph<V, ?> g) {
//		final int kMax = 5;
//		final float C = 0.8f;
//
//		Map<Pair<V>, Float> currentR = computeInitialSimRank(g);
//		Map<Pair<V>, Float> nextR = new LinkedHashMap<Pair<V>, Float>();
//		for (int k = 0; k < kMax; k++) {
//			for (V a : g.getVertices()) {
//				for (V b : g.getVertices()) {
//					float sum = computeSum(g, a, b, currentR);
//					Pair<V> ab = new Pair<V>(a, b);
//					int sia = g.inDegree(a);
//					int sib = g.inDegree(b);
//					if (sia == 0 || sib == 0) {
//						nextR.put(ab, 0.0f);
//					} else {
//						nextR.put(ab, C / (sia * sib) * sum);
//					}
//				}
//			}
//
//			// System.out.println("After iteration "+k);
//			// print(g, nextR);
//
//			Map<Pair<V>, Float> temp = nextR;
//			nextR = currentR;
//			currentR = temp;
//		}
//		return currentR;
//	}
//
//	/**
//	 * Compute the sum of all SimRank values of all incoming neighbors of the
//	 * given vertices
//	 *
//	 * @param <V>
//	 *            The vertex type
//	 * @param g
//	 *            The graph
//	 * @param a
//	 *            The first vertex
//	 * @param b
//	 *            The second vertex
//	 * @param simRank
//	 *            The current SimRank
//	 * @return The sum of the SimRank values of the incoming neighbors of the
//	 *         given vertices
//	 */
//	private static <V> float computeSum(Graph<V, ?> g, V a, V b,
//			Map<Pair<V>, Float> simRank) {
//		Collection<V> ia = g.getPredecessors(a);
//		Collection<V> ib = g.getPredecessors(b);
//		float sum = 0;
//		for (V iia : ia) {
//			for (V ijb : ib) {
//				Pair<V> key = new Pair<V>(iia, ijb);
//				Float r = simRank.get(key);
//				sum += r;
//			}
//		}
//		return sum;
//	}
//
//
//	/**
//	 * Print a table with the SimRank values
//	 *
//	 * @param <V>
//	 *            The vertex type
//	 * @param g
//	 *            The graph
//	 * @param simRank
//	 *            The SimRank
//	 */
//	private static <V> void print(Graph<V, ?> g, Map<Pair<V>, Float> simRank) {
//		final int columnWidth = 8;
//		final String format = "%4.3f";
//
//		List<V> vertices = new ArrayList<V>(g.getVertices());
//		System.out.printf("%" + columnWidth + "s", "");
//		for (int j = 0; j < vertices.size(); j++) {
//			String s = String.valueOf(vertices.get(j));
//			System.out.printf("%" + columnWidth + "s", s);
//		}
//		System.out.println();
//
//		for (int i = 0; i < vertices.size(); i++) {
//			String s = String.valueOf(vertices.get(i));
//			System.out.printf("%" + columnWidth + "s", s);
//			for (int j = 0; j < vertices.size(); j++) {
//				V a = vertices.get(i);
//				V b = vertices.get(j);
//				Pair<V> ab = new Pair<V>(a, b);
//				Float value = simRank.get(ab);
//				String vs = String.format(Locale.ENGLISH, format, value);
//				System.out.printf("%" + columnWidth + "s", vs);
//			}
//			System.out.println();
//		}
//	}
//
//}
