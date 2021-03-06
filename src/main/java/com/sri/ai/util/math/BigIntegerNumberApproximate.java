/*
 * Copyright (c) 2017, SRI International
 * All rights reserved.
 * Licensed under the The BSD 3-Clause License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * 
 * http://opensource.org/licenses/BSD-3-Clause
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the aic-util nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.sri.ai.util.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * An implementation of BigIntegerNumber with approximate semantics.
 * 
 * Intended for use internally by Rational.
 * 
 * @author oreilly
 *
 */
public class BigIntegerNumberApproximate extends BigIntegerNumber {
	private static final long serialVersionUID = 1L;
	//
	// NOTE: loge = is used to indicate natural logarithm, i.e. log base e.
	private static final double LOGE_2  = Math.log(2);
	private static final double LOGE_10 = Math.log(10);
	private static final double LOG2_10 = log2(10);
	//
	// NOTE: Based on BigDecimal restrictions on pow(int) argument. 
	private static int MAX_INT_EXPONENT_VALUE =  999999999;
	//
	private BigDecimal value;
	private MathContext mathContext;
	
	public BigIntegerNumberApproximate(BigDecimal value, MathContext mathContext) {
		this.value = value;
		this.mathContext = mathContext;
	}
	
	public BigIntegerNumberApproximate(long val, MathContext mathContext) {
		this.value = new BigDecimal(val, mathContext);
		this.mathContext = mathContext;
	}
	
	public BigIntegerNumberApproximate(String strNumber, int radix, MathContext mathContext) {
		this.value = new BigDecimal(new BigInteger(strNumber, radix), mathContext);
		this.mathContext = mathContext;
	}
	
	//
	// Object
	@Override
	public boolean equals(Object o) {
		boolean result = false;
		if (o instanceof BigIntegerNumber) {
			result = value.equals(approx((BigIntegerNumber)o));
		}
		return result;
	}
	
	@Override
	public int hashCode() {
		int result = value.hashCode();
		return result;
	}
	
	@Override
	public String toString() {
		String result = value.toString();
		return result;
	}
	
	//
	// Number
	@Override
	public int intValue() {
		int result = value.intValue();
		return result;
	}
	
	@Override
	public long longValue() {
		long result = value.longValue();
		return result;
	}
	
	@Override
	public float floatValue() {
		float result = value.floatValue();
		return result;
	}
	
	@Override
	public double doubleValue() {
		double result = value.doubleValue();
		return result;
	}
	
	//
	// Comparable
	@Override
	public int compareTo(BigIntegerNumber o) {
		int result = value.compareTo(approx(o));
		return result;
	}
	
	//
	// BigIntegerNumber
	@Override
	public BigIntegerNumber abs() {
		BigIntegerNumber result = this;
		if (value.signum() < 0) {
			result = new BigIntegerNumberApproximate(value.abs(), mathContext);
		}
		return result;
	}
	
	@Override
	public BigIntegerNumber add(BigIntegerNumber val) {
		BigDecimal       sum    = value.add(approx(val), mathContext);
		BigIntegerNumber result = new BigIntegerNumberApproximate(sum, mathContext);
		return result;
	}
	
	@Override
	public int bitLength() {
		// NOTE BigInteger.bitLength() is computed as: 
		// ceil(log2(this < 0 ? -this : this+1))
		
		int result;
		if (value.signum() == 0) {
			result = 0;
		}
		else if (value.signum() > 0) {			
			// Note: computing this way. i.e.: floor(log2(n)) + 1 
			// see : http://www.exploringbinary.com/number-of-bits-in-a-decimal-integer/
			// avoids the 'this+1'.		
			double log2_value = log2BigDecimal(approxUnscaledDoubleValue(value), value);				
			
			result = Math.addExact((int) Math.floor(log2_value), 1);
		}
		else { // Negative case conforms to BigInteger.bitLength() computation method.			
			double log2_value = log2BigDecimal(approxUnscaledDoubleValue(value)*-1, value);			
			result = (int) Math.ceil(log2_value);
		}
		
		return result;
	}
	
	@Override
	public BigIntegerNumber divide(BigIntegerNumber val) {	
		BigDecimal divisor  = approx(val);
		BigDecimal quotient = value.divide(divisor, new MathContext(mathContext.getPrecision(), RoundingMode.DOWN));
		if (quotient.scale() > 0) {
			quotient = quotient.setScale(0, RoundingMode.DOWN);
		}
		BigIntegerNumber result = new BigIntegerNumberApproximate(quotient, mathContext);
		return result;
	}
	
