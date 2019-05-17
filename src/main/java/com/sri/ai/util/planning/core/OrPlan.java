package com.sri.ai.util.planning.core;

import static com.sri.ai.util.Util.fill;
import static com.sri.ai.util.Util.increment;
import static com.sri.ai.util.Util.mapIntoArrayList;
import static com.sri.ai.util.Util.myAssert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import com.google.common.base.Predicate;
import com.sri.ai.util.Util;
import com.sri.ai.util.distribution.WeightedFrequencyArrayDistribution;
import com.sri.ai.util.planning.api.Plan;
import com.sri.ai.util.planning.api.State;

public class OrPlan extends AbstractCompoundPlan {
	
	/**
	 * If true, uses sub-plans' sampling rules' {@link SamplingFactor#getEstimatedSuccessWeight()} to set up initial sampling plan; otherwise,
	 * use uniform distribution.
	 */
	public static boolean useEstimatedSuccessWeight = true;
	
	private ArrayList<Integer> numberOfSubPlanExecutions;
	
	public static Plan orPlan(Plan... subPlans) {
		return orPlan(Arrays.asList(subPlans));
	}

	public static Plan orPlan(List<? extends Plan> subPlans) {
		List<? extends Plan> flattenedSubPlans = AbstractCompoundPlan.flatten(subPlans, OrPlan.class, getSubItems());
		if (flattenedSubPlans.size() == 1) {
			return flattenedSubPlans.get(0);
		}
		return new OrPlan(flattenedSubPlans);
	}

	public static Function<OrPlan, List<? extends Plan>> getSubItems() {
		return s -> s.getSubPlans();
	}
	
	public OrPlan(List<? extends Plan> subPlans) {
		this(subPlans, null);
	}

	public OrPlan(List<? extends Plan> subPlans, Random random) {
		super(subPlans);
		numberOfSubPlanExecutions = fill(subPlans.size(), 0);
	}
	
	@Override
	public boolean isFailedPlan() {
		return getSubPlans().isEmpty();
	}

	@Override
	public double computeEstimatedSuccessWeight() {
		// TODO: if we know these are not shared, we can cache them and update incrementally.
		List<Double> probabilities = getDistribution().getProbabilities();
		double result = 0;
		for (int i = 0; i != getSubPlans().size(); i++) {
			result += getSubPlans().get(i).getEstimatedSuccessWeight() * probabilities.get(i);
		}
		return result;
	}

	@Override
	public State execute(State state) {
		if ( ! getSubPlans().isEmpty()) {
			Random random = state.getRandom();
			myAssert(random != null, () -> getClass() + " cannot execute without " + State.class.getSimpleName() + " providing it a Random");
			// println("OrPlan: choosing sub-plan out of " + getSubPlans().size() + " possibilities");
			int i = getDistribution().sample(random);
			// println("OrPlan: choice is " + i);
			State result = getSubPlans().get(i).execute(state);
			increment(numberOfSubPlanExecutions, i);
			return result;
		}
		else {
			return State.FAILED;
		}
	}

	@Override
	public void reward(double reward) {
		// TODO: if we know these are not shared, we can cache them and update incrementally.
		List<Double> probabilities = getDistribution().getProbabilities();
		for (int i = 0; i != getSubPlans().size(); i++) {
			getSubPlans().get(i).reward(reward * probabilities.get(i));
		}
//		List<Double> afterProbabilities = getDistribution().getProbabilities();
//		println("Reward of " + reward);
//		println("Probabilities before reward: " + probabilities);
//		println("Probabilities after reward: " + afterProbabilities);
	}

	public WeightedFrequencyArrayDistribution getDistribution() {
		ArrayList<Double> weights = mapIntoArrayList(getSubPlans(), effectiveWeight());
		WeightedFrequencyArrayDistribution distributionForAbsoluteSubPlansOrNull = makeDistributionForAbsoluteSubPlansOrNull(weights);
		WeightedFrequencyArrayDistribution distribution;
		if (distributionForAbsoluteSubPlansOrNull != null) {
			distribution = distributionForAbsoluteSubPlansOrNull;
		}
		else {
			double smoothingCoefficient = 0.01;
			distribution = new WeightedFrequencyArrayDistribution(weights, smoothingCoefficient);
		}
		return distribution;
	}

	private WeightedFrequencyArrayDistribution makeDistributionForAbsoluteSubPlansOrNull(ArrayList<Double> weights) {
		List<Integer> absolutePlansIndices = Util.collectIndices(getSubPlans(), isAbsolutePlan());
		if ( ! absolutePlansIndices.isEmpty()) {
			return makeUniformDistributionOnAbsolutePlans(absolutePlansIndices);
		}
		else {
			return null;
		}
	}

	public WeightedFrequencyArrayDistribution makeUniformDistributionOnAbsolutePlans(List<Integer> absolutePlansIndices) {
		ArrayList<Double> weightsAfterAbsolute = Util.fill(getSubPlans().size(), 0.0);
		for (int i : absolutePlansIndices) {
			weightsAfterAbsolute.set(i, 1.0);
		}
		return new WeightedFrequencyArrayDistribution(weightsAfterAbsolute, 0.0);
	}

	private com.google.common.base.Function<Plan, Double> effectiveWeight() {
		return 
				useEstimatedSuccessWeight? 
						Plan::getEstimatedSuccessWeight
						: p -> 0.5;
	}

	private Predicate<Plan> isAbsolutePlan() {
		return p -> {
			return p.getEstimatedSuccessWeight() == Plan.MAXIMUM_ESTIMATED_SUCCESS_WEIGHT;
		};
	}

	@Override
	public String operatorName() {
		return "or";
	}
	
	public List<Integer> getNumberOfSubPlanExecutions() {
		return Collections.unmodifiableList(numberOfSubPlanExecutions);
	}

}
