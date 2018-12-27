package com.sri.ai.util.number.statistics.core;

import static com.sri.ai.util.Util.myAssert;

import com.sri.ai.util.number.representation.api.ArithmeticNumber;
import com.sri.ai.util.number.representation.api.ArithmeticNumberFactory;
import com.sri.ai.util.number.statistics.api.Mean;

/**
 * The mean of all n elements in the sample.
 * 
 * @author braz
 *
 */
public class DefaultMean implements Mean {
	
	private ArithmeticNumber sumOfWeightedValues;
	private ArithmeticNumber sumOfWeights;
	private long n = 0;
	private ArithmeticNumberFactory numberFactory;
	
	public DefaultMean(ArithmeticNumberFactory numberFactory) {
		this.sumOfWeightedValues = numberFactory.make(0.0);
		this.sumOfWeights = numberFactory.make(0.0);
		this.n = 0;
		this.numberFactory = numberFactory;
	}
	
	public long getN() {
		return n;
	}

	@Override
	public ArithmeticNumberFactory getNumberFactory() {
		return numberFactory;
	}
	
	@Override
	public void add(ArithmeticNumber number, ArithmeticNumber weight) {
		sumOfWeightedValues = sumOfWeightedValues.add(number.multiply(weight));
		sumOfWeights = sumOfWeights.add(weight);
		n++;
	}
	
	@Override
	public ArithmeticNumber getValue() {
		myAssert(sumOfWeights.compareTo(getNumberFactory().make(0.0)) > 0, () -> "Mean requested but sum of sample weights so far is zero: " + sumOfWeights);
		ArithmeticNumber result = sumOfWeightedValues.divide(sumOfWeights);
		return result;
	}

	@Override
	public ArithmeticNumber getTotalWeight() {
		return sumOfWeights;
	}

}
