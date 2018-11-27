package com.sri.ai.util.discretization.api;

import com.sri.ai.util.number.representation.api.ArithmeticNumber;

public interface Sample<V extends ArithmeticNumber, W extends ArithmeticNumber> {
	
	V getValue();
	
	W getWeight();
	
}