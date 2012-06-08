package au.edu.dsl.dlab.processtools.parser.bpmn;

import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.AttributeMap.SerializableRectangle2D;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.ListenableDirectedGraph;

import au.edu.dsl.dlab.processtools.Edge;
import au.edu.dsl.dlab.processtools.Graph;
import au.edu.dsl.dlab.processtools.Vertex;


public class visualize<T extends Vertex, V extends Edge> {
	public transient JGraphModelAdapter<T,V> m_jgAdapter;
 
//	public static void main(String[] args) {
//		 String inputFile = "Accounting Services .bpmn20.xml";
//		visualize<Vertex, Edge> myView = new visualize<Vertex,Edge>();
//		Activiti<Vertex,Edge> myProcess = new Activiti<Vertex,Edge>();
//		myProcess.parseXmlFile(inputFile);
//		Graph<Vertex, Edge> output = myProcess.parseDocument(inputFile);
//		myProcess.parseGPSDocument(inputFile);
//
//		// create a JGraphT graph
//
//	     // create a visualization using JGraph, via an adapter
//	     ListenableGraph<Vertex,Edge> g = new ListenableDirectedGraph<Vertex,Edge>(output );
//	     myView.m_jgAdapter = new JGraphModelAdapter<Vertex,Edge>( g );
//	     JGraph jgraph = new JGraph( myView.m_jgAdapter );
//	     for(Vertex vert : myProcess.vertexMap.values()){
//	    	 myView.positionVertexAt(vert, vert.x, vert.y);
//	     }
//	     
//	     
//	     JFrame frame = new JFrame();
//	     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//	     frame.add(new JScrollPane(jgraph));
//	     frame.pack();
//	     frame.setSize(frame.getWidth(), frame.getHeight() + 100);
//	     frame.setVisible(true);
//	}
	
	public void showModel(Graph<T,V> output){
	     ListenableGraph<T,V> g = new ListenableDirectedGraph<T,V>(output );
	     m_jgAdapter = new JGraphModelAdapter<T,V>( g );
	     JGraph jgraph = new JGraph( m_jgAdapter );
	     for(T vert : output.vertexSet()){
	    	 positionVertexAt(vert, vert.x, vert.y);
	     }
	     
	     
	     JFrame frame = new JFrame();
	     BoxLayout boxLayout = new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS); 
	     frame.setLayout(boxLayout);
	     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	     frame.add(new JLabel("Effect Scenarios: " + output.effects));
	     frame.add(new JScrollPane(jgraph));
	     frame.pack();
	     frame.setSize(frame.getWidth(), frame.getHeight() + 100);
	     frame.setVisible(true);

	}
	

    private void positionVertexAt( T vertex, int x, int y ) {
    	//System.err.println("Putting " + vertex.name + " at " + x + " , " + y);
        DefaultGraphCell cell = m_jgAdapter.getVertexCell( vertex );
        Map<?, ?>              attr = cell.getAttributes(  );
        SerializableRectangle2D        b    = (SerializableRectangle2D) GraphConstants.getBounds( attr );

        GraphConstants.setBounds( attr, new SerializableRectangle2D( Math.pow(x,1.1), Math.pow(y,1.1), b.width, b.height ) );

        Map<DefaultGraphCell, Map<?, ?>> cellAttr = new HashMap<DefaultGraphCell, Map<?, ?>>(  );
        cellAttr.put( cell, attr );
        m_jgAdapter.edit( cellAttr, null, null, null );
    }

}
