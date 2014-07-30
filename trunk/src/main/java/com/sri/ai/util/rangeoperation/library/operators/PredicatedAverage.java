package com.sri.ai.util.rangeoperation.library.operators;

import com.google.common.base.Predicate;
import com.sri.ai.util.rangeoperation.core.AbstractOperator;
import com.sri.ai.util.rangeoperation.core.RangeOperations;

/**
 * Similar to {@link Average} but taking a predicate selecting items to be considered in average.
 */
public class PredicatedAverage extends AbstractOperator {
	public PredicatedAverage(Predicate<Object> predicate) {
		this.predicate = predicate;
	}

	@Override
	public void initialize() {
		result = 0;
		weight = 0;
	}
	@Override
	public void increment(Object another) {
		if ( ! predicate.apply(another)) {
			return;
		}

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
	protected Predicate<Object> predicate;
}