/**
 * TextSeer, development prototype for ProcessSeer
    Copyright (C) 2011 Evan Morrison

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * @author Evan Morrison (edm92@uow.edu.au)
 *
 */


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
	public static String knowledgeBase = "knowledge.dat";
	public static String prover9ExecString = prover9Binary + " -f " + prover9Input; // + " > " + prover9Output;
	
	public static String homeWeb = "http://www.dlab.uow.edu.au/textseer/";
	public static String version = "0.1";
	
	public static String title = "TextSeer";
	
	public static String rightArrow =" > ";
	
	public static String propertyNotFound = "notFound";
	public static String propertyFilename = "textseer.properties.dat";
	
	public static String database = "process.dat";
	
	public static String visted = "VISITED";
	public static String not_visited = "NOT";
	
	public static String openFile = "newpkg1.xpdl";
	public static String filePath = "e:" + path + "workspace" + path + "XPDL_Loader" + path;
	
	public static boolean debug = false;
	public static boolean showPopups = false;
	
	public static String license = " TextSeer  Copyright (C) 2011  Evan Morrison <br/>"+
    "This program comes with ABSOLUTELY NO WARRANTY; <br/>"+
    "This is free software, and you are welcome to redistribute it<br/>"+
    "under certain conditions; See the readme file for license details<br/>";
}
