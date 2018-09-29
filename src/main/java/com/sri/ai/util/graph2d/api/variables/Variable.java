package com.sri.ai.util.graph2d.api.variables;

import java.math.BigDecimal;
import java.util.Iterator;

import com.sri.ai.util.graph2d.core.EnumVariable;
import com.sri.ai.util.graph2d.core.IntegerVariable;
import com.sri.ai.util.graph2d.core.RealVariable;

/**
 * A mathematical variable; note that it works as a label and does not hold a value or assignment;
 * this is done by {@link Assignment}.
 *
 */
public interface Variable {

	String getName();
	
	Unit getUnit();
	
	/**
	 * An iterator for the possible values for this variable.
	 * @return
	 */
	Iterator<Value> valuesIterator();
	
	public static EnumVariable enumVariable(String name, String... values) {
		return new EnumVariable(name, values);
	}
	
	public static IntegerVariable integerVariable(String name, Unit unit, int first, int last) {
		return new IntegerVariable(name, unit, first, last);
	}
	
	public static RealVariable realVariable(String name, Unit unit, int first, BigDecimal step, int last) {
		return new RealVariable(name, unit, first, step, last);
	}
	
}
