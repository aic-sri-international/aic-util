package com.sri.ai.util.function.core.variables;

import java.math.BigDecimal;

import com.sri.ai.util.function.api.variables.Unit;
import com.sri.ai.util.function.core.values.SetOfRealValues;

public class RealVariable extends AbstractVariable {

	public RealVariable(String name, Unit unit) {
		super(name, unit);
	}

	public RealVariable(String name, Unit unit, SetOfRealValues setOfValues) {
		super(name, unit, setOfValues);
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
	
	@Override
	public String toString() {
		return "RealVariable [getName()=" + getName() + ", getUnit()=" + getUnit() + ", getSetOfValuesOrNull()="
				+ getSetOfValuesOrNull() + "]";
	}

}
