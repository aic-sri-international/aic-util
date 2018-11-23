package com.sri.ai.util.number.algorithm.statistics;

import com.sri.ai.util.number.representation.api.ArithmeticNumberFactory;

public interface StatisticWithNumberFactory<T> extends Statistic<T> {
	
	ArithmeticNumberFactory getNumberFactory();
	
	default void add(double number) {
		add(getNumberFactory().make(number));
	}
	
}
