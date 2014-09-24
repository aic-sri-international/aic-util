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
package com.sri.ai.test.util.math;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;

import com.sri.ai.util.math.MixedRadixNumber;

/**
 * @author Ciaran O'Reilly
 * 
 */
public class MixedRadixNumberTest {

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidRadices1() {
		new MixedRadixNumber(BigInteger.valueOf(100), new int[] { 1, 0, -1 });
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInvalidRadices2() {
		new MixedRadixNumber(BigInteger.valueOf(100), new int[] { 2, 0});
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidMaxValue() {
		new MixedRadixNumber(BigInteger.valueOf(100), new int[] { 3, 3, 3 });
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidInitialValuesValue1() {
		new MixedRadixNumber(new int[] { 0, 0, 4 }, new int[] { 3, 3, 3 });
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidInitialValuesValue2() {
		new MixedRadixNumber(new int[] { 1, 2, -1 }, new int[] { 3, 3, 3 });
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidInitialValuesValue3() {
		new MixedRadixNumber(new int[] { 1, 2, 3, 1 }, new int[] { 3, 3, 3 });
	}

	@Test
	public void testAllowedMaxValue() {
		Assert.assertEquals(
				BigInteger.valueOf(15),
				(new MixedRadixNumber(BigInteger.ZERO, new int[] { 2, 2, 2, 2 })
						.getMaxAllowedValue()));
		Assert.assertEquals(
				BigInteger.valueOf(80),
				(new MixedRadixNumber(BigInteger.ZERO, new int[] { 3, 3, 3, 3 })
						.getMaxAllowedValue()));
		Assert.assertEquals(BigInteger.valueOf(5), (new MixedRadixNumber(
				BigInteger.ZERO, new int[] { 3, 2 }).getMaxAllowedValue()));
		Assert.assertEquals(
				BigInteger.valueOf(35),
				(new MixedRadixNumber(BigInteger.ZERO, new int[] { 3, 3, 2, 2 })
						.getMaxAllowedValue()));
		Assert.assertEquals(
				BigInteger.valueOf(359),
				(new MixedRadixNumber(BigInteger.ZERO, new int[] { 3, 4, 5, 6 })
						.getMaxAllowedValue()));
		Assert.assertEquals(
				BigInteger.valueOf(359),
				(new MixedRadixNumber(BigInteger.ZERO, new int[] { 6, 5, 4, 3 })
						.getMaxAllowedValue()));
		Assert.assertEquals(BigInteger.valueOf(359),
				(new MixedRadixNumber(new int[] { 5, 4, 3, 2 }, new int[] { 6,
						5, 4, 3 }).getMaxAllowedValue()));
		Assert.assertEquals(
				BigInteger.valueOf(7),
		        // In this case the value in position two is always fixed
				(new MixedRadixNumber(BigInteger.ZERO, new int[] { 2, 1, 2, 2 })
						.getMaxAllowedValue()));
	}

	@Test
	public void testIncrement() {
		MixedRadixNumber mrn = new MixedRadixNumber(BigInteger.ZERO, new int[] {
				3});
		int i = 0;
		while (mrn.increment()) {
			i++;
		}
		Assert.assertEquals(BigInteger.valueOf(i), mrn.getMaxAllowedValue());
		
		mrn = new MixedRadixNumber(BigInteger.ZERO, new int[] {
				3, 2 });
		i = 0;
		while (mrn.increment()) {
			i++;
			if (i < mrn.getMaxAllowedValue().intValue()) {
				Assert.assertTrue(mrn.canIncrement());
			}
		}
		Assert.assertEquals(BigInteger.valueOf(i), mrn.getMaxAllowedValue());
		
		mrn = new MixedRadixNumber(BigInteger.ZERO, new int[] {3, 2, 3 });
		i = 0;
		while (mrn.increment()) {
			i++;
			if (i < mrn.getMaxAllowedValue().intValue()) {
				Assert.assertTrue(mrn.canIncrement());
			}
		}
		Assert.assertEquals(BigInteger.valueOf(i), mrn.getMaxAllowedValue());
		
		mrn = new MixedRadixNumber(BigInteger.valueOf(11), new int[] {3, 2, 3 });
		mrn.increment();
		mrn.increment();
		mrn.increment();
		Assert.assertEquals(14, mrn.intValue());
		
		// Test with position 2 always fixed
		mrn = new MixedRadixNumber(BigInteger.valueOf(1), new int[] {3, 1, 3 });
		mrn.increment();
		mrn.increment();
		mrn.increment();
		Assert.assertEquals(4, mrn.intValue());
	}

	@Test
	public void testDecrement() {
		MixedRadixNumber mrn = new MixedRadixNumber(BigInteger.valueOf(5),
				new int[] { 3, 2 });
		int i = 0;
		while (mrn.decrement()) {
			i++;
			if (i < mrn.getMaxAllowedValue().intValue()) {
				Assert.assertTrue(mrn.canDecrement());
			}
		}
		Assert.assertEquals(BigInteger.valueOf(i), mrn.getMaxAllowedValue());
		i = 0;
		while (mrn.increment()) {
			i++;
			if (i < mrn.getMaxAllowedValue().intValue()) {
				Assert.assertTrue(mrn.canDecrement());
			}
		}
		while (mrn.decrement()) {
			i--;
		}
		Assert.assertEquals(i, mrn.intValue());
	}

	@Test
	public void testCurrentNumeralValue() {
		MixedRadixNumber mrn;
		//
		mrn = new MixedRadixNumber(BigInteger.ZERO, new int[] { 3, 3, 2, 2 });
		Assert.assertEquals(0, mrn.getCurrentNumeralValue(0));
		Assert.assertEquals(0, mrn.getCurrentNumeralValue(1));
		Assert.assertEquals(0, mrn.getCurrentNumeralValue(2));
		Assert.assertEquals(0, mrn.getCurrentNumeralValue(3));
		//
		mrn = new MixedRadixNumber(BigInteger.valueOf(35), new int[] { 3, 3, 2,
				2 });
		Assert.assertEquals(2, mrn.getCurrentNumeralValue(0));
		Assert.assertEquals(2, mrn.getCurrentNumeralValue(1));
		Assert.assertEquals(1, mrn.getCurrentNumeralValue(2));
		Assert.assertEquals(1, mrn.getCurrentNumeralValue(3));
		//
		mrn = new MixedRadixNumber(BigInteger.valueOf(25), new int[] { 3, 3, 2,
				2 });
		Assert.assertEquals(2, mrn.getCurrentNumeralValue(0));
		Assert.assertEquals(0, mrn.getCurrentNumeralValue(1));
		Assert.assertEquals(0, mrn.getCurrentNumeralValue(2));
		Assert.assertEquals(1, mrn.getCurrentNumeralValue(3));
		//
		mrn = new MixedRadixNumber(BigInteger.valueOf(17), new int[] { 3, 3, 2,
				2 });
		Assert.assertEquals(1, mrn.getCurrentNumeralValue(0));
		Assert.assertEquals(1, mrn.getCurrentNumeralValue(1));
		Assert.assertEquals(0, mrn.getCurrentNumeralValue(2));
		Assert.assertEquals(1, mrn.getCurrentNumeralValue(3));
		//
		mrn = new MixedRadixNumber(BigInteger.valueOf(8), new int[] { 3, 3, 2,
				2 });
		Assert.assertEquals(0, mrn.getCurrentNumeralValue(0));
		Assert.assertEquals(2, mrn.getCurrentNumeralValue(1));
		Assert.assertEquals(0, mrn.getCurrentNumeralValue(2));
		Assert.assertEquals(0, mrn.getCurrentNumeralValue(3));
		//
		mrn = new MixedRadixNumber(BigInteger.valueOf(359), new int[] { 3, 4,
				5, 6 });
		Assert.assertEquals(2, mrn.getCurrentNumeralValue(0));
		Assert.assertEquals(3, mrn.getCurrentNumeralValue(1));
		Assert.assertEquals(4, mrn.getCurrentNumeralValue(2));
		Assert.assertEquals(5, mrn.getCurrentNumeralValue(3));
		//
		mrn = new MixedRadixNumber(BigInteger.valueOf(359), new int[] { 6, 5,
				4, 3 });
		Assert.assertEquals(5, mrn.getCurrentNumeralValue(0));
		Assert.assertEquals(4, mrn.getCurrentNumeralValue(1));
		Assert.assertEquals(3, mrn.getCurrentNumeralValue(2));
		Assert.assertEquals(2, mrn.getCurrentNumeralValue(3));
		//
		mrn = new MixedRadixNumber(BigInteger.valueOf(3), new int[] { 2, 1, 2 });
		Assert.assertEquals(1, mrn.getCurrentNumeralValue(0));
		Assert.assertEquals(0, mrn.getCurrentNumeralValue(1));
		Assert.assertEquals(1, mrn.getCurrentNumeralValue(2));
	}

	@Test
	public void testCurrentValueFor() {
		MixedRadixNumber mrn;
		//
		mrn = new MixedRadixNumber(BigInteger.ZERO, new int[] { 3, 3, 2, 2 });
		Assert.assertEquals(BigInteger.ZERO,
				mrn.getValueFor(new int[] { 0, 0, 0, 0 }));
		//
		mrn = new MixedRadixNumber(BigInteger.valueOf(35), new int[] { 3, 3, 2,
				2 });
		Assert.assertEquals(BigInteger.valueOf(35),
				mrn.getValueFor(new int[] { 2, 2, 1, 1 }));
		//
		mrn = new MixedRadixNumber(BigInteger.valueOf(25), new int[] { 3, 3, 2,
				2 });
		Assert.assertEquals(BigInteger.valueOf(25),
				mrn.getValueFor(new int[] { 2, 0, 0, 1 }));
		//
		mrn = new MixedRadixNumber(BigInteger.valueOf(17), new int[] { 3, 3, 2,
				2 });
		Assert.assertEquals(BigInteger.valueOf(17),
				mrn.getValueFor(new int[] { 1, 1, 0, 1 }));
		//
		mrn = new MixedRadixNumber(BigInteger.valueOf(8), new int[] { 3, 3, 2,
				2 });
		Assert.assertEquals(BigInteger.valueOf(8),
				mrn.getValueFor(new int[] { 0, 2, 0, 0 }));
		//
		mrn = new MixedRadixNumber(BigInteger.valueOf(359), new int[] { 3, 4,
				5, 6 });
		Assert.assertEquals(BigInteger.valueOf(359),
				mrn.getValueFor(new int[] { 2, 3, 4, 5 }));
		//
		mrn = new MixedRadixNumber(BigInteger.valueOf(359), new int[] { 6, 5,
				4, 3 });
		Assert.assertEquals(BigInteger.valueOf(359),
				mrn.getValueFor(new int[] { 5, 4, 3, 2 }));
		//
		mrn = new MixedRadixNumber(BigInteger.ZERO, new int[] { 2, 1, 2 });
		Assert.assertEquals(BigInteger.valueOf(2),
				mrn.getValueFor(new int[] { 1, 0, 0 }));
	}

	@Test
	public void testSetCurrentValueFor() {
		MixedRadixNumber mrn;
		//
		mrn = new MixedRadixNumber(BigInteger.ZERO, new int[] { 3, 3, 2, 2 });
		mrn.setCurrentValueFor(new int[] { 0, 0, 0, 0 });
		Assert.assertEquals(0, mrn.intValue());
		Assert.assertEquals(0, mrn.getCurrentNumeralValue(0));
		Assert.assertEquals(0, mrn.getCurrentNumeralValue(1));
		Assert.assertEquals(0, mrn.getCurrentNumeralValue(2));
		Assert.assertEquals(0, mrn.getCurrentNumeralValue(3));
		//
		mrn = new MixedRadixNumber(BigInteger.ZERO, new int[] { 3, 3, 2, 2 });
		mrn.setCurrentValueFor(new int[] { 2, 2, 1, 1 });
		Assert.assertEquals(35, mrn.intValue());
		Assert.assertEquals(2, mrn.getCurrentNumeralValue(0));
		Assert.assertEquals(2, mrn.getCurrentNumeralValue(1));
		Assert.assertEquals(1, mrn.getCurrentNumeralValue(2));
		Assert.assertEquals(1, mrn.getCurrentNumeralValue(3));
		//
		mrn = new MixedRadixNumber(BigInteger.ZERO, new int[] { 3, 3, 2, 2 });
		mrn.setCurrentValueFor(new int[] { 2, 0, 0, 1 });
		Assert.assertEquals(25, mrn.intValue());
		Assert.assertEquals(2, mrn.getCurrentNumeralValue(0));
		Assert.assertEquals(0, mrn.getCurrentNumeralValue(1));
		Assert.assertEquals(0, mrn.getCurrentNumeralValue(2));
		Assert.assertEquals(1, mrn.getCurrentNumeralValue(3));
		//
		mrn = new MixedRadixNumber(BigInteger.ZERO, new int[] { 3, 3, 2, 2 });
		mrn.setCurrentValueFor(new int[] { 1, 1, 0, 1 });
		Assert.assertEquals(17, mrn.intValue());
		Assert.assertEquals(1, mrn.getCurrentNumeralValue(0));
		Assert.assertEquals(1, mrn.getCurrentNumeralValue(1));
		Assert.assertEquals(0, mrn.getCurrentNumeralValue(2));
		Assert.assertEquals(1, mrn.getCurrentNumeralValue(3));
		//
		mrn = new MixedRadixNumber(BigInteger.ZERO, new int[] { 3, 3, 2, 2 });
		mrn.setCurrentValueFor(new int[] { 0, 2, 0, 0 });
		Assert.assertEquals(8, mrn.intValue());
		Assert.assertEquals(0, mrn.getCurrentNumeralValue(0));
		Assert.assertEquals(2, mrn.getCurrentNumeralValue(1));
		Assert.assertEquals(0, mrn.getCurrentNumeralValue(2));
		Assert.assertEquals(0, mrn.getCurrentNumeralValue(3));
		//
		mrn = new MixedRadixNumber(BigInteger.ZERO, new int[] { 3, 4, 5, 6 });
		mrn.setCurrentValueFor(new int[] { 2, 3, 4, 5 });
		Assert.assertEquals(359, mrn.intValue());
		Assert.assertEquals(2, mrn.getCurrentNumeralValue(0));
		Assert.assertEquals(3, mrn.getCurrentNumeralValue(1));
		Assert.assertEquals(4, mrn.getCurrentNumeralValue(2));
		Assert.assertEquals(5, mrn.getCurrentNumeralValue(3));
		//
		mrn = new MixedRadixNumber(BigInteger.ZERO, new int[] { 6, 5, 4, 3 });
		mrn.setCurrentValueFor(new int[] { 5, 4, 3, 2 });
		Assert.assertEquals(359, mrn.intValue());
		Assert.assertEquals(5, mrn.getCurrentNumeralValue(0));
		Assert.assertEquals(4, mrn.getCurrentNumeralValue(1));
		Assert.assertEquals(3, mrn.getCurrentNumeralValue(2));
		Assert.assertEquals(2, mrn.getCurrentNumeralValue(3));
		//
		mrn = new MixedRadixNumber(BigInteger.ZERO, new int[] { 2, 1, 2 });
		mrn.setCurrentValueFor(new int[] { 1, 0, 0 });
		Assert.assertEquals(2, mrn.intValue());
		Assert.assertEquals(1, mrn.getCurrentNumeralValue(0));
		Assert.assertEquals(0, mrn.getCurrentNumeralValue(1));
		Assert.assertEquals(0, mrn.getCurrentNumeralValue(2));
	}
}
