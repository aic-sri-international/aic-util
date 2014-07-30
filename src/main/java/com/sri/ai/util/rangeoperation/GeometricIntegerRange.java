package com.sri.ai.util.rangeoperation;

import com.sri.ai.util.experiment.IntegerGeometricSeriesIterator;

/** A geometric range over integer values. */
public class GeometricIntegerRange extends AbstractRange {
	public GeometricIntegerRange(String name, int first, int last, float rate) {
		super(name);
		this.first = first;
		this.last = last;
		this.rate = rate;
	}
	@Override
	public Object apply() { 
		return new IntegerGeometricSeriesIterator(first, last, rate);
	}	
	private int first, last;
	private float rate;
}