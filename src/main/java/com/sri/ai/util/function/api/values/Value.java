package com.sri.ai.util.function.api.values;

import com.sri.ai.util.function.core.values.DefaultValue;

/**
 * The value of a variable; can represent any Java object but typically this will be numbers and strings.
 */
public interface Value {
	
	static Value value(Object value) {
		DefaultValue defaultValue = new DefaultValue(value);
		return defaultValue;
	}
	
	Object objectValue();
	
	String stringValue();
	
	int intValue();
	
	double doubleValue();

}
