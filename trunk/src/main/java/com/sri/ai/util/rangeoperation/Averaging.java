package com.sri.ai.util.rangeoperation;


/** An averaging operation over a range, valid over numbers or arbitrarily nested lists of numbers. */
public class Averaging extends RangeOperation {
	public Averaging(Range range) {
		super(new Average(), range);
	}
	public Averaging(String name, final int first, final int last, final int step) {
		super(new Average(), new IntegerRange(name, first, last));
	}
	public Averaging(String name, final int first, final int last) {
		this(name, first, last, 1);
	}
}