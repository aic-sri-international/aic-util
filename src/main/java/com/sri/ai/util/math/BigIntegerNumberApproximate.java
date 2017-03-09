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
		if (o instanceof BigIntegerNumberApproximate) {
			result = value.equals(approx((BigIntegerNumber)o));
		}
		return result;
	}
	
	@Override
	public int hashCode() {
		int result = value.hashCode();
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
		BigDecimal       sum    = value.add(approx(val));
		BigIntegerNumber result = new BigIntegerNumberApproximate(sum, mathContext);
		return result;
	}
	
	@Override
	public int bitLength() {
// TODO - need to compute.		
		throw new UnsupportedOperationException("TODO - implement");
	}
	
	@Override
	public BigIntegerNumber divide(BigIntegerNumber val) {
		BigDecimal       quotient = value.divideToIntegralValue(approx(val), mathContext);
		BigIntegerNumber result   = new BigIntegerNumberApproximate(quotient, mathContext);
		return result;
	}
	
	@Override
	public BigIntegerNumber[] divideAndRemainder(BigIntegerNumber val) {
		BigDecimal[] divideAndRemainder = value.divideAndRemainder(approx(val));
		
		BigIntegerNumber[] result = new BigIntegerNumber[] {
			new BigIntegerNumberApproximate(divideAndRemainder[0], mathContext),
			new BigIntegerNumberApproximate(divideAndRemainder[1], mathContext)
		};
		return result;
	}
	
	@Override
	public BigIntegerNumber gcd(BigIntegerNumber val) {
		BigDecimal thisValue  = this.value;
		BigDecimal otherValue = approx(val);
		
		BigInteger gcd = thisValue.unscaledValue().gcd(otherValue.unscaledValue());
	
		// Scales will be <= 0 as we are representing big integers (i.e. -scale used).
		BigDecimal scaledGCD = new BigDecimal(gcd, Math.max(thisValue.scale(), otherValue.scale()), mathContext);
		
		BigIntegerNumber result = new BigIntegerNumberApproximate(scaledGCD, mathContext);
		
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
		BigDecimal       pow    = value.pow(exponent, mathContext);
		BigIntegerNumber result = new BigIntegerNumberApproximate(pow, mathContext);
		return result;
	}
	
	@Override
	public BigIntegerNumber remainder(BigIntegerNumber val) {
		BigDecimal       remainder = value.remainder(approx(val), mathContext);
		BigIntegerNumber result    = new BigIntegerNumberApproximate(remainder, mathContext);
		return result;
	}
	
	@Override
	public int signum() {
		int result = value.signum();
		return result;
	}
	
	@Override
	public BigIntegerNumber shiftRight(int n) {
// TODO - need to compute as BigDecimal has no gcd method.		
		throw new UnsupportedOperationException("TODO - implement");	
	}
	
	@Override
	public BigIntegerNumber subtract(BigIntegerNumber val) {
		BigDecimal       difference = value.subtract(approx(val), mathContext);
		BigIntegerNumber result     = new BigIntegerNumberApproximate(difference, mathContext);
		return result;
	}
	
	private BigDecimal approx(BigIntegerNumber val) {
		BigDecimal result = ((BigIntegerNumberApproximate) val).value;
		return result;
	}
	
	@Override
	public String toString(int radix) {	
		String result;
// TODO - likely incorrect and super inefficient		
		if (radix == 10) {
			result = value.toPlainString();
		}
		else {
			result = value.toBigIntegerExact().toString(radix);
		}
		return result;
	}
}
