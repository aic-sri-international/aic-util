package com.sri.ai.util.rangeoperation;

public class IntegerRangeOperation extends RangeOperation {
	public IntegerRangeOperation(Operator operator, String name, final int first, final int last, final int step) {
		super(operator, new IntegerRange(name, first, last));
	}
	public IntegerRangeOperation(Operator operator, String name, final int first, final int last) {
		this(operator, name, first, last, 1);
	}
}