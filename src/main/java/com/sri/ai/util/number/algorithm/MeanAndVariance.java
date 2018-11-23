package com.sri.ai.util.number.algorithm;

import static com.sri.ai.util.base.PairOf.makePairOf;

import com.sri.ai.util.base.PairOf;
import com.sri.ai.util.number.algorithm.statistics.StatisticWithNumberFactory;
import com.sri.ai.util.number.representation.api.ArithmeticNumber;
import com.sri.ai.util.number.representation.api.ArithmeticNumberFactory;

/**
 * Keeps means and variance of whole sample.
 * 
 * @author braz
 *
 */
public class MeanAndVariance implements StatisticWithNumberFactory<PairOf<ArithmeticNumber>> {
	
	private Variance variance;
	
	public MeanAndVariance(ArithmeticNumberFactory numberFactory) {
		this.variance = new Variance(numberFactory, f -> new MeanOfAll(f));
	}

	@Override
	public void add(ArithmeticNumber number) {
		variance.add(number);
	}

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

}
