package com.sri.ai.util.number.algorithm.statistics;

import com.sri.ai.util.number.representation.api.ArithmeticNumber;

public interface Statistic<T> {
	
	void add(ArithmeticNumber number);
	
	T getValue();

}
