package au.edu.dsl.dlab.processtools.parser;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import au.edu.dsl.dlab.processtools.Edge;
import au.edu.dsl.dlab.processtools.Graph;
import au.edu.dsl.dlab.processtools.Vertex;
import au.edu.dsl.dlab.processtools.parser.bpmn.Activiti;

public class PNML  <T extends Vertex,V extends Edge> extends Activiti<T,V>{
	protected transient static Logger logger = Logger.getLogger("GraphClass");
	static String inputFile = "Accounting Services .bpmn20.xml";
	String _filename = "";
	public TreeMap<String, T> vertexMap = new TreeMap<String, T>();
	public TreeMap<String, String> edgeSourceMap = new TreeMap<String, String>();
	public TreeMap<String, String> edgeTargetMap = new TreeMap<String, String>();
	public TreeMap<String, V> edgeMap = new TreeMap<String, V>();
	public TreeMap<String, Graph<T,V>> subProcessMap = new TreeMap<String, Graph<T,V>>();
	
	TreeMap<String, LinkedList<LinkedList<String>>> myFixedEdges = new TreeMap<String, LinkedList<LinkedList<String>>>();
	TreeMap<String, String> edgeType = new TreeMap<String, String>();
	TreeMap<String,LinkedList<String>> sources = new TreeMap<String,LinkedList<String>>();
	TreeMap<String,LinkedList<String>> targets = new TreeMap<String,LinkedList<String>>();
	HashSet<String> possibleXORsrc = new HashSet<String>();
	HashSet<String> possibleXORtrg = new HashSet<String>();
	
	
	public static boolean PREPARE_VISUAL_MODELS = false; 
	Document dom;

	private Graph<Vertex, Edge> loadingGraph = null; 
	
	public Graph<Vertex, Edge> loadModel(String input){
		inputFile = input;
		loadingGraph = null; // Reinitialize as null each time.
		Graph<Vertex, Edge> output;		
		PNML<Vertex,Edge> myProcess = new PNML<Vertex,Edge>();
		myProcess.parseXmlFile(inputFile);
		output = myProcess.parseDocument(inputFile);
		if(output != null)
		output.name = input;
		return output;
	}
	
	/**
	 * Example 
	 */
//	public static void main(String[] args) {
//		
//		Activiti<Vertex,Edge> myProcess = new Activiti<Vertex,Edge>();
//		myProcess.parseXmlFile(inputFile);
//		Graph<Vertex, Edge> output = myProcess.parseDocument(inputFile);
//		myProcess.parseGPSDocument(inputFile);
//		System.out.println("Output: " + output);
//	    
//	      
//		
//	}
	
	/****************************
	 * 
	 * Override the following for more detailed vertex and edge xml reading 
	 * 
	 ****************************/
	
