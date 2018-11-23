package com.sri.ai.util.number.representation.core;

import static com.sri.ai.util.Util.myAssert;

import com.sri.ai.util.number.representation.api.ArithmeticNumber;

public class ArithmeticDouble implements ArithmeticNumber {
	
	private double number;
	
	public ArithmeticDouble(double number) {
		this.number = number;
	}

	public ArithmeticDouble(ArithmeticDouble another) {
		this(another.number);
	}

	@Override
	public ArithmeticDouble add(ArithmeticNumber another) {
		checkType(another);
		double anotherDouble = ((ArithmeticDouble) another).number;
		ArithmeticDouble result = new ArithmeticDouble(number + anotherDouble);
		return result;
	}

	@Override
	public ArithmeticDouble subtract(ArithmeticNumber another) {
		checkType(another);
		double anotherDouble = ((ArithmeticDouble) another).number;
		ArithmeticDouble result = new ArithmeticDouble(number - anotherDouble);
		return result;
	}

	@Override
	public ArithmeticDouble multiply(ArithmeticNumber another) {
		checkType(another);
		double anotherDouble = ((ArithmeticDouble) another).number;
		ArithmeticDouble result = new ArithmeticDouble(number * anotherDouble);
		return result;
	}

	@Override
	public ArithmeticDouble divide(ArithmeticNumber another) {
		checkType(another);
		double anotherDouble = ((ArithmeticDouble) another).number;
		ArithmeticDouble result = new ArithmeticDouble(number / anotherDouble);
		return result;
	}

	@Override
	public ArithmeticDouble pow(ArithmeticNumber another) {
		checkType(another);
		double anotherDouble = ((ArithmeticDouble) another).number;
		ArithmeticDouble result = new ArithmeticDouble(Math.pow(number, anotherDouble));
		return result;
	}

	private void checkType(ArithmeticNumber another) {
		myAssert(another instanceof ArithmeticDouble, () -> this.getClass() + " can only be operated upon with other numbers of the same class, but got " + another + " of class " + another.getClass());
	}

	@Override
	public String toString() {
		return Double.toString(number);
	}

}
