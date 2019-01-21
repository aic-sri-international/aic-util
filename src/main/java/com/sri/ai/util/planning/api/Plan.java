package com.sri.ai.util.planning.api;

import com.google.common.base.Strings;

public interface Plan {

	double MAXIMUM_ESTIMATED_SUCCESS_WEIGHT = Double.MAX_VALUE;

	/**
	 * A "success weight" proportional to the probability of choosing this plan when compared to alternative plans.
	 * Using Double.MAX_VALUE indicates an infinite value and two alternative plans with this value may raise an exception.
	 */
	double getEstimatedSuccessWeight();

	default boolean isDeterministic() {
		return getEstimatedSuccessWeight() == MAXIMUM_ESTIMATED_SUCCESS_WEIGHT;
	}

	/**
	 * Indicates that this plan has no alternatives (can be used to represent a failed plan).
	 * @return
	 */
	boolean isFailedPlan();
	
	void execute(State state);
	
	void reward(double reward);

	default String nestedString() {
		return nestedString(0);
	}

	default String nestedString(int level) {
		return Strings.padStart("", level*4, ' ') + toString();
	}
}
