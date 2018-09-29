package com.sri.ai.util.graph2d.core;

import static com.sri.ai.util.Util.mapIntoArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.sri.ai.util.graph2d.api.variables.Unit;
import com.sri.ai.util.graph2d.api.variables.Value;

public class EnumVariable extends AbstractVariable {
	
	private ArrayList<Value> values;
	
	public EnumVariable(String name, String... values) {
		super(name,Unit.NONE);
		this.values = mapIntoArrayList(Arrays.asList(values), v -> Value.value(v));
	}

	@Override
	public Iterator<Value> valuesIterator() {
		return values.iterator();
	}

}
