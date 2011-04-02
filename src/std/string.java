package std;

public class string {
	public static String endl =System.getProperty("line.separator");
	public static String breakString = "founditHere";
	public static String path = System.getProperty("file.separator");
	public static String userDir = System.getProperty("user.dir");
	public static String homeDir = System.getProperty("user.home");

	public static String prover9sucess = "THEOREM PROVED";
	public static String prover9failed = "SEARCH FAILED";
	
	public static String prover9Path = "e:" + path + "prover9" + path;
	public static String prover9Binary = prover9Path + "bin" + path + "prover9.exe";
	public static String prover9Input = prover9Path + "in.txt";
	public static String prover9Output = prover9Path + "out.txt";
	public static String prover9MaxTime = "1";
	
	public static String prover9ExecString = prover9Binary + " -f " + prover9Input; // + " > " + prover9Output;
	
	public static String homeWeb = "http://www.dlab.uow.edu.au/textseer/";
	public static String version = "0.1";
	
	public static String title = "TextSeer";
	
	public static String rightArrow =" > ";
	
	public static String propertyNotFound = "notFound";
	public static String propertyFilename = "textseer.properties.dat";
	
	public static String visted = "VISITED";
	public static String not_visited = "NOT";
	
	
	public static boolean debug = true;
	public static boolean showPopups = false;
}
