package com.sri.ai.util.number.statistics.core;

import static com.sri.ai.util.Util.getValueOrDefault;
import static com.sri.ai.util.Util.map;
import static com.sri.ai.util.Util.mapValuesNonDestructively;

import java.util.Collections;
import java.util.Map;

import com.sri.ai.util.number.representation.api.ArithmeticNumber;
import com.sri.ai.util.number.statistics.api.Statistic;

public class SampleDistribution<T> implements Statistic<T, Map<T, ArithmeticNumber>> {

	private Map<T, ArithmeticNumber> distribution = map();
	private ArithmeticNumber partition = null;
	private boolean isNormalized = true;
	private ArithmeticNumber totalWeight = null;
	
	@Override
	public void add(T data, ArithmeticNumber weight) {
		updateTotalWeight(weight);
		updatePartition(weight);
		ArithmeticNumber currentWeight = getValueOrDefault(distribution, data, weight.zero());
		ArithmeticNumber newWeight = currentWeight.add(weight);
		distribution.put(data, newWeight);
	}

	private void updateTotalWeight(ArithmeticNumber weight) {
		if (totalWeight == null) {
			totalWeight = weight.zero();
		}
		totalWeight = totalWeight.add(weight);
	}

	private void updatePartition(ArithmeticNumber weight) {
		if (partition == null) {
			partition = weight.zero();
		}
		partition = partition.add(weight);
		isNormalized = false;
	}

	@Override
	public Map<T, ArithmeticNumber> getValue() {
		ensureNormalization();
		Map<T, ArithmeticNumber> result = partitionIsNotZero()? Collections.unmodifiableMap(distribution) : null;
		return result;
	}

	private void ensureNormalization() {
		if ( ! isNormalized) {
			if (partitionIsNotZero()) {
				distribution = mapValuesNonDestructively(distribution, v -> v.divide(partition));
			}
			isNormalized = true;
		}
	}

	private boolean partitionIsNotZero() {
		boolean result = 
				partition != null 
				&& 
				! partition.equals(partition.zero());
		return result;
	}

	@Override
	public ArithmeticNumber getTotalWeight() {
		return totalWeight;
	}

	@Override
	public String toString() {
		ensureNormalization();
		return partitionIsNotZero()? distribution.toString() : "undefined";
	}
}
