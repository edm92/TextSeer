package processBuilding.Parser;

import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeMap;

import org.w3c.dom.*;

import processBuilding.process;
import textSeer.Model.Effect;
import textSeer.Model.Gateway;
import textSeer.Model.Graph;
import textSeer.Model.Predicate;
import textSeer.Model.SequenceEdge;
import textSeer.Model.Vertex;



public class FileTypeCheck {
	public static String SOURCEFILE = std.string.endl + "Error in: processBuilding.Parser.FileTypeCheck" + std.string.endl;
	public static boolean DEBUG = false; // std.string.debug;	// Hopefully true when you edit :P
	public static void debug(String msg){
		if(DEBUG)
			m.E.writeConsole(msg + SOURCEFILE);
	}

	SigBPMNParser parent;
	
	public TreeMap<String, SigObject> elements;
	public TreeMap<String, Vertex> graphElements;
	public TreeMap<String, Effect> effects;
	public LinkedList<String> others ;
	public TreeMap<String, SigEdge> edges;
	
	boolean Signavo = false;
	
	Stack<SigObject> lastElement;
	String incomming = "";
	String outgoing = "";
	int Nincomming = 0;
	int Noutgoing = 0;
	
	public process CreateProcess(){
		process p = new process();
		p.structure = new Graph();
		Graph g = p.structure;
		Stack<SigObject> newElements = new Stack<SigObject>();
		
		for(SigObject e: lastElement){
			
			if(e.type.toLowerCase().compareTo("task") == 0){
				//debug("Adding task " + e.Name);
				elements.put(e.ID, e);
				Vertex v = new Vertex(g, e.Name);
				g.addNode(v);
				graphElements.put(e.ID, v);
				
			}else
			if(e.type.toLowerCase().compareTo("endevent") == 0){
				elements.put(e.ID, e);
				Vertex v = new Vertex(g, e.Name);
				g.addNode(v);
				graphElements.put(e.ID, v);
			}else
			if(e.type.toLowerCase().compareTo("startevent") == 0){
				elements.put(e.ID, e);
				Vertex v = new Vertex(g, e.Name);
				g.addNode(v);
				graphElements.put(e.ID, v);
			}else
			if(e.type.toLowerCase().compareTo("xorgatework") == 0){
				elements.put(e.ID, e);
				Gateway v = new Gateway(g, e.Name);
				v.type = Gateway.gatetype.XOR;
				g.addNode(v);
				graphElements.put(e.ID, v);
				
			}else
			if(e.type.toLowerCase().compareTo("parallelgateway") == 0){
				elements.put(e.ID, e);
				Gateway v = new Gateway(g, e.Name);
				v.type = Gateway.gatetype.AND;
				g.addNode(v);
				graphElements.put(e.ID, v);
				
			}else
			if(e.type.toLowerCase().compareTo("event") == 0){
				elements.put(e.ID, e);
				Vertex v = new Vertex(g, e.Name);
				g.addNode(v);
				graphElements.put(e.ID, v);
				
				
			}else
			if(e.type.toLowerCase().compareTo("annotation") == 0){
				elements.put(e.ID, e);
				Effect v = new Effect();
				v.addPredicate(new Predicate(e.annote.replace("&amp;", "&"), true));
				effects.put(e.ID, v);
			}else{
				newElements.push(e);
			}
		}
		
			
		
		for(SigObject e: newElements){
			if(e.type.toLowerCase().compareTo("sequenceflow") == 0){
				
				//SequenceEdge edge = new SequenceEdge(g, e.Name);
				SigEdge v = (SigEdge)e;
				//m.E.writeConsole("Here +" + v.Source + " " + graphElements.get(v.Source) );
				edges.put(v.ID, v);
				//g.addEdge(edge);
				
				
			}
			
			
			if(e.type.toLowerCase().compareTo("association") == 0){
				
				SigEdge v = (SigEdge)e;
				
				if(v.Source != null && v.Target != null){
					Vertex node = graphElements.get(v.Source);
					
					Effect eff = effects.get(v.Target);
					
					if(node != null && eff != null){
						node.addEffect(eff);
				
								
					}
				}
			}
		}
		
		for(SigObject n: elements.values()){
			SigNode v = (SigNode)n;
			for(String s : v.outgoing.keySet()){
				///m.E.writeConsole(s + v.outgoing.size());
				if(!others.contains(v.ID + s)){
					//m.E.writeConsole("Missing Edge from " + v.Name + " to " + s);
					if(edges.containsKey(s)){
					
						SigEdge ted = edges.get(s);
						ted.Source = v.ID;
						edges.remove(s);
						edges.put(s, ted);
						others.add(v.ID + s);
					}
				}
			}
			for(String s : v.incomming.keySet()){
				if(!others.contains(s + v.ID)){
					//m.E.writeConsole("Missing Edge from " + s + " to " + v.Name);
					if(edges.containsKey(s)){
						SigEdge ted = edges.get(s);
						ted.Target = v.ID;
						others.add(s + v.ID);
					}
				}
			}

		}
		
		for(SigEdge e: edges.values()){
			SequenceEdge edge = new SequenceEdge(g);
			//if(graphElements.get(e.Source) == null) 
			//	m.E.writeGUI("Crap" + e.Source);
			edge.addSource(graphElements.get(e.Source));
			edge.addTarget(graphElements.get(e.Target));
			edge.name = e.Name;
			edge.finalize();
			g.addEdge(edge);
		}
		
		p.structure.finalize();
		return p;		
	}
	
