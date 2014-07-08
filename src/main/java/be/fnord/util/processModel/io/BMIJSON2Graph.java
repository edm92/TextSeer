package be.fnord.util.processModel.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.TreeMap;

import be.fnord.util.processModel.Edge;
import be.fnord.util.processModel.Graph;
import be.fnord.util.processModel.Vertex;
import be.fnord.util.processModel.util.GraphLoader;

import com.google.gson.Gson;

/**
 * TODO make this a re-usable class by removing all static modifiers
 *
 * @author Evan Morrison edm92@uowmail.edu.au http://www.fnord.be Apache
 *         License, Version 2.0, Apache License Version 2.0, January 2004
 *         http://www.apache.org/licenses/
 */
public class BMIJSON2Graph {
	public static TreeMap<String, TreeMap<String, Vertex>> edges = new TreeMap<String, TreeMap<String, Vertex>>();
	public static final boolean PROCESS_LEVEL = true;

	public static final boolean TASK_LEVEL = false;
	public static TreeMap<String, LinkedList<String>> vedges = new TreeMap<String, LinkedList<String>>();

	public static Graph<Vertex, Edge> convert(String name, String json) {
		Graph<Vertex, Edge> result = new Graph<Vertex, Edge>(Edge.class);

		// Loaded into a POJO below, explanation of the structure is commented
		// into the
		// snippet above the wrapper class
		Gson gson = new Gson();

		Wrapper data = gson.fromJson(json, Wrapper.class);
		// Lets cycle through pools with children
		for (ChildShapes pools : data.childShapes) {

			// Handle the main pool and nodes
			if (pools.childShapes.size() > 0) { // Discard pools with no
												// children, these are typically
												// edges
				// Lets make each pool into a new Process
				createGraph(result, pools.childShapes, TASK_LEVEL); // Call the
																	// recursive
																	// function
																	// to
																	// populate
																	// the
																	// process
																	// model

			} else {
				// Handle Edges
				if (pools.stencil != null && pools.stencil.id != null
						&& pools.stencil.id.contains("Flow")) {

					for (Outgoing out : pools.outgoing) {
						if (result.vertexIDRef.containsKey(out.resourceId)) {
							TreeMap<String, Vertex> pair = new TreeMap<String, Vertex>();
							pair.put("trg",
									result.vertexRef.get(result.vertexIDRef
											.get(out.resourceId)));
							edges.put(pools.resourceId, pair);
						}
					}
				}
			}

		}

		// Process the in edges
		for (String key : vedges.keySet()) {
			for (String s : vedges.get(key)) {
				if (edges.containsKey(s)) {
					if (result.vertexIDRef.containsKey(key)) {
						edges.get(s).put(
								"src",
								result.vertexRef.get(result.vertexIDRef
										.get(key)));
					}
				}
			}
		}

		for (String key : edges.keySet()) {
			if (edges.get(key).containsKey("src")
					&& edges.get(key).containsKey("trg")) {
				Edge e = new Edge(edges.get(key).get("src"), edges.get(key)
						.get("trg"));
				result.addE(e);
			} else {

			}
		}

		// System.out.println("Loaded graph: " + result);

		return result;
	}

