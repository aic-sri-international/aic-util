package com.sri.ai.util.number.statistics.core;

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
	
	private ArithmeticNumber total;
	private long n = 0;
	private ArithmeticNumberFactory numberFactory;
	
	public DefaultMean(ArithmeticNumberFactory numberFactory) {
		this.total = numberFactory.make(0.0);
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
	public void add(ArithmeticNumber number) {
		total = total.add(number);
		n++;
	}
	
	@Override
	public ArithmeticNumber getValue() {
		ArithmeticNumber numberOfItems = numberFactory.make(n);
		ArithmeticNumber result = total.divide(numberOfItems);
		return result;
	}

}
