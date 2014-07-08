package be.fnord.util.QUAL.Prefs;

public interface PREF_FUNC {

	static Object bot = null;
	static Object top = null;

	public <T> String combine(T a, T b);

	public <T> boolean compare(T a, T b);

}
