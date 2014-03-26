package be.fnord.collections;
import java.util.*; 


/**
 * POSET 
 * From wikipedia - 
 * A (non-strict) partial order is a binary relation "≤" over a set P which is reflexive, antisymmetric, and transitive, 
 * i.e., which satisfies for all a, b, and c in P:
		1) a ≤ a (reflexivity);
		2) if a ≤ b and b ≤ a then a = b (antisymmetry);
		3) if a ≤ b and b ≤ c then a ≤ c (transitivity).
		
	The use of this class is pretty simple, declare a new POSET
	Poset p = new Poset();
	Then add as many elements as you like to the list (this is a type driven base function)
	p.List( P(anything, anything) , ... );
	
			p.List(P("a", "b"),
					P("b","c"),
					P("a","d"),
					P("d","c"));
					
	The the functions lt (less than) and leq (less than equal to) are available to you for use. 
 * 
 * @author edm92
 *
 */
@SuppressWarnings("unchecked")
public class Poset {
	protected  TreeMap<?, ?> orders; 
	protected  TreeMap<?, ?> backwards;

	
		public  <T> Map<T,HashSet<T>> List(T... elements) { 
			return computeOrders((java.util.List<Pair<T, T>>) new LinkedList<T>(Arrays.asList(elements))); 
		} 

		
		public  <T> Map<T,HashSet<T>> computeOrders(List<Pair<T,T>> s){
			orders = new TreeMap<T,HashSet<T>>();
			backwards = new TreeMap<T,HashSet<T>>();
			for(Pair<T,T> p : s){
				if(orders.containsKey(p.getFirst())){ 
					((HashSet<T>)orders.get(p.getFirst())).add((T) p.getSecond());
				}else{
					HashSet<T> h = new HashSet<T>();
					h.add(p.getSecond());
					((TreeMap<T,HashSet<T>>)orders).put(p.getFirst(), h);
				}
			}
			
			// Do transitive closure
			int backHash = backwards.hashCode();
			int orderHash = orders.hashCode();
			int _backHash = backwards.hashCode();
			int _orderHash = orders.hashCode();
			do{
				backHash = backwards.hashCode();
				orderHash = orders.hashCode();
				doClosure();
				_backHash = backwards.hashCode();
				_orderHash = orders.hashCode();
			}while(backHash != _backHash || orderHash != _orderHash);
			return (Map<T, HashSet<T>>) orders;
		}
		
		public <T> void doClosure(){
			for(T key : ((TreeMap<T,HashSet<T>>)orders).keySet()){
				for(T ele : ((HashSet<T>)orders.get(key))){
					if( ((TreeMap<T,HashSet<T>>)backwards).containsKey(ele)){
						 ((TreeMap<T,HashSet<T>>)backwards).get(ele).add(key);
						 if( ((TreeMap<T,HashSet<T>>)backwards).containsKey(key))
							 ((TreeMap<T,HashSet<T>>)backwards).get(ele).addAll(((TreeMap<T,HashSet<T>>)backwards).get(key));
					}else{
						HashSet<T> h = new HashSet<T>();
						h.add(key);
						h.add(ele);
						((TreeMap<T,HashSet<T>>)backwards).put(ele, h);
					}
				}
			}
		}
		
		public <T> boolean lt(T a, T b){
			if(leq(a,b) && !eq(a,b)) return true;
			else return false;
		}

		public <T> boolean eq(T a, T b){
			return leq(a,b) && leq(b,a);
		}
		
		public <T> boolean leq(T a, T b){	
			
			if(backwards != null && (backwards.containsKey(a) || backwards.containsKey(b))){
				if( 
						(
							(backwards.containsKey(b) &&
							((HashSet<T>)backwards.get(b)).contains(a))
						)) 
					return true;
			}
			return false;
		}
		
		
		
		
		public static void main(String [] args){
			Poset p = new Poset();
			p.List(P("a", "b"), 
					P("1", "a"),
					P("a", "1"),
					P("b","c"),
					P("a","d"),
					P("d","c"));
			System.out.println("backwards " + p.backwards);
			System.out.println("leq(a,a) = " + p.leq("a","a") + " (reflexivity)");
			System.out.println("leq(a,1) = " + p.leq("a","1") + " & leq(1,a) = " + p.leq("1", "a") + " finally eq(1,a) = " + p.eq("1", "a") + " (antisymmetry)");
			System.out.println("leq(a,b) = " + p.leq("a","b") + " & leq(b,c) = " + p.leq("b","c") + " finally leq(a,c) = " + p.leq("a", "c") + " (transitivity)");
			System.out.println("eq(a,b) = " + p.eq("a","b"));
			System.out.println("leq(b,a) = " + p.eq("b","a"));
			System.out.println("lt(a,c) = " + p.lt("a","c"));
			System.out.println("lt(c,a) = " + p.lt("c","a"));
		}
		
		static <T> Pair<T,T> P(T a, T b){
			return new Pair<T,T>(a,b);
		}
	
}
