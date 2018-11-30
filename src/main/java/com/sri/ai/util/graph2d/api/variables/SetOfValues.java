package com.sri.ai.util.graph2d.api.variables;

import java.util.Iterator;

public interface SetOfValues extends Iterable<Value> {
	
	@Override
	Iterator<Value> iterator();
	
	/**
	 * The index of the value or -1 if not contained in the set.
	 * @param value
	 * @return
	 */
	int getIndex(Value value);

	int size();

}
