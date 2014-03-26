package be.fnord.util.processModel.util;

import be.fnord.util.processModel.Edge;
import be.fnord.util.processModel.Graph;
import be.fnord.util.processModel.Vertex;
import be.fnord.yaoqiang.extend.eDefinitions;
import org.w3c.dom.Document;
import org.yaoqiang.bpmn.model.BPMNModelCodec;
import org.yaoqiang.bpmn.model.BPMNModelParsingErrors.ErrorMessage;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.activities.CallActivity;
import org.yaoqiang.bpmn.model.elements.activities.ServiceTask;
import org.yaoqiang.bpmn.model.elements.activities.SubProcess;
import org.yaoqiang.bpmn.model.elements.activities.Task;
import org.yaoqiang.bpmn.model.elements.collaboration.MessageFlow;
import org.yaoqiang.bpmn.model.elements.collaboration.Participant;
import org.yaoqiang.bpmn.model.elements.core.common.SequenceFlow;
import org.yaoqiang.bpmn.model.elements.core.foundation.BaseElement;
import org.yaoqiang.bpmn.model.elements.core.foundation.Documentation;
import org.yaoqiang.bpmn.model.elements.events.*;
import org.yaoqiang.bpmn.model.elements.gateways.ExclusiveGateway;
import org.yaoqiang.bpmn.model.elements.gateways.InclusiveGateway;
import org.yaoqiang.bpmn.model.elements.gateways.ParallelGateway;
import org.yaoqiang.bpmn.model.elements.humaninteraction.UserTask;
import org.yaoqiang.bpmn.model.elements.process.BPMNProcess;

import java.util.LinkedList;
import java.util.TreeMap;
import java.util.UUID;

public class GraphLoader {
    public static final boolean __DEBUG = a.e.__DEBUG;


    public static Graph<Vertex, Edge> loadModel(String filename) {
        return loadModel(filename, a.e.NO_FLAGS);
    }

    public static Graph<Vertex, Edge> loadModel(String filename, int flags) {
        Graph<Vertex, Edge> myModel = new Graph<Vertex, Edge>(Edge.class);
        Document document = null;
        document = BPMNModelUtils.parseDocument(filename, true, new LinkedList<ErrorMessage>());

        if (document != null) {
            BPMNModelCodec myCodec = new BPMNModelCodec();

            eDefinitions myDef = new eDefinitions();
            myCodec.decode(document.getDocumentElement(), myDef);

            // Parse each important BPMN element and save into tEdges / tNodes
            // Change the dont save messages and participants flag to true if you want pools and messages
            for (String key : myCodec.getBPMNElementMap().keySet()) {
                saveElement(myCodec.getBPMNElementMap().get(key),
                        flags, "");
            }

            // Add the nodes to the model
            for (TNode r : tNodes.values()) {
                myModel.addV(r.node);
            }

            // Add the edges to the model
            for (String key : tEdges.keySet()) {
                TEdge r = tEdges.get(key);
                // Make sure we have all of our nodes
                if (!tNodes.containsKey(r.src)) {
                    System.out.println("Unable to find vertex reference ID " + r.src);
                    continue;
                }
                ;
                if (!tNodes.containsKey(r.trg)) {
                    System.out.println("Unable to find vertex reference ID " + r.trg);
                    continue;
                }
                ;
                if (tNodes.get(r.src).node == null) {
                    System.out.println("Node not added ID " + r.src);
                    continue;
                }
                ;
                if (tNodes.get(r.trg).node == null) {
                    System.out.println("Node not added ID " + r.trg);
                    continue;
                }
                ;
                // Create a new edge
                Edge e = new Edge(key + r.name, r.type + "", tNodes.get(r.src).node, tNodes.get(r.trg).node);
                myModel.addE(e);
            }


            // Old Parser commented 4/3/13... remove if no issues arrising
//			BPMNProcess bpmn = BPMNModelUtils.getDefaultProcess(myDef);
//			
////			myModel.isEmpty = bpmn.isEmptyProcess() ;
////			if(myModel.isEmpty) return null;
//			
//			List<SequenceFlow> mySeq = bpmn.getSequenceFlows();
//			TreeMap<String, Vertex> allNodes = new TreeMap<String, Vertex>();
//			
//			for(SequenceFlow seq : mySeq) {
//				String activity1 = seq.getSourceFlowNode().getId();
//				String activity2 = seq.getTargetFlowNode().getId();
//				Vertex myActivity = convertNode(seq.getSourceFlowNode());
//				Vertex myActivity2 = convertNode(seq.getTargetFlowNode());
//				
//				if(!allNodes.containsKey(activity1)){
//					allNodes.put(activity1, myActivity);
//				}
//				if(!allNodes.containsKey(activity2)){
//					allNodes.put(activity2, myActivity2);
//				}
//				myActivity = allNodes.get(activity1);
//				myActivity2 = allNodes.get(activity2);
//				String sequenceFlow = seq.getName();
//				Edge sequence = new Edge(sequenceFlow, 
//						seq.getClass().getCanonicalName().substring(44), 
//						myActivity, 
//						myActivity2);
//				
//				myModel.addV(myActivity);
//				myModel.addV(myActivity2);
//				myModel.addE(sequence);
//				
//				
//				// Add to repository
//				myModel.name = filename;
//				PGraph.allGraphs.put(filename, myModel);
//			}
        } else {
            System.err.println("Can't load file.");
        }
        return myModel;
    }


