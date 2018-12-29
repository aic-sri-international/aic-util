package com.sri.ai.util.number.statistics.api;

import com.sri.ai.util.number.representation.api.ArithmeticNumber;

public interface Statistic<I, O> {
	
	void add(I data, ArithmeticNumber weight);
	
	O getValue();

	ArithmeticNumber getTotalWeight();
}
