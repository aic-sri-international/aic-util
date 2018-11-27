package com.sri.ai.util.planning.core;

import com.sri.ai.util.planning.api.Plan;

/**
 * An abstract plans that keeps an estimated unnormalized probability of success.
 * 
 * @author braz
 *
 */
public abstract class AbstractAtomicPlan implements Plan {
	
	private double estimatedSuccessWeight;
	
	public AbstractAtomicPlan(double weight) {
		this.estimatedSuccessWeight = weight;
	}
	
	public double getEstimatedSuccessWeight() {
		return estimatedSuccessWeight;
	}

	@Override
	public void reward(double reward) {
		estimatedSuccessWeight += reward;
	}

}
