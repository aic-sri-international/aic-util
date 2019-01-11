package com.sri.ai.util.function.api.variables;

import com.sri.ai.util.function.core.variables.DefaultUnit;

/**
* A measurement unit.
*/
public interface Unit {
	
	/** The name of the unit ("meter", "second", etc). */
	String getName();
	
	/** The symbol of the unit ("m" for "meter", "s" for "second", "lb" for pound, etc). */
	String getSymbol();
	
	public static final Unit NONE = new DefaultUnit("none", "");
	public static final Unit YEAR = new DefaultUnit("year", "y");
	public static final Unit METER = new DefaultUnit("meter", "m");
	public static final Unit SECOND = new DefaultUnit("second", "s");
	public static final Unit DOLLAR = new DefaultUnit("dollar", "$");

}
