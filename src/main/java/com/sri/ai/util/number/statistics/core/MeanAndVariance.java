package com.sri.ai.util.number.statistics.core;

import static com.sri.ai.util.base.PairOf.makePairOf;

import com.sri.ai.util.base.PairOf;
import com.sri.ai.util.number.representation.api.ArithmeticNumber;
import com.sri.ai.util.number.representation.api.ArithmeticNumberFactory;
import com.sri.ai.util.number.statistics.api.StatisticOnNumber;

/**
 * Keeps means and variance of whole sample.
 * 
 * @author braz
 *
 */
public class MeanAndVariance implements StatisticOnNumber<PairOf<ArithmeticNumber>> {
	
	private Variance variance;
	
	public MeanAndVariance(ArithmeticNumberFactory numberFactory) {
		this.variance = new Variance(numberFactory);
	}

	@Override
	public void add(ArithmeticNumber number, ArithmeticNumber weight) {
		variance.add(number, weight);
	}
	// TODO: this class is looking useless if variance provides the mean already
	
	@Override
	public PairOf<ArithmeticNumber> getValue() {
		return makePairOf(variance.getMean(), variance.getValue());
	}

	public ArithmeticNumber getMean() {
		return getValue().first;
	}
	
	public ArithmeticNumber getVariance() {
		return getValue().second;
	}
	
	@Override
	public ArithmeticNumberFactory getNumberFactory() {
		return variance.getNumberFactory();
	}

	@Override
	public ArithmeticNumber getTotalWeight() {
		return variance.getTotalWeight();
	}

}
