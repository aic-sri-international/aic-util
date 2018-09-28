package com.sri.ai.util.graph2d.api.variables;

import java.util.Iterator;

/**
 * A mathematical variable; note that it works as a label and does not hold a value or assignment;
 * this is done by {@link Assignment}.
 *
 */
public interface Variable {

	/**
	 * An iterator for the possible values for this variable.
	 * @return
	 */
	Iterator<Value> valuesIterator();
	
}
