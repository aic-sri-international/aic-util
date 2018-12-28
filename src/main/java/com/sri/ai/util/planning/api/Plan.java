package com.sri.ai.util.planning.api;

import com.google.common.base.Strings;

public interface Plan {

	/**
	 * A "success weight" proportional to the probability of choosing this plan when compared to alternative plans.
	 * Using Double.MAX_VALUE indicates an infinite value and two alternative plans with this value may raise an exception.
	 */
	double getEstimatedSuccessWeight();

	void execute(State state);
	
	void reward(double reward);

	default String nestedString() {
		return nestedString(0);
	}

	default String nestedString(int level) {
		return Strings.padStart("", level*4, ' ') + toString();
	}
}