	@Override
	public BigIntegerNumber[] divideAndRemainder(BigIntegerNumber val) {	
		BigDecimal divisor = approx(val);		
	
		BigDecimal quotient = value.divide(divisor, new MathContext(mathContext.getPrecision(), RoundingMode.DOWN));
		if (quotient.scale() > 0) {
			quotient = quotient.setScale(0, RoundingMode.DOWN);
		}
		BigDecimal remainder = value.subtract(quotient.multiply(divisor, mathContext), mathContext);

		BigIntegerNumber[] result = new BigIntegerNumber[] {
			new BigIntegerNumberApproximate(quotient, mathContext),
			new BigIntegerNumberApproximate(remainder, mathContext)
		};
		return result;
	}
	
	@Override
	public BigIntegerNumber gcd(BigIntegerNumber otherVal) {
		BigDecimal thisValue  = this.value;
		BigDecimal otherValue = approx(otherVal);
		
		BigDecimal gcd = gcd(thisValue, otherValue, mathContext);
		
		BigIntegerNumber result = new BigIntegerNumberApproximate(gcd, mathContext);
		
		return result;
	}
	
	@Override
	public int intValueExact() {
		int result = value.intValueExact();
		return result;
	}
	
	@Override
	public BigIntegerNumber multiply(BigIntegerNumber val) {
		BigDecimal       product = value.multiply(approx(val), mathContext);
		BigIntegerNumber result  = new BigIntegerNumberApproximate(product, mathContext);
		return result;
	}
	
	@Override
	public BigIntegerNumber negate() {
		BigIntegerNumber result = new BigIntegerNumberApproximate(value.negate(), mathContext);
		return result;
	}
	
	@Override
	public BigIntegerNumber pow(int exponent) {
		if (exponent < 0) {
			// We are simulating a big integer, not a big decimal
			throw new ArithmeticException("Negative exponent");
		}
		BigIntegerNumber result;
		if (exponent > MAX_INT_EXPONENT_VALUE) {
			result = powLargeIntExponent(exponent);
		}
		else {
			BigDecimal pow = value.pow(exponent, mathContext);
			result = new BigIntegerNumberApproximate(pow, mathContext);
		}
		return result;
	}
	
	// Is a value outside the range BigDecimal.pow allows, so we have break it apart as follows:
	// (b^m)^(e / m) * b^(e % m)
	// NOTE:
	// m = Max positive exponent value allowed. 
	// e = Exponent (is positive)
	// b = Base (i.e. this)
	private BigIntegerNumber powLargeIntExponent(int exponent) {
		int exponentQuotient  = exponent / MAX_INT_EXPONENT_VALUE;
		int exponentRemainder = exponent % MAX_INT_EXPONENT_VALUE;
		// b^m
		BigDecimal quotientBase = value.pow(MAX_INT_EXPONENT_VALUE, mathContext);
				
		// (b^m)^(e / m)
		BigDecimal commonFactorsPow = quotientBase.pow(Math.abs(exponentQuotient), mathContext);
		
		// b^(e % m)
		BigDecimal basePowExpRemainder = value.pow(exponentRemainder, mathContext);
		
		// (b^m)^(e / m) * b^(e % m)
		BigDecimal pow = commonFactorsPow.multiply(basePowExpRemainder, mathContext);
		
		BigIntegerNumber result = new BigIntegerNumberApproximate(pow, mathContext);				
		
		return result;
	}
	
	@Override
	public BigIntegerNumber remainder(BigIntegerNumber val) {
		BigIntegerNumber result = divideAndRemainder(val)[1];
		return result;
	}
	
	@Override
	public int signum() {
		int result = value.signum();
		return result;
	}
	
	@Override
	public BigDecimal log(MathContext logMathContext) {
		if (value.signum() == -1) {			
			throw new UnsupportedOperationException("Cannot compute the log for a negative number: "+value.toEngineeringString());
		}

		double log = logeBigDecimal(approxUnscaledDoubleValue(value), value);
		
		BigDecimal result = new BigDecimal(log, logMathContext);
		
		return result;
	}
	
	@Override
	public BigIntegerNumber subtract(BigIntegerNumber val) {
		BigDecimal       difference = value.subtract(approx(val), mathContext);
		BigIntegerNumber result     = new BigIntegerNumberApproximate(difference, mathContext);
		return result;
	}
	
	@Override
	public String toString(int radix) {	
		String result;	
		if (radix == 10) {
			result = value.toPlainString();
		}
		else {
			result = value.toBigIntegerExact().toString(radix);
		}
		return result;
	}
	
	BigIntegerNumberExact toBigIntegerNumberExact() {
		BigIntegerNumberExact result = new BigIntegerNumberExact(value.toBigIntegerExact());
		return result;
	}
	
