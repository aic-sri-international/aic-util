package com.sri.ai.util.rangeoperation.library.rangeoperations;

import com.sri.ai.util.rangeoperation.api.Range;
import com.sri.ai.util.rangeoperation.api.RangeOperation;
import com.sri.ai.util.rangeoperation.library.operators.Average;
import com.sri.ai.util.rangeoperation.library.ranges.IntegerRange;


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