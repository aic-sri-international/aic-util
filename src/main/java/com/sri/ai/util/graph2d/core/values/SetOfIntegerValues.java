package com.sri.ai.util.graph2d.core.values;

import static com.sri.ai.util.collect.FunctionIterator.functionIterator;

import java.util.Iterator;

import com.sri.ai.util.collect.IntegerIterator;
import com.sri.ai.util.graph2d.api.variables.SetOfValues;
import com.sri.ai.util.graph2d.api.variables.Value;
import org.apache.commons.lang3.Validate;

public class SetOfIntegerValues implements SetOfValues {
	
	private int first;
	private int last;

	/**
	 * Construct a set of integer values
	 * @param first starting integer
	 * @param last ending integer - inclusive
	 */
	public SetOfIntegerValues(int first, int last) {
		Validate.isTrue(first <= last,
				String.format("first=%d must be less than or equal to last=%d", first, last));

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
		return functionIterator(new IntegerIterator(first, last + 1), Value::value);
	}

	@Override
	public String toString() {
		return "SetOfIntegerValues{" +
				"first=" + first +
				", last=" + last +
				'}';
	}
}