	/***************************
	 * 
	 * Reading the generic structure of a node and edge
	 * 
	 ****************************/
	
	
	void parseXmlFile(String filename){
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			dom = db.parse(filename);


		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public Graph<T,V> parseDocument(String filename){
		//get the root element
		
		Element docEle = dom.getDocumentElement();
		_filename = filename;
		//get a nodelist of elements
		NodeList nl = docEle.getElementsByTagName("net");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				Element el = (Element)nl.item(i);
				//System.err.println("Looking t " + el);
				Graph<T,V> newProc = (Graph<T, V>) getProcess(el, filename);
				
				return newProc;
				//add it to list
				//myEmpls.add(e);
			}
		}
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	public Graph<T, V> getProcess(Element pEl, String filename) {
		Graph<Vertex,Edge> myModel = new Graph<Vertex,Edge>();
		if(loadingGraph == null) loadingGraph = (Graph<Vertex, Edge>) myModel;
		myModel.filename = filename;
		LinkedList<String> myNodes = new LinkedList<String>();
		LinkedList<String> myEdges = new LinkedList<String>();
		NodeList nl = pEl.getElementsByTagName("name");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				Element el = (Element)nl.item(i);
				String Results = (String) getText(el);
				if(Results != null && Results.length() > 2 && !Results.contains("Petrinet from")){
					if(Results.contains("-->"))
						myEdges.add(Results);
					else{
						if(!(Results.compareTo("Start") == 0 || Results.compareTo("End") == 0))
						myNodes.add(Results);
					}
				}
			}
		}
		
		for(String nodes : myNodes){
			//System.err.println("Node:" + nodes);
			Vertex myNode = new Vertex(nodes);
			vertexMap.put(nodes.trim(), (T) myNode);
			myModel.addVertex(myNode);
		}
		
		// Edge set , sources - > edgeset , targets -> edgeset
		// edgeSet linkedList<TreeMap>
		
		myFixedEdges = new TreeMap<String, LinkedList<LinkedList<String>>>();
		edgeType = new TreeMap<String, String>();
		sources = new TreeMap<String,LinkedList<String>>();
		targets = new TreeMap<String,LinkedList<String>>();
		possibleXORsrc = new HashSet<String>();
		possibleXORtrg = new HashSet<String>();
		boolean isFirstEdge = true;
		String firstEdge = "";

		myEdges = cleanEdges(myEdges,  myModel);
		
		for(String edges : myEdges){
			if(isFirstEdge) {firstEdge = edges; isFirstEdge = false;};
			processEdge(edges);
		}
		processEdge(firstEdge);

		LinkedList<String> edgeAdded = new LinkedList<String>();
		for(String src: sources.keySet()){
			for(String trg : sources.get(src)){
				boolean set = false;
				if(edgeAdded.contains(src+"-->"+trg)) continue;
				for(String _type : edgeType.keySet()){
					if(_type.contains(src+"-->"+trg)){
						set = true;
						// Make a gateway if none already
						String gateName = "";
						if(edgeType.get(_type).contains("JOIN"))
							gateName= "GATE"+trg.replaceAll("\\+complete","")+edgeType.get(_type);
						else
							gateName= "GATE"+src.replaceAll("\\+complete","")+edgeType.get(_type);
						
						if(!vertexMap.containsKey(gateName)){
							//System.err.println("New gate " + gateName);
							Vertex v = new Vertex(gateName);
							myModel.addVertex(v);
							vertexMap.put(v.name,(T) v);
							if(edgeType.get(_type).contains("AND")) v.makeANDGate();
							else v.makeXORGate();
						}
						
						Vertex myGate = vertexMap.get(gateName);
						myModel.addEdge(vertexMap.get(src), myGate);

						myModel.addEdge(myGate, vertexMap.get(trg));
						
						edgeAdded.add(src+"-->"+trg);
						break;
					}
				}
				if(!set){
					myModel.addEdge(vertexMap.get(src), vertexMap.get(trg));
					edgeAdded.add(src+"-->"+trg);
				}
			}
		}
		

//		System.out.println(sources);
//		System.out.println(targets);
//		System.out.println(edgeType);
		
		// Check if there is an error
		//Vertex.TO_STRING_WITH_EFFECTS = true;
		myModel.cleanup(false);
