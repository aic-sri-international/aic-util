/*
 * Copyright (c) 2013, SRI International
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

/* <b>Note:</b> Tests closely based on those in freely available version of 
 * 'BigRational.java', developed by Eric Laroche, which can be found at: <a
 * href="http://www.lrdev.com/lr/java/">http://www.lrdev.com/lr/java/</a>
 * BigRational.java -- dynamically sized big rational numbers.
**
** Copyright (C) 2002-2010 Eric Laroche.  All rights reserved.
**
** @author Eric Laroche <laroche@lrdev.com>
** @version @(#)$Id: BigRational.java,v 1.3 2010/03/24 20:11:34 laroche Exp $
**
** This program is free software;
** you can redistribute it and/or modify it.
**
** This program is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
**
*/
package com.sri.ai.test.util.math;

import static com.sri.ai.util.Util.factorial;
import static com.sri.ai.util.Util.mapIntoList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.google.common.primitives.Longs;
import com.sri.ai.util.math.Multinomial;
import com.sri.ai.util.math.MultinomialIterator;
import com.sri.ai.util.math.Rational;

public class MultinomialTest {

	@Test
	public void testFactorial() {
		assertEquals(new Rational(1), factorial(1));
		assertEquals(new Rational(2), factorial(2));
		assertEquals(new Rational(6), factorial(3));
		assertEquals(new Rational(24), factorial(4));
		assertEquals(new Rational(120), factorial(5));
		assertEquals(new Rational(720), factorial(6));
	}
	
