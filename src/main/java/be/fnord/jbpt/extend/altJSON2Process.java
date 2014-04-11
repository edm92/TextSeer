package be.fnord.jbpt.extend;

import be.fnord.util.processModel.Edge;
import be.fnord.util.processModel.Graph;
import be.fnord.util.processModel.Vertex;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.io.JSON2Process;
import org.jbpt.throwable.SerializationException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


/**
 * This class loads an inputted JSON string using JSON2Process in jbpt and then converts the processModel into a graph structure.
 * TODO the main convert method is currently not completed. Because jbpt sucks
 * 
 *
 * @author Evan Morrison edm92@uowmail.edu.au http://www.fnord.be
 *         Apache License, Version 2.0, Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */
public class altJSON2Process {

    // 
    public static Graph<Vertex, Edge> convert(String name, String json) {
        Graph<Vertex, Edge> result = new Graph<Vertex, Edge>(Edge.class);
        result.name = name;
        ProcessModel jbptProcess = null;
        try {
            jbptProcess = JSON2Process.convert(json);
        } catch (SerializationException e) {
            e.printStackTrace();
        }
        if (jbptProcess == null) return null;

        // Lets copy the process to a graph
        //TODO this code fragment is broken. Please fix
        for (ControlFlow<?> e : jbptProcess.getEdges()) {
            a.e.println("altJSON2Process.convert(String name, String json) is not completed. This method needs to be fixed!", a.e.DEBUG);
            if (e != null) break; // Remove me
//			FlowNode src = (FlowNode) e.getSource();
//			FlowNode trg = (FlowNode) e.getTarget();
//			Vertex _src = new Vertex(src.getName()+src.getId(), "task"); // 
//			Vertex _trg = new Vertex(trg.getName()+trg.getId(), "task");
//			_src.id = src.getId();
//			_trg.id = trg.getId();
//			result.addV(_src);
//			result.addV(_trg);
//			Edge ee = new Edge(_src,_trg);
//			result.addE(ee);

        }


        return result;
    }

    @SuppressWarnings({"resource"})
    public static String readFile(String file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        if (reader == null) return "";
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
