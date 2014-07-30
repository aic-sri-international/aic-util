package com.sri.ai.util.experiment;

import java.util.HashMap;

/**
 * A {@link HashMap} with a get method that allows the user to provide a default in case the underlying value is <code>null</code>.
 */
@SuppressWarnings("serial")
public class HashMapWithGetWithDefault extends HashMap {
    public Object getWithDefault(Object key, Object defaultValue) {
	Object value = get(key);
	if (value == null)
	    return defaultValue;
	return value;
    }
}
