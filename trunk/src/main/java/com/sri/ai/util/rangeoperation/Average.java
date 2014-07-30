package com.sri.ai.util.rangeoperation;

public class Average extends AbstractOperator {
	@Override
	public void initialize() {
		result = 0;
		weight = 0;
	}
	@Override
	public void increment(Object another) {
		if (weight == 0) {
			result = another;
			weight = 1;
		}
		else {
			result = RangeOperations.incrementalComponentWiseAverageArbitraryDepth(result, weight, another);
			weight++;
		}
	}

	protected int weight = 0;
}