package com.sri.ai.util.rangeoperation;

import java.util.Collection;

/** An operation concatenating values into a list, with {@link Concatenate}. */
public class Axis extends RangeOperation {
	public Axis(Range range) {
		super(range);
	}
	public Axis(String name, final int first, final int last, final int step) {
		super(new IntegerRange(name, first, last, step));
	}
	public Axis(String name, final int first, final int last) {
		this(name, first, last, 1);
	}
	/** Creates an axis on a geometric integer series. */
	public Axis(String name, final int first, final int last, final float rate) {
		super(new GeometricIntegerRange(name, first, last, rate));
	}
	/** Creates an axis on a discrete range. */
	public Axis(String name, Collection collection) {
		super(new DiscreteRange(name, collection));
	}
}