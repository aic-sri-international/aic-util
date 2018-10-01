package com.sri.ai.util.graph2d.core.values;

import static com.sri.ai.util.collect.FunctionIterator.functionIterator;
import static com.sri.ai.util.graph2d.api.variables.Value.value;

import java.util.Iterator;

import com.sri.ai.util.collect.IntegerIterator;
import com.sri.ai.util.graph2d.api.variables.SetOfValues;
import com.sri.ai.util.graph2d.api.variables.Value;

public class SetOfIntegerValues implements SetOfValues {
	
	private int first;
	private int last;
	
	public SetOfIntegerValues(int first, int last) {
		this.first = first;
		this.last = last;
	}
	
	public static SetOfIntegerValues setOfIntegerValues(int first, int last) {
		return new SetOfIntegerValues(first, last);
	}

	public int getFirst() {
		return first;
	}
	
	public int getLast() {
		return last;
	}
	
	@Override
	public Iterator<Value> iterator() {
		return functionIterator(new IntegerIterator(first, last - 1), i -> value(i));
	}

}
