package com.sri.ai.util.rangeoperation;


public class Summation extends RangeOperation {
	public Summation(String name, final int first, final int last, final int step) {
		super(new Sum(), new IntegerRange(name, first, last));
	}
	public Summation(String name, final int first, final int last) {
		this(name, first, last, 1);
	}
}