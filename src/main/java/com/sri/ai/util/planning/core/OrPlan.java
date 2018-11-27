package com.sri.ai.util.planning.core;

import static com.sri.ai.util.Util.mapIntoArrayList;
import static com.sri.ai.util.Util.myAssert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import com.sri.ai.util.distribution.DiscreteDistribution;
import com.sri.ai.util.planning.api.Plan;
import com.sri.ai.util.planning.api.State;

public class OrPlan extends AbstractCompoundPlan {
	
	private Random random;
	
	public static Plan or(Plan... subPlans) {
		return or(Arrays.asList(subPlans));
	}

	public static Plan and(List<? extends Plan> subPlans) {
		if (subPlans.isEmpty()) return null;
		List<? extends Plan> flattenedSubPlans = AbstractCompoundPlan.flatten(subPlans, OrPlan.class, getSubItems());
		if (flattenedSubPlans.size() == 1) {
			return flattenedSubPlans.get(0);
		}
		return new OrPlan(flattenedSubPlans);
	}

	public static Function<OrPlan, List<? extends Plan>> getSubItems() {
		return s -> ((OrPlan) s).getSubPlans();
	}
	
	public static Plan or(List<? extends Plan> plans) {
		Plan result;
		if (plans.isEmpty()) {
			result = null;
		}
		else if (plans.size() == 1){
			result = plans.get(0);
		}
		else {
			result = new OrPlan(plans);
		}
		return result;
	}

	public OrPlan(List<? extends Plan> subPlans) {
		this(subPlans, null);
	}

	public OrPlan(List<? extends Plan> subPlans, Random random) {
		super(subPlans);
		this.random = random;
	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

	@Override
	public double getEstimatedSuccessWeight() {
		// TODO: if we know these are not shared, we can cache them and update incrementally.
		List<Double> probabilities = getDistribution().getProbabilities();
		double result = 0;
		for (int i = 0; i != getSubPlans().size(); i++) {
			result += getSubPlans().get(i).getEstimatedSuccessWeight() * probabilities.get(i);
		}
		return result;
	}

	@Override
	public void execute(State state) {
		Random random = getRandom();
		myAssert(random != null, () -> getClass() + " cannot execute without having been provided a Random");
		int i = getDistribution().sample(random);
		getSubPlans().get(i).execute(state);
	}

	@Override
	public void reward(double reward) {
		// TODO: if we know these are not shared, we can cache them and update incrementally.
		List<Double> probabilities = getDistribution().getProbabilities();
		for (int i = 0; i != getSubPlans().size(); i++) {
			getSubPlans().get(i).reward(reward * probabilities.get(i));
		}
	}

	public DiscreteDistribution getDistribution() {
		// TODO: can we keep these probabilities stored and updated as needed?
		// If we know that sub-plans are not shared anywhere else, yes.
		ArrayList<Double> weights = mapIntoArrayList(getSubPlans(), Plan::getEstimatedSuccessWeight);
		double smoothingCoefficient = 0.01;
		DiscreteDistribution distribution = new DiscreteDistribution(weights, smoothingCoefficient);
		return distribution;
	}

	@Override
	protected String operatorName() {
		return "or";
	}
	
}
