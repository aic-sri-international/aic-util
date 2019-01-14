package com.sri.ai.util.function.core.values;

import static com.sri.ai.util.Util.join;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang3.Validate;

import com.sri.ai.util.Util;
import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.api.variables.SetOfValues;

public class DefaultSetOfValues implements SetOfValues {
	
	private ArrayList<Value> values;
	
	public DefaultSetOfValues(ArrayList<Value> collection) {
		this.values = Validate.notEmpty(collection);
	}
	
	@Override
	public Iterator<Value> iterator() {
		return values.iterator();
	}

	@Override
	public Value get(int i) {
		return values.get(i);
	}
	
	@Override
	public int getIndexOf(Value value) {
		int index = Util.getIndexOf(values, value);
		return index;
	}

	@Override
	public int size() {
		return values.size();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((values == null) ? 0 : values.hashCode());
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
		DefaultSetOfValues other = (DefaultSetOfValues) obj;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SetOfEnumValues(" + join(values)+ ")";
	}

}