	private BigDecimal approx(BigIntegerNumber val) {
		BigDecimal result;
		if (val instanceof BigIntegerNumberApproximate) {
			result = ((BigIntegerNumberApproximate) val).value;
		}
		else {
			result = ((BigIntegerNumberExact)val).toBigIntegerNumberApproximate(mathContext).value;
		}
		return result;
	}
	
	private BigDecimal gcd(BigDecimal a, BigDecimal b, MathContext mathContext) {			
		BigDecimal result = null;
		
		if (a.signum() == 0) {
			result = b.abs();
		}
		else if (b.signum() == 0) {
			result = a.abs();
		}
		else if (a.compareTo(b) == 0) {
			result = a; // i.e. they are the same, pick either one
		}
		else {
			// Handle cases where, the precision digits do
			// not have a common factor, e.g.:
			// gcd(25, 190), with precision = 2.
			// Note: both need to be incremented if possible to
			// handle cases like:
			// gcd(260, 190)
			a = incScaleIfPossible(a);
			b = incScaleIfPossible(b);
			
			BigInteger aUnscaled = a.unscaledValue();
			BigInteger bUnscaled = b.unscaledValue();
			BigInteger gcd = aUnscaled.gcd(bUnscaled);
		
			// Scales will be <= 0 as we are representing big integers (i.e. -scale used).
			int gcdScale = Math.max(a.scale(), b.scale());
			BigDecimal scaledGCD = new BigDecimal(gcd, gcdScale, mathContext);
			if (!BigDecimal.ONE.equals(scaledGCD)) {
				// Note: On recursive factored gcd call we need to increase the precision by 1 so that we don't loose information,
				// e.g.: precision = 2, gcd(6,2300). It will find 2 but 2300/2 = 1150, which is 3 digits of precision, and if
				// that is rounded you end up getting another gcd != 1.
				MathContext mathContextPlusOnePrecision;
				if (mathContext.getPrecision() == 0) {
					// i.e. 0 indicates infinite precision
					mathContextPlusOnePrecision = mathContext;
				}
				else {
					mathContextPlusOnePrecision = new MathContext(mathContext.getPrecision()+1, mathContext.getRoundingMode());
				}
				BigDecimal factoredAValue = a.divide(scaledGCD, mathContextPlusOnePrecision);
				BigDecimal factoredBValue = b.divide(scaledGCD, mathContextPlusOnePrecision);
			
				BigDecimal factoredGCD = gcd(factoredAValue, factoredBValue, mathContextPlusOnePrecision);
							
				scaledGCD = scaledGCD.multiply(factoredGCD, mathContext);
			}
			
			result = scaledGCD;
		}
		
		return result;
	}
	
	private BigDecimal incScaleIfPossible(BigDecimal bd) {
		BigDecimal result = bd;
		if (bd.scale() < 0) {
			result = bd.setScale(bd.scale()+1);
		}
		return result;
	}
	
	// To convert to log2 using loge:
	// log2(value) = loge(value)/loge(2)
	private static double log2(double value) {
		double result = Math.log(value) / LOGE_2;
		return result;
	}
	
	// To compute loge of BigDecimal:
	// value = unscaled*10^(-scale)
	// loge(value) = loge(unscaled*10^(-scale)) = loge(unscaled) + (-scale)*loge(10)
	private static double logeBigDecimal(double approxUnscaledValueOfBigDecimal, BigDecimal bigDecimalValue) {
		double loge_unscaled = Math.log(approxUnscaledValueOfBigDecimal);				
		double result        = loge_unscaled+((-bigDecimalValue.scale())*LOGE_10);	
		
		return result;
	}
	
	// To compute log2 of BigDecimal:
	// value = unscaled*10^(-scale)
	// log2(value) = log2(unscaled*10^(-scale)) = log2(unscaled) + (-scale)*log2(10)
	private static double log2BigDecimal(double approxUnscaledValueOfBigDecimal, BigDecimal bigDecimalValue) {
		double log2_unscaled = log2(approxUnscaledValueOfBigDecimal);				
		double result        = log2_unscaled+((-bigDecimalValue.scale())*LOG2_10);	
		
		return result;
	}

	private static double approxUnscaledDoubleValue(BigDecimal value) {
		double result;
		if (value.precision() > 307) {
			// Note: loss of information here
			result = new BigDecimal(value.unscaledValue(), value.scale(), new MathContext(307)).unscaledValue().doubleValue();
		}
		else {
			result = value.unscaledValue().doubleValue();
		}
		return result;
	}
}
