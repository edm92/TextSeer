package be.fnord.util.QUAL.Prefs;

import java.util.TreeSet;

import be.fnord.util.functions.Poset.Pair;
import be.fnord.util.functions.Poset.Poset;

public class MIN_SKILL extends Preferences<String> implements PREF_FUNC {

	public enum hml {
		HIGH("HIGH"), LOW("LOW"), MED("MED");

		String _type = "HIGH";
		hml type;

		hml(String _set) {
			_type = _set;

		}

		@Override
		public String toString() {
			return _type;
		}

		public hml toType(String _set) {
			if (_set.compareTo("HIGH") == 0) {
				type = hml.HIGH;
			}
			if (_set.compareTo("MED") == 0) {
				type = hml.MED;
			}
			if (_set.compareTo("LOW") == 0) {
				type = hml.LOW;
			}
			return type;
		}

	};

	static hml bot = hml.HIGH;
	static hml top = hml.LOW;

	public static void main(String[] args) {
		MIN_SKILL m = new MIN_SKILL();
		a.e.println("isBetter(bot,top) = " + m.compare(bot, top));
		a.e.println("isBetter(top,bot) = " + m.compare(top, bot));
		a.e.println("isBetter(top,MED) = " + m.compare(top, hml.MED));
		a.e.println("Combine(top,bot) = " + m.combine(top, bot));
		a.e.println("Combine(bot,top) = " + m.combine(bot, top));
	}

	static <T> Pair<T, T> P(T a, T b) {
		return new Pair<T, T>(a, b);
	}

	Poset hmlSet;

	Ranges.type range = Ranges.type.HIGH_MED_LOW;

	public MIN_SKILL() {
		super();
		setup();
	}

	@Override
	public <T> String combine(T a, T b) {
		if (hmlSet.leq(a, b)) {
			return a.toString();
		} else {
			return b.toString();
		}
	}

	@Override
	public <T> boolean compare(T a, T b) {
		return hmlSet.leq(a, b);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setup() {
		TreeSet<String> s = new TreeSet<String>();
		s.add(hml.HIGH.toString());
		s.add(hml.MED.toString());
		s.add(hml.LOW.toString());
		hmlSet = new Poset();
		hmlSet.List(P(hml.LOW.toString(), hml.MED.toString()),
				P(hml.MED.toString(), hml.HIGH.toString()));
	}

}