//		myModel.printPaths();
		
		for(Vertex v: myModel.vertexSet()){
			if(v.name.contains("End+complete")) v.name = "ENDEDALL+complete";
		}
		
		return (Graph<T, V>) myModel;
	}
	
	private static int generic = 1; 
	
	@SuppressWarnings("unchecked")
	private LinkedList<String> cleanEdges(LinkedList<String> myEdges, Graph<Vertex, Edge> myModel) {
		LinkedList<String> clean = new LinkedList<String>();
		for(String edge: myEdges){
			StringTokenizer st = new StringTokenizer(edge, "-->");
			if(st.countTokens() == 2){
				// We have a well formed edge to play with 
				String first = st.nextToken(); 
				String second = st.nextToken();
				StringTokenizer stFirst = new StringTokenizer(first, ",");
				StringTokenizer stSecond = new StringTokenizer(second, ",");
				if(stFirst.countTokens() > 1 && stSecond.countTokens() > 1){
					String newy = "GEN"+generic+"+complete";
					Vertex newyv = new Vertex(newy);
					myModel.addVertex(newyv);
					vertexMap.put(newy.trim(), (T) newyv);
					clean.add(first + " --> " + newy);
					clean.add(newy + " --> " + second);
				}else
					clean.add(edge);
			}
		}
		return clean;
	}

	private void processEdge(String edges) {
		//System.out.println("Processing " + edges);
		StringTokenizer st = new StringTokenizer(edges, "-->");
		if(st.countTokens() == 2){
			// We have a well formed edge to play with 
			String first = st.nextToken(); 
			String second = st.nextToken();
			first = first.replaceAll("\\[" , "").replaceAll("\\]", "").replaceAll("\\{","").replaceAll("\\}","");
			second = second.replaceAll("\\[" , "").replaceAll("\\]", "").replaceAll("\\{","").replaceAll("\\}","");
			StringTokenizer stFirst = new StringTokenizer(first, ",");
			StringTokenizer stSecond = new StringTokenizer(second, ",");
			HashSet<String> sourceSet = new HashSet<String>();
			HashSet<String> targetSet = new HashSet<String>();
			LinkedList<String> trimmedSrc = new LinkedList<String>();
			LinkedList<String> trimmedTrg = new LinkedList<String>();
			while(stFirst.hasMoreTokens()){ String trimmed = stFirst.nextToken().trim(); sourceSet.add(trimmed); trimmedSrc.add(trimmed);}
			while(stSecond.hasMoreTokens()){ String trimmed = stSecond.nextToken().trim(); targetSet.add(trimmed); trimmedTrg.add(trimmed);}	
			
			if(trimmedSrc.size() > 1) possibleXORsrc.addAll(trimmedSrc);
			if(trimmedTrg.size() > 1) possibleXORtrg.addAll(trimmedTrg);
			
			//System.out.println("possibleXORsrc" + possibleXORsrc);
			//System.out.println("possibleXORtrg" + possibleXORtrg);
			
			//System.err.println("SourceSet " + sourceSet);
			//System.err.println("targetSet " + targetSet);
			
			for(String src : sourceSet){
				for(String trg : targetSet){
					LinkedList<String> targetsForSrc = sources.get(src);
					if(targetsForSrc == null) targetsForSrc = new LinkedList<String>();
					if(!targetsForSrc.contains(trg))
						targetsForSrc.add(trg);
					sources.put(src, targetsForSrc);
				}
			}
			
			for(String trg : targetSet){
				for(String src : sourceSet){
					LinkedList<String> sourcesForTrg = targets.get(trg);
					if(sourcesForTrg == null) sourcesForTrg = new LinkedList<String>();
					if(!sourcesForTrg.contains(src))
						sourcesForTrg.add(src);
					targets.put(trg, sourcesForTrg);
				}
			}
			
			// Say if And split
			for(String src : sources.keySet()){
				if(sources.get(src).size() > 1){
					// AND Gateway
					for(String trg : sources.get(src)){
						int i = 0;
						String s = "";
						do{
							String key = src+"-->"+trg+s;
							if(edgeType.containsKey(key)){
								if(edgeType.get(key).compareTo("AND-SPLIT") == 0 || edgeType.get(key).compareTo("XOR-SPLIT") == 0) break;
								s = "" + ++i;
							}else{
								if(!possibleXORsrc.contains(src) && !possibleXORtrg.contains(trg))
									edgeType.put(key, "AND-SPLIT");
								else
									edgeType.put(key, "XOR-SPLIT");
								break;
							}							
						}while(true);
					}
				}
			}
			// say if And join
			for(String trg : targets.keySet()){
				if(targets.get(trg).size() > 1){
					// AND Gateway
					for(String src : targets.get(trg)){
						int i = 0;
						String s = "";
						do{
							String key = src+"-->"+trg+s;
							if(edgeType.containsKey(key)){
								if(edgeType.get(key).compareTo("AND-JOIN") == 0  || edgeType.get(key).compareTo("XOR-JOIN") == 0) break;
								s = "" + ++i;
							}else{
								if(!possibleXORsrc.contains(src) && !possibleXORtrg.contains(trg))
									edgeType.put(key, "AND-JOIN");
								else
									edgeType.put(key, "XOR-JOIN");
								break;
							}							
						}while(true);
					}
				}
			}
			
			//System.err.println("Edge from " + sourceSet + " to -> to " + targetSet);
		}
		
	}

	String getText(Element el){
		
		return getTextValue(el, "text");
	}

	
	Object methodCaller(Object theObject, String methodName, Element el) {
		   try {
			   //System.err.println("methodName:" + methodName + " ; theObject: " + theObject + " ; el:" + el);
			 Object myFunction = theObject.getClass().getMethod(methodName, Element.class); //.invoke(theObject);
			 Object result = ((Method) myFunction).invoke(theObject, el);
			 return result;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			//e.printStackTrace();
			System.err.println("Ignoring error"); logger.error("Invocation Target Exception");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
		
		}


	/**
	 * I take a xml element and the tag name, look for the tag and get
	 * the text content
	 * i.e for <employee><name>John</name></employee> xml snippet if
	 * the Element points to employee node and tagName is 'name' I will return John
	 */
	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}
		if(textVal == null){
			textVal = ele.getAttribute(tagName);
		}

		return textVal;
	}


	/**
	 * Calls getTextValue and returns a int value
	 */
