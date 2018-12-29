package com.sri.ai.util.number.statistics.core;

import static com.sri.ai.util.Util.myAssert;

import com.sri.ai.util.number.representation.api.ArithmeticNumber;
import com.sri.ai.util.number.representation.api.ArithmeticNumberFactory;
import com.sri.ai.util.number.statistics.api.Statistic;

/**
 * A statistic formed by feeding the values of a base statistic to another one.
 * Example: <code>chain(new {@link DefaultMean}(), {@link DefaultMean}(), new {@link Variance}())</code>
 */
public class StatisticsChain<I, O> implements Statistic<I, O> {

	private Statistic<I, ArithmeticNumber> base;
	private Statistic<ArithmeticNumber, O> next;
	private ArithmeticNumberFactory numberFactory;
	
	@SuppressWarnings("unchecked")
	public static <I, T> StatisticsChain<I, T> chain(ArithmeticNumberFactory numberFactory, Statistic...sequence) {
		myAssert(sequence.length != 0, () -> "Cannot create a statistic chain with no statistics");
		Statistic current = sequence[0];
		for (int i = 1; i != sequence.length; i++) {
			current = new StatisticsChain<I, T>(numberFactory, current, sequence[i]);
		}
		return (StatisticsChain<I, T>) current;
	}
	
	public StatisticsChain(ArithmeticNumberFactory numberFactory, Statistic<I, ArithmeticNumber> base, Statistic<ArithmeticNumber, O> next) {
		this.base = base;
		this.next = next;
		this.numberFactory = numberFactory;
	}

	public Statistic<I, ArithmeticNumber> getBase() {
		return base;
	}

	@Override
	public void add(I number, ArithmeticNumber weight) {
		if (weight.doubleValue() != 0.0) {
			base.add(number, weight);
			next.add(base.getValue(), numberFactory.make(1.0));
		}
	}

	@Override
	public O getValue() {
		return next.getValue();
	}

	@Override
	public ArithmeticNumber getTotalWeight() {
		return base.getTotalWeight();
	}

}
