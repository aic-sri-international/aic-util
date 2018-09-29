package com.sri.ai.util.graph2d.core;

import java.math.BigDecimal;
import java.util.Iterator;

import com.sri.ai.util.graph2d.api.variables.Unit;
import com.sri.ai.util.graph2d.api.variables.Value;

public class RealVariable extends AbstractVariable {
	
	private BigDecimal first;
	private BigDecimal step;
	private BigDecimal last;
	
	public RealVariable(String name, Unit unit, int first, BigDecimal step) {
		this(name, unit, first, step, Integer.MAX_VALUE);
	}

	public RealVariable(String name, Unit unit, String first, String step) {
		this(name, unit, first, step, Integer.toString(Integer.MAX_VALUE));
	}

	public RealVariable(String name, Unit unit, int first, BigDecimal step, int last) {
		super(name, unit);
		this.first = new BigDecimal(first);
		this.step = step;
		this.last = new BigDecimal(last);
	}

	public RealVariable(String name, Unit unit, String first, String step, String last) {
		super(name, unit);
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
	public Iterator<Value> valuesIterator() {
		return null; // TODO implement
	}

}
