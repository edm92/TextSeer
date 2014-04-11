package be.fnord.util.processModel.visual;

import be.fnord.util.processModel.Edge;
import be.fnord.util.processModel.Vertex;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

import javax.swing.*;
import java.awt.*;

public class jungViewer {

    public void displayGraph(be.fnord.util.processModel.Graph<Vertex, Edge> g, String name) {
        Graph<String, String> vg = new SparseMultigraph<String, String>();

        for (Vertex v : g.vertexSet()) {
            vg.addVertex(v.toString());
        }
        for (Edge e : g.edgeSet()) {
            vg.addEdge(e.id.toString(), e.getSource().toString(), e.getTarget().toString());
        }

        // The Layout<V, E> is parameterized by the vertex and edge types
        Layout<String, String> layout = new FRLayout<String, String>(vg);
        layout.setSize(new Dimension(600, 600)); // sets the initial size of the space
        // The BasicVisualizationServer<V,E> is parameterized by the edge types
        BasicVisualizationServer<String, String> vv =
                new BasicVisualizationServer<String, String>(layout);
        vv.setPreferredSize(new Dimension(800, 800)); //Sets the viewing area size


        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<String>());
//		 vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
        vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);

        JFrame frame = new JFrame(name + " - Graph Display");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);

    }


}
