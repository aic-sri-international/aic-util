package com.sri.ai.util.number.representation.api;

public interface ArithmeticNumber {
	
	ArithmeticNumber add(ArithmeticNumber another);
	
	ArithmeticNumber subtract(ArithmeticNumber another);
	
	ArithmeticNumber multiply(ArithmeticNumber another);
	
	ArithmeticNumber divide(ArithmeticNumber another);
	
	ArithmeticNumber pow(ArithmeticNumber another);
	
	double doubleValue();

}
