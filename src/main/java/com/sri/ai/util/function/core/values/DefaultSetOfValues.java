package com.sri.ai.util.function.core.values;

import static com.sri.ai.util.Util.join;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang3.Validate;

import com.sri.ai.util.Util;
import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.api.variables.SetOfValues;

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

	@Override
	public int getIndex(Value value) {
		int index = Util.getIndexOf(collection, value);
		return index;
	}

	@Override
	public int size() {
		return collection.size();
	}

}
