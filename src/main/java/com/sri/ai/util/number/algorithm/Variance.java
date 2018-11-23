package com.sri.ai.util.number.algorithm;

import java.util.function.Function;

import com.sri.ai.util.number.algorithm.statistics.MeanWithNumberFactory;
import com.sri.ai.util.number.algorithm.statistics.StatisticWithNumberFactory;
import com.sri.ai.util.number.representation.api.ArithmeticNumber;
import com.sri.ai.util.number.representation.api.ArithmeticNumberFactory;

/**
 * A {@link MeanOfAll} that also provides the variance of the values seens so far
 * 
 * @author braz
 *
 */
public class Variance implements StatisticWithNumberFactory<ArithmeticNumber> {
	
	private MeanWithNumberFactory mean;
	private MeanWithNumberFactory variance;
	private ArithmeticNumber two;
	
	public Variance(ArithmeticNumberFactory numberFactory, Function<ArithmeticNumberFactory, MeanWithNumberFactory> meanMaker) {
		this.mean = meanMaker.apply(numberFactory);
		this.variance= meanMaker.apply(numberFactory);
		this.two = numberFactory.make(2);
	}
	
	@Override
	public void add(ArithmeticNumber number) {
		mean.add(number);
		ArithmeticNumber deviation = number.subtract(getMean());
		ArithmeticNumber numberVariance = deviation.pow(two);
		variance.add(numberVariance);
	}
	
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

}