    public static final int BPMNShape = "org.yaoqiang.bpmn.model.elements.bpmndi.BPMNShape".hashCode();
    public static final int ExclusiveGateway = "org.yaoqiang.bpmn.model.elements.gateways.ExclusiveGateway".hashCode();
    public static final int ParallelGateway = "org.yaoqiang.bpmn.model.elements.gateways.ParallelGateway".hashCode();
    public static final int InclusiveGateway = "org.yaoqiang.bpmn.model.elements.gateways.InclusiveGateway".hashCode();
    public static final int BPMNEdge = "org.yaoqiang.bpmn.model.elements.bpmndi.BPMNEdge".hashCode();
    public static final int SequenceFlow = "org.yaoqiang.bpmn.model.elements.core.common.SequenceFlow".hashCode();
    public static final int Collaboration = "org.yaoqiang.bpmn.model.elements.collaboration.Collaboration".hashCode();
    public static final int Task = "org.yaoqiang.bpmn.model.elements.activities.Task".hashCode();
    public static final int BPMNDiagram = "org.yaoqiang.bpmn.model.elements.bpmndi.BPMNDiagram".hashCode();
    public static final int EndEvent = "org.yaoqiang.bpmn.model.elements.events.EndEvent".hashCode();
    public static final int StartEvent = "org.yaoqiang.bpmn.model.elements.events.StartEvent".hashCode();
    public static final int Participant = "org.yaoqiang.bpmn.model.elements.collaboration.Participant".hashCode();
    public static final int eDefinitions = "be.fnord.yaoqiang.extend.eDefinitions".hashCode();
    public static final int BPMNProcess = "org.yaoqiang.bpmn.model.elements.process.BPMNProcess".hashCode();
    public static final int MessageFlow = "org.yaoqiang.bpmn.model.elements.collaboration.MessageFlow".hashCode();
    public static final int IntermediateCatchEvent = "org.yaoqiang.bpmn.model.elements.events.IntermediateCatchEvent".hashCode();
    public static final int IntermediateThrowEvent = "org.yaoqiang.bpmn.model.elements.events.IntermediateThrowEvent".hashCode();
    public static final int BoundaryEvent = "org.yaoqiang.bpmn.model.elements.events.BoundaryEvent".hashCode();
    public static final int ErrorEventDefinition = "org.yaoqiang.bpmn.model.elements.events.ErrorEventDefinition".hashCode();
    public static final int ServiceTask = "org.yaoqiang.bpmn.model.elements.activities.ServiceTask".hashCode();
    public static final int UserTask = "org.yaoqiang.bpmn.model.elements.humaninteraction.UserTask".hashCode();
    public static final int CallActivity = "org.yaoqiang.bpmn.model.elements.activities.CallActivity".hashCode();
    public static final int SubProcess = "org.yaoqiang.bpmn.model.elements.activities.SubProcess".hashCode();
    public static final int Documentation = "org.yaoqiang.bpmn.model.elements.core.foundation.Documentation".hashCode();
    public static final int BPMNPlane = "org.yaoqiang.bpmn.model.elements.bpmndi.BPMNPlane".hashCode(); // Useless

