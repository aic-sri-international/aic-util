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
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((setOfValuesOrNull == null) ? 0 : setOfValuesOrNull.hashCode());
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractVariable other = (AbstractVariable) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}