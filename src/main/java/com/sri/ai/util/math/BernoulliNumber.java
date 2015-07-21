/*
 * Copyright (c) 2015, SRI International
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
 * Neither the name of the aic-praise nor the names of its
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

import java.util.HashMap;
import java.util.Map;

import com.google.common.annotations.Beta;

/**
 * Compute Bernoulli number:<br>
 * https://en.wikipedia.org/wiki/Bernoulli_number
 * 
 * @author oreilly
 *
 */
@Beta
public class BernoulliNumber {
	private static final Map<Integer, Rational> _precomputed = new HashMap<>();
	static {
		_precomputed.put(2,  new Rational(      1,    6));
		_precomputed.put(4,  new Rational(     -1,   30));
		_precomputed.put(6,  new Rational(      1,   42));
		_precomputed.put(8,  new Rational(     -1,   30));
		_precomputed.put(10, new Rational(      5,   66));
		_precomputed.put(12, new Rational(   -691, 2730));
		_precomputed.put(14, new Rational(      7,    6));
		_precomputed.put(16, new Rational(  -3617,  510));
		_precomputed.put(18, new Rational(  43867,  798));
		_precomputed.put(20, new Rational(-174611,  330)); 
	}
	private static final Rational _firstB1  = new Rational(-1, 2);
	private static final Rational _secondB1 = new Rational( 1, 2);

	/**
	 * Compute the first Bernoulli numbers (i.e. B<sub>1</sub> = -1/2).
	 * NOTE: Used by <a href="https://en.wikipedia.org/wiki/Faulhaber%27s_formula">Faulhaber's formula</a>.
	 * 
	 * @param n
	 *        the nth first Bernoulli number to compute.
	 * @return the first Bernoulli number n.
	 */
	public static Rational computeFirst(int n) {
		return compute(n, _firstB1);
	}
	
	/**
	 * Compute the second Bernoulli numbers (i.e. B<sub>1</sub> = 1/2).
	 * 
	 * @param n
	 *        the nth second Bernoulli number to compute.
	 * @return the second Bernoulli number n.
	 */
	public static Rational computeSecond(int n) {
		return compute(n, _secondB1);
	}
	
	//
	// PRIVATE
	//
	private static Rational compute(int n, Rational b1Value) {
		if (n < 0) {
			throw new IllegalArgumentException("n must be >= 0");
		}
		Rational result;
		if (n == 0) {
			result = Rational.ONE;
		}
		else if (n == 1) {
			result = b1Value;
		}
		else if (n % 2 == 1) {
			result = Rational.ZERO;
		}
		else {
			result = _precomputed.get(n);
			if (result == null) {
				// NOTE: Simple implementation based on:
				// https://en.wikipedia.org/wiki/Bernoulli_number#Algorithmic_description
				Rational[] a = new Rational[n+1];
				for (int m = 0; m <= n; m++) {
					a[m] = Rational.ONE.divide(m+1);
					for (int j = m; j >= 1; j--) {
						a[j-1] = new Rational(j).multiply(a[j-1].subtract(a[j]));
					}
				}
				result = a[0];
			}
		}
		
		return result;
	}
}