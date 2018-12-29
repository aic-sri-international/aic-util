package com.sri.ai.util.number.statistics.core;

import static com.sri.ai.util.Util.myAssert;

import com.sri.ai.util.number.representation.api.ArithmeticNumber;
import com.sri.ai.util.number.statistics.api.Statistic;

/**
 * A statistic formed by feeding the values of a base statistic to another one.
 * Example: <code>chain(new {@link DefaultMean}(), {@link DefaultMean}(), new {@link Variance}())</code>
 */
public class CompoundStatistic<I, M, O> implements Statistic<I, O> {

	private Statistic<I, M> base;
	private Statistic<M, O> next;
	
	@SuppressWarnings("unchecked")
	public static <I, M, O> CompoundStatistic<I, M, O> chain(Statistic...sequence) {
		myAssert(sequence.length != 0, () -> "Cannot create a statistic chain with no statistics");
		Statistic current = sequence[0];
		for (int i = 1; i != sequence.length; i++) {
			current = new CompoundStatistic<I, M, O>(current, sequence[i]);
		}
		return (CompoundStatistic<I, M, O>) current;
	}
	
	public CompoundStatistic(Statistic<I, M> base, Statistic<M, O> next) {
		this.base = base;
		this.next = next;
	}

	public Statistic<I, M> getBase() {
		return base;
	}

	@Override
	public void add(I number, ArithmeticNumber weight) {
		if (weight.doubleValue() != 0.0) {
			base.add(number, weight);
			next.add(base.getValue(), weight.one());
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