	public static void createGraph(Graph<Vertex, Edge> results,
			LinkedList<ChildShapes> data, boolean level) {

		// On first call will deal with pools
		// On second call will deal with top level tasks
		// On second call will deal with sub processes
		// etc.
		if (level == PROCESS_LEVEL) {
			for (ChildShapes pools : data) {
				if (pools.childShapes.size() > 0) { // Discard pools with no
													// children, these are
													// typically graphical
													// elements
					// Lets make each pool into a new sub Process
					Graph<Vertex, Edge> subResult = new Graph<Vertex, Edge>(
							Edge.class);
					Vertex v = (Vertex) subResult; // Place holder for the
													// subprocess, we can drill
													// into it if needed later
					v.id = pools.resourceId; // We'll try to keep the pool ID so
												// that we can get the edge link
					v.setSubprocess(true);
					subResult.name = pools.properties.name.replaceAll("\n", "")
							.replaceAll("\r", "").trim()
							+ "((" + pools.resourceId + "))"; // Give the
																// process the
																// label of the
																// file and the
																// Pool Name
					createGraph(subResult, pools.childShapes, TASK_LEVEL); // Call
																			// the
																			// recursive
																			// function
																			// to
																			// populate
																			// the
																			// process
																			// model
					for (Outgoing out : pools.outgoing) {
						if (vedges.containsKey(v.id)) {
							vedges.get(v.id).add(out.resourceId);
						} else {
							LinkedList<String> ll = new LinkedList<String>();
							ll.add(out.resourceId);
							vedges.put(v.id, ll);
						}
					}
					results.addV(v);
				}
			}
		} else if (level == TASK_LEVEL) { // Work with tasks
			for (ChildShapes tasks : data) {
				if (tasks.childShapes.size() > 0) { // Discard pools with no
													// children, these are
													// typically graphical
													// elements
					for (ChildShapes task : tasks.childShapes) {
						// Add each task to the process model

						Vertex v;
						String name = task.properties.name.replaceAll("\n", "")
								.replaceAll("\r", "").trim()
								+ "((" + task.resourceId + "))";
						if (task.properties.gatewaytype != null
								&& task.properties.gatewaytype.length() > 0) {
							// This is a gateway
							v = new Vertex(name, GraphLoader.ParallelGateway);
							if ("XOR".compareTo(task.properties.gatewaytype) == 0) {
								v.isXOR = true;
								v.type = GraphLoader.ExclusiveGateway;
							} else if ("XOR"
									.compareTo(task.properties.gatewaytype) == 0) {
								v.isAND = true;
							} else if ("OR"
									.compareTo(task.properties.gatewaytype) == 0) {
								v.isOR = true;
								v.type = GraphLoader.InclusiveGateway;
							} else {
								v.isXOR = true;
							}
						} else {
							// This is a task or event
							if (task.stencil != null && task.stencil.id != null
									&& task.stencil.id.length() > 0
									&& task.stencil.id.contains("Event")) {
								// New Event
								v = new Vertex(name,
										GraphLoader.IntermediateThrowEvent);
							} else {
								// New Task

								v = new Vertex(name, GraphLoader.Task);
							}
						}
						v.name = name;
						v.id = task.resourceId;
						for (Outgoing out : task.outgoing) {
							if (vedges.containsKey(v.id)) {
								vedges.get(v.id).add(out.resourceId);
							} else {
								LinkedList<String> ll = new LinkedList<String>();
								ll.add(out.resourceId);
								vedges.put(v.id, ll);
							}
						}
						results.addV(v);

					}
				}
			}
		}

	}

	@SuppressWarnings({ "resource" })
	public static String readFile(String file) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		if (reader == null) {
			return "";
		}
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");

		try {
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return stringBuilder.toString();
	}

}

// For understanding how the processes is stored in the wrapper:
// for(ChildShapes cc : data.childShapes){
// System.out.println("Got4 " + cc.properties.name + " ;;;; " + cc.toString());
// // Pools
// if(cc.childShapes.size() > 0)
// for(ChildShapes cs : cc.childShapes){
// System.out.println("Got3 " + cs.properties.name + " ;;;; " + cs.toString());
// // Top level Tasks
// if(cs.childShapes.size() > 0)
// for(ChildShapes ct : cs.childShapes){
// System.out.println("Got1 " + ct.properties.name + " ;;;; " + ct.toString());
// // Subprocesses
// if(ct.childShapes.size() > 0)
// for(ChildShapes cu : ct.childShapes){
// System.out.println("Got " + cu.properties.name + " ;;;; " + cu.toString());
// // Sub Tasks
// }
// }
// }
// }

class Bounds {
	public Coords lowerRight;

	public Coords upperLeft;

	@Override
	public String toString() {
		return "Bounds [lowerRight=" + lowerRight + ", upperLeft=" + upperLeft
				+ "]";
	}
}

class ChildShapes {
	public Bounds bounds;

	public LinkedList<ChildShapes> childShapes = new LinkedList<ChildShapes>();

	public LinkedList<Coords> dockers;
	public LinkedList<Outgoing> outgoing = new LinkedList<Outgoing>();
	public MyProperties properties;
	public String resourceId;
	public Stencil stencil;
	public Target target;

	@Override
	public String toString() {
		return "childShapes [dockers=" + dockers + ", bounds=" + bounds
				+ ", resourceId=" + resourceId + ", childShapes=" + childShapes
				+ ", properties=" + properties + ", stencil=" + stencil
				+ ", outgoing=" + outgoing + "]";
	}
}

class Coords {
	public String x;

	public String y;

	@Override
	public String toString() {
		return "Coords [y=" + y + ", x=" + x + "]";
	}
}

class ExternalDocuments {
	String empty;
}

class MyProperties {

	public String activitytype;

	public String adhoc;

	public String adhoccancelremaininginstances;

