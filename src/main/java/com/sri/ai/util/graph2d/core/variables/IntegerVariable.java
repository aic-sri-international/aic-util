package com.sri.ai.util.graph2d.core.variables;

import com.sri.ai.util.graph2d.api.variables.SetOfValues;
import com.sri.ai.util.graph2d.api.variables.Unit;
import com.sri.ai.util.graph2d.core.values.SetOfIntegerValues;

public class IntegerVariable extends AbstractVariable {
	
	public IntegerVariable(String name, Unit unit) {
		super(name, unit, null);
	}

	public IntegerVariable(String name, Unit unit, SetOfValues setOfValues) {
		super(name, unit, setOfValues);
	}

	public IntegerVariable(String name, Unit unit, int first, int last) {
		super(name, unit, new SetOfIntegerValues(first, last));
	}
}
