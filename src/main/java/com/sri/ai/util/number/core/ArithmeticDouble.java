package com.sri.ai.util.number.core;

import static com.sri.ai.util.Util.myAssert;

import com.sri.ai.util.number.api.ArithmeticNumber;

public class ArithmeticDouble implements ArithmeticNumber {
	
	private double number;
	
	public ArithmeticDouble(double number) {
		this.number = number;
	}

	@Override
	public ArithmeticNumber add(ArithmeticNumber another) {
		checkType(another);
		double anotherDouble = ((ArithmeticDouble) another).number;
		ArithmeticDouble result = new ArithmeticDouble(number + anotherDouble);
		return result;
	}

	@Override
	public ArithmeticNumber subtract(ArithmeticNumber another) {
		checkType(another);
		double anotherDouble = ((ArithmeticDouble) another).number;
		ArithmeticDouble result = new ArithmeticDouble(number - anotherDouble);
		return result;
	}

	@Override
	public ArithmeticNumber multiply(ArithmeticNumber another) {
		checkType(another);
		double anotherDouble = ((ArithmeticDouble) another).number;
		ArithmeticDouble result = new ArithmeticDouble(number * anotherDouble);
		return result;
	}

	@Override
	public ArithmeticNumber divide(ArithmeticNumber another) {
		checkType(another);
		double anotherDouble = ((ArithmeticDouble) another).number;
		ArithmeticDouble result = new ArithmeticDouble(number / anotherDouble);
		return result;
	}

	@Override
	public ArithmeticNumber pow(ArithmeticNumber another) {
		checkType(another);
		double anotherDouble = ((ArithmeticDouble) another).number;
		ArithmeticDouble result = new ArithmeticDouble(Math.pow(number, anotherDouble));
		return result;
	}

	private void checkType(ArithmeticNumber another) {
		myAssert(another instanceof ArithmeticDouble, () -> this.getClass() + " can only be operated upon with other numbers of the same class.");
	}

}
