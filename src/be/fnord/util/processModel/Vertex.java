package be.fnord.util.processModel;

import java.util.LinkedList;
import java.util.UUID;
import be.fnord.util.logic.Effect;
import be.fnord.util.processModel.util.GraphLoader;

/**
 * 
 * @author Evan Morrison edm92@uowmail.edu.au http://www.fnord.be
 * Apache License, Version 2.0, Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */
public class Vertex extends Graph<Vertex, Edge>{
	private static final long serialVersionUID = 1L;
	public String name = "";
	public int type = 0;
	public String id = "";
	public boolean isSplit = false;
	public boolean isJoin = false;
	public boolean isSubstructural = false;
	public boolean isAND = false;
	public boolean isXOR = false;
	public boolean isOR = false;
	public boolean isGateway = false;
	public boolean isSubprocess = false;
	public boolean isTrace = false;
	
	private String effect = "";
	private Effect immEffect = new Effect();
	
	public Vertex corresponding = null;
	public LinkedList<String> boundaryRefs = new LinkedList<String>();
	
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public String getType() {return GraphLoader.getType(type);}
	public void setType(int type) {this.type = type;	}
	public Vertex() { this(UUID.randomUUID().toString()); }
	public Vertex(String _name){this(_name, GraphLoader.Task);}
	public Vertex(String _name, int _type){super(); this.name=_name; this.type = _type;}
	public Vertex getCorresponding() {	return corresponding;	}
	public void setCorresponding(Vertex corresponding) {if(corresponding.corresponding != null || corresponding.isXOR) return ; 
														this.corresponding = corresponding; this.corresponding.corresponding = this;}
	public Effect getEffect(){immEffect.setFormula(effect); return immEffect;};
	public void setEffect(String _effect){	effect = _effect; 	}
	public void addEffect(String _effect){ if(effect.length() > 0){effect = "( " + effect + " ) " + a.e.AND + " ( " + _effect + " )";}else	setEffect(_effect);	}

	public String toString(){
		String result = "";
		if(this.getEffect().getFormula().compareTo(a.e.EMPTY_EFFECT) != 0){			
			result += "{"+this.getEffect().getFormula()+"}";
		}
		if(isSubprocess) {
			if(boundaryRefs.size() > 0){
				result += "[boundedEvents:";
				for(String s: boundaryRefs){
					result += s + ",";
				}
				result = result.substring(0, result.length() - 1) + "]";
			}
		}
		return this.name+"("+GraphLoader.getType(type)+ result +")";	}
	public boolean isSubprocess() {	return isSubprocess;}
	public void setSubprocess(boolean isSubprocess) {	this.isSubprocess = isSubprocess;	}
	
	// TODO implement this at some point
	public Vertex makeCopy(){ return this; }
}

