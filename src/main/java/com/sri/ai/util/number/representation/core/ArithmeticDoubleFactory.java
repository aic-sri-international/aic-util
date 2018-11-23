package com.sri.ai.util.number.representation.core;

import com.sri.ai.util.number.representation.api.ArithmeticNumberFactory;

public class ArithmeticDoubleFactory implements ArithmeticNumberFactory {
	
	public static final ArithmeticDoubleFactory INSTANCE = new ArithmeticDoubleFactory();

	@Override
	public ArithmeticDouble make(double value) {
		return new ArithmeticDouble(value);
	}

}
