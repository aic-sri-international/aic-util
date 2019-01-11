package com.sri.ai.util.function.core.values;

import static com.sri.ai.util.Util.mapIntoList;

import java.util.Arrays;

import com.sri.ai.util.function.api.values.Value;

public class SetOfEnumValues extends DefaultSetOfValues {
	
	public SetOfEnumValues(String... values) {
		super(mapIntoList(Arrays.asList(values), Value::value));
	}

	public static SetOfEnumValues setOfEnumValues(String... values) {
		SetOfEnumValues setOfEnumValues = new SetOfEnumValues(values);
		return setOfEnumValues;
	}

}
