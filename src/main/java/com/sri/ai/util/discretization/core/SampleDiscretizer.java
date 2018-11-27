package com.sri.ai.util.discretization.core;

import java.util.ArrayList;

import com.sri.ai.util.number.representation.api.ArithmeticNumber;

public interface SampleDiscretizer<V extends ArithmeticNumber, W extends ArithmeticNumber> {
	
	void add(V value, W weight);
	
	ArrayList<V> getBins();
}
