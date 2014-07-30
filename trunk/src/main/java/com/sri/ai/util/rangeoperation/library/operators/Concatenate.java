package com.sri.ai.util.rangeoperation.library.operators;

import java.util.List;

import com.sri.ai.util.Util;
import com.sri.ai.util.rangeoperation.core.AbstractOperator;

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