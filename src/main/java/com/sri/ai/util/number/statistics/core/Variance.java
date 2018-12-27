package com.sri.ai.util.number.statistics.core;

import com.sri.ai.util.number.representation.api.ArithmeticNumber;
import com.sri.ai.util.number.representation.api.ArithmeticNumberFactory;
import com.sri.ai.util.number.statistics.api.Mean;
import com.sri.ai.util.number.statistics.api.StatisticOnNumber;

/**
 * A {@link DefaultMean} that also provides the variance of the values seen so far
 * 
 * @author braz
 *
 */
public class Variance implements StatisticOnNumber<ArithmeticNumber> {
	
	private Mean mean;
	private Mean variance;
	private ArithmeticNumber two;
	
	public Variance(ArithmeticNumberFactory numberFactory) {
		this.mean = new DefaultMean(numberFactory);
		this.variance= new DefaultMean(numberFactory);
		this.two = numberFactory.make(2);
	}
	
	@Override
	public void add(ArithmeticNumber number, ArithmeticNumber weight) {
		if (weight.doubleValue() != 0.0) {
			mean.add(number, weight);
			ArithmeticNumber deviation = number.subtract(getMean());
			ArithmeticNumber numberVariance = deviation.pow(two);
			variance.add(numberVariance, weight);
		}
	}
	
	@Override
	public ArithmeticNumber getValue() {
		return variance.getValue();
	}

	public ArithmeticNumber getMean() {
		return mean.getValue();
	}
	
	@Override
	public ArithmeticNumberFactory getNumberFactory() {
		return mean.getNumberFactory();
	}

	@Override
	public ArithmeticNumber getTotalWeight() {
		return mean.getTotalWeight();
	}

}
