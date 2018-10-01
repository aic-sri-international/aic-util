package com.sri.ai.util.graph2d.core.values;

import java.util.Collection;
import java.util.Iterator;

import com.sri.ai.util.graph2d.api.variables.SetOfValues;
import com.sri.ai.util.graph2d.api.variables.Value;

public class DefaultSetOfValues implements SetOfValues {
	
	private Collection<Value> collection;
	
	public DefaultSetOfValues(Collection<Value> collection) {
		this.collection = collection;
	}
	
	@Override
	public Iterator<Value> iterator() {
		return collection.iterator();
	}

}
