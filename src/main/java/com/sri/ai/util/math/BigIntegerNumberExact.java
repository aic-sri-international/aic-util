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
 * An implementation of BigIntegerNumber with exact semantics.
 * 
 * Intended for use internally by Rational.
 * 
 * @author oreilly
 *
 */
public class BigIntegerNumberExact extends BigIntegerNumber {
	private static final long serialVersionUID = 1L;
	//
	private static final BigDecimal LOG_2 = new BigDecimal(Math.log(2.0));	
	//
	private BigInteger value;
	
	public BigIntegerNumberExact(BigInteger value) {
		this.value = value;
	}
	
	public BigIntegerNumberExact(long val) {
		value = BigInteger.valueOf(val);
	}
	
	public BigIntegerNumberExact(String strNumber, int radix) {
		value = new BigInteger(strNumber, radix);
	}
	
	//
	// Object
	@Override
	public boolean equals(Object o) {
		boolean result = false;
		if (o instanceof BigIntegerNumberExact) {
			result = value.equals(exact((BigIntegerNumber)o));
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
		int result = value.compareTo(exact(o));
		return result;
	}
	
	//
	// BigIntegerNumber
	@Override
	public BigIntegerNumber abs() {
		BigIntegerNumber result = this;
		if (value.signum() < 0) {
			result = new BigIntegerNumberExact(value.abs());
		}
		return result;
	}
	
	@Override
	public BigIntegerNumber add(BigIntegerNumber val) {
		BigInteger       sum    = value.add(exact(val));
		BigIntegerNumber result = new BigIntegerNumberExact(sum);
		return result;
	}
	
	@Override
	public int bitLength() {
		int result = value.bitLength();
		return result;
	}
	
	@Override
	public BigIntegerNumber divide(BigIntegerNumber val) {
		BigInteger       quotient = value.divide(exact(val));
		BigIntegerNumber result   = new BigIntegerNumberExact(quotient);
		return result;
	}
	
	@Override
	public BigIntegerNumber[] divideAndRemainder(BigIntegerNumber val) {
		BigInteger[] divideAndRemainder = value.divideAndRemainder(exact(val));
		
		BigIntegerNumber[] result = new BigIntegerNumber[] {
			new BigIntegerNumberExact(divideAndRemainder[0]),
			new BigIntegerNumberExact(divideAndRemainder[1])
		};
		return result;
	}
	
	@Override
	public BigIntegerNumber gcd(BigIntegerNumber val) {
		BigInteger       gcd    = value.gcd(exact(val));		
		BigIntegerNumber result = new BigIntegerNumberExact(gcd);
		return result;
	}
	
	@Override
	public int intValueExact() {
		int result = value.intValueExact();
		return result;
	}
	
	@Override
	public BigIntegerNumber multiply(BigIntegerNumber val) {
		BigInteger       product = value.multiply(exact(val));
		BigIntegerNumber result  = new BigIntegerNumberExact(product);
		return result;
	}
	
	@Override
	public BigIntegerNumber negate() {
		BigIntegerNumber result = new BigIntegerNumberExact(value.negate());
		return result;
	}
	
	@Override
	public BigIntegerNumber pow(int exponent) {
		BigInteger       pow    = value.pow(exponent);
		BigIntegerNumber result = new BigIntegerNumberExact(pow);
		return result;
	}
	
	@Override
	public BigIntegerNumber remainder(BigIntegerNumber val) {
		BigInteger       remainder = value.remainder(exact(val));
		BigIntegerNumber result    = new BigIntegerNumberExact(remainder);
		return result;
	}
	
	@Override
	public int signum() {
		int result = value.signum();
		return result;
	}
	
	@Override
	public BigDecimal log(MathContext logMathContext) {
		// log(a)=log(a/2^k)+k*log(2)
		// see: http://stackoverflow.com/questions/6827516/logarithm-for-biginteger
		
		BigInteger b = value;
		if (b.signum() == -1) {			
			throw new UnsupportedOperationException("Cannot compute the log for a negative number: "+b);
		}
		
		int k = b.bitLength() - 1022;
		if (k > 0) {
			// NOTE: we lose precision here
			b = b.shiftRight(k);
		}
		double log = Math.log(b.doubleValue());
		BigDecimal result = new BigDecimal(log, logMathContext);
		if (k > 0) {
			BigDecimal kTimesLog2 = LOG_2.multiply(BigDecimal.valueOf(k), logMathContext); 
			result = result.add(kTimesLog2, logMathContext);
		}
		
		return result;
	}
	
	@Override
	public BigIntegerNumber subtract(BigIntegerNumber val) {
		BigInteger       difference = value.subtract(exact(val));
		BigIntegerNumber result     = new BigIntegerNumberExact(difference);
		return result;
	}
	
	@Override
	public String toString(int radix) {
		String result = value.toString(radix);
		return result;
	}
	
	private BigInteger exact(BigIntegerNumber val) {
		BigInteger result = ((BigIntegerNumberExact) val).value;
		return result;
	}
}
