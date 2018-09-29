package com.sri.ai.util.graph2d.api.variables;

/**
 * The value of a variable; can represent any Java object but typically this will be numbers and strings.
 */
public interface Value {
	
	public static Value value(Object value) {
		return null;
	}
	
	String stringValue();
	
	int intValue();
	
	double doubleValue();

}
