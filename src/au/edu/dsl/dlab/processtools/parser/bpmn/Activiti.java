/**
 * TODO Add repository + save to DB (with effects)
 * TODO Deal with cycles so that snippet that can't reach end is added as a new path before start
 */




package au.edu.dsl.dlab.processtools.parser.bpmn;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jgrapht.alg.BellmanFordShortestPath;
import org.jgrapht.alg.CycleDetector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import au.edu.dsl.dlab.processtools.Edge;
import au.edu.dsl.dlab.processtools.Graph;
import au.edu.dsl.dlab.processtools.Vertex;
import au.edu.dsl.dlab.processtools.Vertex._TYPE;

public class Activiti <T extends Vertex,V extends Edge> {
	protected transient static Logger logger = Logger.getLogger("GraphClass");
	static String inputFile = "Accounting Services .bpmn20.xml";
	String _filename = "";
	public TreeMap<String, T> vertexMap = new TreeMap<String, T>();
	public TreeMap<String, String> edgeSourceMap = new TreeMap<String, String>();
	public TreeMap<String, String> edgeTargetMap = new TreeMap<String, String>();
	public TreeMap<String, V> edgeMap = new TreeMap<String, V>();
	public TreeMap<String, Graph<T,V>> subProcessMap = new TreeMap<String, Graph<T,V>>();
	
	public static boolean PREPARE_VISUAL_MODELS = false; 
	Document dom;

	private Graph<Vertex, Edge> loadingGraph = null; 
	
