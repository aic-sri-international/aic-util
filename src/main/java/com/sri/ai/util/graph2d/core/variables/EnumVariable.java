package com.sri.ai.util.graph2d.core.variables;

import com.sri.ai.util.graph2d.api.variables.Unit;
import com.sri.ai.util.graph2d.core.values.SetOfEnumValues;

public class EnumVariable extends AbstractVariable {
	
	public EnumVariable(String name, String... values) {
		super(name,Unit.NONE, new SetOfEnumValues(values));
	}
	
	public EnumVariable(String name, SetOfEnumValues enumSetOfValues) {
		super(name,Unit.NONE, enumSetOfValues);
	}
	
	@Override
	public SetOfEnumValues setOfValuesOrNull() {
		return (SetOfEnumValues) super.setOfValuesOrNull();
	}
}