    public static String getType(int _type) {
        if (_type == ExclusiveGateway) return "ExclusiveGateway";
        if (_type == ParallelGateway) return "ParallelGateway";
        if (_type == InclusiveGateway) return "InclusiveGateway";
        if (_type == SequenceFlow) return "SequenceFlow";
        if (_type == Task) return "Task";
        if (_type == ErrorEventDefinition) return "ErrorEventDefinition";
        if (_type == ServiceTask) return "ServiceTask";
        if (_type == UserTask) return "UserTask";
        if (_type == CallActivity) return "CallActivity";
        if (_type == SubProcess) return "SubProcess";
        if (_type == EndEvent) return "EndEvent";
        if (_type == StartEvent) return "StartEvent";
        if (_type == IntermediateCatchEvent) return "IntermediateCatchEvent";
        if (_type == IntermediateThrowEvent) return "IntermediateThrowEvent";
        if (_type == BoundaryEvent) return "BoundaryEvent";
        if (_type == Participant) return "Participant";
        if (_type == Documentation) return "Documentation";
        return "unknown";
    }

    private static TreeMap<String, TEdge> tEdges = new TreeMap<String, TEdge>();
    private static TreeMap<String, TNode> tNodes = new TreeMap<String, TNode>();
    private static String ProcessName = "";