	public FileTypeCheck(SigBPMNParser myP){
		parent = myP;
		lastElement = new Stack<SigObject>();
		elements = new TreeMap<String, SigObject>();
		graphElements = new TreeMap<String, Vertex>();
		effects = new TreeMap<String, Effect>();
		others = new LinkedList<String>();
		edges = new TreeMap<String, SigEdge>();
		

	}
	
	public void moveLast(){
		if(lastElement != null && lastElement.size() > 0){
			SigObject mObj = lastElement.pop();
			if(mObj.ID.compareTo("") != 0 && mObj.Name.compareTo("") != 0){
				elements.put(mObj.ID, mObj);
			}
		}
	}
	
	public boolean checkName(String name, Node n){
		
		
		
		if(name.toLowerCase().compareTo("id") == 0){
			SigObject mNode = lastElement.pop();
			if(mNode.ID == "")
				mNode.ID = n.getNodeValue();
			lastElement.push(mNode);
			debug("ID = " + n.getNodeValue());
		}else
		if(name.toLowerCase().compareTo("name") == 0){
			SigObject mNode = lastElement.pop();
			if(mNode.Name == "")
			mNode.Name = n.getNodeValue();
			lastElement.push(mNode);
			debug("NAME = " + n.getNodeValue() + " add to " + mNode.ID);
		}else
			if(name.toLowerCase().compareTo("outgoing") == 0){
				SigNode mNode = (SigNode) lastElement.pop();
				mNode.outgoing.put(n.toString().replace("<outgoing>", "").replace("</outgoing>",""), null);
				lastElement.push(mNode);
				//m.E.writeConsole("Outgoing ID = " + n.toString().replace("<outgoing>", "").replace("</outgoing>",""));
				
				
	            
		}else
			if(name.toLowerCase().compareTo("incoming") == 0){
				SigNode mNode = (SigNode) lastElement.pop();
				mNode.incomming.put(n.toString().replace("<incoming>", "").replace("</incoming>",""), null);
				lastElement.push(mNode);
				debug("Incomming ID = " + n.toString().replace("<incoming>", "").replace("</incoming>",""));	            
	            
		}else
			if(name.toLowerCase().compareTo("text") == 0){
				SigNode mNode = (SigNode) lastElement.pop();
				mNode.annote = n.toString().replace("<text>", "").replace("</text>","" );
				lastElement.push(mNode);
				debug("Text = " + n.toString().replace("<text>", "").replace("</text>",""));	            
	            
		}
		
		
		// Handle BPMN elements
		if(name.compareTo("bpmndi:BPMNDiagram") == 0){
			debug("Halt");
			return false;
		}else
		if(name.compareTo("sequenceFlow") == 0){
			debug("sequenceFlow " + n.getAttributes().getLength() );
			NamedNodeMap atts = n.getAttributes();
			SigEdge newObj = new SigEdge();
			newObj.type = "sequenceFlow";

            for (int i = 0; i < atts.getLength(); i++) {
            	
                Node att = atts.item(i);
                debug(att.getNodeName() + " " + att.getNodeValue());
                if(att.getNodeName().compareTo("sourceRef") == 0){
                	newObj.Source = (String) att.getNodeValue();
                	if(newObj.Target!= null && newObj.Target.length() > 0){
                		lastElement.push(newObj);
                		newObj = new SigEdge();
                		newObj.type = "sequenceFlow";
                	}
                }else{
                	if(att.getNodeName().compareTo("targetRef") == 0){
                    	newObj.Target = (String) att.getNodeValue();
                    	if(newObj.Source!= null && newObj.Source.length() > 0){
                    		lastElement.push(newObj);
                    		newObj = new SigEdge();
                    		newObj.type  = "sequenceFlow";
                    	}
                    }
                }
            }
			
			
		}else
			if(name.compareTo("association") == 0){
				debug("association " + n.getAttributes().getLength() );
				NamedNodeMap atts = n.getAttributes();
				SigEdge newObj = new SigEdge();
	            for (int i = 0; i < atts.getLength(); i++) {
	                Node att = atts.item(i);
	                debug(att.getNodeName() + " " + att.getNodeValue());
	                if(att.getNodeName().compareTo("sourceRef") == 0){
	                	newObj.Source = (String) att.getNodeValue();
	                }else{
	                	if(att.getNodeName().compareTo("targetRef") == 0){
	                    	newObj.Target = (String) att.getNodeValue();
	                    }
	                }
	            }
				
				newObj.type = "association";
				
				
				lastElement.push(newObj);
			}else
		if(name.compareTo("task") == 0){
			debug("task");
			SigNode newObj = new SigNode();
			newObj.type = "task";
			lastElement.push(newObj);
		}else
		if(name.compareTo("parallelGateway") == 0){
			debug("parallelGateway");
			SigNode newObj = new SigNode();
			newObj.type = "parallelGateway";
			lastElement.push(newObj);
		}else
		if(name.compareTo("userTask") == 0){
			debug("userTask");
			SigNode newObj = new SigNode();
			newObj.type = "task";
			lastElement.push(newObj);
		}else
		if(name.compareTo("potentialOwner") == 0){
			debug("potential Owner");
			SigNode newObj = new SigNode();
			newObj.type = "potentialOwner";
			lastElement.push(newObj);
		}else
		if(name.compareTo("definitions") == 0){
			debug("Workflow Definitions");
			SigNode newObj = new SigNode();
			newObj.type = "definitions";
			lastElement.push(newObj);
		}else
		if(name.compareTo("endEvent") == 0){
			debug("End Event");
			SigNode newObj = new SigNode();
			newObj.type = "endEvent";
			lastElement.push(newObj);
		}else
		if(name.compareTo("startEvent") == 0){
			debug("Start Event");
			SigNode newObj = new SigNode();
			newObj.type = "startEvent";
			lastElement.push(newObj);
		}else			
		if(name.compareTo("process") == 0){
			debug("New Process");
			SigNode newObj = new SigNode();
			newObj.type = "Process";
			lastElement.push(newObj);
		}else
		if(name.compareTo("exclusiveGateway") == 0){
			debug("XOR Split");

			SigNode newObj = new SigNode();
			newObj.type = "XORGatework";
			lastElement.push(newObj);
		}else
			if(name.compareTo("textAnnotation") == 0){
				debug("Annotation");

				SigNode newObj = new SigNode();
				newObj.type = "Annotation";
				lastElement.push(newObj);
			}
		
		else
			if(name.contains("Event") && name.contains("intermediate")){
				debug("Event");

				SigNode newObj = new SigNode();
				newObj.type = "Event";
				lastElement.push(newObj);
			}
		
		
		
		return true;
	}
	
}
