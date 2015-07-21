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

import com.google.common.annotations.Beta;

@Beta
public class BernoulliNumber {

	/**
	 * Compute the first Bernoulli numbers (i.e. B1 = -1/2).
	 * 
	 * @param n
	 *        the Bernoully number to compute.
	 * @return the first Bernoulli number n.
	 */
	public static Rational computeFirst(int n) {
		if (n < 0) {
			throw new IllegalArgumentException("n must be >= 0");
		}
		Rational result;
		if (n == 0) {
			result = Rational.ONE;
		}
		else if (n == 1) {
			result = new Rational(-1, 2);
		}
		else if (n % 2 == 1) {
			result = Rational.ZERO;
		}
		else {
			switch (n) {
			case  2: result = new Rational(1, 6); break;
			case  4: result = new Rational(-1, 30); break;
			case  6: result = new Rational(1, 42); break;
			case  8: result = new Rational(-1, 30); break;
			case 10: result = new Rational(5, 66); break;
			case 12: result = new Rational(-691, 2730); break;
			case 14: result = new Rational(7, 6); break;
			case 16: result = new Rational(-3617, 510); break;
			case 18: result = new Rational(43867, 798); break;
			case 20: result = new Rational(-174611, 330); break;
			default:
				throw new UnsupportedOperationException("n > 20 currently not supported");			
			}
		}
		
		return result;
	}
}
