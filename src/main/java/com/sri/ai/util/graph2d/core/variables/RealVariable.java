package com.sri.ai.util.graph2d.core.variables;

import java.math.BigDecimal;

import com.sri.ai.util.graph2d.api.variables.Unit;
import com.sri.ai.util.graph2d.core.values.SetOfRealValues;

public class RealVariable extends AbstractVariable {
	
	public RealVariable(String name, Unit unit) {
		super(name, unit);
	}

	public RealVariable(String name, Unit unit, int first, BigDecimal step) {
		super(name, unit, new SetOfRealValues(first, step, Integer.MAX_VALUE));
	}

	public RealVariable(String name, Unit unit, String first, String step) {
		super(name, unit, new SetOfRealValues(first, step, Integer.toString(Integer.MAX_VALUE)));
	}

	public RealVariable(String name, Unit unit, int first, BigDecimal step, int last) {
		super(name, unit, new SetOfRealValues(first, step, last));
	}

	public RealVariable(String name, Unit unit, String first, String step, String last) {
		super(name, unit, new SetOfRealValues(first, step, last));
	}
}