	@Test
	public void testMultinomial() {
		
		Multinomial m;
		long expected;
		boolean hadSuccessor;
		List<Rational> coefficients;
		MultinomialIterator iterator;
		long[] expectedCoefficients;
		List<Rational> expectedRationals;
		
		try {
			m = new Multinomial(3, 0);
			fail("Should have thrown an assertion error as number of classes must be at least 1.");
		}
		catch (AssertionError e) {}
		
		try {
			m = new Multinomial(new int[]{});
			fail("Should have thrown an assertion error as number of classes must be at least 1.");
		}
		catch (AssertionError e) {}
		
		m = new Multinomial(0, 1);
		expected = 1;
		assertEquals(new Rational(expected), m.choose());
		
		m = new Multinomial(3, 1);
		expected = 1;
		assertEquals(new Rational(expected), m.choose());
		
		m = new Multinomial(3, 1);
		expected = 1;
		assertEquals(new Rational(expected), m.choose());
		
		m = new Multinomial(new int[]{2, 1});
		expected = 3;
		assertEquals(new Rational(expected), m.choose());
		
		m = new Multinomial(new int[]{3, 4, 5, 8});
		expected = 3491888400L;
		assertEquals(new Rational(expected), m.choose());
		
		
		
		m = new Multinomial(3, 2); // 3 objects, 2 classes, starting with all objects in the last class
		expected = 1;
		assertEquals(new Rational(expected), m.choose());
		
		hadSuccessor = m.iterate();
		expected = 3;
		assertEquals(new Rational(expected), m.choose());
		assertEquals(true, hadSuccessor);
		
		hadSuccessor = m.iterate();
		expected = 3;
		assertEquals(new Rational(expected), m.choose());
		assertEquals(true, hadSuccessor);
		
		hadSuccessor = m.iterate();
		expected = 1;
		assertEquals(new Rational(expected), m.choose());
		assertEquals(true, hadSuccessor);
		
		hadSuccessor = m.iterate(); // should remain the same.
		expected = 1;
		assertEquals(new Rational(expected), m.choose());
		assertEquals(false, hadSuccessor); // iteration failed.
		
		
		
		iterator = new MultinomialIterator(8, 4);
		coefficients = mapIntoList(iterator, mValue -> {
			Rational result = mValue.choose();
//			System.out.println("coefficient of " + mValue + ": " + result);	
//			System.out.println();	
			return result;
		});
//		System.out.println("coefficients: " + mapIntoList(coefficients, e -> e + "L"));	
		expectedCoefficients = new long[]{ 1L, 8L, 28L, 56L, 70L, 56L, 28L, 8L, 1L, 8L, 56L, 168L, 280L, 280L, 168L, 56L, 8L, 28L, 168L, 420L, 560L, 420L, 168L, 28L, 56L, 280L, 560L, 560L, 280L, 56L, 70L, 280L, 420L, 280L, 70L, 56L, 168L, 168L, 56L, 28L, 56L, 28L, 8L, 8L, 1L, 8L, 56L, 168L, 280L, 280L, 168L, 56L, 8L, 56L, 336L, 840L, 1120L, 840L, 336L, 56L, 168L, 840L, 1680L, 1680L, 840L, 168L, 280L, 1120L, 1680L, 1120L, 280L, 280L, 840L, 840L, 280L, 168L, 336L, 168L, 56L, 56L, 8L, 28L, 168L, 420L, 560L, 420L, 168L, 28L, 168L, 840L, 1680L, 1680L, 840L, 168L, 420L, 1680L, 2520L, 1680L, 420L, 560L, 1680L, 1680L, 560L, 420L, 840L, 420L, 168L, 168L, 28L, 56L, 280L, 560L, 560L, 280L, 56L, 280L, 1120L, 1680L, 1120L, 280L, 560L, 1680L, 1680L, 560L, 560L, 1120L, 560L, 280L, 280L, 56L, 70L, 280L, 420L, 280L, 70L, 280L, 840L, 840L, 280L, 420L, 840L, 420L, 280L, 280L, 70L, 56L, 168L, 168L, 56L, 168L, 336L, 168L, 168L, 168L, 56L, 28L, 56L, 28L, 56L, 56L, 28L, 8L, 8L, 8L, 1L };
		expectedRationals = mapIntoList(Longs.asList(expectedCoefficients), Rational::new);
		assertEquals(expectedRationals, coefficients);
		
		
		
		iterator = new MultinomialIterator(4, 8);
		coefficients = mapIntoList(iterator, mValue -> {
			Rational result = mValue.choose();
//			System.out.println("coefficient of " + mValue + ": " + result);	
//			System.out.println();	
			return result;
		});
//		System.out.println("coefficients: " + mapIntoList(coefficients, e -> e + "L"));	
		expectedCoefficients = new long[]{ 1L, 4L, 6L, 4L, 1L, 4L, 12L, 12L, 4L, 6L, 12L, 6L, 4L, 4L, 1L, 4L, 12L, 12L, 4L, 12L, 24L, 12L, 12L, 12L, 4L, 6L, 12L, 6L, 12L, 12L, 6L, 4L, 4L, 4L, 1L, 4L, 12L, 12L, 4L, 12L, 24L, 12L, 12L, 12L, 4L, 12L, 24L, 12L, 24L, 24L, 12L, 12L, 12L, 12L, 4L, 6L, 12L, 6L, 12L, 12L, 6L, 12L, 12L, 12L, 6L, 4L, 4L, 4L, 4L, 1L, 4L, 12L, 12L, 4L, 12L, 24L, 12L, 12L, 12L, 4L, 12L, 24L, 12L, 24L, 24L, 12L, 12L, 12L, 12L, 4L, 12L, 24L, 12L, 24L, 24L, 12L, 24L, 24L, 24L, 12L, 12L, 12L, 12L, 12L, 4L, 6L, 12L, 6L, 12L, 12L, 6L, 12L, 12L, 12L, 6L, 12L, 12L, 12L, 12L, 6L, 4L, 4L, 4L, 4L, 4L, 1L, 4L, 12L, 12L, 4L, 12L, 24L, 12L, 12L, 12L, 4L, 12L, 24L, 12L, 24L, 24L, 12L, 12L, 12L, 12L, 4L, 12L, 24L, 12L, 24L, 24L, 12L, 24L, 24L, 24L, 12L, 12L, 12L, 12L, 12L, 4L, 12L, 24L, 12L, 24L, 24L, 12L, 24L, 24L, 24L, 12L, 24L, 24L, 24L, 24L, 12L, 12L, 12L, 12L, 12L, 12L, 4L, 6L, 12L, 6L, 12L, 12L, 6L, 12L, 12L, 12L, 6L, 12L, 12L, 12L, 12L, 6L, 12L, 12L, 12L, 12L, 12L, 6L, 4L, 4L, 4L, 4L, 4L, 4L, 1L, 4L, 12L, 12L, 4L, 12L, 24L, 12L, 12L, 12L, 4L, 12L, 24L, 12L, 24L, 24L, 12L, 12L, 12L, 12L, 4L, 12L, 24L, 12L, 24L, 24L, 12L, 24L, 24L, 24L, 12L, 12L, 12L, 12L, 12L, 4L, 12L, 24L, 12L, 24L, 24L, 12L, 24L, 24L, 24L, 12L, 24L, 24L, 24L, 24L, 12L, 12L, 12L, 12L, 12L, 12L, 4L, 12L, 24L, 12L, 24L, 24L, 12L, 24L, 24L, 24L, 12L, 24L, 24L, 24L, 24L, 12L, 24L, 24L, 24L, 24L, 24L, 12L, 12L, 12L, 12L, 12L, 12L, 12L, 4L, 6L, 12L, 6L, 12L, 12L, 6L, 12L, 12L, 12L, 6L, 12L, 12L, 12L, 12L, 6L, 12L, 12L, 12L, 12L, 12L, 6L, 12L, 12L, 12L, 12L, 12L, 12L, 6L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 1L };
		expectedRationals = mapIntoList(Longs.asList(expectedCoefficients), Rational::new);
		assertEquals(expectedRationals, coefficients);



		iterator = new MultinomialIterator(0, 8);
		coefficients = mapIntoList(iterator, mValue -> {
			Rational result = mValue.choose();
			//		System.out.println("coefficient of " + mValue + ": " + result);	
			//		System.out.println();	
			return result;
		});
//		System.out.println("coefficients: " + mapIntoList(coefficients, e -> e + "L"));	
		expectedCoefficients = new long[]{ 1L };
		expectedRationals = mapIntoList(Longs.asList(expectedCoefficients), Rational::new);
		assertEquals(expectedRationals, coefficients);



		// This one for stress testing only: the expected array is too large to put in a Java compilation unit.
		// Kept commented out in order to avoid making routinely ran tests too long.
//		iterator = new MultinomialIterator(20, 8);
//		coefficients = mapIntoList(iterator, mValue -> {
//			Rational result = mValue.choose();
//			// System.out.println("coefficient of " + mValue + ": " + result);	
//			// System.out.println();	
//			return result;
//		});
	}

}
