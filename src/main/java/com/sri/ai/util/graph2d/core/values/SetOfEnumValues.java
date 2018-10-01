package com.sri.ai.util.graph2d.core.values;

import static com.sri.ai.util.Util.mapIntoList;

import java.util.Arrays;

import com.sri.ai.util.graph2d.api.variables.Value;

public class SetOfEnumValues extends DefaultSetOfValues {
	
	public SetOfEnumValues(String... values) {
		super(mapIntoList(Arrays.asList(values), v -> Value.value(v)));
	}

	public static SetOfEnumValues setOfEnumValues(String... values) {
		return new SetOfEnumValues(values);
	}

}
