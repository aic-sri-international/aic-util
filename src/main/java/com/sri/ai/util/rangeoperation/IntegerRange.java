package com.sri.ai.util.rangeoperation;

import com.sri.ai.util.experiment.IntegerArithmeticSeriesIterator;

/** A range over integer values. */
public class IntegerRange extends AbstractRange {
	public IntegerRange(String name, int first, int last, int step) {
		super(name);
		this.first = first;
		this.last = last;
		this.step = step;
	}
	public IntegerRange(String name, int first, int last) {
		this(name, first, last, 1);
	}
	@Override
	public Object apply() { 
		return new IntegerArithmeticSeriesIterator(first, last, step);
	}	
	private int first, last, step;
}