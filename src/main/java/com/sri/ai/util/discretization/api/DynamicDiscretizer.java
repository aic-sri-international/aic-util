package com.sri.ai.util.discretization.api;

import com.sri.ai.util.number.representation.api.ArithmeticNumber;

public interface DynamicDiscretizer<T, W extends ArithmeticNumber> {
	
	void add(T value, W weight);
	
	Bin getBins();
}