//	private int getIntValue(Element ele, String tagName) {
//		//in production application you would catch the exception
//		return Integer.parseInt(getTextValue(ele,tagName));
//	}	
	
	public static String createPNet(Graph<Vertex, Edge> myResult) {
		int nodeNumber = 1;
		TreeMap<String, String> nameNode = new TreeMap<String, String>();
		TreeMap<String, Vertex> nameVertex = new TreeMap<String, Vertex>();
		TreeMap<String, String> nameXML = new TreeMap<String, String>();
		TreeMap<String, String> arcXML = new TreeMap<String, String>();
		
		String places = "";
		String transistions = "";
		String arcs = "";
		String currentModel = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><pnml><net id=\"net1\" type=\"\"><name><text>Cleaned Petrinet TextSeerProcessTools</text></name><page id=\"node0\">";
		
		// Make all the transistions
		for(Vertex v: myResult.vertexSet()){
			String nodeID = "node" + nodeNumber++;
			nameNode.put(v.name, nodeID);
			nameVertex.put(v.name, v);
			//if(!v.name.contains("GATE")) 
			nameXML.put(v.name, "<transition id=\""+nodeID+"\"><name><text>"+v.name+"</text></name></transition>");
			//System.err.println("<transition id=\""+nodeID+"\"><name><text>"+v.name+"</text></name></transition>");
		}
		
		for(Edge e: myResult.edgeSet()){
			if(myResult.getEdgeSource(e).isActivity() && myResult.getEdgeTarget(e).isActivity() ){
				String nodeID = "node" + nodeNumber++;
				String name = e.toString(myResult);
				nameNode.put(name, nodeID);
				nameXML.put(name, "<place id=\""+nodeID+"\"><name><text>"+name+"</text></name></place>");
				
				nodeID = "arc" + nodeNumber++;
				arcXML.put(name, "<arc id=\""+nodeID+"\" source=\"" + nameNode.get(myResult.getEdgeSource(e).name) + 
						"\" target=\"" + nameNode.get(myResult.getEdgeTarget(e).name)  + "\"><name><text>1</text></name><arctype><text>normal</text></arctype></arc>");
			}else{
				if(myResult.getEdgeTarget(e).isGate() && 
						(myResult.getEdgeTarget(e).name.contains("SPLIT") && myResult.outDegreeOf(myResult.getEdgeTarget(e)) > 1)){
					//System.err.println("Looking at " + e.toString(myResult));
					String nodeID = "node" + nodeNumber++;
					String name = e.toString(myResult);
					nameNode.put(name, nodeID);
					// Make edges for opening 
					nameXML.put(name, "<place id=\""+nodeID+"\"><name><text>"+name+"</text></name></place>");
					// Make edges for closing
					String compname ="";
					if(myResult.outgoingEdgesOf(myResult.getEdgeTarget(e).CorrespondingVertex).size() > 1) System.err.println("Error here - more than one");
					for(Edge eo: myResult.outgoingEdgesOf(myResult.getEdgeTarget(e).CorrespondingVertex)){
						compname = myResult.getEdgeTarget(eo).name;
						//System.err.println("Comp target = " + compname);
					}
					if(compname.length() > 0){
						nodeID = "node" + nodeNumber++;
						name =  "{" + myResult.getEdgeTarget(e).CorrespondingVertex.name + "} --&gt; {" + compname + "}";
						nameNode.put(name, nodeID);
						nameXML.put(name, "<place id=\""+nodeID+"\"><name><text>"+name+"</text></name></place>");
					}
					nodeID = "arc" + nodeNumber++;
					arcXML.put(name, "<arc id=\""+nodeID+"\" source=\"" + nameNode.get(myResult.getEdgeSource(e).name) + 
							"\" target=\"" + nameNode.get(myResult.getEdgeTarget(e).name)  + "\"><name><text>1</text></name><arctype><text>normal</text></arctype></arc>");
					nodeID = "arc" + nodeNumber++;
					arcXML.put(name, "<arc id=\""+nodeID+"\" source=\"" + nameNode.get(myResult.getEdgeTarget(e).CorrespondingVertex.name) + 
							"\" target=\"" + nameNode.get(compname)  + "\"><name><text>1</text></name><arctype><text>normal</text></arctype></arc>");					
					
					if(myResult.getEdgeTarget(e).isXOR()){
						if(myResult.getEdgeTarget(e).CorrespondingVertex != null){
							nodeID = "node" + nodeNumber++;
							name = "";
							for(Edge oe: myResult.outgoingEdgesOf(myResult.getEdgeTarget(e))){
								nodeID = "arc" + nodeNumber++;
								arcXML.put(name, "<arc id=\""+nodeID+"\" source=\"" + nameNode.get(myResult.getEdgeTarget(e).CorrespondingVertex.name) + 
										"\" target=\"" + nameNode.get(myResult.getEdgeTarget(oe).name)  + "\"><name><text>1</text></name><arctype><text>normal</text></arctype></arc>");
								name += myResult.getEdgeTarget(oe).name + ", ";					
							}
							if(name.length() > 2) name = name.substring(0, name.length() - 2);
							name = "{" + myResult.getEdgeTarget(e).name + "} --> {[" + name + "]}";
							nameNode.put(name, nodeID);
							nameXML.put(name, "<place id=\""+nodeID+"\"><name><text>"+name+"</text></name></place>");

							// Close gateway
							nodeID = "node" + nodeNumber++;
							name = "";
							for(Edge oe: myResult.incomingEdgesOf(myResult.getEdgeTarget(e).CorrespondingVertex)){
								name += myResult.getEdgeSource(oe).name + ", ";	
								nodeID = "arc" + nodeNumber++;
								arcXML.put(name, "<arc id=\""+nodeID+"\" source=\"" + nameNode.get( myResult.getEdgeSource(oe).name) + 
										"\" target=\"" + nameNode.get(myResult.getEdgeTarget(e).CorrespondingVertex.name)  + "\"><name><text>1</text></name><arctype><text>normal</text></arctype></arc>");
							}
							if(name.length() > 2) name = name.substring(0, name.length() - 2);
							name = "{[" + name + "]} --> {" + myResult.getEdgeTarget(e).CorrespondingVertex.name + "}";
							nameNode.put(name, nodeID);
							nameXML.put(name, "<place id=\""+nodeID+"\"><name><text>"+name+"</text></name></place>");

							
						}						
					}else{	// AND Gateways
						if(myResult.getEdgeTarget(e).CorrespondingVertex != null){
							for(Edge oe: myResult.outgoingEdgesOf(myResult.getEdgeTarget(e))){
								nodeID = "node" + nodeNumber++;
								name = oe.toString(myResult);
								nameNode.put(name, nodeID);
								nameXML.put(name, "<place id=\""+nodeID+"\"><name><text>"+name+"</text></name></place>");
								nodeID = "arc" + nodeNumber++;
								arcXML.put(name, "<arc id=\""+nodeID+"\" source=\"" + nameNode.get(myResult.getEdgeTarget(e).name) + 
										"\" target=\"" + nameNode.get(myResult.getEdgeTarget(oe).name)  + "\"><name><text>1</text></name><arctype><text>normal</text></arctype></arc>");
							}

							for(Edge oe: myResult.incomingEdgesOf(myResult.getEdgeTarget(e).CorrespondingVertex)){
								nodeID = "node" + nodeNumber++;
								name = oe.toString(myResult);
								nameNode.put(name, nodeID);
								nameXML.put(name, "<place id=\""+nodeID+"\"><name><text>"+name+"</text></name></place>");
								nodeID = "arc" + nodeNumber++;
								arcXML.put(name, "<arc id=\""+nodeID+"\" source=\"" + nameNode.get(myResult.getEdgeSource(oe).name) + 
										"\" target=\"" + nameNode.get(myResult.getEdgeTarget(e).CorrespondingVertex.name)  + "\"><name><text>1</text></name><arctype><text>normal</text></arctype></arc>");
								
							}
						}
					}

					
				}else{
					// Ignore 
				
				}
			}
		}
		for(String s: nameXML.values())
			places += s;
//			System.err.println(";;" + s);
		for(String s: arcXML.values())
			arcs += s;
//			System.err.println(";;" + s);
		currentModel += places + transistions + arcs + "</page></net></pnml>";
		return currentModel;
	}

	

}

