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
package com.sri.ai.test.util.math;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.sri.ai.util.math.BernoulliNumber;
import com.sri.ai.util.math.Rational;

/**
 * 
 * @author oreilly
 *
 */
public class BernoulliNumberTest {
	
	@Test
	public void testComputeZero() {
		Assert.assertEquals(new Rational(1), BernoulliNumber.computeFirst(0));
		Assert.assertEquals(new Rational(1), BernoulliNumber.computeSecond(0)); 
	}
	
	@Test
	public void testComputeFirstB1() {
		Assert.assertEquals(new Rational(-1, 2), BernoulliNumber.computeFirst(1)); 
	}
	
	@Test
	public void testComputeSecondB1() {
		Assert.assertEquals(new Rational(1, 2), BernoulliNumber.computeSecond(1)); 
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testIllegalFirstArgumentException() {
		BernoulliNumber.computeFirst(-1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testIllegalSecondArgumentException() {
		BernoulliNumber.computeSecond(-1);
	}
	
	@Test
	public void test2To22() {
		List<Rational> expected = Arrays.asList(
				new Rational(      1,    6),
				Rational.ZERO,
				new Rational(     -1,   30),
				Rational.ZERO,
				new Rational(      1,   42),
				Rational.ZERO,
				new Rational(     -1,   30),
				Rational.ZERO,
				new Rational(      5,   66),
				Rational.ZERO,
				new Rational(   -691, 2730),
				Rational.ZERO,
				new Rational(      7,    6),
				Rational.ZERO,
				new Rational(  -3617,  510),
				Rational.ZERO,
				new Rational(  43867,  798),
				Rational.ZERO,
				new Rational(-174611,  330),
				Rational.ZERO,
				new Rational( 854513,  138)
				);
		
		for (int n = 0; n < expected.size(); n++) {
			Assert.assertEquals(expected.get(n), BernoulliNumber.computeFirst(n+2));
			Assert.assertEquals(expected.get(n), BernoulliNumber.computeSecond(n+2));
		}
	}
}
