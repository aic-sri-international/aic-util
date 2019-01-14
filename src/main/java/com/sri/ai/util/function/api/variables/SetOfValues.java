package com.sri.ai.util.function.api.variables;

import java.util.Iterator;

import com.sri.ai.util.function.api.values.Value;

public interface SetOfValues extends Iterable<Value> {
	
	@Override
	Iterator<Value> iterator();
	
	Value get(int i);
	
	/**
	 * The index of the value or -1 if not contained in the set.
	 * @param value
	 * @return
	 */
	int getIndexOf(Value value);

	int size();

}
