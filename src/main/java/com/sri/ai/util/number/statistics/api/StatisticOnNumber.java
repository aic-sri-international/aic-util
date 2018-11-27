package com.sri.ai.util.number.statistics.api;

import com.sri.ai.util.number.representation.api.ArithmeticNumberFactory;

public interface StatisticOnNumber<T> extends Statistic<T> {
	
	ArithmeticNumberFactory getNumberFactory();
	
	default void add(double number) {
		add(getNumberFactory().make(number));
	}
	
}
