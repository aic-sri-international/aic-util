package com.sri.ai.util.rangeoperation;

import com.google.common.base.Predicate;

/**
 * An averaging operation over a range, valid over numbers or arbitrarily nested lists of numbers, ignoring items
 * not satisfying a given predicate.
 */
public class PredicatedAveraging extends RangeOperation {
	public PredicatedAveraging(Range range, Predicate<Object> predicate) {
		super(new PredicatedAverage(predicate), range);
	}
	public PredicatedAveraging(String name, Predicate<Object> predicate, final int first, final int last, final int step) {
		super(new PredicatedAverage(predicate), new IntegerRange(name, first, last));
	}
	public PredicatedAveraging(String name, Predicate<Object> predicate, final int first, final int last) {
		this(name, predicate, first, last, 1);
	}
}