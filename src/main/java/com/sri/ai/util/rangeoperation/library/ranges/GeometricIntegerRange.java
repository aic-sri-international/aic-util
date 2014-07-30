package com.sri.ai.util.rangeoperation.library.ranges;

import com.sri.ai.util.experiment.IntegerGeometricSeriesIterator;
import com.sri.ai.util.rangeoperation.core.AbstractRange;

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