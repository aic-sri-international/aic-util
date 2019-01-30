package com.sri.ai.util.function.core.values;

import static com.sri.ai.util.collect.FunctionIterator.functionIterator;
import static com.sri.ai.util.function.api.values.Value.value;

import java.util.Iterator;

import com.sri.ai.util.collect.IntegerIterator;
import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.api.variables.SetOfValues;

public class SetOfIntegerValues implements SetOfValues {
	
	private int first;
	private int last;

	/**
	 * Construct a set of integer values
	 * @param first starting integer
	 * @param last ending integer - inclusive
	 */
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
		return functionIterator(new IntegerIterator(first, last + 1), Value::value);
	}

	@Override
	public Value get(int i) {
		if (i >= 0 && i < size()) {
			return value(first + i);
		}
		throw new IndexOutOfBoundsException("Set of integer values has size " + size() + " but index was " + i);
	}

	@Override
	public int getIndexOf(Value value) {
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

	@Override
	public String toString() {
		return "SetOfIntegerValues{" +
				"first=" + first +
				", last=" + last +
				'}';
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + first;
		result = prime * result + last;
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
		SetOfIntegerValues other = (SetOfIntegerValues) obj;
		if (first != other.first)
			return false;
		if (last != other.last)
			return false;
		return true;
	}
}
