package com.sri.ai.util.graph2d.core.values;

import static com.sri.ai.util.collect.FunctionIterator.functionIterator;

import java.util.Iterator;

import org.apache.commons.lang3.Validate;

import com.sri.ai.util.collect.IntegerIterator;
import com.sri.ai.util.graph2d.api.variables.SetOfValues;
import com.sri.ai.util.graph2d.api.variables.Value;

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

	@Override
	public int getIndex(Value value) {
		int valueAsInt = value.intValue();
		if(valueAsInt < first && valueAsInt > last) {
			return -1;
		}
		else {
			int result = valueAsInt - first;
			return result;
		}
	}

	@Override
	public int size() {
		return last - first + 1;
	}
}
