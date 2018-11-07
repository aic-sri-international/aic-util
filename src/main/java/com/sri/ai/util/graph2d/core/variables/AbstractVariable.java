package com.sri.ai.util.graph2d.core.variables;

import com.sri.ai.util.graph2d.api.variables.SetOfValues;
import com.sri.ai.util.graph2d.api.variables.Unit;
import com.sri.ai.util.graph2d.api.variables.Variable;

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
	public SetOfValues setOfValuesOrNull() {
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