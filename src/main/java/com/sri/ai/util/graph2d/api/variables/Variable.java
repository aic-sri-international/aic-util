package com.sri.ai.util.graph2d.api.variables;

import java.math.BigDecimal;

import com.sri.ai.util.graph2d.core.variables.EnumVariable;
import com.sri.ai.util.graph2d.core.variables.IntegerVariable;
import com.sri.ai.util.graph2d.core.variables.RealVariable;

/**
 * A mathematical variable; note that it works as a label and does not hold a value or assignment;
 * this is done by {@link Assignment}.
 *
 */
public interface Variable {

	String getName();
	
	Unit getUnit();
	
	SetOfValues getSetOfValuesOrNull();
	
	public static EnumVariable enumVariable(String name, String... values) {
		return new EnumVariable(name, values);
	}
	
	public static IntegerVariable integerVariable(String name, Unit unit) {
		return new IntegerVariable(name, unit);
	}
	
	public static IntegerVariable integerVariable(String name, Unit unit, int first, int last) {
		return new IntegerVariable(name, unit, first, last);
	}
	
	public static RealVariable realVariable(String name, Unit unit, int first, BigDecimal step, int last) {
		return new RealVariable(name, unit, first, step, last);
	}
	
	public static RealVariable realVariable(String name, Unit unit, String first, String step, String last) {
		return new RealVariable(name, unit, first, step, last);
	}
	
	public static RealVariable realVariable(String name, Unit unit) {
		return new RealVariable(name, unit);
	}
	
}
