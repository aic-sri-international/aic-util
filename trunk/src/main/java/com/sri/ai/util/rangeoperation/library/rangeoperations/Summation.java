package com.sri.ai.util.rangeoperation.library.rangeoperations;

import com.sri.ai.util.rangeoperation.api.RangeOperation;
import com.sri.ai.util.rangeoperation.library.operators.Sum;
import com.sri.ai.util.rangeoperation.library.ranges.IntegerRange;


public class Summation extends RangeOperation {
	public Summation(String name, final int first, final int last, final int step) {
		super(new Sum(), new IntegerRange(name, first, last));
	}
	public Summation(String name, final int first, final int last) {
		this(name, first, last, 1);
	}
}