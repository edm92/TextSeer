import be.fnord.collections.Pair;
import be.fnord.collections.Poset;


public class PosetExample {
	static <T> Pair<T,T> P(T a, T b){
		return new Pair<T,T>(a,b);
	}
	
	
	@SuppressWarnings("unchecked")
	public static void main(String [] args){
		Poset p = new Poset();
		p.List(P("a", "b"), 
				P("1", "a"),
				P("a", "1"),
				P("b","c"),
				P("a","d"),
				P("d","c"));
		System.out.println("leq(a,a) = " + p.leq("a","a") + " (reflexivity)");
		System.out.println("leq(a,1) = " + p.leq("a","1") + " & leq(1,a) = " + p.leq("1", "a") + " finally eq(1,a) = " + p.eq("1", "a") + " (antisymmetry)");
		System.out.println("leq(a,b) = " + p.leq("a","b") + " & leq(b,c) = " + p.leq("b","c") + " finally leq(a,c) = " + p.leq("a", "c") + " (transitivity)");
		System.out.println("eq(a,b) = " + p.eq("a","b"));
		System.out.println("leq(b,a) = " + p.eq("b","a"));
		System.out.println("lt(a,c) = " + p.lt("a","c"));
		System.out.println("lt(c,a) = " + p.lt("c","a"));
	}
	
}