	//Process each type
	String[] processElements = new String[]  { "startEvent", "userTask", "sequenceFlow", "exclusiveGateway", 
												"subProcess", "endEvent", "serviceTask", "parallelGateway"};
	public Graph<Vertex, Edge> loadModel(String input){
		inputFile = input;
		loadingGraph = null; // Reinitialize as null each time.
		Graph<Vertex, Edge> output;
		
		if(PREPARE_VISUAL_MODELS){
			Activiti<Vertex,Edge> myProcess = new Activiti<Vertex,Edge>();
			myProcess.parseXmlFile(inputFile);
			output = myProcess.parseDocument(inputFile);
			output.name = input;
			myProcess.parseGPSDocument(inputFile);
		}else{
			Activiti<Vertex,Edge> myProcess = new Activiti<Vertex,Edge>();
			inputFile = input;
			myProcess.parseXmlFile(inputFile);
			output = myProcess.parseDocument(inputFile);
			output.name = input;
		}
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
	
	public T getstartEvent(Element el){ return getNode(_TYPE.NODE,el); }
	public T getuserTask(Element el){ return getNode(_TYPE.NODE,el);  } 
	public V getsequenceFlow(Element el){ return getEdge(el);  } 
	public T getexclusiveGateway(Element el){ T v = getNode(_TYPE.GATEWAY,el); v.makeXORGate(); return v;  }
	public T getparallelGateway(Element el){ T v = getNode(_TYPE.GATEWAY,el); v.makeANDGate(); return v;  }
	
	
	public T getsubProcess(Element pEl){
		T v = getNode(_TYPE.NODE,pEl); 
		Activiti<T,V> subProcess = new Activiti<T,V>();
		Graph<T,V> mySub = subProcess.getProcess(pEl, _filename);
		subProcessMap.put(v.id, mySub);
		return  v;
	}

	/***************************
	 * 
	 * Reading the generic structure of a node and edge
	 * 
	 ****************************/
	
	@SuppressWarnings("unchecked")
	public T getNode(_TYPE nodeType, Element el) { 
		
		String name = getTextValue(el,"name");
		String id = getTextValue(el,"id");
		String documentation = getTextValue(el, "documentation");
		T v;
		if(name != null) v = (T) new Vertex(name); else v = (T) new Vertex();
		if(id != null) v.id = id;
		if(documentation != null){
			//System.err.println("Trying " + documentation + " cleaned " + cleanDoc(documentation));
			if(loadingGraph != null)
				v.addEffect(loadingGraph.fixEffects(cleanDoc(documentation)));
			else
				v.addEffect(cleanDoc(documentation));
		}
		if(id != null && id.length() > 1) vertexMap.put(id, v);
		return v;		
	}
	
	
	@SuppressWarnings("unchecked")
	public V getEdge(Element el) { 
		String name = getTextValue(el,"name");
		String id = getTextValue(el,"id");
		String documentation = getTextValue(el, "documentation");
		V v;
		if(name != null) v = (V) new Edge(name); else v = (V) new Edge();
		if(id != null) v.id = id;
		if(documentation != null){
			if(loadingGraph != null)
				v.addEffect(loadingGraph.fixEffects(cleanDoc(documentation)));
			else
				v.addEffect(cleanDoc(documentation));
			
		}
		
		String sourceRef = getTextValue(el,"sourceRef");
		String targetRef = getTextValue(el,"targetRef");
		if(targetRef != null && sourceRef != null && sourceRef.length() > 1 && targetRef.length() > 1 && id != null && id.length() > 1){
			edgeSourceMap.put(id,sourceRef);
			edgeTargetMap.put(id,targetRef);
			edgeMap.put(id, v);
		}
		
		
		return v; 
		
	}
	
	
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
	
	
	public Graph<T,V> parseDocument(String filename){
		//get the root element
		Element docEle = dom.getDocumentElement();
		_filename = filename;
		//get a nodelist of elements
		NodeList nl = docEle.getElementsByTagName("process");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				Element el = (Element)nl.item(i);
				Graph<T,V> newProc = getProcess(el, filename);
				
				return newProc;
				//add it to list
				//myEmpls.add(e);
			}
		}
		return null;
	}
	
	public void parseGPSDocument(String filename){
		//get the root element
		Element docEle = dom.getDocumentElement();
		_filename = filename;
		//get a nodelist of elements
		NodeList nl = docEle.getElementsByTagName("bpmndi:BPMNDiagram");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				Element el = (Element)nl.item(i);
				getGPSProcess(el, filename);
				
				return;
				//add it to list
				//myEmpls.add(e);
			}
		}
		return;
	}
	
	public void getGPSProcess(Element pEl, String filename) {
		// Store basic process model stuff
		if(pEl != null){
			NodeList nl = pEl.getElementsByTagName("bpmndi:BPMNShape");
			if(nl != null && nl.getLength() > 0) {
				for(int i = 0 ; i < nl.getLength();i++) {
					Element el = (Element)nl.item(i);
					
					String id = getTextValue(el, "bpmnElement");
					String height = "0";
					String width = "0";
					String x = "0";
					String y = "0";
					NodeList nl2 = el.getElementsByTagName("omgdc:Bounds");
					if(nl2 != null && nl2.getLength() > 0) {
						for(int j = 0 ; j < nl2.getLength();j++) {
							Element el2 = (Element)nl2.item(j);
							height = getTextValue(el2, "height");
							width = getTextValue(el2, "width");
							x = getTextValue(el2, "x");
							y = getTextValue(el2, "y");
						}
					}
					try{
						if(vertexMap.containsKey(id)){
							//System.err.println("Attaching " + id + " with " + x + "," + y);
							T vert = vertexMap.get(id);
							vert.x = Integer.parseInt(x);
							vert.y = Integer.parseInt(y);
							vert.width = Integer.parseInt(width);
							vert.height = Integer.parseInt(height);
							
						}
					}catch(Exception e){
						e.printStackTrace();
					}

				}
			}
		}
		
	}
	



	
	private Graph<T, V> cleanProc(Graph<T, V> newProc) {

		
		// Add vertex
		for(T v: vertexMap.values()){
			newProc.addVertex(v);
		}
		// Add Edges
		for(V e: edgeMap.values()){
//				System.err.println("Looking for " + e.id + " " + edgeSourceMap.get(e.id) + " " + edgeTargetMap.get(e.id));
//				System.err.println("\t got " + vertexMap.get(edgeSourceMap.get(e.id)) );
//				System.err.println("\t got " + vertexMap.get(edgeTargetMap.get(e.id)) );
//				if(vertexMap.get(edgeTargetMap.get(e.id)) == null){ System.err.println("_filename:" + inputFile); };
				newProc.addEdge( vertexMap.get(edgeSourceMap.get(e.id)), vertexMap.get(edgeTargetMap.get(e.id)), e );	
		}
		
		/// Remove all subprocess elements
		for(String subProc: subProcessMap.keySet()){					
				Graph<T,V> subProcess = subProcessMap.get(subProc);
				for(T nodes: subProcess.vertexSet()){
					newProc.removeVertex(vertexMap.get(nodes.id));
				}
				
				for(V edges: subProcess.edgeSet()){
					newProc.removeEdge(edgeMap.get(edges.id));
				}
		}
		
		for(String subProc: subProcessMap.keySet()){
			
			Graph<T,V> subProcess = subProcessMap.get(subProc);
			//System.err.println("Cleaning:" + subProcess);
			for(T nodes: subProcess.vertexSet()){
				if(nodes.id.toLowerCase().contains("startevent")){
					subProcess.trueStart = nodes;
				}
				if(nodes.id.toLowerCase().contains("endevent")){
					subProcess.trueEnd = nodes;
				}
				//System.err.println("Adding:" +  nodes.id);
				vertexMap.put(nodes.id, nodes);
				newProc.addVertex(nodes);
			}
			
			for(V edges: subProcess.edgeSet()){
				edgeMap.put(edges.id, edges);
				newProc.addEdge(subProcess.getEdgeSource(edges), subProcess.getEdgeTarget(edges), edges);
			}
			
			String sourceID = subProcess.trueStart.id;
			String targetID = subProcess.trueEnd.id;
			
			String existingOutEdge = "";
			String existingOutVertex = "";
			for(String ss : edgeSourceMap.keySet()){
				if(edgeSourceMap.get(ss).compareTo(subProc) == 0){
					existingOutEdge = ss;
					existingOutVertex = edgeTargetMap.get(ss);
				}
			}
			if(existingOutEdge.length() > 0 && existingOutVertex.length() >0 ){
				V oldEdge = newProc.getEdge(vertexMap.get(subProc) , vertexMap.get(existingOutVertex) );
				newProc.removeEdge(oldEdge);
				for(String id : edgeSourceMap.keySet()){
					if(edgeSourceMap.get(id).compareTo(subProc) == 0){
						//System.err.println("Id of source : " + id);
						newProc.addEdge(vertexMap.get(targetID), vertexMap.get(edgeTargetMap.get(id)));
					}
				}

			}
			newProc.addEdge(vertexMap.get(subProc), vertexMap.get(sourceID));
		}
		
		for(T v: newProc.vertexSet()){
			if(newProc.inDegreeOf(v) == 0) newProc.trueStart = v;
			if(newProc.outDegreeOf(v) == 0) newProc.trueEnd = v;
		}
		
		for(V e: newProc.edgeSet()){
			newProc.setEdgeWeight(e,1);
		}
		
		// Clean loops
		CycleDetector<T,V> myCycler = new CycleDetector<T,V>(newProc);
		if(myCycler.detectCycles()){
			newProc = myCycleFixer(myCycler, newProc);
		}
		
		return newProc;
	}
	
	

	private Graph<T, V> myCycleFixer(CycleDetector<T, V> myCycler,
			Graph<T, V> newProc) {

		Set<T> myCycles = myCycler.findCycles();
		//System.err.println("Found a cycle!" + myCycles + " from " +  newProc.trueStart);
		// Find earliest point
		double shortest = 10000000;
		double furthest = 0;
		T first = null;
		T last = null;
		for(T item: myCycles){
			List<V> shortestPath = BellmanFordShortestPath.findPathBetween(newProc, newProc.trueStart, item);
			double value = 0;
			for(V pathEdge : shortestPath){
				value += newProc.getEdgeWeight(pathEdge);
			}
			if(value < shortest && value > 0){
				shortest = value;
				first = item;
			}
			if(value > furthest){
				furthest = value;
				last = item;
			}			
		}
		
		//System.err.println("Loops between " + first + " and " + last + " chopping link >:)");
		if(newProc.containsEdge(last, first)) newProc.removeEdge(last, first);
		
		return newProc;
	}

	private String cleanDoc(String documentation) {
		documentation = documentation.replaceAll("&amp;", "&");
		return documentation;
	}

	
	@SuppressWarnings("unchecked")
	public Graph<T,V> getProcess(Element pEl, String filename) {
		Graph<T,V> myModel = new Graph<T,V>();
		if(loadingGraph == null) loadingGraph = (Graph<Vertex, Edge>) myModel;
		myModel.filename = filename;
		
		// Store basic process model stuff
		String name = getTextValue(pEl,"name");
		String id = getTextValue(pEl,"id");
		String documentation = getTextValue(pEl, "documentation");
		if(documentation != null && documentation.contains("QOS-")){
			loadingGraph.QOS = documentation;
			documentation = null;
		}
		if(name != null) myModel.name="myModel";
		if(id != null) myModel.id = id;
		if(documentation != null) myModel.documentation = cleanDoc(documentation);
		
		// Do the rest.
		//System.out.println("Name:" + name + " ; id: " + id + "; documentation:" + documentation);

		
		for(String thisElement: processElements){
			NodeList nl = pEl.getElementsByTagName(thisElement);
			if(nl != null && nl.getLength() > 0) {
				for(int i = 0 ; i < nl.getLength();i++) {
					Element el = (Element)nl.item(i);
					methodCaller(this,"get"+thisElement, el);
				}
			}
		}
		
		
		return cleanProc(myModel);
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
			if(methodName.contains("Task") || methodName.contains("sub") || methodName.contains("Event")){
				return getNode(_TYPE.NODE, el);
			}else if(methodName.contains("Flow")){
				return getEdge(el);
			}else if(methodName.contains("Gateway")){
				Vertex v = getNode(_TYPE.GATEWAY, el);
				v.makeXORGate();
				return v;
			}
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

}

