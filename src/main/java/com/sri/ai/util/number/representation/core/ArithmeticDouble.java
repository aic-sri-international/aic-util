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
	public double doubleValue() {
		return number;
	}

	@Override
	public String toString() {
		return Double.toString(number);
	}

	@Override
	public int compareTo(ArithmeticNumber another) {
		int result;
		double anotherAsDouble = another.doubleValue();
		if (number < anotherAsDouble) {
			result = -1;
		}
		else if (number == anotherAsDouble) {
			result = 0;
		}
		else {
			result = 1;
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(number);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArithmeticDouble other = (ArithmeticDouble) obj;
		if (Double.doubleToLongBits(number) != Double.doubleToLongBits(other.number))
			return false;
		return true;
	}

}
