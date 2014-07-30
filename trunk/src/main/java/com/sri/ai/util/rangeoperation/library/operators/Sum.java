package com.sri.ai.util.rangeoperation.library.operators;

import com.sri.ai.util.rangeoperation.core.AbstractOperator;

public class Sum extends AbstractOperator {
	@Override
	public void initialize() {
		result = 0;
	}
	@Override
	public void increment(Object another) {
		result = ((Number)result).doubleValue() + ((Number)another).doubleValue();
	}
}