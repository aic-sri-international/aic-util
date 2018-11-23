package com.sri.ai.util.number.algorithm;

import com.sri.ai.util.number.algorithm.statistics.MeanWithNumberFactory;
import com.sri.ai.util.number.representation.api.ArithmeticNumber;
import com.sri.ai.util.number.representation.api.ArithmeticNumberFactory;

/**
 * The mean of all n elements in the sample.
 * 
 * @author braz
 *
 */
public class MeanOfAll implements MeanWithNumberFactory {
	
	private ArithmeticNumber total;
	private long n = 0;
	private ArithmeticNumberFactory numberFactory;
	
	public MeanOfAll(ArithmeticNumberFactory numberFactory) {
		this.total = numberFactory.make(0.0);
		this.n= 0;
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
