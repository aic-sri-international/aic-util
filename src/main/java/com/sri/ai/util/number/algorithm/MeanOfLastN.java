package com.sri.ai.util.number.algorithm;

import java.util.ArrayDeque;
import java.util.Deque;

import com.sri.ai.util.number.algorithm.statistics.MeanWithNumberFactory;
import com.sri.ai.util.number.representation.api.ArithmeticNumber;
import com.sri.ai.util.number.representation.api.ArithmeticNumberFactory;

/**
 * The mean of the last n elements in the sample.
 * 
 * @author braz
 *
 */
public class MeanOfLastN implements MeanWithNumberFactory {
	
	private ArithmeticNumber total;
	private Deque<ArithmeticNumber> deque;
	private int n;
	private ArithmeticNumberFactory numberFactory;
	
	public MeanOfLastN(int n, ArithmeticNumberFactory numberFactory) {
		this.total = numberFactory.make(0.0);
		this.deque = new ArrayDeque<>(n);
		this.n= n;
		this.numberFactory = numberFactory;
	}
	
	public int size() {
		return deque.size();
	}

	@Override
	public ArithmeticNumberFactory getNumberFactory() {
		return numberFactory;
	}
	
	@Override
	public void add(ArithmeticNumber number) {
		if (deque.size() == n) {
			ArithmeticNumber last = deque.removeLast();
			total = total.subtract(last);
		}
		deque.addFirst(number);
		total = total.add(number);
	}
	
	@Override
	public ArithmeticNumber getValue() {
		ArithmeticNumber numberOfItems = numberFactory.make(deque.size());
		ArithmeticNumber result = total.divide(numberOfItems);
		return result;
	}

}
