package com.sri.ai.util.rangeoperation;

import java.util.List;

import com.sri.ai.util.Util;

/** Cumulative operator concatenating values in a list. */
public class Concatenate extends AbstractOperator {
	@Override
	public void initialize() {
		result = Util.list();
	}
	@SuppressWarnings("unchecked")
	@Override
	public void increment(Object value) {
		((List<Object>)result).add(value);
	}
}