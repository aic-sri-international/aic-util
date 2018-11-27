package com.sri.ai.util.distribution;

import static com.sri.ai.util.Util.probabilities;

import java.util.ArrayList;
import java.util.Random;

import com.sri.ai.util.Util;
import com.sri.ai.util.base.Pair;

public class DiscreteDistribution {
	
	private ArrayList<Double> weights;

	private double smoothingCoefficient;
	private ArrayList<Double> normalized;
	private double partition;

	public DiscreteDistribution(ArrayList<Double> weights, double smoothingCoefficient) {
		this.weights = weights;
		this.normalized = null;
		this.smoothingCoefficient = smoothingCoefficient;
	}
	
	public static DiscreteDistribution fromProbabilities(ArrayList<Double> probabilities) {
		DiscreteDistribution distribution = new DiscreteDistribution(probabilities, 0.0);
		distribution.normalized = distribution.weights;
		distribution.partition = 1.0;
		return distribution;
	}
	
	public double getSmoothingCoefficient() {
		return smoothingCoefficient;
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