    /**
     * Updated converter function for all XMLElements
     * used when processing value elements of myCodec.getBPMNElementMap()
     */
    public static void saveElement(XMLElement _ele, int getMessages, String documentation) {
        String _name = _ele.getClass().getCanonicalName();
        int hashCode = _name.hashCode();
        if (hashCode == BPMNShape) {
//			BPMNShape r = (BPMNShape)_ele;
//			a.e.println("Shape " + " "  + r.getId().replace("Yaoqiang-", ""));
            // Doesn't seem important?
            return;
        }
        // Handle WFFs
        if (hashCode == Documentation) {
            Documentation r = (Documentation) _ele;
            BaseElement et = ((BaseElement) r.getParent().getParent());

            if (__DEBUG)
                a.e.println("documentation " + " " + r.getId() + " " + r.toValue() + " saving to " + et.getId());
            if (tNodes.containsKey(et.getId())) {
                TNode at = (TNode) tNodes.get(et.getId());
                tNodes.remove(et.getId());
                at.node.addWFF(r.toValue());
                tNodes.put(et.getId(), at);
            } else {
                saveElement((XMLElement) et, getMessages, r.toValue());
            }
            return;
        }

        if (hashCode == ExclusiveGateway) {
            ExclusiveGateway r = (ExclusiveGateway) _ele;
            TNode myNode = new TNode(r.getId(), r.getName(), ExclusiveGateway, _ele);
            Vertex v = new Vertex(r.getId() + r.getName(), ExclusiveGateway);
            v.id = r.getId();
            v.isXOR = true;
            v.isGateway = true;
            v.setWFF(documentation);
            myNode.node = v;
            if (!tNodes.containsKey(r.getId()))
                tNodes.put(r.getId(), myNode);
            return;
        }
        if (hashCode == ParallelGateway) {
            ParallelGateway r = (ParallelGateway) _ele;
            TNode myNode = new TNode(r.getId(), r.getName(), ParallelGateway, _ele);
            Vertex v = new Vertex(r.getId() + r.getName(), ParallelGateway);
            v.id = r.getId();
            v.isAND = true;
            v.isGateway = true;
            v.setWFF(documentation);
            myNode.node = v;
            if (!tNodes.containsKey(r.getId()))
                tNodes.put(r.getId(), myNode);
            return;
        }
        if (hashCode == InclusiveGateway) {
            InclusiveGateway r = (InclusiveGateway) _ele;
            TNode myNode = new TNode(r.getId(), r.getName(), InclusiveGateway, _ele);
            Vertex v = new Vertex(r.getId() + r.getName(), InclusiveGateway);
            v.id = r.getId();
            v.isOR = true;
            v.isGateway = true;
            v.setWFF(documentation);
            myNode.node = v;
            if (!tNodes.containsKey(r.getId()))
                tNodes.put(r.getId(), myNode);
            return;
        }
        if (hashCode == ServiceTask) {
            ServiceTask r = (ServiceTask) _ele;
            TNode myNode = new TNode(r.getId(), r.getName(), ServiceTask, _ele);
            Vertex v = new Vertex(r.getId() + r.getName(), ServiceTask);
            v.id = r.getId();
            v.isOR = true;
            v.setWFF(documentation);
            if (r.getBoundaryEventRefs() != null)
                for (BoundaryEvent e : r.getBoundaryEventRefs()) {
                    v.boundaryRefs.add(e.getId());
                }
            myNode.node = v;
            if (!tNodes.containsKey(r.getId()))
                tNodes.put(r.getId(), myNode);
            return;
        }
        if (hashCode == UserTask) {
            UserTask r = (UserTask) _ele;
            TNode myNode = new TNode(r.getId(), r.getName(), UserTask, _ele);
            Vertex v = new Vertex(r.getId() + r.getName(), UserTask);
            v.id = r.getId();
            v.isOR = true;
            v.setWFF(documentation);
            if (r.getBoundaryEventRefs() != null)
                for (BoundaryEvent e : r.getBoundaryEventRefs()) {
                    v.boundaryRefs.add(e.getId());
                }
            myNode.node = v;
            if (!tNodes.containsKey(r.getId()))
                tNodes.put(r.getId(), myNode);
            return;
        }
        if (hashCode == CallActivity) {
            CallActivity r = (CallActivity) _ele;
            TNode myNode = new TNode(r.getId(), r.getName(), CallActivity, _ele);
            Vertex v = new Vertex(r.getId() + r.getName(), CallActivity);
            v.id = r.getId();
            v.setWFF(documentation);
            if (r.getBoundaryEventRefs() != null)
                for (BoundaryEvent e : r.getBoundaryEventRefs()) {
                    v.boundaryRefs.add(e.getId());
                }
            myNode.node = v;
            if (!tNodes.containsKey(r.getId()))
                tNodes.put(r.getId(), myNode);
            return;
        }

        if (hashCode == SubProcess) {
            SubProcess r = (SubProcess) _ele;
            TNode myNode = new TNode(r.getId(), r.getName(), SubProcess, _ele);
            Vertex v = new Vertex(r.getId() + r.getName(), SubProcess);
            v.id = r.getId();
            v.isSubprocess = true;
            v.setWFF(documentation);
            for (BoundaryEvent e : r.getBoundaryEventRefs()) {
                v.boundaryRefs.add(e.getId());
            }
            myNode.node = v;
            if (!tNodes.containsKey(r.getId()))
                tNodes.put(r.getId(), myNode);
            return;
        }
        if (hashCode == ErrorEventDefinition) {
            ErrorEventDefinition r = (ErrorEventDefinition) _ele;
            TNode myNode = new TNode(r.getId(), r.toName(), ErrorEventDefinition, _ele);
            Vertex v = new Vertex(r.getId() + r.toName(), ErrorEventDefinition);
            v.id = r.getId();
            v.isOR = true;
            v.setWFF(documentation);
            myNode.node = v;
            if (!tNodes.containsKey(r.getId()))
                tNodes.put(r.getId(), myNode);
            return;
        }

        if (hashCode == BPMNEdge) {

            return;
        }
        if (hashCode == SequenceFlow) {
            SequenceFlow r = (SequenceFlow) _ele;
            TEdge myEdge = new TEdge(r.getId(), r.getName(), r.getSourceRef(), r.getTargetRef(), SequenceFlow, _ele);
            tEdges.put(r.getId(), myEdge);
            //a.e.println("Flow " + " " + r.getId() + " " + r.getName() + " between " + r.getSourceRef() + " / " + r.getSourceFlowNode().getId());
            return;
        }
        if (hashCode == Collaboration) {
            //Collaboration r = (Collaboration)_ele;
            //a.e.println("Collaboration " + " " + r.getId() + " " + r.getConversationNodes().toElements().get(0).toValue());
            // Doesn't seem important?
            return;
        }
        if (hashCode == Task) {
            Task r = (Task) _ele;
            TNode myNode = new TNode(r.getId(), r.getName(), Task, _ele);
            Vertex v = new Vertex(r.getId() + r.getName(), Task);
            v.id = r.getId();
            if (r.getBoundaryEventRefs() != null)
                for (BoundaryEvent e : r.getBoundaryEventRefs()) {
                    v.boundaryRefs.add(e.getId());
                }
            v.setWFF(documentation);
            myNode.node = v;
            if (!tNodes.containsKey(r.getId()))
                tNodes.put(r.getId(), myNode);
            return;
        }
        if (hashCode == BPMNDiagram) {

            return;
        }
        if (hashCode == Participant) {
            if ((getMessages & a.e.DONT_SAVE_MESSAGES_AND_PARTICIPANTS) > 0) return;
            Participant r = (Participant) _ele;
            TNode myNode = new TNode(r.getId(), r.getName(), Participant, _ele);
            Vertex v = new Vertex(r.getId() + r.getName(), Participant);
            v.id = r.getId();
            v.setWFF(documentation);
            myNode.node = v;
            a.e.println("Participant " + r.getId() + " " + r.getName() + " -- ");
            if (!tNodes.containsKey(r.getId()))
                tNodes.put(r.getId(), myNode);
            return;
        }
        if (hashCode == StartEvent) {
            StartEvent r = (StartEvent) _ele;
            TNode myNode = new TNode(r.getId(), r.getName(), StartEvent, _ele);
            Vertex v = new Vertex(r.getId() + r.getName(), StartEvent);
            v.id = r.getId();
            v.setWFF(documentation);
            myNode.node = v;
            if (!tNodes.containsKey(r.getId()))
                tNodes.put(r.getId(), myNode);
            return;
        }
        if (hashCode == IntermediateCatchEvent) {
            IntermediateThrowEvent r = (IntermediateThrowEvent) _ele;
            TNode myNode = new TNode(r.getId(), r.getName(), IntermediateCatchEvent, _ele);
            Vertex v = new Vertex(r.getId() + r.getName(), IntermediateCatchEvent);
            v.id = r.getId();
            v.setWFF(documentation);
            myNode.node = v;
            if (!tNodes.containsKey(r.getId()))
                tNodes.put(r.getId(), myNode);
            return;
        }
        if (hashCode == IntermediateThrowEvent) {
            IntermediateThrowEvent r = (IntermediateThrowEvent) _ele;
            TNode myNode = new TNode(r.getId(), r.getName(), IntermediateThrowEvent, _ele);
            Vertex v = new Vertex(r.getId() + r.getName(), IntermediateThrowEvent);
            v.id = r.getId();
            v.setWFF(documentation);
            myNode.node = v;
            if (!tNodes.containsKey(r.getId()))
                tNodes.put(r.getId(), myNode);
            return;
        }
        if (hashCode == BoundaryEvent) {
            BoundaryEvent r = (BoundaryEvent) _ele;
            TNode myNode = new TNode(r.getId(), r.getName(), BoundaryEvent, _ele);
            Vertex v = new Vertex(r.getId() + r.getName(), BoundaryEvent);
            v.id = r.getId();
            v.setWFF(documentation);
            myNode.node = v;
            if (!tNodes.containsKey(r.getId()))
                tNodes.put(r.getId(), myNode);
            return;
        }
        if (hashCode == EndEvent) {
            EndEvent r = (EndEvent) _ele;
            TNode myNode = new TNode(r.getId(), r.getName(), EndEvent, _ele);
            Vertex v = new Vertex(r.getId() + r.getName(), EndEvent);
            v.id = r.getId();
            v.setWFF(documentation);
            myNode.node = v;
            if (!tNodes.containsKey(r.getId()))
                tNodes.put(r.getId(), myNode);
            return;
        }
        if (hashCode == eDefinitions) {
            // Who cares?
            return;
        }
        if (hashCode == BPMNProcess) {
            BPMNProcess r = (BPMNProcess) _ele;
            if (ProcessName != null && ProcessName.length() < 1 && r.getName().length() > 0) ProcessName = r.getName();
            return;
        }
        if (hashCode == MessageFlow) {
            if ((getMessages & a.e.DONT_SAVE_MESSAGES_AND_PARTICIPANTS) > 0) return;

            MessageFlow r = (MessageFlow) _ele;
            TEdge myEdge = new TEdge(r.getId(), r.getName(), r.getSourceRef(), r.getTargetRef(), SequenceFlow, _ele);
            tEdges.put(r.getId(), myEdge);
            return;
        }
        if( hashCode == BPMNPlane){
            // Can't see why this is important?
            // Ignoring
            return;
        }


        a.e.println("Don't have a handler in the graphLoader for elements of type " + _ele.getClass().getCanonicalName());
        a.e.println("If you could let me know or implement a converter then that would be super!");

        return;
    }

