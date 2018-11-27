package com.sri.ai.util.planning.core;

import static com.sri.ai.util.Util.join;
import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.product;
import static com.sri.ai.util.collect.FunctionIterator.functionIterator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.sri.ai.util.collect.FunctionIterator;
import com.sri.ai.util.planning.api.Plan;
import com.sri.ai.util.planning.api.State;

public class SequentialPlan extends AbstractCompoundPlan {

	public static Plan and(Plan... subPlans) {
		return and(Arrays.asList(subPlans));
	}

	public static Plan and(List<? extends Plan> subPlans) {
		if (subPlans.isEmpty()) return null;
		List<? extends Plan> flattenedSubPlans = flatten(subPlans, SequentialPlan.class, getSubItems());
		if (flattenedSubPlans.size() == 1) {
			return flattenedSubPlans.get(0);
		}
		return new SequentialPlan(flattenedSubPlans);
	}

	public static Function<SequentialPlan, List<? extends Plan>> getSubItems() {
		return s -> ((SequentialPlan) s).getSubPlans();
	}
	
	public SequentialPlan() {
		this(list());
	}

	public SequentialPlan(List<? extends Plan> subPlans) {
		super(subPlans);
	}

	@Override
	public void execute(State state) {
		for (Plan executer : getSubPlans()) {
			executer.execute(state);
		}
	}

	@Override
	public void reward(double reward) {
		double rewardShare = reward/(getSubPlans().size() + 0.0);
		for (Plan executer : getSubPlans()) {
			executer.reward(rewardShare);
		}
	}

	@Override
	public double getEstimatedSuccessWeight() {
		Double result = (Double) product(subPlanEstimatedSuccessWeights());
		return result;
	}

	private FunctionIterator<? extends Plan, Double> subPlanEstimatedSuccessWeights() {
		return functionIterator(getSubPlans(), s -> s.getEstimatedSuccessWeight());
	}

	@Override
	public String toString() {
		return "SequentialPlan(" + join(getSubPlans()) + ")";
	}

	@Override
	protected String operatorName() {
		return "and";
	}
}