	public String adhoccompletioncondition;
	public String adhocordering;
	public String applyincalc;
	public String artifacttype;
	public String assignments;
	public String auditing;
	public String author;
	public String behavior;
	public String bgcolor;
	public String bordercolor;
	public String boundaryvisible;
	public String callacitivity;
	public String categories;
	public String completioncondition;
	public String completionquantity;
	public String complexbehaviordefinition;
	public String conditionexpression;
	public String conditiontype;
	public String costcenter;
	public String costs;
	public String creationdate;
	public String datainput;
	public String datainputassociations;
	public String datainputset;
	public String dataoutput;
	public String dataoutputassociations;
	public String dataoutputset;
	public String defaultgate;
	public String diagramref;
	public String documentation;
	public String enableinstancecompensation;
	public String entry;
	public String eventdefinitionref;
	public String eventdefinitions;
	public String expressionlanguage;
	public LinkedList<ExternalDocuments> externaldocuments;
	public String frequency;
	public String gate_assignments;
	public String gate_outgoingsequenceflow;
	public String gates;
	public String gates_assignments;
	public String gates_outgoingsequenceflow;
	public String gatewaytype;
	public String implementation;
	public String inmessagename;
	public String inmsgimport;
	public String inmsgiscollection;
	public String inmsgitemkind;
	public String inmsgstructure;
	public String inputdataitem;
	public String inputmaps;
	public String inputset;
	public String instantiate;
	public String isadhoc;
	public String isatransaction;
	public String isclosed;
	public String isclosedisclosedisclosed;
	public String isexecutable;
	public String isforcompensation;
	public String isimmediate;
	public String lanes;
	public String language;
	public String loopcardinality;
	public String loopcondition;
	public String loopdatainput;
	public String loopdataoutput;
	public String loopmaximum;
	public String looptype;
	public String markervisible;
	public String maximum;
	public String messageref;
	public String minimum;
	public String modificationdate;
	public String monitoring;
	public String multiinstance;
	public String name;
	public String namespaces;
	public String nonebehavioreventref;
	public String onebehavioreventref;
	public String operationname;
	public String operationref;
	public String orientation;
	public String outmessagename;
	public String outmsgimport;
	public String outmsgiscollection;
	public String outmsgitemkind;
	public String outmsgstructure;
	public String outputdataitem;
	public String outputmaps;
	public String outputset;
	public String parentlane;
	public String parentpool;
	public String pool;
	public String probability;
	public String processcategories;
	public String processdocumentation;
	public String processid;
	public String processlink_bounds;
	public String processname;
	public String processref;
	public String processtype;
	public String resources;
	public String risklevel;
	public String script;
	public String script_language;
	public String scriptformat;
	public String showcaption;
	public String showdiamondmarker;
	public String startquantity;
	public String status;
	public String subprocesstype;
	public String suppressjoinfailure;
	public String targetnamespace;
	public String tasktype;
	public String testbefore;
	public String text;
	public String textformat;
	public String time;
	public String transaction;
	public String transactionmethod;
	public String transformation;
	public String trigger;
	public String type;
	public String typelanguage;
	public String version;
	public String xortype;

