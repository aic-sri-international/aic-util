package com.sri.ai.util.number.statistics.core;

import static com.sri.ai.util.Util.myAssert;

import com.sri.ai.util.number.representation.api.ArithmeticNumber;
import com.sri.ai.util.number.representation.api.ArithmeticNumberFactory;
import com.sri.ai.util.number.statistics.api.Statistic;

/**
 * A statistic formed by feeding the values of a base statistic to another one.
 * Example: <code>chain(new {@link DefaultMean}(), {@link DefaultMean}(), new {@link Variance}())</code>
 */
public class StatisticsChain<T> implements Statistic<T> {

	private Statistic<ArithmeticNumber> base;
	private Statistic<T> next;
	private ArithmeticNumberFactory numberFactory; // TODO: here just to get "1.0" below. Instead create ONE in ArithmeticNumber
	
	@SuppressWarnings("unchecked")
	public static <T> StatisticsChain<T> chain(ArithmeticNumberFactory numberFactory, Statistic...sequence) {
		myAssert(sequence.length != 0, () -> "Cannot create a statistic chain with no statistics");
		Statistic current = sequence[0];
		for (int i = 1; i != sequence.length; i++) {
			current = new StatisticsChain(numberFactory, current, sequence[i]);
		}
		return (StatisticsChain<T>) current;
	}
	
	public StatisticsChain(ArithmeticNumberFactory numberFactory, Statistic<ArithmeticNumber> base, Statistic<T> next) {
		this.base = base;
		this.next = next;
		this.numberFactory = numberFactory;
	}

	public Statistic<ArithmeticNumber> getBase() {
		return base;
	}

	@Override
	public void add(ArithmeticNumber number, ArithmeticNumber weight) {
		base.add(number, weight);
		next.add(base.getValue(), numberFactory.make(1.0));
	}

	@Override
	public T getValue() {
		return next.getValue();
	}

}
