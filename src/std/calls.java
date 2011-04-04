package std;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import textSeer.Model.Vertex;

public class calls {

	//public static int currentMarker = 0;
	
	public static int popupquery(String msg, String title){
		Object[] options = {"No",
        "No"};
		int n = 0;
		if(std.string.showPopups){
		n = JOptionPane.showOptionDialog(null,
			    msg,
			    title, JOptionPane.YES_NO_OPTION,
			    JOptionPane.QUESTION_MESSAGE,
			    null,     //do not use a custom Icon
			    options,  //the titles of buttons
			    options[0]); //default button title
		}else
			return 0;
		
		return n;
	}
	
	public static boolean showResult(String displayDetails){
		
		gui.results.resultsArea.append("\n" + displayDetails);
        
        
		return true;
	}	
	
	public static boolean display(String displayDetails){
		
		String threadName = Thread.currentThread().getName();
        System.out.format("%s: %s%n", threadName, displayDetails);
        
        
		return true;
	}
	
	public static boolean debug(String displayDetails){
		
		String threadName = Thread.currentThread().getName();
        System.out.format("%s: %s%n", threadName, displayDetails);
               
		return true;
	}
	
	public static void shutdown(){
		System.exit(0);
	}
	
	public static void init(){
		string.prover9Path = std.Property.getProperty("prover9Path");
		string.prover9Binary = string.prover9Path + std.Property.getProperty("prover9Binary");
		string.prover9Input = std.Property.getProperty("prover9TempPath") + std.Property.getProperty("prover9Input");
		string.prover9Output = std.Property.getProperty("prover9TempPath") + std.Property.getProperty("prover9Output");
		string.prover9ExecString = string.prover9Binary + " -f " + string.prover9Input;
		string.prover9MaxTime = std.Property.getProperty("prover9maxTime");
		string.filePath = std.Property.getProperty("filePath");
		string.knowledgeBase = std.Property.getProperty("knowledge");
	}
	
	public static long makeMarker(int Cmarker){
		if(Cmarker == 0) 
			return (long) (java.lang.Math.pow(2 , Cmarker));
		else
			return (long) (1 + java.lang.Math.pow(2 , Cmarker));
	}
	
	public static Set<Long> getMarkers(Long l){
		Set<Long> mySet = new HashSet<Long>();
		for(long i = 0 ; i < 63; i++){
			Long test = (long) (1 + java.lang.Math.pow(2 , i));
			if((l & test) == 1)
					mySet.add(test);
			if(i > l)
				break;
			
		}
		
		return mySet;
	}
	
	public class method{
		public Object theObject;
		public String methodName;
		public Vertex VertexArg;
		public method(Object myObject, String myMethod){
			theObject = myObject;
			methodName = myMethod;
			VertexArg = new Vertex(null);
		}
	}
	// Really dodgy hack. Call reflections to inject function into search classes.
	// In this case we fix the method to Vertex as in args.
	// Call return with method.invoke(obj, 4);
	/* Example:
	 * String theDescription = methodCaller(object1, "toString");
	 *	Class theClass = methodCaller(object2, "getClass");
	 */
	Object methodCaller(method FunctionCall) {
		   try {
			   Method myMethod = FunctionCall.theObject.getClass().getMethod(FunctionCall.methodName, new Class[] {Vertex.class});
			return myMethod.invoke(FunctionCall.theObject, FunctionCall.VertexArg);
		} catch (IllegalArgumentException e) {
			debug(e.toString());
		} catch (SecurityException e) {
			debug(e.toString());
		} catch (NoSuchMethodException e) {
			debug(e.toString());
		} catch (IllegalAccessException e) {
			debug(e.toString());
		} catch (InvocationTargetException e) {
			debug(e.toString());
		}
		return null;
		}

	
}
