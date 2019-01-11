package com.sri.ai.util.function.core.variables;

import com.sri.ai.util.function.api.variables.Unit;
import com.sri.ai.util.function.core.values.SetOfEnumValues;

public class EnumVariable extends AbstractVariable {
	
	public EnumVariable(String name, String... values) {
		super(name, Unit.NONE, values.length == 0? null : new SetOfEnumValues(values));
	}
	
	public EnumVariable(String name, SetOfEnumValues enumSetOfValues) {
		super(name, Unit.NONE, enumSetOfValues);
	}
	
	@Override
	public SetOfEnumValues getSetOfValuesOrNull() {
		return (SetOfEnumValues) super.getSetOfValuesOrNull();
	}
}
