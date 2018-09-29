package com.sri.ai.util.graph2d.core;

import com.sri.ai.util.graph2d.api.variables.Unit;
import com.sri.ai.util.graph2d.api.variables.Variable;

public abstract class AbstractVariable implements Variable {

	protected String name;
	protected Unit unit;

	public AbstractVariable(String name, Unit unit) {
		this.name = name;
		this.unit = unit;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Unit getUnit() {
		return unit;
	}

}