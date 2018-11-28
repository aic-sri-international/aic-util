package com.sri.ai.util.distribution;

import static com.sri.ai.util.Util.fill;
import static com.sri.ai.util.Util.probabilities;

import java.util.ArrayList;
import java.util.Random;

import com.sri.ai.util.Util;
import com.sri.ai.util.base.Pair;

public class WeightedFrequencyArrayDistribution {
	
	private ArrayList<Double> weights;

	private double smoothingCoefficient;
	private ArrayList<Double> normalized;
	private double partition;

	public WeightedFrequencyArrayDistribution(int n, double smoothingCoefficient) {
		this(fill(n, 0.0), smoothingCoefficient);
	}
	
	public WeightedFrequencyArrayDistribution(ArrayList<Double> weights, double smoothingCoefficient) {
		this.weights = weights;
		this.normalized = null;
		this.smoothingCoefficient = smoothingCoefficient;
	}
	
	public static WeightedFrequencyArrayDistribution fromProbabilities(ArrayList<Double> probabilities) {
		WeightedFrequencyArrayDistribution distribution = new WeightedFrequencyArrayDistribution(probabilities, 0.0);
		distribution.normalized = distribution.weights;
		distribution.partition = 1.0;
		return distribution;
	}
	
	public double getSmoothingCoefficient() {
		return smoothingCoefficient;
	}
	
	public void add(int i, double extraWeight) {
		weights.set(i, weights.get(i) + extraWeight);
		partition += extraWeight;
		normalized = null;
	}
	
	public ArrayList<Double> getProbabilities() {
		ensureNormalization();
		return normalized;
	}
	
	public double getPartition() {
		ensureNormalization();
		return partition;
	}

	private void ensureNormalization() {
		if (normalized == null) {
			Pair<ArrayList<Double>, Double> probabilitiesAndPartition = probabilities(weights, smoothingCoefficient);
			normalized = probabilitiesAndPartition.first;
			partition = probabilitiesAndPartition.second;
		}
	}
	
	public int sample(Random random) {
		int sample = Util.sample(getProbabilities(), getPartition(), random);
		return sample;
	}
}