    public int c(String _in) {
        return _in.hashCode();
    }

    /**
     * Convert a yaoqiang bpmn node to a simple Vertex
     * Try to use actual mapping but if can't figure it out try to use basic element names
     *
     * @param inputNode
     * @return
     */
    public static Vertex convertNode(org.yaoqiang.bpmn.model.elements.core.common.FlowNode inputNode) {
        Vertex outputNode = null;
        String type = inputNode.getClass().getCanonicalName();
        if (outputNode == null) {
            if (type.toLowerCase().contains("gateway")) {
                //a.e.println(type + " " + inputNode.getName());
                outputNode = new Vertex("" + inputNode.getName(), GraphLoader.ExclusiveGateway);
                if (type.toLowerCase().contains("exclusivegateway")) {
                    outputNode.isXOR = true;
                }
                if (type.toLowerCase().contains("parallelgateway")) {
                    outputNode.isAND = true;
                }
                if (type.toLowerCase().contains("inclusivegateway")) {
                    outputNode.isOR = true;
                }
                if (inputNode.getOutgoingSequenceFlows().size() > 1)
                    outputNode.isSplit = true;
                else
                    outputNode.isJoin = true;

            } else if (type.toLowerCase().contains("event")) {
                outputNode = new Vertex("" + inputNode.getName(), GraphLoader.IntermediateThrowEvent);
            } else {
                outputNode = new Vertex("" + inputNode.getName(), GraphLoader.Task);
            }
        }
        outputNode.id = UUID.randomUUID().toString();
        return outputNode;
    }

}

class TEdge {
    String id;
    String name;
    String src;
    String trg;
    int type = GraphLoader.SequenceFlow;
    XMLElement ele;
    Edge edge;

    public TEdge(String _id, String _name, String _src, String _trg, int _type, XMLElement _ele) {
        id = _id;
        name = _name;
        src = _src;
        trg = _trg;
        type = _type;
        ele = _ele;
    }
}

class TNode {
    String id;
    String name;
    int type = GraphLoader.SequenceFlow;
    XMLElement ele;
    Vertex node;

    public TNode(String _id, String _name, int _type, XMLElement _ele) {
        id = _id;
        name = _name;
        type = _type;
        ele = _ele;
    }

}