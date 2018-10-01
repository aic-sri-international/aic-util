package com.sri.ai.util.graph2d.core.values;

import java.math.BigDecimal;
import java.util.Iterator;

import com.sri.ai.util.graph2d.api.variables.SetOfValues;
import com.sri.ai.util.graph2d.api.variables.Unit;
import com.sri.ai.util.graph2d.api.variables.Value;

public class SetOfRealValues implements SetOfValues {
	
	private BigDecimal first;
	private BigDecimal step;
	private BigDecimal last;
	
	public SetOfRealValues(String name, Unit unit, int first, BigDecimal step) {
		this(first, step, Integer.MAX_VALUE);
	}

	public SetOfRealValues(String first, String step) {
		this(first, step, Integer.toString(Integer.MAX_VALUE));
	}

	public SetOfRealValues(int first, BigDecimal step, int last) {
		this.first = new BigDecimal(first);
		this.step = step;
		this.last = new BigDecimal(last);
	}

	public SetOfRealValues(String first, String step, String last) {
		this.first = new BigDecimal(first);
		this.step = new BigDecimal(step);
		this.last = new BigDecimal(last);
	}

	public BigDecimal getFirst() {
		return first;
	}
	
	public BigDecimal getStep() {
		return step;
	}
	
	public BigDecimal getLast() {
		return last;
	}
	
	@Override
	public Iterator<Value> iterator() {
		return null; // TODO implement: similar to IntegerIterator
	}

}
