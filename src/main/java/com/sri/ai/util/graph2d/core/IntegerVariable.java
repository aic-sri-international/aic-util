package com.sri.ai.util.graph2d.core;

import static com.sri.ai.util.collect.FunctionIterator.functionIterator;
import static com.sri.ai.util.graph2d.api.variables.Value.value;

import java.util.Iterator;

import com.sri.ai.util.collect.IntegerIterator;
import com.sri.ai.util.graph2d.api.variables.Unit;
import com.sri.ai.util.graph2d.api.variables.Value;

public class IntegerVariable extends AbstractVariable {
	
	private int first;
	private int last;
	
	public IntegerVariable(String name, Unit unit, int first, int last) {
		super(name, unit);
		this.first = first;
		this.last = last;
	}

	public int getFirst() {
		return first;
	}
	
	public int getLast() {
		return last;
	}
	
	@Override
	public Iterator<Value> valuesIterator() {
		return functionIterator(new IntegerIterator(first, last - 1), i -> value(i));
	}

}
