package com.sri.ai.util.distribution;

import static com.sri.ai.util.Util.getValuePossiblyCreatingIt;
import static com.sri.ai.util.Util.map;
import static com.sri.ai.util.Util.myAssert;

import java.util.ArrayList;
import java.util.Map;

/**
 * A class keeping track of distributions of an integer main variable for each assignment to a tuple of other integer remaining variables
 * based on their provided occurrences.
 * @author braz
 *
 */
public class WeightedFrequencyArrayConditionalDistribution {
	
	private Map<ArrayList<Integer>, WeightedFrequencyArrayDistribution> distributions = map();

	private int numberOfMainVariableValues; 
	
	private int numberOfSamples = 0;
	private double totalWeight = 0;

	public WeightedFrequencyArrayConditionalDistribution(int numberOfMainVariableValues) {
		this.numberOfMainVariableValues = numberOfMainVariableValues;
	}

	public void register(int mainValue, ArrayList<Integer> remainingValues, double weight) {
		myAssert(isValid(mainValue), () -> getClass() + " received main value " + mainValue + " out of range 0.." + (numberOfMainVariableValues - 1));
		WeightedFrequencyArrayDistribution distribution = getDistributionOnMainVariableGivenRemainingValues(remainingValues);
		distribution.add(mainValue, weight);
		numberOfSamples++;
		totalWeight += weight;
	}

	public double getProbability(int mainValue, ArrayList<Integer> remainingValues) {
		WeightedFrequencyArrayDistribution distribution = getDistributionOnMainVariableGivenRemainingValues(remainingValues);
		double probability = distribution.getProbabilities().get(mainValue);
		return probability;
	}

	public WeightedFrequencyArrayDistribution getDistributionOnMainVariableGivenRemainingValues(ArrayList<Integer> remainingValues) {
		WeightedFrequencyArrayDistribution result = 
				getValuePossiblyCreatingIt(
						distributions, 
						remainingValues, 
						key -> new WeightedFrequencyArrayDistribution(numberOfMainVariableValues, 0.0));
		return result;
	}

	public boolean averageWeightIsZero() {
		return 
				getNumberOfSamples() != 0 
				&& 
				getTotalWeight() == 0.0;
	}
	
	public int getNumberOfSamples() {
		return numberOfSamples;
	}

	public double getTotalWeight() {
		return totalWeight;
	}

	private boolean isValid(int mainValue) {
		return mainValue >= 0 && mainValue < numberOfMainVariableValues;
	}
	
	@Override
	public String toString() {
		return distributions.toString();
	}
}