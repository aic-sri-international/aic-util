package com.sri.ai.util.function.core.variables;

import com.sri.ai.util.function.api.variables.SetOfValues;
import com.sri.ai.util.function.api.variables.Unit;
import com.sri.ai.util.function.api.variables.Variable;

public abstract class AbstractVariable implements Variable {

	protected String name;
	protected Unit unit;
	protected SetOfValues setOfValuesOrNull;

	public AbstractVariable(String name, Unit unit) {
		this.name = name;
		this.unit = unit;
		this.setOfValuesOrNull = null;
	}

	public AbstractVariable(String name, Unit unit, SetOfValues setOfValuesOrNull) {
		this.name = name;
		this.unit = unit;
		this.setOfValuesOrNull = setOfValuesOrNull;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Unit getUnit() {
		return unit;
	}
	
	@Override
	public SetOfValues getSetOfValuesOrNull() {
		return setOfValuesOrNull;
	}

	@Override
	public String toString() {
		return "AbstractVariable{" +
				"name='" + name + '\'' +
				", unit=" + unit +
				", setOfValuesOrNull=" + setOfValuesOrNull +
				'}';
	}
}