	@Override
	public String toString() {
		return "MyProperties [datainputassociations=" + datainputassociations
				+ ", isexecutable=" + isexecutable + ", externaldocuments="
				+ externaldocuments + ", processid=" + processid + ", xortype="
				+ xortype + ", isclosed=" + isclosed + ", maximum=" + maximum
				+ ", bordercolor=" + bordercolor + ", markervisible="
				+ markervisible + ", minimum=" + minimum + ", orientation="
				+ orientation + ", inputset=" + inputset + ", processtype="
				+ processtype + ", callacitivity=" + callacitivity
				+ ", loopcondition=" + loopcondition + ", parentlane="
				+ parentlane + ", inmsgitemkind=" + inmsgitemkind + ", name="
				+ name + ", gates_assignments=" + gates_assignments
				+ ", entry=" + entry + ", dataoutputassociations="
				+ dataoutputassociations + ", adhoccompletioncondition="
				+ adhoccompletioncondition + ", language=" + language
				+ ", targetnamespace=" + targetnamespace + ", testbefore="
				+ testbefore + ", isclosedisclosedisclosed="
				+ isclosedisclosedisclosed + ", modificationdate="
				+ modificationdate + ", inputmaps=" + inputmaps + ", version="
				+ version + ", outmsgstructure=" + outmsgstructure
				+ ", loopmaximum=" + loopmaximum + ", script=" + script
				+ ", outputset=" + outputset + ", datainput=" + datainput
				+ ", loopdataoutput=" + loopdataoutput + ", resources="
				+ resources + ", gate_assignments=" + gate_assignments
				+ ", isimmediate=" + isimmediate + ", inmsgimport="
				+ inmsgimport + ", adhocordering=" + adhocordering
				+ ", outputmaps=" + outputmaps + ", transformation="
				+ transformation + ", costcenter=" + costcenter + ", text="
				+ text + ", expressionlanguage=" + expressionlanguage
				+ ", status=" + status + ", operationref=" + operationref
				+ ", lanes=" + lanes + ", datainputset=" + datainputset
				+ ", isatransaction=" + isatransaction + ", assignments="
				+ assignments + ", activitytype=" + activitytype
				+ ", artifacttype=" + artifacttype + ", processdocumentation="
				+ processdocumentation + ", frequency=" + frequency
				+ ", author=" + author + ", inmessagename=" + inmessagename
				+ ", applyincalc=" + applyincalc
				+ ", enableinstancecompensation=" + enableinstancecompensation
				+ ", instantiate=" + instantiate + ", loopdatainput="
				+ loopdatainput + ", documentation=" + documentation
				+ ", textformat=" + textformat + ", costs=" + costs
				+ ", looptype=" + looptype + ", auditing=" + auditing
				+ ", behavior=" + behavior + ", parentpool=" + parentpool
				+ ", gates_outgoingsequenceflow=" + gates_outgoingsequenceflow
				+ ", type=" + type + ", boundaryvisible=" + boundaryvisible
				+ ", inmsgiscollection=" + inmsgiscollection + ", categories="
				+ categories + ", processlink_bounds=" + processlink_bounds
				+ ", tasktype=" + tasktype + ", gate_outgoingsequenceflow="
				+ gate_outgoingsequenceflow + ", isadhoc=" + isadhoc
				+ ", eventdefinitionref=" + eventdefinitionref
				+ ", complexbehaviordefinition=" + complexbehaviordefinition
				+ ", completionquantity=" + completionquantity
				+ ", showcaption=" + showcaption + ", script_language="
				+ script_language + ", typelanguage=" + typelanguage
				+ ", defaultgate=" + defaultgate + ", gatewaytype="
				+ gatewaytype + ", trigger=" + trigger + ", startquantity="
				+ startquantity + ", isforcompensation=" + isforcompensation
				+ ", inmsgstructure=" + inmsgstructure + ", conditiontype="
				+ conditiontype + ", processref=" + processref
				+ ", processname=" + processname + ", nonebehavioreventref="
				+ nonebehavioreventref + ", probability=" + probability
				+ ", outmsgiscollection=" + outmsgiscollection
				+ ", outputdataitem=" + outputdataitem + ", multiinstance="
				+ multiinstance + ", risklevel=" + risklevel
				+ ", outmsgitemkind=" + outmsgitemkind + ", inputdataitem="
				+ inputdataitem + ", implementation=" + implementation
				+ ", eventdefinitions=" + eventdefinitions + ", outmsgimport="
				+ outmsgimport + ", conditionexpression=" + conditionexpression
				+ ", diagramref=" + diagramref
				+ ", adhoccancelremaininginstances="
				+ adhoccancelremaininginstances + ", transaction="
				+ transaction + ", messageref=" + messageref + ", adhoc="
				+ adhoc + ", transactionmethod=" + transactionmethod
				+ ", showdiamondmarker=" + showdiamondmarker
				+ ", outmessagename=" + outmessagename + ", loopcardinality="
				+ loopcardinality + ", creationdate=" + creationdate
				+ ", dataoutput=" + dataoutput + ", operationname="
				+ operationname + ", completioncondition="
				+ completioncondition + ", processcategories="
				+ processcategories + ", dataoutputset=" + dataoutputset
				+ ", time=" + time + ", gates=" + gates
				+ ", suppressjoinfailure=" + suppressjoinfailure
				+ ", namespaces=" + namespaces + ", pool=" + pool
				+ ", subprocesstype=" + subprocesstype + ", bgcolor=" + bgcolor
				+ ", scriptformat=" + scriptformat + ", onebehavioreventref="
				+ onebehavioreventref + ", monitoring=" + monitoring + "]";
	}

}

class Outgoing {
	public String resourceId;

	@Override
	public String toString() {
		return "Outgoing [resourceId=" + resourceId + "]";
	}
}

class Stencil {
	public String id;

	@Override
	public String toString() {
		return "Stencil [id=" + id + "]";
	}
}

class Target {
	public String resourceId;
}

class Wrapper {
	public Bounds bounds;
	LinkedList<ChildShapes> childShapes = new LinkedList<ChildShapes>();
	public MyProperties properties;
	public String resourceId;
	public Stencil stencil;
}
