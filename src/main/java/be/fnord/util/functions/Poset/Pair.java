package be.fnord.util.functions.Poset;

/**
 * Copy pasted from
 * http://stackoverflow.com/questions/156275/what-is-the-equivalent
 * -of-the-c-pairl-r-in-java
 *
 *
 * @param <A>
 * @param <B>
 */
public class Pair<A, B> {
	public int extra = 1; // Really bad hack for sentence similarity
	private A first;
	private B second;

	public Pair(A first, B second) {
		super();
		this.first = first;
		this.second = second;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object other) {
		if (other instanceof Pair) {

			Pair<A, B> otherPair = (Pair<A, B>) other;
			return (this.first == otherPair.first || this.first != null
					&& otherPair.first != null
					&& this.first.equals(otherPair.first))
					&& (this.second == otherPair.second || this.second != null
							&& otherPair.second != null
							&& this.second.equals(otherPair.second));
		}

		return false;
	}

	public A getFirst() {
		return first;
	}

	public B getSecond() {
		return second;
	}

	@Override
	public int hashCode() {
		int hashFirst = first != null ? first.hashCode() : 0;
		int hashSecond = second != null ? second.hashCode() : 0;

		return (hashFirst + hashSecond) * hashSecond + hashFirst;
	}

	public void setFirst(A first) {
		this.first = first;
	}

	public void setSecond(B second) {
		this.second = second;
	}

	@Override
	public String toString() {
		return "(" + first + ", " + second + ")";
	}
}
