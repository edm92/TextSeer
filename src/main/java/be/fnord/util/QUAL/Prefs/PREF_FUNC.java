package be.fnord.util.QUAL.Prefs;


public interface PREF_FUNC {
	
	static Object top = null;
	static Object bot = null;
	
	public <T> boolean compare(T a, T b);
	public <T> String combine(T a, T b);
	
	
}
