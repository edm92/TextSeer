package be.fnord.util.functions.OCP;

import java.util.LinkedList;

/**
 * 
 * @author Evan Morrison edm92@uowmail.edu.au http://www.fnord.be
 * Apache License, Version 2.0, Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 * @param <T>
 */
public class PartitionListElement<T> extends LinkedList<T>{
	public PartitionListElement(PartitionListItem<T> results3) {
		for(T s : results3.get(0)){
			add(s); 
		}
	}

	public PartitionListElement() {
		super();
	}

	private static final long serialVersionUID = 1L;

	
}
