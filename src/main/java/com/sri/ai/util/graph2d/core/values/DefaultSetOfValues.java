package com.sri.ai.util.graph2d.core.values;

import static com.sri.ai.util.Util.join;

import java.util.Collection;
import java.util.Iterator;

import com.sri.ai.util.graph2d.api.variables.SetOfValues;
import com.sri.ai.util.graph2d.api.variables.Value;
import org.apache.commons.lang3.Validate;

public class DefaultSetOfValues implements SetOfValues {
	
	private Collection<Value> collection;
	
	public DefaultSetOfValues(Collection<Value> collection) {
		this.collection = Validate.notEmpty(collection);
	}
	
	@Override
	public Iterator<Value> iterator() {
		return collection.iterator();
	}

	
	@Override
	public String toString() {
		return "SetOfEnumValues(" + join(collection)+ ")";
	}

}
