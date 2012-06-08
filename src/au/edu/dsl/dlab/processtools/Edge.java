package au.edu.dsl.dlab.processtools;

import java.util.UUID;

import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Extension of the DefaultEdge. Any changes to the edge class should go here rather than in the utility functions.
 * @author edm92
 *
 */
public class Edge extends DefaultWeightedEdge {
	private static final long serialVersionUID = 1L;
	private Effect immEffect = new Effect();
	private String effect;
	public String name = "";
	public String id;
	
	private UUID ID;
	public Effect getEffect(){immEffect.setFormula(effect); return immEffect;};
	
	public String getID() { return ID.toString(); };
	public Edge(){super();ID = UUID.randomUUID();	}
	public Edge(String _name){this(); name = _name;}
	public void addEffect(String _effect){
		effect = _effect;
	}
	
	@SuppressWarnings("unchecked")
	public String toString(Graph<?,?> in){
		String result = "";
		try{
			Graph<Vertex, Edge> newG = (Graph<Vertex, Edge>) in;
			
			if(newG != null){
				if(newG.getEdgeSource(this) != null && newG.getEdgeTarget(this) != null){
					result = "{" + newG.getEdgeSource(this).name + "} --&gt; {" + newG.getEdgeTarget(this).name + "}";
				}
			}
		}catch(Exception e){
			return "";
		};
		
		return result;
	}

}
