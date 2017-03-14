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

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.sri.ai.util.math.Rational;

/**
 * @author Eric Laroche <laroche@lrdev.com>
 * @author oreilly
 *
 */
@RunWith(Parameterized.class)
public class RationalTest {	
	
	@Parameters(name = "{index}: approx = {0}")
	public static Collection<Object[]> implementations() {
		return Arrays.asList(new Object[][] { { Boolean.FALSE }, { Boolean.TRUE } });				
	}
	
	@Parameter
	public Boolean approximate;
	
	@Before
	public void setUp() {
		// NOTE: this level of precision required to pass the contained tests.
		int          precision    = MathContext.DECIMAL128.getPrecision()+1;
		RoundingMode roundingMode = MathContext.DECIMAL128.getRoundingMode();
		if (approximate) {			
			
			Rational.resetApproximationConfiguration(true, precision, roundingMode);
		}
		else {
			Rational.resetApproximationConfiguration(false, precision, roundingMode);
		}
	}

	@After
	public void tearDown() {
		Rational.resetApproximationConfigurationFromAICUtilConfiguration();
	}
	
	@Test
	public void testApproxAndExactInteroperate() {
		Rational.resetApproximationConfiguration(false, MathContext.DECIMAL128.getPrecision()+1, MathContext.DECIMAL128.getRoundingMode());
		Rational eZero             = new Rational(0);
		Rational eOne              = new Rational(1);
		Rational eMinusOne         = new Rational(-1);
		Rational eSevenThirteenths = new Rational(7, 13);		
		Rational.resetApproximationConfiguration(true, MathContext.DECIMAL128.getPrecision()+1, MathContext.DECIMAL128.getRoundingMode());		
		Rational aZero             = new Rational(0);
		Rational aOne              = new Rational(1);
		Rational aMinusOne         = new Rational(-1);
		Rational aSevenThirteenths = new Rational(7, 13);	
		
		Assert.assertEquals(eZero, aZero);
		Assert.assertEquals(eOne, aOne);
		Assert.assertEquals(eMinusOne, aMinusOne);
		Assert.assertEquals(eSevenThirteenths, aSevenThirteenths);
		
		Assert.assertEquals(new Rational(7*7, 13*13), eSevenThirteenths.multiply(aSevenThirteenths));
	}

	@Test
	public void testDefaultRadix() {
		Assert.assertEquals(10, Rational.DEFAULT_RADIX);
	}

	@Test(expected = NumberFormatException.class)
	public void testZeroDenominator() {
		new Rational(2, 0);
	}
	
	@Test
	public void testEquality() {
		// long/long ctor
		Assert.assertEquals(new Rational(3, 5),  new Rational(21, 35));
		Assert.assertEquals(new Rational(-3, 5), new Rational(-21, 35));
		Assert.assertEquals(new Rational(3, -5), new Rational(-21, 35));
		Assert.assertEquals(new Rational(-3, 5), new Rational(21, -35));
		Assert.assertEquals(new Rational(3, 5),  new Rational(-21, -35));

		// long ctor
		Assert.assertEquals(new Rational(1),  new Rational(1));
		Assert.assertEquals(new Rational(0),  new Rational(0));
		Assert.assertEquals(new Rational(2),  new Rational(2));
		Assert.assertEquals(new Rational(-1), new Rational(-1));
	}
	
	@Test
	public void testNormalization() {
		Assert.assertEquals("11",  new Rational("11").toString());
		Assert.assertEquals("-11", new Rational("-11").toString());
		Assert.assertEquals("11",  new Rational("+11").toString());

		Assert.assertEquals("3/5",  new Rational("21/35").toString());
		Assert.assertEquals("-3/5", new Rational("-21/35").toString());
		Assert.assertEquals("-3/5", new Rational("21/-35").toString());
		// special, but defined
		Assert.assertEquals("3/5", new Rational("-21/-35").toString());
		
		Assert.assertEquals("3/5", new Rational("+21/35").toString());
		// special, but defined
		Assert.assertEquals("3/5", new Rational("21/+35").toString());
		// special, but defined
		Assert.assertEquals("3/5", new Rational("+21/+35").toString());
	}
	
	@Test
	public void testSpecialFormats() {
		// 1/x
		Assert.assertEquals("1/3",  new Rational("/3").toString());
		Assert.assertEquals("-1/3", new Rational("-/3").toString());
		Assert.assertEquals("-1/3", new Rational("/-3").toString());
		
		// x/1
		Assert.assertEquals("3",  new Rational("3/").toString());
		Assert.assertEquals("-3", new Rational("-3/").toString());
		Assert.assertEquals("-3", new Rational("3/-").toString());
		Assert.assertEquals("3",  new Rational("-3/-").toString());
		
		// special, but defined
		Assert.assertEquals("-1", new Rational("-/").toString());
		Assert.assertEquals("-1", new Rational("/-").toString());
		// even more special, but defined
		Assert.assertEquals("1", new Rational("-/-").toString());

		// "Rational normalization"
		Assert.assertEquals("17/5",  new Rational("3.4").toString());
		Assert.assertEquals("-17/5", new Rational("-3.4").toString());
		Assert.assertEquals("17/5",  new Rational("+3.4").toString());

		// "Rational missing leading/trailing zero"
		Assert.assertEquals("5",     new Rational("5.").toString());
		Assert.assertEquals("-5",    new Rational("-5.").toString());
		Assert.assertEquals("7/10",  new Rational(".7").toString());
		Assert.assertEquals("-7/10", new Rational("-.7").toString());
		
		// special, but defined
		Assert.assertEquals("0", new Rational("-.").toString());
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalFormatPlus() {
		new Rational("+");
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalFormatMinus() {
		new Rational("-");
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalFormatDivide() {
		new Rational("/");
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalFormatPeriod() {
		new Rational(".");
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalFormatEmptyString() {
		new Rational("");
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalFormatEmptyString2() {
		// i.e. should remove white spaces.
		new Rational("   ");
	}
		
	@Test
	public void testLegalRadix() {
		// "Rational radix"
		Assert.assertEquals("3/11",                new Rational("f/37", 0x10).toString());
		Assert.assertEquals("3895/256",            new Rational("f.37", 0x10).toString());
		Assert.assertEquals("-46112938320/279841", new Rational("-dcba.efgh", 23).toString());
		Assert.assertEquals("bad6",                new Rational("1011101011010110", 2).toString(0x10));
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalRadix1() {
		new Rational("101", 1);
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalRadix2() {
		new Rational("101", -1);
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalRadix3() {
		new Rational("101", -2);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalRadix4() {
		(new Rational("33")).toString(1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalRadix5() {
		(new Rational("33")).toString(0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalRadix6() {
		(new Rational("33")).toString(-1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalRadix7() {
		(new Rational("33")).toString(-2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalRadix8() {
		(new Rational("33")).toStringDot(4, 1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalRadix9() {
		(new Rational("33")).toStringDotRelative(4, 1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalRadix10() {
		(new Rational("33")).toStringExponent(4, 1);
	}
	
	@Test
	public void testSlashAndDot() {
		Assert.assertEquals("5/6",  new Rational("2.5/3").toString());
		Assert.assertEquals("4/7",  new Rational("2/3.5").toString());
		Assert.assertEquals("5/7",  new Rational("2.5/3.5").toString());
		Assert.assertEquals("-5/6", new Rational("-2.5/3").toString());
		Assert.assertEquals("-4/7", new Rational("-2/3.5").toString());
		Assert.assertEquals("-5/7", new Rational("-2.5/3.5").toString());
		Assert.assertEquals("-5/6", new Rational("2.5/-3").toString());
		Assert.assertEquals("-4/7", new Rational("2/-3.5").toString());
		Assert.assertEquals("-5/7", new Rational("2.5/-3.5").toString());
		Assert.assertEquals("5/6",  new Rational("-2.5/-3").toString());
		Assert.assertEquals("4/7",  new Rational("-2/-3.5").toString());
		Assert.assertEquals("5/7",  new Rational("-2.5/-3.5").toString());
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalSigns1() {
		new Rational("+-2/3");
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalSigns2() {
		new Rational("-+2/3");
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalSigns3() {
		new Rational("++2/3");
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalSigns4() {
		new Rational("--2/3");
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalSigns5() {
		new Rational("2-/3");
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalSigns6() {
		new Rational("2+/3");
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalSigns7() {
		new Rational("2.+3");
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalSigns8() {
		new Rational("2.-3");
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalSigns9() {
		new Rational("2.3+");
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalSigns10() {
		new Rational("2.3-");
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalSlashNesting() {
		new Rational("2/3/5");
	}
	
	@Test
	public void testExponentConstruction() {
		// representations with exponent
		Assert.assertEquals("100",   new Rational("1E2").toString());
		Assert.assertEquals("10",    new Rational("1E1").toString());
		Assert.assertEquals("1",     new Rational("1E0").toString());
		Assert.assertEquals("1/10",  new Rational("1E-1").toString());
		Assert.assertEquals("1/100", new Rational("1E-2").toString());
		//
		Assert.assertEquals("-327/5000",           new Rational("-65.4E-3").toString());
		Assert.assertEquals("78962960182680.6000", new Rational("7.89629601826806E13").toStringDot(4));

		// special representations with exponent

		Assert.assertEquals("10", new Rational("1E+1").toString());
		Assert.assertEquals("1",  new Rational("1E+0").toString());
		Assert.assertEquals("1",  new Rational("1E-0").toString());
		Assert.assertEquals("1",  new Rational("1E").toString());
		Assert.assertEquals("1",  new Rational("1E+").toString());
		Assert.assertEquals("1",  new Rational("1E-").toString());

		Assert.assertEquals("770", new Rational("77E+1").toString());
		Assert.assertEquals("77",  new Rational("77E+0").toString());
		Assert.assertEquals("77",  new Rational("77E-0").toString());
		Assert.assertEquals("77",  new Rational("77E").toString());
		Assert.assertEquals("77",  new Rational("77E+").toString());
		Assert.assertEquals("77",  new Rational("77E-").toString());


		Assert.assertEquals("1",   new Rational("+E").toString());
		Assert.assertEquals("10",  new Rational("+E+1").toString());
		Assert.assertEquals("1",   new Rational("+E+0").toString());
		Assert.assertEquals("1",   new Rational("+E-0").toString());
		Assert.assertEquals("1",   new Rational("+E+").toString());
		Assert.assertEquals("1",   new Rational("+E-").toString());
		Assert.assertEquals("-1",  new Rational("-E").toString());
		Assert.assertEquals("-10", new Rational("-E+1").toString());
		Assert.assertEquals("-1",  new Rational("-E+0").toString());
		Assert.assertEquals("-1",  new Rational("-E-0").toString());
		Assert.assertEquals("-1",  new Rational("-E+").toString());
		Assert.assertEquals("-1",  new Rational("-E-").toString());

		Assert.assertEquals("617/2839",   new Rational("12.34/56.78").toString());
		Assert.assertEquals("6170/2839",  new Rational("12.34E1/56.78").toString());
		Assert.assertEquals("617/28390",  new Rational("12.34/56.78E1").toString());
		Assert.assertEquals("617/2839",   new Rational("12.34E1/56.78E1").toString());
		Assert.assertEquals("617/28390",  new Rational("12.34E-1/56.78").toString());
		Assert.assertEquals("6170/2839",  new Rational("12.34/56.78E-1").toString());
		Assert.assertEquals("617/2839",   new Rational("12.34E-1/56.78E-1").toString());
		Assert.assertEquals("-617/2839",  new Rational("-12.34/56.78").toString());
		Assert.assertEquals("-6170/2839", new Rational("-12.34E1/56.78").toString());
		Assert.assertEquals("-617/28390", new Rational("-12.34/56.78E1").toString());
		Assert.assertEquals("-617/2839",  new Rational("-12.34E1/56.78E1").toString());
		Assert.assertEquals("-617/28390", new Rational("-12.34E-1/56.78").toString());
		Assert.assertEquals("-6170/2839", new Rational("-12.34/56.78E-1").toString());
		Assert.assertEquals("-617/2839",  new Rational("-12.34E-1/56.78E-1").toString());
		Assert.assertEquals("-617/2839",  new Rational("12.34/-56.78").toString());
		Assert.assertEquals("-6170/2839", new Rational("12.34E1/-56.78").toString());
		Assert.assertEquals("-617/28390", new Rational("12.34/-56.78E1").toString());
		Assert.assertEquals("-617/2839",  new Rational("12.34E1/-56.78E1").toString());
		Assert.assertEquals("-617/28390", new Rational("12.34E-1/-56.78").toString());
		Assert.assertEquals("-6170/2839", new Rational("12.34/-56.78E-1").toString());
		Assert.assertEquals("-617/2839",  new Rational("12.34E-1/-56.78E-1").toString());
		Assert.assertEquals("617/2839",   new Rational("-12.34/-56.78").toString());
		Assert.assertEquals("6170/2839",  new Rational("-12.34E1/-56.78").toString());
		Assert.assertEquals("617/28390",  new Rational("-12.34/-56.78E1").toString());
		Assert.assertEquals("617/2839",   new Rational("-12.34E1/-56.78E1").toString());
		Assert.assertEquals("617/28390",  new Rational("-12.34E-1/-56.78").toString());
		Assert.assertEquals("6170/2839",  new Rational("-12.34/-56.78E-1").toString());
		Assert.assertEquals("617/2839",   new Rational("-12.34E-1/-56.78E-1").toString());
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalExponentiationConstruction1() {
		new Rational("E").toString();
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalExponentiationConstruction2() {
		new Rational("E+1").toString();
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalExponentiationConstruction3() {
		new Rational("E+0").toString();
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalExponentiationConstruction4() {
		new Rational("E-0").toString();
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalExponentiationConstruction5() {
		new Rational("E+").toString();
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalExponentiationConstruction6() {
		new Rational("E-").toString();
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalFractionalExponent() {
		new Rational("1E2.5");
	}
	
	@Test(expected = NumberFormatException.class)
	public void testExponentOverflow() {
		new Rational("1E" + ((long)Integer.MAX_VALUE * 3));
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalExponentNesting() {
		new Rational("1E2E3").equals((new Rational(10)).pow(2000));
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalNullString() {
		new Rational((String)null);
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalNaN() {
		new Rational(String.valueOf(Double.NaN));
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalPositiveInfinity() {
		new Rational(String.valueOf(Double.POSITIVE_INFINITY));
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalNegativeInfinity() {
		new Rational(String.valueOf(Double.NEGATIVE_INFINITY));
	}
	
	@Test
	public void testClone() throws CloneNotSupportedException {
		Assert.assertTrue(Rational.ONE.equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.clone() != Rational.ONE);
		Assert.assertTrue(Rational.ONE.clone().equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.equals(Rational.ONE.clone()));
		
		Assert.assertTrue(new Rational(Rational.ONE) != Rational.ONE);
		Assert.assertTrue((new Rational(Rational.ONE)).equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.equals(new Rational(Rational.ONE)));
	}
	
	@Test
	public void testScaling() {
		// zero scale
		// "Rational normalization"
		Assert.assertTrue((new Rational(123456, 0, Rational.DEFAULT_RADIX)).toString().equals("123456"));
		Assert.assertTrue((new Rational(123456, 1, Rational.DEFAULT_RADIX)).toString().equals("61728/5"));
		Assert.assertTrue((new Rational(123456, 2, Rational.DEFAULT_RADIX)).toString().equals("30864/25"));
		Assert.assertTrue((new Rational(123456, 5, Rational.DEFAULT_RADIX)).toString().equals("3858/3125"));
		// <1
		Assert.assertTrue((new Rational(123456, 6, Rational.DEFAULT_RADIX)).toString().equals("1929/15625"));
		// negative scale
		Assert.assertTrue((new Rational(123456, -1, Rational.DEFAULT_RADIX)).toString().equals("1234560"));
		Assert.assertTrue((new Rational(123456, -2, Rational.DEFAULT_RADIX)).toString().equals("12345600"));
	}
	
	@Test
	public void testConstants() {
		Assert.assertTrue(Rational.ZERO.toString().equals("0"));
		Assert.assertTrue(Rational.ONE.toString().equals("1"));
		Assert.assertTrue(Rational.MINUS_ONE.toString().equals("-1"));
	}
	
	@Test
	public void testPredicates() {
		// predicates (isPositive, isNegative, isZero, isOne, isMinusOne, isInteger)
		Assert.assertTrue(!Rational.ZERO.isPositive());
		Assert.assertTrue(!(new Rational(0)).isPositive());
		Assert.assertTrue(Rational.ONE.isPositive());
		Assert.assertTrue((new Rational(1)).isPositive());
		Assert.assertTrue(!Rational.MINUS_ONE.isPositive());
		Assert.assertTrue(!(new Rational(-1)).isPositive());
		Assert.assertTrue((new Rational(77)).isPositive());
		Assert.assertTrue(!(new Rational(-77)).isPositive());
		Assert.assertTrue((new Rational(3, 5)).isPositive());
		Assert.assertTrue(!(new Rational(-3, 5)).isPositive());

		Assert.assertTrue(!Rational.ZERO.isNegative());
		Assert.assertTrue(!(new Rational(0)).isNegative());
		Assert.assertTrue(!Rational.ONE.isNegative());
		Assert.assertTrue(!(new Rational(1)).isNegative());
		Assert.assertTrue(Rational.MINUS_ONE.isNegative());
		Assert.assertTrue((new Rational(-1)).isNegative());
		Assert.assertTrue(!(new Rational(77)).isNegative());
		Assert.assertTrue((new Rational(-77)).isNegative());
		Assert.assertTrue(!(new Rational(3, 5)).isNegative());
		Assert.assertTrue((new Rational(-3, 5)).isNegative());

		Assert.assertTrue(Rational.ZERO.isZero());
		Assert.assertTrue((new Rational(0)).isZero());
		Assert.assertTrue(!Rational.ONE.isZero());
		Assert.assertTrue(!(new Rational(1)).isZero());
		Assert.assertTrue(!Rational.MINUS_ONE.isZero());
		Assert.assertTrue(!(new Rational(-1)).isZero());
		Assert.assertTrue(!(new Rational(77)).isZero());
		Assert.assertTrue(!(new Rational(-77)).isZero());
		Assert.assertTrue(!(new Rational(3, 5)).isZero());
		Assert.assertTrue(!(new Rational(-3, 5)).isZero());

		Assert.assertTrue(!Rational.ZERO.isOne());
		Assert.assertTrue(!(new Rational(0)).isOne());
		Assert.assertTrue(Rational.ONE.isOne());
		Assert.assertTrue((new Rational(1)).isOne());
		Assert.assertTrue(!Rational.MINUS_ONE.isOne());
		Assert.assertTrue(!(new Rational(-1)).isOne());
		Assert.assertTrue(!(new Rational(77)).isOne());
		Assert.assertTrue(!(new Rational(-77)).isOne());
		Assert.assertTrue(!(new Rational(3, 5)).isOne());
		Assert.assertTrue(!(new Rational(-3, 5)).isOne());

		Assert.assertTrue(!Rational.ZERO.isMinusOne());
		Assert.assertTrue(!(new Rational(0)).isMinusOne());
		Assert.assertTrue(!Rational.ONE.isMinusOne());
		Assert.assertTrue(!(new Rational(1)).isMinusOne());
		Assert.assertTrue(Rational.MINUS_ONE.isMinusOne());
		Assert.assertTrue((new Rational(-1)).isMinusOne());
		Assert.assertTrue(!(new Rational(77)).isMinusOne());
		Assert.assertTrue(!(new Rational(-77)).isMinusOne());
		Assert.assertTrue(!(new Rational(3, 5)).isMinusOne());
		Assert.assertTrue(!(new Rational(-3, 5)).isMinusOne());

		Assert.assertTrue(Rational.ZERO.isInteger());
		Assert.assertTrue((new Rational(0)).isInteger());
		Assert.assertTrue(Rational.ONE.isInteger());
		Assert.assertTrue((new Rational(1)).isInteger());
		Assert.assertTrue(Rational.MINUS_ONE.isInteger());
		Assert.assertTrue((new Rational(-1)).isInteger());
		Assert.assertTrue((new Rational(77)).isInteger());
		Assert.assertTrue((new Rational(-77)).isInteger());
		Assert.assertTrue(!(new Rational(3, 5)).isInteger());
		Assert.assertTrue(!(new Rational(-3, 5)).isInteger());
	}
	
	@Test
	public void testStringDot() {
		Assert.assertTrue((new Rational("1234.5678")).toStringDot(4).equals("1234.5678"));
		Assert.assertTrue((new Rational("1234.5678")).toStringDot(5).equals("1234.56780"));
		Assert.assertTrue((new Rational("1234.5678")).toStringDot(6).equals("1234.567800"));
		Assert.assertTrue((new Rational("1234.5678")).toStringDot(3).equals("1234.568"));
		Assert.assertTrue((new Rational("1234.5678")).toStringDot(2).equals("1234.57"));
		Assert.assertTrue((new Rational("1234.5678")).toStringDot(1).equals("1234.6"));
		Assert.assertTrue((new Rational("1234.5678")).toStringDot(0).equals("1235"));
		Assert.assertTrue((new Rational("1234.5678")).toStringDot(-1).equals("1230"));
		Assert.assertTrue((new Rational("1234.5678")).toStringDot(-2).equals("1200"));
		Assert.assertTrue((new Rational("1234.5678")).toStringDot(-3).equals("1000"));
		Assert.assertTrue((new Rational("1234.5678")).toStringDot(-4).equals("0"));
		Assert.assertTrue((new Rational("1234.5678")).toStringDot(-5).equals("0"));
		Assert.assertTrue((new Rational("1234.5678")).toStringDot(-6).equals("0"));

		Assert.assertTrue((new Rational("8765.4321")).toStringDot(-2).equals("8800"));
		Assert.assertTrue((new Rational("0.0148")).toStringDot(6).equals("0.014800"));
		Assert.assertTrue((new Rational("0.0148")).toStringDot(4).equals("0.0148"));
		Assert.assertTrue((new Rational("0.0148")).toStringDot(3).equals("0.015"));
		Assert.assertTrue((new Rational("0.0148")).toStringDot(2).equals("0.01"));
		Assert.assertTrue((new Rational("0.0148")).toStringDot(1).equals("0.0"));
		Assert.assertTrue((new Rational("0.001")).toStringDot(4).equals("0.0010"));
		Assert.assertTrue((new Rational("0.001")).toStringDot(3).equals("0.001"));
		Assert.assertTrue((new Rational("0.001")).toStringDot(2).equals("0.00"));
		Assert.assertTrue((new Rational("0.001")).toStringDot(1).equals("0.0"));
		Assert.assertTrue((new Rational("0.001")).toStringDot(0).equals("0"));
		Assert.assertTrue((new Rational("0.001")).toStringDot(-1).equals("0"));
		Assert.assertTrue((new Rational("0.001")).toStringDot(-2).equals("0"));

		Assert.assertTrue((new Rational("-1234.5678")).toStringDot(4).equals("-1234.5678"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringDot(5).equals("-1234.56780"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringDot(6).equals("-1234.567800"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringDot(3).equals("-1234.568"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringDot(2).equals("-1234.57"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringDot(1).equals("-1234.6"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringDot(0).equals("-1235"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringDot(-1).equals("-1230"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringDot(-2).equals("-1200"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringDot(-3).equals("-1000"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringDot(-4).equals("0"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringDot(-5).equals("0"));

		Assert.assertTrue((new Rational("0.0")).toStringDot(4).equals("0.0000"));
		Assert.assertTrue((new Rational("0.0")).toStringDot(3).equals("0.000"));
		Assert.assertTrue((new Rational("0.0")).toStringDot(2).equals("0.00"));
		Assert.assertTrue((new Rational("0.0")).toStringDot(1).equals("0.0"));
		Assert.assertTrue((new Rational("0.0")).toStringDot(0).equals("0"));
		Assert.assertTrue((new Rational("0.0")).toStringDot(-1).equals("0"));
		Assert.assertTrue((new Rational("0.0")).toStringDot(-2).equals("0"));
		Assert.assertTrue((new Rational("0.0")).toStringDot(-3).equals("0"));
		Assert.assertTrue((new Rational("0.0")).toStringDot(-4).equals("0"));
	}
	
	@Test
	public void testStringDotRadix() {
		Assert.assertTrue((new Rational("1234.5678", 20)).toStringDot(3, 20).equals("1234.567"));
		Assert.assertTrue((new Rational("abcd.5b7g", 20)).toStringDot(-2, 20).equals("ac00"));
	}
	
	@Test
	public void testStringDotRelative() {
		Assert.assertTrue((new Rational("1234.5678")).toStringDotRelative(8).equals("1234.5678"));
		Assert.assertTrue((new Rational("1234.5678")).toStringDotRelative(9).equals("1234.56780"));
		Assert.assertTrue((new Rational("1234.5678")).toStringDotRelative(10).equals("1234.567800"));
		Assert.assertTrue((new Rational("1234.5678")).toStringDotRelative(7).equals("1234.568"));
		Assert.assertTrue((new Rational("1234.5678")).toStringDotRelative(6).equals("1234.57"));
		Assert.assertTrue((new Rational("1234.5678")).toStringDotRelative(5).equals("1234.6"));
		Assert.assertTrue((new Rational("1234.5678")).toStringDotRelative(4).equals("1235"));
		Assert.assertTrue((new Rational("1234.5678")).toStringDotRelative(3).equals("1230"));
		Assert.assertTrue((new Rational("1234.5678")).toStringDotRelative(2).equals("1200"));
		Assert.assertTrue((new Rational("1234.5678")).toStringDotRelative(1).equals("1000"));
		Assert.assertTrue((new Rational("1234.5678")).toStringDotRelative(0).equals("0"));
		Assert.assertTrue((new Rational("1234.5678")).toStringDotRelative(-1).equals("0"));
		Assert.assertTrue((new Rational("1234.5678")).toStringDotRelative(-2).equals("0"));

		Assert.assertTrue((new Rational("-1234.5678")).toStringDotRelative(8).equals("-1234.5678"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringDotRelative(9).equals("-1234.56780"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringDotRelative(10).equals("-1234.567800"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringDotRelative(7).equals("-1234.568"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringDotRelative(6).equals("-1234.57"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringDotRelative(5).equals("-1234.6"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringDotRelative(4).equals("-1235"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringDotRelative(3).equals("-1230"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringDotRelative(2).equals("-1200"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringDotRelative(1).equals("-1000"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringDotRelative(0).equals("0"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringDotRelative(-1).equals("0"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringDotRelative(-2).equals("0"));

		Assert.assertTrue((new Rational("0.00012345678")).toStringDotRelative(8).equals("0.00012345678"));
		Assert.assertTrue((new Rational("0.00012345678")).toStringDotRelative(9).equals("0.000123456780"));
		Assert.assertTrue((new Rational("0.00012345678")).toStringDotRelative(10).equals("0.0001234567800"));
		Assert.assertTrue((new Rational("0.00012345678")).toStringDotRelative(7).equals("0.0001234568"));
		Assert.assertTrue((new Rational("0.00012345678")).toStringDotRelative(6).equals("0.000123457"));
		Assert.assertTrue((new Rational("0.00012345678")).toStringDotRelative(5).equals("0.00012346"));
		Assert.assertTrue((new Rational("0.00012345678")).toStringDotRelative(4).equals("0.0001235"));
		Assert.assertTrue((new Rational("0.00012345678")).toStringDotRelative(3).equals("0.000123"));
		Assert.assertTrue((new Rational("0.00012345678")).toStringDotRelative(2).equals("0.00012"));
		Assert.assertTrue((new Rational("0.00012345678")).toStringDotRelative(1).equals("0.0001"));
		Assert.assertTrue((new Rational("0.00012345678")).toStringDotRelative(0).equals("0"));
		Assert.assertTrue((new Rational("0.00012345678")).toStringDotRelative(-1).equals("0"));
		Assert.assertTrue((new Rational("0.00012345678")).toStringDotRelative(-2).equals("0"));

		Assert.assertTrue((new Rational("-0.00012345678")).toStringDotRelative(8).equals("-0.00012345678"));
		Assert.assertTrue((new Rational("-0.00012345678")).toStringDotRelative(9).equals("-0.000123456780"));
		Assert.assertTrue((new Rational("-0.00012345678")).toStringDotRelative(10).equals("-0.0001234567800"));
		Assert.assertTrue((new Rational("-0.00012345678")).toStringDotRelative(7).equals("-0.0001234568"));
		Assert.assertTrue((new Rational("-0.00012345678")).toStringDotRelative(6).equals("-0.000123457"));
		Assert.assertTrue((new Rational("-0.00012345678")).toStringDotRelative(5).equals("-0.00012346"));
		Assert.assertTrue((new Rational("-0.00012345678")).toStringDotRelative(4).equals("-0.0001235"));
		Assert.assertTrue((new Rational("-0.00012345678")).toStringDotRelative(3).equals("-0.000123"));
		Assert.assertTrue((new Rational("-0.00012345678")).toStringDotRelative(2).equals("-0.00012"));
		Assert.assertTrue((new Rational("-0.00012345678")).toStringDotRelative(1).equals("-0.0001"));
		Assert.assertTrue((new Rational("-0.00012345678")).toStringDotRelative(0).equals("0"));
		Assert.assertTrue((new Rational("-0.00012345678")).toStringDotRelative(-1).equals("0"));
		Assert.assertTrue((new Rational("-0.00012345678")).toStringDotRelative(-2).equals("0"));

		Assert.assertTrue((new Rational("8765.4321")).toStringDotRelative(8).equals("8765.4321"));
		Assert.assertTrue((new Rational("8765.4321")).toStringDotRelative(9).equals("8765.43210"));
		Assert.assertTrue((new Rational("8765.4321")).toStringDotRelative(10).equals("8765.432100"));
		Assert.assertTrue((new Rational("8765.4321")).toStringDotRelative(7).equals("8765.432"));
		Assert.assertTrue((new Rational("8765.4321")).toStringDotRelative(6).equals("8765.43"));
		Assert.assertTrue((new Rational("8765.4321")).toStringDotRelative(5).equals("8765.4"));
		Assert.assertTrue((new Rational("8765.4321")).toStringDotRelative(4).equals("8765"));
		Assert.assertTrue((new Rational("8765.4321")).toStringDotRelative(3).equals("8770"));
		Assert.assertTrue((new Rational("8765.4321")).toStringDotRelative(2).equals("8800"));
		Assert.assertTrue((new Rational("8765.4321")).toStringDotRelative(1).equals("9000"));
		Assert.assertTrue((new Rational("8765.4321")).toStringDotRelative(0).equals("0"));
		Assert.assertTrue((new Rational("8765.4321")).toStringDotRelative(-1).equals("0"));
		Assert.assertTrue((new Rational("8765.4321")).toStringDotRelative(-2).equals("0"));

		Assert.assertTrue((new Rational("0.0")).toStringDotRelative(4).equals("0"));
		Assert.assertTrue((new Rational("0.0")).toStringDotRelative(3).equals("0"));
		Assert.assertTrue((new Rational("0.0")).toStringDotRelative(2).equals("0"));
		Assert.assertTrue((new Rational("0.0")).toStringDotRelative(1).equals("0"));
		Assert.assertTrue((new Rational("0.0")).toStringDotRelative(0).equals("0"));
		Assert.assertTrue((new Rational("0.0")).toStringDotRelative(-1).equals("0"));
		Assert.assertTrue((new Rational("0.0")).toStringDotRelative(-2).equals("0"));
		Assert.assertTrue((new Rational("0.0")).toStringDotRelative(-3).equals("0"));
		Assert.assertTrue((new Rational("0.0")).toStringDotRelative(-4).equals("0"));

		Assert.assertTrue((new Rational(1499)).toStringDotRelative(1).equals("1000"));
		Assert.assertTrue((new Rational(1500)).toStringDotRelative(1).equals("2000"));
		Assert.assertTrue((new Rational(1501)).toStringDotRelative(1).equals("2000"));
		Assert.assertTrue((new Rational(1001)).toStringDotRelative(1).equals("1000"));
		Assert.assertTrue((new Rational(1000)).toStringDotRelative(1).equals("1000"));
		Assert.assertTrue((new Rational(999)).toStringDotRelative(1).equals("1000"));
		Assert.assertTrue((new Rational(951)).toStringDotRelative(1).equals("1000"));
		Assert.assertTrue((new Rational(950)).toStringDotRelative(1).equals("1000"));
		Assert.assertTrue((new Rational(949)).toStringDotRelative(1).equals("900"));

		// check inverted radix pow cases too
		Assert.assertTrue((new Rational(1, 1)).toStringDotRelative(1).equals("1"));
		Assert.assertTrue((new Rational(1, 2)).toStringDotRelative(1).equals("0.5"));
		Assert.assertTrue((new Rational(1, 3)).toStringDotRelative(1).equals("0.3"));
		Assert.assertTrue((new Rational(1, 5)).toStringDotRelative(1).equals("0.2"));
		Assert.assertTrue((new Rational(1, 10)).toStringDotRelative(1).equals("0.1"));
		Assert.assertTrue((new Rational(1, 20)).toStringDotRelative(1).equals("0.05"));
		Assert.assertTrue((new Rational(1, 30)).toStringDotRelative(1).equals("0.03"));
		Assert.assertTrue((new Rational(1, 50)).toStringDotRelative(1).equals("0.02"));
		Assert.assertTrue((new Rational(1, 100)).toStringDotRelative(1).equals("0.01"));
		Assert.assertTrue((new Rational(1, 200)).toStringDotRelative(1).equals("0.005"));
		Assert.assertTrue((new Rational(1, 300)).toStringDotRelative(1).equals("0.003"));
		Assert.assertTrue((new Rational(1, 500)).toStringDotRelative(1).equals("0.002"));
		Assert.assertTrue((new Rational(1, 1000)).toStringDotRelative(1).equals("0.001"));
		Assert.assertTrue((new Rational(1, 4)).toStringDotRelative(1).equals("0.3"));
		Assert.assertTrue((new Rational(1, 11)).toStringDotRelative(1).equals("0.09"));
		Assert.assertTrue((new Rational(1, 12)).toStringDotRelative(1).equals("0.08"));
		Assert.assertTrue((new Rational(1, 31)).toStringDotRelative(1).equals("0.03"));
		Assert.assertTrue((new Rational(1, 99)).toStringDotRelative(1).equals("0.01"));
		Assert.assertTrue((new Rational("0.0100001")).toStringDotRelative(1).equals("0.01"));

		// intermediately excess zeros
		Assert.assertTrue((new Rational(1, 101)).toStringDotRelative(1).equals("0.01"));
		Assert.assertTrue((new Rational("0.0099999")).toStringDotRelative(1).equals("0.01"));
	}
	
	@Test
	public void testExponentRepresentation() {
		Assert.assertTrue((new Rational("1234.5678")).toStringExponent(8).equals("1.2345678E3"));
		Assert.assertTrue((new Rational("1234.5678")).toStringExponent(4).equals("1.235E3"));
		Assert.assertTrue((new Rational("1234.5678")).toStringExponent(2).equals("1.2E3"));
		Assert.assertTrue((new Rational("1234.5678")).toStringExponent(1).equals("1E3"));
		Assert.assertTrue((new Rational("1234.5678")).toStringExponent(0).equals("0"));
		Assert.assertTrue((new Rational("0.00012345678")).toStringExponent(8).equals("1.2345678E-4"));
		Assert.assertTrue((new Rational("0.00012345678")).toStringExponent(4).equals("1.235E-4"));
		Assert.assertTrue((new Rational("0.00012345678")).toStringExponent(2).equals("1.2E-4"));
		Assert.assertTrue((new Rational("0.00012345678")).toStringExponent(1).equals("1E-4"));
		Assert.assertTrue((new Rational("0.00012345678")).toStringExponent(0).equals("0"));

		Assert.assertTrue((new Rational("-1234.5678")).toStringExponent(8).equals("-1.2345678E3"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringExponent(4).equals("-1.235E3"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringExponent(2).equals("-1.2E3"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringExponent(1).equals("-1E3"));
		Assert.assertTrue((new Rational("-1234.5678")).toStringExponent(0).equals("0"));
		Assert.assertTrue((new Rational("-0.00012345678")).toStringExponent(8).equals("-1.2345678E-4"));
		Assert.assertTrue((new Rational("-0.00012345678")).toStringExponent(4).equals("-1.235E-4"));
		Assert.assertTrue((new Rational("-0.00012345678")).toStringExponent(2).equals("-1.2E-4"));
		Assert.assertTrue((new Rational("-0.00012345678")).toStringExponent(1).equals("-1E-4"));
		Assert.assertTrue((new Rational("-0.00012345678")).toStringExponent(0).equals("0"));

		Assert.assertTrue((new Rational("1234")).toStringExponent(8).equals("1.234E3"));
		Assert.assertTrue((new Rational("1234")).toStringExponent(4).equals("1.234E3"));
		Assert.assertTrue((new Rational("1234")).toStringExponent(2).equals("1.2E3"));
		Assert.assertTrue((new Rational("1234")).toStringExponent(1).equals("1E3"));
		Assert.assertTrue((new Rational("1234")).toStringExponent(0).equals("0"));

		Assert.assertTrue((new Rational("10")).toStringExponent(6).equals("1E1"));
		Assert.assertTrue((new Rational("1")).toStringExponent(6).equals("1"));
		Assert.assertTrue((new Rational("0")).toStringExponent(6).equals("0"));
		Assert.assertTrue((new Rational("0.1")).toStringExponent(6).equals("1E-1"));
		Assert.assertTrue((new Rational("10")).toStringExponent(1).equals("1E1"));
		Assert.assertTrue((new Rational("1")).toStringExponent(1).equals("1"));
		Assert.assertTrue((new Rational("0")).toStringExponent(1).equals("0"));
		Assert.assertTrue((new Rational("0.1")).toStringExponent(1).equals("1E-1"));

		Assert.assertTrue(Rational.valueOf("2E222").toStringExponent(4).equals("2E222"));
		Assert.assertTrue(Rational.valueOf("-2E222").toStringExponent(4).equals("-2E222"));
		Assert.assertTrue(Rational.valueOf("2E-222").toStringExponent(4).equals("2E-222"));
		Assert.assertTrue(Rational.valueOf("-2E-222").toStringExponent(4).equals("-2E-222"));

		// non-default radix
		Assert.assertTrue(new Rational("2E222", 5).toStringExponent(4, 5).equals("2E222"));
		Assert.assertTrue(new Rational("-2E222", 5).toStringExponent(4, 5).equals("-2E222"));
		Assert.assertTrue(new Rational("2E-222", 5).toStringExponent(4, 5).equals("2E-222"));
		Assert.assertTrue(new Rational("-2E-222", 5).toStringExponent(4, 5).equals("-2E-222"));
	}
	
	@Test
	public void testAdd() {
		Assert.assertTrue((new Rational(3, 5)).add(new Rational(7, 11)).toString().equals("68/55"));
		Assert.assertTrue((new Rational(3, 5)).add(new Rational(-7, 11)).toString().equals("-2/55"));
		Assert.assertTrue((new Rational(-3, 5)).add(new Rational(7, 11)).toString().equals("2/55"));
		Assert.assertTrue((new Rational(-3, 5)).add(new Rational(-7, 11)).toString().equals("-68/55"));
		// same denominator
		Assert.assertTrue((new Rational(3, 5)).add(new Rational(1, 5)).toString().equals("4/5"));
		// with integers
		Assert.assertTrue((new Rational(3, 5)).add(new Rational(1)).toString().equals("8/5"));
		Assert.assertTrue((new Rational(2)).add(new Rational(3, 5)).toString().equals("13/5"));
		// zero
		Assert.assertTrue((new Rational(3, 5)).add(Rational.ZERO).toString().equals("3/5"));
		Assert.assertTrue(Rational.ZERO.add(new Rational(3, 5)).toString().equals("3/5"));
	}
	
	@Test
	public void testSubtract() {
		Assert.assertTrue((new Rational(3, 5)).subtract(new Rational(7, 11)).toString().equals("-2/55"));
		Assert.assertTrue((new Rational(3, 5)).subtract(new Rational(-7, 11)).toString().equals("68/55"));
		Assert.assertTrue((new Rational(-3, 5)).subtract(new Rational(7, 11)).toString().equals("-68/55"));
		Assert.assertTrue((new Rational(-3, 5)).subtract(new Rational(-7, 11)).toString().equals("2/55"));
		// same denominator
		Assert.assertTrue((new Rational(3, 5)).subtract(new Rational(1, 5)).toString().equals("2/5"));
		// with integers
		Assert.assertTrue((new Rational(3, 5)).subtract(new Rational(1)).toString().equals("-2/5"));
		Assert.assertTrue((new Rational(2)).subtract(new Rational(3, 5)).toString().equals("7/5"));
		// zero
		Assert.assertTrue((new Rational(3, 5)).subtract(Rational.ZERO).toString().equals("3/5"));
		Assert.assertTrue(Rational.ZERO.subtract(new Rational(3, 5)).toString().equals("-3/5"));

		// normalization, e.g after subtract
		// "Rational normalization"
		Assert.assertTrue((new Rational(7, 5)).subtract(new Rational(2, 5)).compareTo(1) == 0);
		Assert.assertTrue((new Rational(7, 5)).subtract(new Rational(7, 5)).compareTo(0) == 0);
		Assert.assertTrue((new Rational(7, 5)).subtract(new Rational(12, 5)).compareTo(-1) == 0);
	}
	
	@Test
	public void testMultiply() {
		// "Rational multiply"
		Assert.assertTrue((new Rational(3, 5)).multiply(new Rational(7, 11)).toString().equals("21/55"));
		Assert.assertTrue((new Rational(3, 5)).multiply(new Rational(-7, 11)).toString().equals("-21/55"));
		Assert.assertTrue((new Rational(-3, 5)).multiply(new Rational(7, 11)).toString().equals("-21/55"));
		Assert.assertTrue((new Rational(-3, 5)).multiply(new Rational(-7, 11)).toString().equals("21/55"));
		Assert.assertTrue((new Rational(3, 5)).multiply(7).toString().equals("21/5"));
		Assert.assertTrue((new Rational(-3, 5)).multiply(7).toString().equals("-21/5"));
		Assert.assertTrue((new Rational(3, 5)).multiply(-7).toString().equals("-21/5"));
		Assert.assertTrue((new Rational(-3, 5)).multiply(-7).toString().equals("21/5"));

		// multiply() with integers, 0, etc. (some repetitions too)
		// "Rational multiply"
		Assert.assertTrue((new Rational(3, 5)).multiply(new Rational(7, 1)).toString().equals("21/5"));
		Assert.assertTrue((new Rational(3, 5)).multiply(new Rational(1, 7)).toString().equals("3/35"));
		Assert.assertTrue((new Rational(3, 1)).multiply(new Rational(7, 11)).toString().equals("21/11"));
		Assert.assertTrue((new Rational(3, 5)).multiply(new Rational(0)).toString().equals("0"));
		Assert.assertTrue((new Rational(0)).multiply(new Rational(3, 5)).toString().equals("0"));
		Assert.assertTrue((new Rational(3, 5)).multiply(new Rational(1)).toString().equals("3/5"));
		Assert.assertTrue((new Rational(3, 5)).multiply(new Rational(-1)).toString().equals("-3/5"));
		Assert.assertTrue((new Rational(3, 5)).multiply(new Rational(-1, 3)).toString().equals("-1/5"));

		// special cases (zeroing, negating)
		Assert.assertTrue(Rational.ZERO.multiply(Rational.ZERO).equals(Rational.ZERO));
		Assert.assertTrue(Rational.ZERO.multiply(Rational.ONE).equals(Rational.ZERO));
		Assert.assertTrue(Rational.ONE.multiply(Rational.ZERO).equals(Rational.ZERO));
		Assert.assertTrue(Rational.ZERO.multiply(Rational.MINUS_ONE).equals(Rational.ZERO));
		Assert.assertTrue(Rational.MINUS_ONE.multiply(Rational.ZERO).equals(Rational.ZERO));
		Assert.assertTrue(Rational.ZERO.multiply(new Rational(5)).equals(Rational.ZERO));
		Assert.assertTrue((new Rational(5)).multiply(Rational.ZERO).equals(Rational.ZERO));
		Assert.assertTrue(Rational.ZERO.multiply(new Rational(-5)).equals(Rational.ZERO));
		Assert.assertTrue((new Rational(-5)).multiply(Rational.ZERO).equals(Rational.ZERO));
		Assert.assertTrue(Rational.ONE.multiply(Rational.ONE).equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.multiply(Rational.MINUS_ONE).equals(Rational.MINUS_ONE));
		Assert.assertTrue(Rational.MINUS_ONE.multiply(Rational.ONE).equals(Rational.MINUS_ONE));
		Assert.assertTrue(Rational.ONE.multiply(new Rational(5)).equals(new Rational(5)));
		Assert.assertTrue((new Rational(5)).multiply(Rational.ONE).equals(new Rational(5)));
		Assert.assertTrue(Rational.ONE.multiply(new Rational(-5)).equals(new Rational(-5)));
		Assert.assertTrue((new Rational(-5)).multiply(Rational.ONE).equals(new Rational(-5)));
		Assert.assertTrue(Rational.MINUS_ONE.multiply(Rational.MINUS_ONE).equals(Rational.ONE));
		Assert.assertTrue(Rational.MINUS_ONE.multiply(new Rational(5)).equals(new Rational(-5)));
		Assert.assertTrue((new Rational(5)).multiply(Rational.MINUS_ONE).equals(new Rational(-5)));
		Assert.assertTrue(Rational.MINUS_ONE.multiply(new Rational(-5)).equals(new Rational(5)));
		Assert.assertTrue((new Rational(-5)).multiply(Rational.MINUS_ONE).equals(new Rational(5)));
	}
	
	@Test
	public void testDivide() {
		Assert.assertTrue((new Rational(3, 5)).divide(new Rational(7, 11)).toString().equals("33/35"));
		Assert.assertTrue((new Rational(3, 5)).divide(new Rational(-7, 11)).toString().equals("-33/35"));
		Assert.assertTrue((new Rational(-3, 5)).divide(new Rational(7, 11)).toString().equals("-33/35"));
		Assert.assertTrue((new Rational(-3, 5)).divide(new Rational(-7, 11)).toString().equals("33/35"));
		Assert.assertTrue((new Rational(3, 5)).divide(7).toString().equals("3/35"));
		Assert.assertTrue((new Rational(-3, 5)).divide(7).toString().equals("-3/35"));
		Assert.assertTrue((new Rational(3, 5)).divide(-7).toString().equals("-3/35"));
		Assert.assertTrue((new Rational(-3, 5)).divide(-7).toString().equals("3/35"));

		Assert.assertTrue((new Rational(0)).divide(new Rational(7, 11)).toString().equals("0"));
	}
	
	@Test(expected = ArithmeticException.class)
	public void testIllegalDivide1() {
		new Rational(3, 5).divide(new Rational(0));
	}
	
	@Test(expected = ArithmeticException.class)
	public void testIllegalDivide2() {
		new Rational(-3, 5).divide(new Rational(0));
	}
	
	@Test(expected = ArithmeticException.class)
	public void testIllegalDivide3() {
		new Rational(3, 5).divide(0);
	}
	
	@Test(expected = ArithmeticException.class)
	public void testIllegalDivide4() {
		new Rational(-3, 5).divide(0);
	}
	
	@Test(expected = ArithmeticException.class)
	public void testIllegalDivide5() {
		new Rational(0).divide(0);
	}
	
	@Test
	public void testPowIntExponent() {
		Assert.assertTrue((new Rational(3, 5)).pow(7).toString().equals("2187/78125"));
		Assert.assertTrue((new Rational(-3, 5)).pow(7).toString().equals("-2187/78125"));
		Assert.assertTrue((new Rational(3, 5)).pow(-7).toString().equals("78125/2187"));
		Assert.assertTrue((new Rational(-3, 5)).pow(-7).toString().equals("-78125/2187"));
		Assert.assertTrue((new Rational(3, 5)).pow(6).toString().equals("729/15625"));
		Assert.assertTrue((new Rational(-3, 5)).pow(6).toString().equals("729/15625"));
		Assert.assertTrue((new Rational(3, 5)).pow(0).toString().equals("1"));
		Assert.assertTrue((new Rational(-3, 5)).pow(0).toString().equals("1"));
		Assert.assertTrue((new Rational(0)).pow(1).toString().equals("0"));
		Assert.assertTrue((new Rational(1)).pow(0).toString().equals("1"));

		Assert.assertTrue((new Rational(3, 5)).pow(0).equals(Rational.ONE));
		Assert.assertTrue((new Rational(3, 5)).pow(1).equals(new Rational(3, 5)));
		Assert.assertTrue((new Rational(3, 5)).pow(-1).equals((new Rational(3, 5)).invert()));
		Assert.assertTrue((new Rational(2)).pow(2).equals(new Rational(4)));

		// special cases
		Assert.assertTrue(Rational.ZERO.pow(1).equals(Rational.ZERO));
		Assert.assertTrue(Rational.ZERO.pow(2).equals(Rational.ZERO));
		Assert.assertTrue(Rational.ZERO.pow(3).equals(Rational.ZERO));
		Assert.assertTrue(Rational.ZERO.pow(4).equals(Rational.ZERO));
		Assert.assertTrue(Rational.ONE.pow(0).equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.pow(1).equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.pow(2).equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.pow(3).equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.pow(4).equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.pow(-1).equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.pow(-2).equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.pow(-3).equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.pow(-4).equals(Rational.ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(0).equals(Rational.ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(1).equals(Rational.MINUS_ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(2).equals(Rational.ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(3).equals(Rational.MINUS_ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(4).equals(Rational.ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(-1).equals(Rational.MINUS_ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(-2).equals(Rational.ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(-3).equals(Rational.MINUS_ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(-4).equals(Rational.ONE));
	}
	
	@Test
	public void testPowIntRationalNumberExponent() {
		Assert.assertTrue((new Rational(3, 5)).pow(new Rational(7)).toString().equals("2187/78125"));
		Assert.assertTrue((new Rational(-3, 5)).pow(new Rational(7)).toString().equals("-2187/78125"));
		Assert.assertTrue((new Rational(3, 5)).pow(new Rational(-7)).toString().equals("78125/2187"));
		Assert.assertTrue((new Rational(-3, 5)).pow(new Rational(-7)).toString().equals("-78125/2187"));
		Assert.assertTrue((new Rational(3, 5)).pow(new Rational(6)).toString().equals("729/15625"));
		Assert.assertTrue((new Rational(-3, 5)).pow(new Rational(6)).toString().equals("729/15625"));
		Assert.assertTrue((new Rational(3, 5)).pow(new Rational(0)).toString().equals("1"));
		Assert.assertTrue((new Rational(-3, 5)).pow(new Rational(0)).toString().equals("1"));
		Assert.assertTrue((new Rational(0)).pow(new Rational(1)).toString().equals("0"));
		Assert.assertTrue((new Rational(1)).pow(new Rational(0)).toString().equals("1"));

		Assert.assertTrue((new Rational(3, 5)).pow(new Rational(0)).equals(Rational.ONE));
		Assert.assertTrue((new Rational(3, 5)).pow(new Rational(1)).equals(new Rational(3, 5)));
		Assert.assertTrue((new Rational(3, 5)).pow(new Rational(-1)).equals((new Rational(3, 5)).invert()));
		Assert.assertTrue((new Rational(2)).pow(new Rational(2)).equals(new Rational(4)));

		// special cases
		Assert.assertTrue(Rational.ZERO.pow(new Rational(1)).equals(Rational.ZERO));
		Assert.assertTrue(Rational.ZERO.pow(new Rational(2)).equals(Rational.ZERO));
		Assert.assertTrue(Rational.ZERO.pow(new Rational(3)).equals(Rational.ZERO));
		Assert.assertTrue(Rational.ZERO.pow(new Rational(4)).equals(Rational.ZERO));
		Assert.assertTrue(Rational.ONE.pow(new Rational(0)).equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.pow(new Rational(1)).equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.pow(new Rational(2)).equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.pow(new Rational(3)).equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.pow(new Rational(4)).equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.pow(new Rational(-1)).equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.pow(new Rational(-2)).equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.pow(new Rational(-3)).equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.pow(new Rational(-4)).equals(Rational.ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(new Rational(0)).equals(Rational.ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(new Rational(1)).equals(Rational.MINUS_ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(new Rational(2)).equals(Rational.ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(new Rational(3)).equals(Rational.MINUS_ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(new Rational(4)).equals(Rational.ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(new Rational(-1)).equals(Rational.MINUS_ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(new Rational(-2)).equals(Rational.ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(new Rational(-3)).equals(Rational.MINUS_ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(new Rational(-4)).equals(Rational.ONE));
	}
	
	@Test
	public void testPowFractionalRationalNumberExponent() {
		double delta = 0.000000000001;
		Assert.assertEquals("64^(1/2)", Math.pow(64.0, 1.0/2.0), (new Rational(64)).pow(new Rational(1, 2)).doubleValue(), delta);
		Assert.assertEquals("64^(1/3)", Math.pow(64.0, 1.0/3.0), (new Rational(64)).pow(new Rational(1, 3)).doubleValue(), delta);
	}
	
	@Test
	public void testPowBigIntegerNumberExponent() {
		Assert.assertTrue((new Rational(3, 5)).pow(new Rational(7).bigIntegerValue()).toString().equals("2187/78125"));
		Assert.assertTrue((new Rational(-3, 5)).pow(new Rational(7).bigIntegerValue()).toString().equals("-2187/78125"));
		Assert.assertTrue((new Rational(3, 5)).pow(new Rational(-7).bigIntegerValue()).toString().equals("78125/2187"));
		Assert.assertTrue((new Rational(-3, 5)).pow(new Rational(-7).bigIntegerValue()).toString().equals("-78125/2187"));
		Assert.assertTrue((new Rational(3, 5)).pow(new Rational(6).bigIntegerValue()).toString().equals("729/15625"));
		Assert.assertTrue((new Rational(-3, 5)).pow(new Rational(6).bigIntegerValue()).toString().equals("729/15625"));
		Assert.assertTrue((new Rational(3, 5)).pow(new Rational(0).bigIntegerValue()).toString().equals("1"));
		Assert.assertTrue((new Rational(-3, 5)).pow(new Rational(0).bigIntegerValue()).toString().equals("1"));
		Assert.assertTrue((new Rational(0)).pow(new Rational(1).bigIntegerValue()).toString().equals("0"));
		Assert.assertTrue((new Rational(1)).pow(new Rational(0).bigIntegerValue()).toString().equals("1"));

		Assert.assertTrue((new Rational(3, 5)).pow(new Rational(0).bigIntegerValue()).equals(Rational.ONE));
		Assert.assertTrue((new Rational(3, 5)).pow(new Rational(1).bigIntegerValue()).equals(new Rational(3, 5)));
		Assert.assertTrue((new Rational(3, 5)).pow(new Rational(-1).bigIntegerValue()).equals((new Rational(3, 5)).invert()));
		Assert.assertTrue((new Rational(2)).pow(new Rational(2).bigIntegerValue()).equals(new Rational(4)));

		// special cases
		Assert.assertTrue(Rational.ZERO.pow(new Rational(1).bigIntegerValue()).equals(Rational.ZERO));
		Assert.assertTrue(Rational.ZERO.pow(new Rational(2).bigIntegerValue()).equals(Rational.ZERO));
		Assert.assertTrue(Rational.ZERO.pow(new Rational(3).bigIntegerValue()).equals(Rational.ZERO));
		Assert.assertTrue(Rational.ZERO.pow(new Rational(4).bigIntegerValue()).equals(Rational.ZERO));
		Assert.assertTrue(Rational.ONE.pow(new Rational(0).bigIntegerValue()).equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.pow(new Rational(1).bigIntegerValue()).equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.pow(new Rational(2).bigIntegerValue()).equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.pow(new Rational(3).bigIntegerValue()).equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.pow(new Rational(4).bigIntegerValue()).equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.pow(new Rational(-1).bigIntegerValue()).equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.pow(new Rational(-2).bigIntegerValue()).equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.pow(new Rational(-3).bigIntegerValue()).equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.pow(new Rational(-4).bigIntegerValue()).equals(Rational.ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(new Rational(0).bigIntegerValue()).equals(Rational.ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(new Rational(1).bigIntegerValue()).equals(Rational.MINUS_ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(new Rational(2).bigIntegerValue()).equals(Rational.ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(new Rational(3).bigIntegerValue()).equals(Rational.MINUS_ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(new Rational(4).bigIntegerValue()).equals(Rational.ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(new Rational(-1).bigIntegerValue()).equals(Rational.MINUS_ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(new Rational(-2).bigIntegerValue()).equals(Rational.ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(new Rational(-3).bigIntegerValue()).equals(Rational.MINUS_ONE));
		Assert.assertTrue(Rational.MINUS_ONE.pow(new Rational(-4).bigIntegerValue()).equals(Rational.ONE));
	}
	
	@Test(expected = ArithmeticException.class)
	public void testIllegalPow1() {
		(new Rational(0)).pow(0);
	}
	
	@Test(expected = ArithmeticException.class)
	public void testIllegalPow2() {
		Rational.ZERO.pow(-1);
	}
	
	@Test(expected = ArithmeticException.class)
	public void testIllegalPow3() {
		Rational.ZERO.pow(-2);
	}
	
	@Test
	public void testRemainder() {
		Assert.assertTrue((new Rational(5)).remainder(new Rational(3)).equals(new Rational(2)));
		Assert.assertTrue((new Rational(-5)).remainder(new Rational(3)).equals(new Rational(-2)));
		Assert.assertTrue((new Rational(5)).remainder(new Rational(-3)).equals(new Rational(2)));
		Assert.assertTrue((new Rational(-5)).remainder(new Rational(-3)).equals(new Rational(-2)));
		Assert.assertTrue((new Rational(0)).remainder(new Rational(1)).equals(new Rational(0)));
		Assert.assertTrue((new Rational("5.6")).remainder(new Rational("1.8")).equals(new Rational(1, 5)));
		Assert.assertTrue((new Rational("-5.6")).remainder(new Rational("1.8")).equals(new Rational(-1, 5)));
		Assert.assertTrue((new Rational("5.6")).remainder(new Rational("-1.8")).equals(new Rational(1, 5)));
		Assert.assertTrue((new Rational("-5.6")).remainder(new Rational("-1.8")).equals(new Rational(-1, 5)));
		Assert.assertTrue((new Rational("1")).remainder(new Rational("0.13")).equals(new Rational("0.09")));
	}
	
	@Test(expected = ArithmeticException.class)
	public void testIllegalRemainder1() {
		Rational.ONE.remainder(0);
	}
	
	@Test(expected = ArithmeticException.class)
	public void testIllegalRemainder2() {
		Rational.ZERO.remainder(0);
	}
	
	@Test
	public void testMod() {
		Assert.assertTrue((new Rational(5)).mod(new Rational(3)).equals(new Rational(2)));
		Assert.assertTrue((new Rational(-5)).mod(new Rational(3)).equals(new Rational(1)));
		Assert.assertTrue((new Rational(5)).mod(new Rational(-3)).equals(new Rational(-1)));
		Assert.assertTrue((new Rational(-5)).mod(new Rational(-3)).equals(new Rational(-2)));
		Assert.assertTrue((new Rational(0)).mod(new Rational(1)).equals(new Rational(0)));
		Assert.assertTrue((new Rational("5.6")).mod(new Rational("1.8")).equals(new Rational(1, 5)));
		Assert.assertTrue((new Rational("-5.6")).mod(new Rational("1.8")).equals(new Rational(8, 5)));
		Assert.assertTrue((new Rational("5.6")).mod(new Rational("-1.8")).equals(new Rational(-8, 5)));
		Assert.assertTrue((new Rational("-5.6")).mod(new Rational("-1.8")).equals(new Rational(-1, 5)));
		Assert.assertTrue((new Rational("1")).mod(new Rational("0.13")).equals(new Rational("0.09")));
	}
	
	@Test(expected = ArithmeticException.class)
	public void testIllegalMod1() {
		Rational.ONE.mod(0);
	}
	
	@Test(expected = ArithmeticException.class)
	public void testIllegalMod2() {
		Rational.ZERO.mod(0);
	}
	
	@Test
	public void testSignum() {
		Assert.assertTrue((new Rational(0)).signum() == 0);
		Assert.assertTrue((new Rational(1)).signum() == 1);
		Assert.assertTrue((new Rational(-1)).signum() == -1);
		Assert.assertTrue((new Rational(2)).signum() == 1);
		Assert.assertTrue((new Rational(-2)).signum() == -1);
		Assert.assertTrue((new Rational(3, 5)).signum() == 1);
		Assert.assertTrue((new Rational(-3, 5)).signum() == -1);
	}
	
	@Test
	public void testAbs() {
		Assert.assertTrue((new Rational(0)).abs().toString().equals("0"));
		Assert.assertTrue((new Rational(3, 5)).abs().toString().equals("3/5"));
		Assert.assertTrue((new Rational(-3, 5)).abs().toString().equals("3/5"));
		Assert.assertTrue((new Rational(1)).abs().toString().equals("1"));
		Assert.assertTrue((new Rational(-1)).abs().toString().equals("1"));
		Assert.assertTrue(Rational.ZERO.abs().equals(Rational.ZERO));
		Assert.assertTrue(Rational.ONE.abs().equals(Rational.ONE));
		Assert.assertTrue(Rational.MINUS_ONE.abs().equals(Rational.ONE));
	}
	
	@Test
	public void testNegate() {
		Assert.assertTrue((new Rational(0)).negate().toString().equals("0"));
		Assert.assertTrue((new Rational(1)).negate().toString().equals("-1"));
		Assert.assertTrue((new Rational(-1)).negate().toString().equals("1"));
		Assert.assertTrue((new Rational(3, 5)).negate().toString().equals("-3/5"));
		Assert.assertTrue((new Rational(-3, 5)).negate().toString().equals("3/5"));
		Assert.assertTrue(Rational.ZERO.negate().equals(Rational.ZERO));
		Assert.assertTrue(Rational.ONE.negate().equals(Rational.MINUS_ONE));
		Assert.assertTrue(Rational.MINUS_ONE.negate().equals(Rational.ONE));
	}
	
	@Test
	public void testInvert() {
		Assert.assertTrue((new Rational(3, 5)).invert().toString().equals("5/3"));
		Assert.assertTrue((new Rational(-3, 5)).invert().toString().equals("-5/3"));
		Assert.assertTrue((new Rational(11, 7)).invert().toString().equals("7/11"));
		Assert.assertTrue((new Rational(-11, 7)).invert().toString().equals("-7/11"));
		Assert.assertTrue((new Rational(1)).invert().toString().equals("1"));
		Assert.assertTrue((new Rational(-1)).invert().toString().equals("-1"));
		Assert.assertTrue((new Rational(2)).invert().toString().equals("1/2"));
		Assert.assertTrue(Rational.ONE.invert().equals(Rational.ONE));
		Assert.assertTrue(Rational.MINUS_ONE.invert().equals(Rational.MINUS_ONE));
	}
	
	@Test(expected = ArithmeticException.class)
	public void testIllegalInvert() {
		Rational.ZERO.invert();
	}
	
	@Test
	public void testMin() {
		Assert.assertTrue((new Rational(3, 5)).min(new Rational(7, 11)).toString().equals("3/5"));
		Assert.assertTrue((new Rational(-3, 5)).min(new Rational(7, 11)).toString().equals("-3/5"));
		Assert.assertTrue((new Rational(3, 5)).min(new Rational(-7, 11)).toString().equals("-7/11"));
		Assert.assertTrue((new Rational(-3, 5)).min(new Rational(-7, 11)).toString().equals("-7/11"));
		Assert.assertTrue((new Rational(7, 11)).min(new Rational(3, 5)).toString().equals("3/5"));
	}
	
	@Test
	public void testMax() {
		Assert.assertTrue((new Rational(3, 5)).max(new Rational(7, 11)).toString().equals("7/11"));
		Assert.assertTrue((new Rational(-3, 5)).max(new Rational(7, 11)).toString().equals("7/11"));
		Assert.assertTrue((new Rational(3, 5)).max(new Rational(-7, 11)).toString().equals("3/5"));
		Assert.assertTrue((new Rational(-3, 5)).max(new Rational(-7, 11)).toString().equals("-3/5"));
		Assert.assertTrue((new Rational(7, 11)).max(new Rational(3, 5)).toString().equals("7/11"));
	}
	
	@Test
	public void testEquals() {
		// "Rational [polymorph] equals"
		Assert.assertTrue(Rational.ONE.equals(Rational.ONE));
		Assert.assertTrue(Rational.ONE.equals(new Rational(3, 3)));
		Assert.assertTrue(Rational.ONE.equals(new Rational(987654, 987654)));
		Assert.assertTrue(!Rational.ONE.equals(new Rational(987653, 987654)));
		Assert.assertTrue(Rational.ZERO.equals(new Rational(0, 987654)));
		Assert.assertTrue(Rational.MINUS_ONE.equals(new Rational(-987654, 987654)));
		Assert.assertTrue(!Rational.MINUS_ONE.equals(new Rational(-987653, 987654)));
		Assert.assertTrue((new Rational(3, 5)).equals(new Rational(3, 5)));
		Assert.assertTrue(!(new Rational(3, 5)).equals(new Rational(5, 3)));
		Assert.assertTrue(Rational.ZERO.equals(Rational.ZERO));
		Assert.assertTrue(Rational.ONE.equals(Rational.ONE));
		Assert.assertTrue(Rational.MINUS_ONE.equals(Rational.MINUS_ONE));
		Assert.assertTrue(!Rational.ZERO.equals(Rational.ONE));
		Assert.assertTrue(!Rational.ONE.equals(Rational.ZERO));
		Assert.assertTrue(!Rational.ONE.equals(Rational.MINUS_ONE));
		Assert.assertTrue(!Rational.MINUS_ONE.equals(Rational.ONE));

		// following tests address things that changed from earlier version;
		// i.e. will fail with that version
		Assert.assertTrue(!Rational.ZERO.equals(new Integer(0)));
		Assert.assertTrue(!(new Rational(3)).equals(new Integer(3)));
		Assert.assertTrue(!Rational.ZERO.equals(new Long(0)));
		Assert.assertTrue(!(new Rational(3)).equals(new Long(3)));
		Assert.assertTrue(!Rational.ZERO.equals("0"));
		Assert.assertTrue(!(new Rational(3)).equals("3"));

		Assert.assertTrue(!(new Integer(0)).equals(Rational.ZERO));
		Assert.assertTrue(!(new Long(0)).equals(Rational.ZERO));
		Assert.assertTrue(!(new String("0")).equals(Rational.ZERO));

		Assert.assertTrue(!Rational.ONE.equals(new RuntimeException()));
		Assert.assertTrue(!(new RuntimeException()).equals(Rational.ONE));
	}
	
	@Test
	public void testHashCode() {
		Assert.assertTrue((new Rational(3, 5)).hashCode() == (new Rational(6, 10)).hashCode());

		Assert.assertTrue(Rational.ONE.hashCode() != Rational.ZERO.hashCode());
		Assert.assertTrue((new Rational(3, 5)).hashCode() != (new Rational(5, 3)).hashCode());
		Assert.assertTrue((new Rational(3, 5)).hashCode() != (new Rational(-3, 5)).hashCode());
		Assert.assertTrue((new Rational(4)).hashCode() != (new Rational(8)).hashCode());
	}
	
	@Test
	public void testCompareTo() {
		Assert.assertTrue((new Rational(3, 5)).compareTo(new Rational(3, 5)) == 0);
		Assert.assertTrue((new Rational(3, 5)).compareTo(new Rational(5, 3)) == -1);
		Assert.assertTrue((new Rational(5, 3)).compareTo(new Rational(3, 5)) == 1);
		Assert.assertTrue((new Rational(3, 5)).compareTo(new Rational(-5, 3)) == 1);
		Assert.assertTrue((new Rational(-5, 3)).compareTo(new Rational(3, 5)) == -1);

		Assert.assertTrue(Rational.ZERO.compareTo(Rational.ZERO) == 0);
		Assert.assertTrue(Rational.ZERO.compareTo(Rational.ONE) == -1);
		Assert.assertTrue(Rational.ONE.compareTo(Rational.ZERO) == 1);
		Assert.assertTrue(Rational.ZERO.compareTo(Rational.MINUS_ONE) == 1);
		Assert.assertTrue(Rational.MINUS_ONE.compareTo(Rational.ZERO) == -1);
		Assert.assertTrue(Rational.ONE.compareTo(Rational.MINUS_ONE) == 1);
		Assert.assertTrue(Rational.MINUS_ONE.compareTo(Rational.ONE) == -1);
		Assert.assertTrue(Rational.ONE.compareTo(Rational.ONE) == 0);

		// "Rational polymorph compare to"
		Assert.assertTrue((new Rational(3, 5)).compareTo((Object)new Rational(3, 5)) == 0);
		Assert.assertTrue((new Rational(3, 5)).compareTo((Object)new Rational(5, 3)) == -1);
		Assert.assertTrue((new Rational(5, 3)).compareTo((Object)new Rational(3, 5)) == 1);
		Assert.assertTrue((new Rational(3, 5)).compareTo((Object)new Rational(-5, 3)) == 1);
		Assert.assertTrue((new Rational(-5, 3)).compareTo((Object)new Rational(3, 5)) == -1);

		// "Rational small type compare to"
		Assert.assertTrue((new Rational(3, 5)).compareTo(0) != 0);
		Assert.assertTrue((new Rational(0)).compareTo(0) == 0);
		Assert.assertTrue((new Rational(1)).compareTo(1) == 0);
		Assert.assertTrue((new Rational(2)).compareTo(2) == 0);
		Assert.assertTrue((new Rational(3, 5)).compareTo(new Long(0)) != 0);
		Assert.assertTrue((new Rational(1)).compareTo(new Long(1)) == 0);
		Assert.assertTrue((new Rational(1)).compareTo(new Integer(1)) == 0);

		// "Rational long/int value"
		Assert.assertTrue((new Rational(7)).longValue() == 7);
		Assert.assertTrue((new Rational(-7)).intValue() == -7);
		Assert.assertTrue(Rational.ZERO.longValue() == 0);
		Assert.assertTrue(Rational.MINUS_ONE.longValue() == -1);
		Assert.assertTrue(Rational.MINUS_ONE.intValue() == -1);
		Assert.assertTrue(Rational.MINUS_ONE.shortValue() == -1);
		Assert.assertTrue(Rational.MINUS_ONE.byteValue() == -1);
	}
	
	@Test
	public void testRound() {
		Assert.assertTrue((new Rational("23.49")).round(Rational.ROUND_UP).toString().equals("24"));
		Assert.assertTrue((new Rational("23.49")).round(Rational.ROUND_DOWN).toString().equals("23"));
		Assert.assertTrue((new Rational("23.49")).round(Rational.ROUND_CEILING).toString().equals("24"));
		Assert.assertTrue((new Rational("23.49")).round(Rational.ROUND_FLOOR).toString().equals("23"));
		Assert.assertTrue((new Rational("23.49")).round(Rational.ROUND_HALF_UP).toString().equals("23"));
		Assert.assertTrue((new Rational("23.49")).round(Rational.ROUND_HALF_DOWN).toString().equals("23"));
		Assert.assertTrue((new Rational("23.49")).round(Rational.ROUND_HALF_CEILING).toString().equals("23"));
		Assert.assertTrue((new Rational("23.49")).round(Rational.ROUND_HALF_FLOOR).toString().equals("23"));
		Assert.assertTrue((new Rational("23.49")).round(Rational.ROUND_HALF_EVEN).toString().equals("23"));
		Assert.assertTrue((new Rational("23.49")).round(Rational.ROUND_HALF_ODD).toString().equals("23"));

		Assert.assertTrue((new Rational("-23.49")).round(Rational.ROUND_UP).toString().equals("-24"));
		Assert.assertTrue((new Rational("-23.49")).round(Rational.ROUND_DOWN).toString().equals("-23"));
		Assert.assertTrue((new Rational("-23.49")).round(Rational.ROUND_CEILING).toString().equals("-23"));
		Assert.assertTrue((new Rational("-23.49")).round(Rational.ROUND_FLOOR).toString().equals("-24"));
		Assert.assertTrue((new Rational("-23.49")).round(Rational.ROUND_HALF_UP).toString().equals("-23"));
		Assert.assertTrue((new Rational("-23.49")).round(Rational.ROUND_HALF_DOWN).toString().equals("-23"));
		Assert.assertTrue((new Rational("-23.49")).round(Rational.ROUND_HALF_CEILING).toString().equals("-23"));
		Assert.assertTrue((new Rational("-23.49")).round(Rational.ROUND_HALF_FLOOR).toString().equals("-23"));
		Assert.assertTrue((new Rational("-23.49")).round(Rational.ROUND_HALF_EVEN).toString().equals("-23"));
		Assert.assertTrue((new Rational("-23.49")).round(Rational.ROUND_HALF_ODD).toString().equals("-23"));

		Assert.assertTrue((new Rational("23.51")).round(Rational.ROUND_UP).toString().equals("24"));
		Assert.assertTrue((new Rational("23.51")).round(Rational.ROUND_DOWN).toString().equals("23"));
		Assert.assertTrue((new Rational("23.51")).round(Rational.ROUND_CEILING).toString().equals("24"));
		Assert.assertTrue((new Rational("23.51")).round(Rational.ROUND_FLOOR).toString().equals("23"));
		Assert.assertTrue((new Rational("23.51")).round(Rational.ROUND_HALF_UP).toString().equals("24"));
		Assert.assertTrue((new Rational("23.51")).round(Rational.ROUND_HALF_DOWN).toString().equals("24"));
		Assert.assertTrue((new Rational("23.51")).round(Rational.ROUND_HALF_CEILING).toString().equals("24"));
		Assert.assertTrue((new Rational("23.51")).round(Rational.ROUND_HALF_FLOOR).toString().equals("24"));
		Assert.assertTrue((new Rational("23.51")).round(Rational.ROUND_HALF_EVEN).toString().equals("24"));
		Assert.assertTrue((new Rational("23.51")).round(Rational.ROUND_HALF_ODD).toString().equals("24"));

		Assert.assertTrue((new Rational("-23.51")).round(Rational.ROUND_UP).toString().equals("-24"));
		Assert.assertTrue((new Rational("-23.51")).round(Rational.ROUND_DOWN).toString().equals("-23"));
		Assert.assertTrue((new Rational("-23.51")).round(Rational.ROUND_CEILING).toString().equals("-23"));
		Assert.assertTrue((new Rational("-23.51")).round(Rational.ROUND_FLOOR).toString().equals("-24"));
		Assert.assertTrue((new Rational("-23.51")).round(Rational.ROUND_HALF_UP).toString().equals("-24"));
		Assert.assertTrue((new Rational("-23.51")).round(Rational.ROUND_HALF_DOWN).toString().equals("-24"));
		Assert.assertTrue((new Rational("-23.51")).round(Rational.ROUND_HALF_CEILING).toString().equals("-24"));
		Assert.assertTrue((new Rational("-23.51")).round(Rational.ROUND_HALF_FLOOR).toString().equals("-24"));
		Assert.assertTrue((new Rational("-23.51")).round(Rational.ROUND_HALF_EVEN).toString().equals("-24"));
		Assert.assertTrue((new Rational("-23.51")).round(Rational.ROUND_HALF_ODD).toString().equals("-24"));

		Assert.assertTrue((new Rational("23.5")).round(Rational.ROUND_UP).toString().equals("24"));
		Assert.assertTrue((new Rational("23.5")).round(Rational.ROUND_DOWN).toString().equals("23"));
		Assert.assertTrue((new Rational("23.5")).round(Rational.ROUND_CEILING).toString().equals("24"));
		Assert.assertTrue((new Rational("23.5")).round(Rational.ROUND_FLOOR).toString().equals("23"));
		Assert.assertTrue((new Rational("23.5")).round(Rational.ROUND_HALF_UP).toString().equals("24"));
		Assert.assertTrue((new Rational("23.5")).round(Rational.ROUND_HALF_DOWN).toString().equals("23"));
		Assert.assertTrue((new Rational("23.5")).round(Rational.ROUND_HALF_CEILING).toString().equals("24"));
		Assert.assertTrue((new Rational("23.5")).round(Rational.ROUND_HALF_FLOOR).toString().equals("23"));
		Assert.assertTrue((new Rational("23.5")).round(Rational.ROUND_HALF_EVEN).toString().equals("24"));
		Assert.assertTrue((new Rational("23.5")).round(Rational.ROUND_HALF_ODD).toString().equals("23"));

		Assert.assertTrue((new Rational("-23.5")).round(Rational.ROUND_UP).toString().equals("-24"));
		Assert.assertTrue((new Rational("-23.5")).round(Rational.ROUND_DOWN).toString().equals("-23"));
		Assert.assertTrue((new Rational("-23.5")).round(Rational.ROUND_CEILING).toString().equals("-23"));
		Assert.assertTrue((new Rational("-23.5")).round(Rational.ROUND_FLOOR).toString().equals("-24"));
		Assert.assertTrue((new Rational("-23.5")).round(Rational.ROUND_HALF_UP).toString().equals("-24"));
		Assert.assertTrue((new Rational("-23.5")).round(Rational.ROUND_HALF_DOWN).toString().equals("-23"));
		Assert.assertTrue((new Rational("-23.5")).round(Rational.ROUND_HALF_CEILING).toString().equals("-23"));
		Assert.assertTrue((new Rational("-23.5")).round(Rational.ROUND_HALF_FLOOR).toString().equals("-24"));
		Assert.assertTrue((new Rational("-23.5")).round(Rational.ROUND_HALF_EVEN).toString().equals("-24"));
		Assert.assertTrue((new Rational("-23.5")).round(Rational.ROUND_HALF_ODD).toString().equals("-23"));

		Assert.assertTrue((new Rational("22")).round(Rational.ROUND_UP).toString().equals("22"));
		Assert.assertTrue((new Rational("22")).round(Rational.ROUND_DOWN).toString().equals("22"));
		Assert.assertTrue((new Rational("22")).round(Rational.ROUND_CEILING).toString().equals("22"));
		Assert.assertTrue((new Rational("22")).round(Rational.ROUND_FLOOR).toString().equals("22"));
		Assert.assertTrue((new Rational("22")).round(Rational.ROUND_HALF_UP).toString().equals("22"));
		Assert.assertTrue((new Rational("22")).round(Rational.ROUND_HALF_DOWN).toString().equals("22"));
		Assert.assertTrue((new Rational("22")).round(Rational.ROUND_HALF_CEILING).toString().equals("22"));
		Assert.assertTrue((new Rational("22")).round(Rational.ROUND_HALF_FLOOR).toString().equals("22"));
		Assert.assertTrue((new Rational("22")).round(Rational.ROUND_HALF_EVEN).toString().equals("22"));
		Assert.assertTrue((new Rational("22")).round(Rational.ROUND_HALF_ODD).toString().equals("22"));

		Assert.assertTrue((new Rational("-22")).round(Rational.ROUND_UP).toString().equals("-22"));
		Assert.assertTrue((new Rational("-22")).round(Rational.ROUND_DOWN).toString().equals("-22"));
		Assert.assertTrue((new Rational("-22")).round(Rational.ROUND_CEILING).toString().equals("-22"));
		Assert.assertTrue((new Rational("-22")).round(Rational.ROUND_FLOOR).toString().equals("-22"));
		Assert.assertTrue((new Rational("-22")).round(Rational.ROUND_HALF_UP).toString().equals("-22"));
		Assert.assertTrue((new Rational("-22")).round(Rational.ROUND_HALF_DOWN).toString().equals("-22"));
		Assert.assertTrue((new Rational("-22")).round(Rational.ROUND_HALF_CEILING).toString().equals("-22"));
		Assert.assertTrue((new Rational("-22")).round(Rational.ROUND_HALF_FLOOR).toString().equals("-22"));
		Assert.assertTrue((new Rational("-22")).round(Rational.ROUND_HALF_EVEN).toString().equals("-22"));
		Assert.assertTrue((new Rational("-22")).round(Rational.ROUND_HALF_ODD).toString().equals("-22"));

		// "Rational round unnecessary"
		Assert.assertTrue((new Rational("23")).round(Rational.ROUND_UNNECESSARY).toString().equals("23"));
		Assert.assertTrue((new Rational("-23")).round(Rational.ROUND_UNNECESSARY).toString().equals("-23"));
		
		// round special cases
		Assert.assertTrue(Rational.ZERO.round().equals(Rational.ZERO));
		Assert.assertTrue(Rational.ZERO.round(Rational.ROUND_UNNECESSARY).equals(Rational.ZERO));

	}
	
	@Test(expected = ArithmeticException.class)
	public void testIllegalRound1() {
		(new Rational("23.5")).round(Rational.ROUND_UNNECESSARY);
	}
	
	@Test(expected = ArithmeticException.class)
	public void testIllegalRound2() {
		(new Rational("-23.5")).round(Rational.ROUND_UNNECESSARY);
	}
	
	@Test
	public void testIntegerPart() {
		Assert.assertTrue((new Rational("56.8")).integerPart().toString().equals("56"));
		Assert.assertTrue((new Rational("-56.8")).integerPart().toString().equals("-56"));
		Assert.assertTrue((new Rational("0.8")).integerPart().toString().equals("0"));
		Assert.assertTrue((new Rational("-0.8")).integerPart().toString().equals("0"));
	}
	
	@Test
	public void testFractionalPart() {
		Assert.assertTrue((new Rational("56.8")).fractionalPart().equals(new Rational("0.8")));
		Assert.assertTrue((new Rational("-56.8")).fractionalPart().equals(new Rational("-0.8")));
		Assert.assertTrue((new Rational("0.8")).fractionalPart().equals(new Rational("0.8")));
		Assert.assertTrue((new Rational("-0.8")).fractionalPart().equals(new Rational("-0.8")));
	}
	
	@Test
	public void testIntegerAndFractionalPart() {
		Assert.assertTrue((new Rational("56.8")).integerAndFractionalPart()[0].equals(new Rational("56")));
		Assert.assertTrue((new Rational("56.8")).integerAndFractionalPart()[1].equals(new Rational("0.8")));
		Assert.assertTrue((new Rational("-56.8")).integerAndFractionalPart()[0].equals(new Rational("-56")));
		Assert.assertTrue((new Rational("-56.8")).integerAndFractionalPart()[1].equals(new Rational("-0.8")));
	}
	
	@Test
	public void testSanityOfInternalConstants() {
		// sanity/harmlessness of using internal constants of commonly used numbers
		// scanned in by commonly used radixes
		Assert.assertTrue((new Rational("0", 2)).toString(2).equals("0"));
		Assert.assertTrue((new Rational("0", 10)).toString(10).equals("0"));
		Assert.assertTrue((new Rational("0", 16)).toString(16).equals("0"));
		Assert.assertTrue((new Rational("1", 2)).toString(2).equals("1"));
		Assert.assertTrue((new Rational("1", 10)).toString(10).equals("1"));
		Assert.assertTrue((new Rational("1", 16)).toString(16).equals("1"));
		Assert.assertTrue((new Rational("-1", 2)).toString(2).equals("-1"));
		Assert.assertTrue((new Rational("-1", 10)).toString(10).equals("-1"));
		Assert.assertTrue((new Rational("-1", 16)).toString(16).equals("-1"));
		Assert.assertTrue((new Rational("10", 2)).toString(2).equals("10"));
		Assert.assertTrue((new Rational("10", 10)).toString(10).equals("10"));
		Assert.assertTrue((new Rational("10", 16)).toString(16).equals("10"));

		// more constants
		Assert.assertTrue((new Rational("2", 10)).toString(10).equals("2"));
		Assert.assertTrue((new Rational("2", 16)).toString(16).equals("2"));
		Assert.assertTrue((new Rational("-2", 10)).toString(10).equals("-2"));
		Assert.assertTrue((new Rational("-2", 16)).toString(16).equals("-2"));
		Assert.assertTrue((new Rational("16", 10)).toString(10).equals("16"));
		Assert.assertTrue((new Rational("16", 16)).toString(16).equals("16"));
	}
	
	@Test
	public void testValueOfDouble() {
		// floating point
		// remember: [IEEE 754] floats are exact where the quotient is a power of two

		Assert.assertTrue(Rational.valueOf(0.0).equals(Rational.ZERO));
		Assert.assertTrue(Rational.valueOf(1.0).equals(Rational.ONE));
		Assert.assertTrue(Rational.valueOf(-1.0).equals(Rational.MINUS_ONE));

		Assert.assertTrue(Rational.valueOf(2.0).equals(Rational.valueOf(2)));
		Assert.assertTrue(Rational.valueOf(-2.0).equals(Rational.valueOf(-2)));
		Assert.assertTrue(Rational.valueOf(4.0).equals(Rational.valueOf(4)));
		Assert.assertTrue(Rational.valueOf(-4.0).equals(Rational.valueOf(-4)));
		Assert.assertTrue(Rational.valueOf(16.0).equals(Rational.valueOf(16)));
		Assert.assertTrue(Rational.valueOf(-16.0).equals(Rational.valueOf(-16)));

		Assert.assertTrue(Rational.valueOf(3.0).equals(Rational.valueOf(3)));
		Assert.assertTrue(Rational.valueOf(-3.0).equals(Rational.valueOf(-3)));
		Assert.assertTrue(Rational.valueOf(6.0).equals(Rational.valueOf(6)));
		Assert.assertTrue(Rational.valueOf(-6.0).equals(Rational.valueOf(-6)));
		Assert.assertTrue(Rational.valueOf(12.0).equals(Rational.valueOf(12)));
		Assert.assertTrue(Rational.valueOf(-12.0).equals(Rational.valueOf(-12)));

		Assert.assertTrue(Rational.valueOf(0.5).equals(new Rational(1, 2)));
		Assert.assertTrue(Rational.valueOf(-0.5).equals(new Rational(-1, 2)));
		Assert.assertTrue(Rational.valueOf(0.25).equals(new Rational(1, 4)));
		Assert.assertTrue(Rational.valueOf(-0.25).equals(new Rational(-1, 4)));
		Assert.assertTrue(Rational.valueOf(0.0625).equals(new Rational(1, 16)));
		Assert.assertTrue(Rational.valueOf(-0.0625).equals(new Rational(-1, 16)));

		Assert.assertTrue(Rational.valueOf(1.5).equals(new Rational(3, 2)));
		Assert.assertTrue(Rational.valueOf(-1.5).equals(new Rational(-3, 2)));
		Assert.assertTrue(Rational.valueOf(0.75).equals(new Rational(3, 4)));
		Assert.assertTrue(Rational.valueOf(-0.75).equals(new Rational(-3, 4)));
		Assert.assertTrue(Rational.valueOf(0.375).equals(new Rational(3, 8)));
		Assert.assertTrue(Rational.valueOf(-0.375).equals(new Rational(-3, 8)));

		// other conversion direction

		Assert.assertTrue(Rational.ZERO.doubleValue() == 0.0);
		Assert.assertTrue(Rational.ONE.doubleValue() == 1.0);
		Assert.assertTrue(Rational.MINUS_ONE.doubleValue() == -1.0);

		Assert.assertTrue(Rational.valueOf(2).doubleValue() == 2.0);
		Assert.assertTrue(Rational.valueOf(-2).doubleValue() == -2.0);
		Assert.assertTrue(Rational.valueOf(4).doubleValue() == 4.0);
		Assert.assertTrue(Rational.valueOf(-4).doubleValue() == -4.0);
		Assert.assertTrue(Rational.valueOf(16).doubleValue() == 16.0);
		Assert.assertTrue(Rational.valueOf(-16).doubleValue() == -16.0);

		Assert.assertTrue(Rational.valueOf(3).doubleValue() == 3.0);
		Assert.assertTrue(Rational.valueOf(-3).doubleValue() == -3.0);
		Assert.assertTrue(Rational.valueOf(6).doubleValue() == 6.0);
		Assert.assertTrue(Rational.valueOf(-6).doubleValue() == -6.0);
		Assert.assertTrue(Rational.valueOf(12).doubleValue() == 12.0);
		Assert.assertTrue(Rational.valueOf(-12).doubleValue() == -12.0);

		Assert.assertTrue((new Rational(1, 2)).doubleValue() == 0.5);
		Assert.assertTrue((new Rational(-1, 2)).doubleValue() == -0.5);
		Assert.assertTrue((new Rational(1, 4)).doubleValue() == 0.25);
		Assert.assertTrue((new Rational(-1, 4)).doubleValue() == -0.25);
		Assert.assertTrue((new Rational(1, 16)).doubleValue() == 0.0625);
		Assert.assertTrue((new Rational(-1, 16)).doubleValue() == -0.0625);

		Assert.assertTrue((new Rational(3, 2)).doubleValue() == 1.5);
		Assert.assertTrue((new Rational(-3, 2)).doubleValue() == -1.5);
		Assert.assertTrue((new Rational(3, 4)).doubleValue() == 0.75);
		Assert.assertTrue((new Rational(-3, 4)).doubleValue() == -0.75);
		Assert.assertTrue((new Rational(3, 8)).doubleValue() == 0.375);
		Assert.assertTrue((new Rational(-3, 8)).doubleValue() == -0.375);

		// conversion forth and back

		Assert.assertTrue(Rational.valueOf(0.0).doubleValue() == 0.0);
		Assert.assertTrue(Rational.valueOf(1.0).doubleValue() == 1.0);
		Assert.assertTrue(Rational.valueOf(-1.0).doubleValue() == -1.0);
		Assert.assertTrue(Rational.valueOf(2.0).doubleValue() == 2.0);
		Assert.assertTrue(Rational.valueOf(-2.0).doubleValue() == -2.0);

		// maximal and minimal values, and near there
		Assert.assertTrue(Rational.valueOf(Double.MAX_VALUE).doubleValue() == Double.MAX_VALUE);
		Assert.assertTrue(Rational.valueOf(-Double.MAX_VALUE).doubleValue() == -Double.MAX_VALUE);
		Assert.assertTrue(Rational.valueOf(Double.MAX_VALUE / 16).doubleValue() == Double.MAX_VALUE / 16);
		Assert.assertTrue(Rational.valueOf(-Double.MAX_VALUE / 16).doubleValue() == -Double.MAX_VALUE / 16);
		// [subnormal value]
		Assert.assertTrue(Rational.valueOf(Double.MIN_VALUE).doubleValue() == Double.MIN_VALUE);
		Assert.assertTrue(Rational.valueOf(-Double.MIN_VALUE).doubleValue() == -Double.MIN_VALUE);
		Assert.assertTrue(Rational.valueOf(Double.MIN_VALUE * 16).doubleValue() == Double.MIN_VALUE * 16);
		Assert.assertTrue(Rational.valueOf(-Double.MIN_VALUE * 16).doubleValue() == -Double.MIN_VALUE * 16);

		// overflow
		Assert.assertTrue(Rational.valueOf(Double.MAX_VALUE).multiply(2).doubleValue() == Double.POSITIVE_INFINITY);
		Assert.assertTrue(Rational.valueOf(Double.MAX_VALUE).multiply(4).doubleValue() == Double.POSITIVE_INFINITY);
		Assert.assertTrue(Rational.valueOf(Double.MAX_VALUE).multiply(Rational.valueOf(1.2)).doubleValue() == Double.POSITIVE_INFINITY);
		Assert.assertTrue(Rational.valueOf(Double.MAX_VALUE).multiply(16).doubleValue() == Double.POSITIVE_INFINITY);
		Assert.assertTrue(Rational.valueOf(-Double.MAX_VALUE).multiply(2).doubleValue() == Double.NEGATIVE_INFINITY);
		Assert.assertTrue(Rational.valueOf(-Double.MAX_VALUE).multiply(4).doubleValue() == Double.NEGATIVE_INFINITY);
		Assert.assertTrue(Rational.valueOf(-Double.MAX_VALUE).multiply(Rational.valueOf(1.2)).doubleValue() == Double.NEGATIVE_INFINITY);
		Assert.assertTrue(Rational.valueOf(-Double.MAX_VALUE).multiply(16).doubleValue() == Double.NEGATIVE_INFINITY);

		// underflow
		// note that the (double)x==(double)y test yields true for 0.0==-0.0
		Assert.assertTrue(Rational.valueOf(Double.MIN_VALUE).divide(2).doubleValue() == 0.0);
		Assert.assertTrue(Rational.valueOf(Double.MIN_VALUE).divide(4).doubleValue() == 0.0);
		Assert.assertTrue(Rational.valueOf(Double.MIN_VALUE).divide(Rational.valueOf(1.2)).doubleValue() == 0.0);
		Assert.assertTrue(Rational.valueOf(Double.MIN_VALUE).divide(16).doubleValue() == 0.0);
		// returning -0.0 (signed zero)
		Assert.assertTrue(Rational.valueOf(-Double.MIN_VALUE).divide(2).doubleValue() == -0.0);
		Assert.assertTrue(Rational.valueOf(-Double.MIN_VALUE).divide(4).doubleValue() == -0.0);
		Assert.assertTrue(Rational.valueOf(-Double.MIN_VALUE).divide(Rational.valueOf(1.2)).doubleValue() == -0.0);
		Assert.assertTrue(Rational.valueOf(-Double.MIN_VALUE).divide(16).doubleValue() == -0.0);

		// signed underflow, alternative tests
		Assert.assertTrue(String.valueOf(Rational.valueOf(Double.MIN_VALUE).divide(2).doubleValue()).equals("0.0"));
		Assert.assertTrue(String.valueOf(Rational.valueOf(Double.MIN_VALUE).divide(4).doubleValue()).equals("0.0"));
		Assert.assertTrue(String.valueOf(Rational.valueOf(Double.MIN_VALUE).divide(Rational.valueOf(1.2)).doubleValue()).equals("0.0"));
		Assert.assertTrue(String.valueOf(Rational.valueOf(Double.MIN_VALUE).divide(16).doubleValue()).equals("0.0"));
		Assert.assertTrue(String.valueOf(Rational.valueOf(-Double.MIN_VALUE).divide(2).doubleValue()).equals("-0.0"));
		Assert.assertTrue(String.valueOf(Rational.valueOf(-Double.MIN_VALUE).divide(4).doubleValue()).equals("-0.0"));
		Assert.assertTrue(String.valueOf(Rational.valueOf(-Double.MIN_VALUE).divide(Rational.valueOf(1.2)).doubleValue()).equals("-0.0"));
		Assert.assertTrue(String.valueOf(Rational.valueOf(-Double.MIN_VALUE).divide(16).doubleValue()).equals("-0.0"));

		// ulp
		Assert.assertTrue(Rational.valueOf(1.0 - StrictMath.ulp(1.0)).doubleValue() == 1.0 - StrictMath.ulp(1.0));
		Assert.assertTrue(Rational.valueOf(1.0 + StrictMath.ulp(1.0)).doubleValue() == 1.0 + StrictMath.ulp(1.0));
		Assert.assertTrue(Rational.valueOf(1.0 - StrictMath.ulp(1.0) / 2).doubleValue() == 1.0 - StrictMath.ulp(1.0) / 2);
		Assert.assertTrue(Rational.valueOf(1.0 + StrictMath.ulp(1.0) / 2).doubleValue() == 1.0 + StrictMath.ulp(1.0) / 2);
		Assert.assertTrue(Rational.valueOf(1.0 - StrictMath.ulp(1.0) / 4).doubleValue() == 1.0 - StrictMath.ulp(1.0) / 4);
		Assert.assertTrue(Rational.valueOf(1.0 + StrictMath.ulp(1.0) / 4).doubleValue() == 1.0 + StrictMath.ulp(1.0) / 4);

		// mantissa rounding
		// a delta of ulp/4 is expected to be rounded
		Assert.assertTrue(Rational.valueOf(1.0).subtract(Rational.valueOf(StrictMath.ulp(1.0)).divide(4)).doubleValue() == 1.0);
		Assert.assertTrue(Rational.valueOf(16.0).subtract(Rational.valueOf(StrictMath.ulp(16.0)).divide(4)).doubleValue() == 16.0);
		// note: MAX_VALUE is not a power-of-two, so it won't run into the mantissa rounding case in question;
		// and 1/MIN_VALUE is larger than MAX_VALUE (due to MIN_VALUE being subnormal)
		// Assert.assertTrue(Rational.valueOf(0x1P1023).subtract(Rational.valueOf(StrictMath.ulp(0x1P1023)).divide(4)).doubleValue() == 0x1P1023);

		// more values in between [0 and max/min]
		Assert.assertTrue(Rational.valueOf(StrictMath.sqrt(Double.MAX_VALUE)).doubleValue() == StrictMath.sqrt(Double.MAX_VALUE));
		Assert.assertTrue(Rational.valueOf(StrictMath.pow(Double.MAX_VALUE, 0.2)).doubleValue() == StrictMath.pow(Double.MAX_VALUE, 0.2));
		Assert.assertTrue(Rational.valueOf(Double.MAX_VALUE).pow(2).doubleValue() == Double.POSITIVE_INFINITY);
		Assert.assertTrue(Rational.valueOf(Double.MAX_VALUE).pow(5).doubleValue() == Double.POSITIVE_INFINITY);
		Assert.assertTrue(Rational.valueOf(StrictMath.sqrt(Double.MIN_VALUE)).doubleValue() == StrictMath.sqrt(Double.MIN_VALUE));
		Assert.assertTrue(Rational.valueOf(StrictMath.pow(Double.MIN_VALUE, 0.2)).doubleValue() == StrictMath.pow(Double.MIN_VALUE, 0.2));
		Assert.assertTrue(Rational.valueOf(Double.MIN_VALUE).pow(2).doubleValue() == 0.0);
		Assert.assertTrue(Rational.valueOf(Double.MIN_VALUE).pow(5).doubleValue() == 0.0);
		
		Assert.assertTrue(Rational.valueOf(StrictMath.E).doubleValue() == StrictMath.E);
		Assert.assertTrue(Rational.valueOf(StrictMath.PI).doubleValue() == StrictMath.PI);
		Assert.assertTrue(Rational.valueOf(StrictMath.pow(StrictMath.E, 2)).doubleValue() == StrictMath.pow(StrictMath.E, 2));
		Assert.assertTrue(Rational.valueOf(StrictMath.sqrt(StrictMath.E)).doubleValue() == StrictMath.sqrt(StrictMath.E));
		Assert.assertTrue(Rational.valueOf(StrictMath.pow(StrictMath.PI, 2)).doubleValue() == StrictMath.pow(StrictMath.PI, 2));
		Assert.assertTrue(Rational.valueOf(StrictMath.sqrt(StrictMath.PI)).doubleValue() == StrictMath.sqrt(StrictMath.PI));
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalValueOfDoublePositiveInfinity() {
		Rational.valueOf(Double.POSITIVE_INFINITY);
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalValueOfDoubleNegativeInfinity() {
		Rational.valueOf(Double.NEGATIVE_INFINITY);
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalValueOfDoubleNaN() {
		Rational.valueOf(Double.NaN);
	}
	
	@Test
	public void testValueOfFloat() {
		// same tests with single precision float

		Assert.assertTrue(Rational.valueOf(0.0f).equals(Rational.ZERO));
		Assert.assertTrue(Rational.valueOf(1.0f).equals(Rational.ONE));
		Assert.assertTrue(Rational.valueOf(-1.0f).equals(Rational.MINUS_ONE));

		Assert.assertTrue(Rational.valueOf(2.0f).equals(Rational.valueOf(2)));
		Assert.assertTrue(Rational.valueOf(-2.0f).equals(Rational.valueOf(-2)));
		Assert.assertTrue(Rational.valueOf(4.0f).equals(Rational.valueOf(4)));
		Assert.assertTrue(Rational.valueOf(-4.0f).equals(Rational.valueOf(-4)));
		Assert.assertTrue(Rational.valueOf(16.0f).equals(Rational.valueOf(16)));
		Assert.assertTrue(Rational.valueOf(-16.0f).equals(Rational.valueOf(-16)));

		Assert.assertTrue(Rational.valueOf(3.0f).equals(Rational.valueOf(3)));
		Assert.assertTrue(Rational.valueOf(-3.0f).equals(Rational.valueOf(-3)));
		Assert.assertTrue(Rational.valueOf(6.0f).equals(Rational.valueOf(6)));
		Assert.assertTrue(Rational.valueOf(-6.0f).equals(Rational.valueOf(-6)));
		Assert.assertTrue(Rational.valueOf(12.0f).equals(Rational.valueOf(12)));
		Assert.assertTrue(Rational.valueOf(-12.0f).equals(Rational.valueOf(-12)));

		Assert.assertTrue(Rational.valueOf(0.5f).equals(new Rational(1, 2)));
		Assert.assertTrue(Rational.valueOf(-0.5f).equals(new Rational(-1, 2)));
		Assert.assertTrue(Rational.valueOf(0.25f).equals(new Rational(1, 4)));
		Assert.assertTrue(Rational.valueOf(-0.25f).equals(new Rational(-1, 4)));
		Assert.assertTrue(Rational.valueOf(0.0625f).equals(new Rational(1, 16)));
		Assert.assertTrue(Rational.valueOf(-0.0625f).equals(new Rational(-1, 16)));

		Assert.assertTrue(Rational.valueOf(1.5f).equals(new Rational(3, 2)));
		Assert.assertTrue(Rational.valueOf(-1.5f).equals(new Rational(-3, 2)));
		Assert.assertTrue(Rational.valueOf(0.75f).equals(new Rational(3, 4)));
		Assert.assertTrue(Rational.valueOf(-0.75f).equals(new Rational(-3, 4)));
		Assert.assertTrue(Rational.valueOf(0.375f).equals(new Rational(3, 8)));
		Assert.assertTrue(Rational.valueOf(-0.375f).equals(new Rational(-3, 8)));

		// other conversion direction

		Assert.assertTrue(Rational.ZERO.floatValue() == 0.0f);
		Assert.assertTrue(Rational.ONE.floatValue() == 1.0f);
		Assert.assertTrue(Rational.MINUS_ONE.floatValue() == -1.0f);

		Assert.assertTrue(Rational.valueOf(2).floatValue() == 2.0f);
		Assert.assertTrue(Rational.valueOf(-2).floatValue() == -2.0f);
		Assert.assertTrue(Rational.valueOf(4).floatValue() == 4.0f);
		Assert.assertTrue(Rational.valueOf(-4).floatValue() == -4.0f);
		Assert.assertTrue(Rational.valueOf(16).floatValue() == 16.0f);
		Assert.assertTrue(Rational.valueOf(-16).floatValue() == -16.0f);

		Assert.assertTrue(Rational.valueOf(3).floatValue() == 3.0f);
		Assert.assertTrue(Rational.valueOf(-3).floatValue() == -3.0f);
		Assert.assertTrue(Rational.valueOf(6).floatValue() == 6.0f);
		Assert.assertTrue(Rational.valueOf(-6).floatValue() == -6.0f);
		Assert.assertTrue(Rational.valueOf(12).floatValue() == 12.0f);
		Assert.assertTrue(Rational.valueOf(-12).floatValue() == -12.0f);

		Assert.assertTrue((new Rational(1, 2)).floatValue() == 0.5f);
		Assert.assertTrue((new Rational(-1, 2)).floatValue() == -0.5f);
		Assert.assertTrue((new Rational(1, 4)).floatValue() == 0.25f);
		Assert.assertTrue((new Rational(-1, 4)).floatValue() == -0.25f);
		Assert.assertTrue((new Rational(1, 16)).floatValue() == 0.0625f);
		Assert.assertTrue((new Rational(-1, 16)).floatValue() == -0.0625f);

		Assert.assertTrue((new Rational(3, 2)).floatValue() == 1.5f);
		Assert.assertTrue((new Rational(-3, 2)).floatValue() == -1.5f);
		Assert.assertTrue((new Rational(3, 4)).floatValue() == 0.75f);
		Assert.assertTrue((new Rational(-3, 4)).floatValue() == -0.75f);
		Assert.assertTrue((new Rational(3, 8)).floatValue() == 0.375f);
		Assert.assertTrue((new Rational(-3, 8)).floatValue() == -0.375f);

		// conversion forth and back

		Assert.assertTrue(Rational.valueOf(0.0f).floatValue() == 0.0f);
		Assert.assertTrue(Rational.valueOf(1.0f).floatValue() == 1.0f);
		Assert.assertTrue(Rational.valueOf(-1.0f).floatValue() == -1.0f);
		Assert.assertTrue(Rational.valueOf(2.0f).floatValue() == 2.0f);
		Assert.assertTrue(Rational.valueOf(-2.0f).floatValue() == -2.0f);

		// maximal and minimal values, and near there
		Assert.assertTrue(Rational.valueOf(Float.MAX_VALUE).floatValue() == Float.MAX_VALUE);
		Assert.assertTrue(Rational.valueOf(-Float.MAX_VALUE).floatValue() == -Float.MAX_VALUE);
		Assert.assertTrue(Rational.valueOf(Float.MAX_VALUE / 16).floatValue() == Float.MAX_VALUE / 16);
		Assert.assertTrue(Rational.valueOf(-Float.MAX_VALUE / 16).floatValue() == -Float.MAX_VALUE / 16);
		// [subnormal value]
		Assert.assertTrue(Rational.valueOf(Float.MIN_VALUE).floatValue() == Float.MIN_VALUE);
		Assert.assertTrue(Rational.valueOf(-Float.MIN_VALUE).floatValue() == -Float.MIN_VALUE);
		Assert.assertTrue(Rational.valueOf(Float.MIN_VALUE * 16).floatValue() == Float.MIN_VALUE * 16);
		Assert.assertTrue(Rational.valueOf(-Float.MIN_VALUE * 16).floatValue() == -Float.MIN_VALUE * 16);

		// overflow
		Assert.assertTrue(Rational.valueOf(Float.MAX_VALUE).multiply(2).floatValue() == Float.POSITIVE_INFINITY);
		Assert.assertTrue(Rational.valueOf(Float.MAX_VALUE).multiply(4).floatValue() == Float.POSITIVE_INFINITY);
		Assert.assertTrue(Rational.valueOf(Float.MAX_VALUE).multiply(Rational.valueOf(1.2f)).floatValue() == Float.POSITIVE_INFINITY);
		Assert.assertTrue(Rational.valueOf(Float.MAX_VALUE).multiply(16).floatValue() == Float.POSITIVE_INFINITY);
		Assert.assertTrue(Rational.valueOf(-Float.MAX_VALUE).multiply(2).floatValue() == Float.NEGATIVE_INFINITY);
		Assert.assertTrue(Rational.valueOf(-Float.MAX_VALUE).multiply(4).floatValue() == Float.NEGATIVE_INFINITY);
		Assert.assertTrue(Rational.valueOf(-Float.MAX_VALUE).multiply(Rational.valueOf(1.2f)).floatValue() == Float.NEGATIVE_INFINITY);
		Assert.assertTrue(Rational.valueOf(-Float.MAX_VALUE).multiply(16).floatValue() == Float.NEGATIVE_INFINITY);

		// underflow
		// note that the (float)x==(float)y test yields true for 0.0f==-0.0f
		Assert.assertTrue(Rational.valueOf(Float.MIN_VALUE).divide(2).floatValue() == 0.0f);
		Assert.assertTrue(Rational.valueOf(Float.MIN_VALUE).divide(4).floatValue() == 0.0f);
		Assert.assertTrue(Rational.valueOf(Float.MIN_VALUE).divide(Rational.valueOf(1.2f)).floatValue() == 0.0f);
		Assert.assertTrue(Rational.valueOf(Float.MIN_VALUE).divide(16).floatValue() == 0.0f);
		// returning -0.0f (signed zero)
		Assert.assertTrue(Rational.valueOf(-Float.MIN_VALUE).divide(2).floatValue() == -0.0f);
		Assert.assertTrue(Rational.valueOf(-Float.MIN_VALUE).divide(4).floatValue() == -0.0f);
		Assert.assertTrue(Rational.valueOf(-Float.MIN_VALUE).divide(Rational.valueOf(1.2f)).floatValue() == -0.0f);
		Assert.assertTrue(Rational.valueOf(-Float.MIN_VALUE).divide(16).floatValue() == -0.0f);

		// signed underflow, alternative tests
		Assert.assertTrue(String.valueOf(Rational.valueOf(Float.MIN_VALUE).divide(2).floatValue()).equals("0.0"));
		Assert.assertTrue(String.valueOf(Rational.valueOf(Float.MIN_VALUE).divide(4).floatValue()).equals("0.0"));
		Assert.assertTrue(String.valueOf(Rational.valueOf(Float.MIN_VALUE).divide(Rational.valueOf(1.2f)).floatValue()).equals("0.0"));
		Assert.assertTrue(String.valueOf(Rational.valueOf(Float.MIN_VALUE).divide(16).floatValue()).equals("0.0"));
		Assert.assertTrue(String.valueOf(Rational.valueOf(-Float.MIN_VALUE).divide(2).floatValue()).equals("-0.0"));
		Assert.assertTrue(String.valueOf(Rational.valueOf(-Float.MIN_VALUE).divide(4).floatValue()).equals("-0.0"));
		Assert.assertTrue(String.valueOf(Rational.valueOf(-Float.MIN_VALUE).divide(Rational.valueOf(1.2f)).floatValue()).equals("-0.0"));
		Assert.assertTrue(String.valueOf(Rational.valueOf(-Float.MIN_VALUE).divide(16).floatValue()).equals("-0.0"));

		// ulp
		Assert.assertTrue(Rational.valueOf(1.0f - StrictMath.ulp(1.0f)).floatValue() == 1.0f - StrictMath.ulp(1.0f));
		Assert.assertTrue(Rational.valueOf(1.0f + StrictMath.ulp(1.0f)).floatValue() == 1.0f + StrictMath.ulp(1.0f));
		Assert.assertTrue(Rational.valueOf(1.0f - StrictMath.ulp(1.0f) / 2).floatValue() == 1.0f - StrictMath.ulp(1.0f) / 2);
		Assert.assertTrue(Rational.valueOf(1.0f + StrictMath.ulp(1.0f) / 2).floatValue() == 1.0f + StrictMath.ulp(1.0f) / 2);
		Assert.assertTrue(Rational.valueOf(1.0f - StrictMath.ulp(1.0f) / 4).floatValue() == 1.0f - StrictMath.ulp(1.0f) / 4);
		Assert.assertTrue(Rational.valueOf(1.0f + StrictMath.ulp(1.0f) / 4).floatValue() == 1.0f + StrictMath.ulp(1.0f) / 4);

		// mantissa rounding
		// a delta of ulp/4 is expected to be rounded
		Assert.assertTrue(Rational.valueOf(1.0f).subtract(Rational.valueOf(StrictMath.ulp(1.0f)).divide(4)).floatValue() == 1.0f);
		Assert.assertTrue(Rational.valueOf(16.0f).subtract(Rational.valueOf(StrictMath.ulp(16.0f)).divide(4)).floatValue() == 16.0f);
		// note: MAX_VALUE is not a power-of-two, so it won't run into the mantissa rounding case in question;
		// and 1/MIN_VALUE is larger than MAX_VALUE (due to MIN_VALUE being subnormal)
		// Assert.assertTrue(Rational.valueOf(0x1P127f).subtract(Rational.valueOf(StrictMath.ulp(0x1P127f)).divide(4)).floatValue() == 0x1P127f);

		// more values in between [0 and max/min]
		Assert.assertTrue(Rational.valueOf((float)StrictMath.sqrt(Float.MAX_VALUE)).floatValue() == (float)StrictMath.sqrt(Float.MAX_VALUE));
		Assert.assertTrue(Rational.valueOf((float)StrictMath.pow(Float.MAX_VALUE, 0.2f)).floatValue() == (float)StrictMath.pow(Float.MAX_VALUE, 0.2f));
		Assert.assertTrue(Rational.valueOf(Float.MAX_VALUE).pow(2).floatValue() == Float.POSITIVE_INFINITY);
		Assert.assertTrue(Rational.valueOf(Float.MAX_VALUE).pow(5).floatValue() == Float.POSITIVE_INFINITY);
		Assert.assertTrue(Rational.valueOf((float)StrictMath.sqrt(Float.MIN_VALUE)).floatValue() == (float)StrictMath.sqrt(Float.MIN_VALUE));
		Assert.assertTrue(Rational.valueOf((float)StrictMath.pow(Float.MIN_VALUE, 0.2f)).floatValue() == (float)StrictMath.pow(Float.MIN_VALUE, 0.2f));
		Assert.assertTrue(Rational.valueOf(Float.MIN_VALUE).pow(2).floatValue() == 0.0f);
		Assert.assertTrue(Rational.valueOf(Float.MIN_VALUE).pow(5).floatValue() == 0.0f);
		
		Assert.assertTrue(Rational.valueOf((float)StrictMath.E).floatValue() == (float)StrictMath.E);
		Assert.assertTrue(Rational.valueOf((float)StrictMath.PI).floatValue() == (float)StrictMath.PI);
		Assert.assertTrue(Rational.valueOf((float)StrictMath.pow((float)StrictMath.E, 2)).floatValue() == (float)StrictMath.pow((float)StrictMath.E, 2));
		Assert.assertTrue(Rational.valueOf((float)StrictMath.sqrt((float)StrictMath.E)).floatValue() == (float)StrictMath.sqrt((float)StrictMath.E));
		Assert.assertTrue(Rational.valueOf((float)StrictMath.pow((float)StrictMath.PI, 2)).floatValue() == (float)StrictMath.pow((float)StrictMath.PI, 2));
		Assert.assertTrue(Rational.valueOf((float)StrictMath.sqrt((float)StrictMath.PI)).floatValue() == (float)StrictMath.sqrt((float)StrictMath.PI));
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalValueOfFloatPositiveInfinity() {
		Rational.valueOf(Float.POSITIVE_INFINITY);
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalValueOfFloatNegativeInfinity() {
		Rational.valueOf(Float.NEGATIVE_INFINITY);
	}
	
	@Test(expected = NumberFormatException.class)
	public void testIllegalValueOfFloatNaN() {
		Rational.valueOf(Float.NaN);
	}
	
	@Test
	public void testValueOfBits() {
		// half (5 exponent bits)
		Assert.assertTrue(Rational.valueOfHalfBits((short)0).equals(Rational.valueOf("0")));
		Assert.assertTrue(Rational.valueOf("0").halfBitsValue() == 0);
		Assert.assertTrue(Rational.valueOfHalfBits((short)0x3c00).equals(Rational.valueOf("1")));
		Assert.assertTrue(Rational.valueOf("1").halfBitsValue() == (short)0x3c00);
		Assert.assertTrue(Rational.valueOfHalfBits((short)0xbc00).equals(Rational.valueOf("-1")));
		Assert.assertTrue(Rational.valueOf("-1").halfBitsValue() == (short)0xbc00);
		Assert.assertTrue(Rational.valueOfHalfBits((short)0x3e00).equals(Rational.valueOf("1.5")));
		Assert.assertTrue(Rational.valueOf("1.5").halfBitsValue() == (short)0x3e00);
		Assert.assertTrue(Rational.valueOfHalfBits((short)0xbe00).equals(Rational.valueOf("-1.5")));
		Assert.assertTrue(Rational.valueOf("-1.5").halfBitsValue() == (short)0xbe00);
		Assert.assertTrue(Rational.valueOfHalfBits((short)0x3f00).equals(Rational.valueOf("1.75")));
		Assert.assertTrue(Rational.valueOf("1.75").halfBitsValue() == (short)0x3f00);
		Assert.assertTrue(Rational.valueOfHalfBits((short)0xbf00).equals(Rational.valueOf("-1.75")));
		Assert.assertTrue(Rational.valueOf("-1.75").halfBitsValue() == (short)0xbf00);

		// float (8 exponent bits)
		Assert.assertTrue(Rational.valueOfFloatBits(0).equals(Rational.valueOf("0")));
		Assert.assertTrue(Rational.valueOf("0").floatBitsValue() == 0);
		Assert.assertTrue(Rational.valueOfFloatBits(0x3f800000).equals(Rational.valueOf("1")));
		Assert.assertTrue(Rational.valueOf("1").floatBitsValue() == 0x3f800000);
		Assert.assertTrue(Rational.valueOfFloatBits(0xbf800000).equals(Rational.valueOf("-1")));
		Assert.assertTrue(Rational.valueOf("-1").floatBitsValue() == 0xbf800000);
		Assert.assertTrue(Rational.valueOfFloatBits(0x3fc00000).equals(Rational.valueOf("1.5")));
		Assert.assertTrue(Rational.valueOf("1.5").floatBitsValue() == 0x3fc00000);
		Assert.assertTrue(Rational.valueOfFloatBits(0xbfc00000).equals(Rational.valueOf("-1.5")));
		Assert.assertTrue(Rational.valueOf("-1.5").floatBitsValue() == 0xbfc00000);
		Assert.assertTrue(Rational.valueOfFloatBits(0x3fe00000).equals(Rational.valueOf("1.75")));
		Assert.assertTrue(Rational.valueOf("1.75").floatBitsValue() == 0x3fe00000);
		Assert.assertTrue(Rational.valueOfFloatBits(0xbfe00000).equals(Rational.valueOf("-1.75")));
		Assert.assertTrue(Rational.valueOf("-1.75").floatBitsValue() == 0xbfe00000);

		// double (11 exponent bits)
		Assert.assertTrue(Rational.valueOfDoubleBits(0).equals(Rational.valueOf("0")));
		Assert.assertTrue(Rational.valueOf("0").doubleBitsValue() == 0);
		Assert.assertTrue(Rational.valueOfDoubleBits(0x3ff0000000000000l).equals(Rational.valueOf("1")));
		Assert.assertTrue(Rational.valueOf("1").doubleBitsValue() == 0x3ff0000000000000l);
		Assert.assertTrue(Rational.valueOfDoubleBits(0xbff0000000000000l).equals(Rational.valueOf("-1")));
		Assert.assertTrue(Rational.valueOf("-1").doubleBitsValue() == 0xbff0000000000000l);
		Assert.assertTrue(Rational.valueOfDoubleBits(0x3ff8000000000000l).equals(Rational.valueOf("1.5")));
		Assert.assertTrue(Rational.valueOf("1.5").doubleBitsValue() == 0x3ff8000000000000l);
		Assert.assertTrue(Rational.valueOfDoubleBits(0xbff8000000000000l).equals(Rational.valueOf("-1.5")));
		Assert.assertTrue(Rational.valueOf("-1.5").doubleBitsValue() == 0xbff8000000000000l);
		Assert.assertTrue(Rational.valueOfDoubleBits(0x3ffc000000000000l).equals(Rational.valueOf("1.75")));
		Assert.assertTrue(Rational.valueOf("1.75").doubleBitsValue() == 0x3ffc000000000000l);
		Assert.assertTrue(Rational.valueOfDoubleBits(0xbffc000000000000l).equals(Rational.valueOf("-1.75")));
		Assert.assertTrue(Rational.valueOf("-1.75").doubleBitsValue() == 0xbffc000000000000l);

		// quadBitsEqual
		Assert.assertTrue(Rational.quadBitsEqual(new long[] {0xfffeffffffffffffl, 0xffffffffffffffffl,}, new long[] {0xfffeffffffffffffl, 0xffffffffffffffffl,}));
		Assert.assertTrue(!Rational.quadBitsEqual(new long[] {0xfffeffffffffffffl, 0xffffffffffffffffl,}, new long[] {0xffffffffffffffffl, 0xfffeffffffffffffl,}));

		// quad (15 exponent bits)
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOf("0").quadBitsValue(), new long[] {0, 0,}));
		Assert.assertTrue(Rational.valueOfQuadBits(new long[] {0x3fff000000000000l, 0,}).equals(Rational.valueOf("1")));
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOf("1").quadBitsValue(), new long[] {0x3fff000000000000l, 0,}));
		Assert.assertTrue(Rational.valueOfQuadBits(new long[] {0xbfff000000000000l, 0,}).equals(Rational.valueOf("-1")));
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOf("-1").quadBitsValue(), new long[] {0xbfff000000000000l, 0,}));
		Assert.assertTrue(Rational.valueOfQuadBits(new long[] {0x3fff800000000000l, 0,}).equals(Rational.valueOf("1.5")));
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOf("1.5").quadBitsValue(), new long[] {0x3fff800000000000l, 0,}));
		Assert.assertTrue(Rational.valueOfQuadBits(new long[] {0xbfff800000000000l, 0,}).equals(Rational.valueOf("-1.5")));
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOf("-1.5").quadBitsValue(), new long[] {0xbfff800000000000l, 0,}));
		Assert.assertTrue(Rational.valueOfQuadBits(new long[] {0x3fffc00000000000l, 0,}).equals(Rational.valueOf("1.75")));
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOf("1.75").quadBitsValue(), new long[] {0x3fffc00000000000l, 0,}));
		Assert.assertTrue(Rational.valueOfQuadBits(new long[] {0xbfffc00000000000l, 0,}).equals(Rational.valueOf("-1.75")));
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOf("-1.75").quadBitsValue(), new long[] {0xbfffc00000000000l, 0,}));

		// more quad tests
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOfQuadBits(new long[] {0x0000000000000000l, 0x0000000000000000l,}).quadBitsValue(), new long[] {0x0000000000000000l, 0x0000000000000000l,}));
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOfQuadBits(new long[] {0x0000000000000001l, 0x0000000000000000l,}).quadBitsValue(), new long[] {0x0000000000000001l, 0x0000000000000000l,}));
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOfQuadBits(new long[] {0x0000000000000000l, 0x8000000000000000l,}).quadBitsValue(), new long[] {0x0000000000000000l, 0x8000000000000000l,}));
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOfQuadBits(new long[] {0x0000000000000000l, 0x0000000000000001l,}).quadBitsValue(), new long[] {0x0000000000000000l, 0x0000000000000001l,}));
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOfQuadBits(new long[] {0x0000000000000002l, 0x0000000000000000l,}).quadBitsValue(), new long[] {0x0000000000000002l, 0x0000000000000000l,}));
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOfQuadBits(new long[] {0x0000000000000000l, 0x4000000000000000l,}).quadBitsValue(), new long[] {0x0000000000000000l, 0x4000000000000000l,}));
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOfQuadBits(new long[] {0x0000000000000000l, 0x0000000000000002l,}).quadBitsValue(), new long[] {0x0000000000000000l, 0x0000000000000002l,}));
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOfQuadBits(new long[] {0x7ffe000000000000l, 0x0000000000000000l,}).quadBitsValue(), new long[] {0x7ffe000000000000l, 0x0000000000000000l,}));
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOfQuadBits(new long[] {0x7ffe000000000001l, 0x0000000000000000l,}).quadBitsValue(), new long[] {0x7ffe000000000001l, 0x0000000000000000l,}));
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOfQuadBits(new long[] {0x7ffe000000000000l, 0x8000000000000000l,}).quadBitsValue(), new long[] {0x7ffe000000000000l, 0x8000000000000000l,}));
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOfQuadBits(new long[] {0x7ffe000000000000l, 0x0000000000000001l,}).quadBitsValue(), new long[] {0x7ffe000000000000l, 0x0000000000000001l,}));
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOfQuadBits(new long[] {0x7ffe000000000002l, 0x0000000000000000l,}).quadBitsValue(), new long[] {0x7ffe000000000002l, 0x0000000000000000l,}));
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOfQuadBits(new long[] {0x7ffe000000000000l, 0x4000000000000000l,}).quadBitsValue(), new long[] {0x7ffe000000000000l, 0x4000000000000000l,}));
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOfQuadBits(new long[] {0x7ffe000000000000l, 0x0000000000000002l,}).quadBitsValue(), new long[] {0x7ffe000000000000l, 0x0000000000000002l,}));

		// forth and back

		// most bits possible set (i.e. largest negative)
		Assert.assertTrue(Rational.valueOfHalfBits((short)0xfbff).halfBitsValue() == (short)0xfbff);
		Assert.assertTrue(Rational.valueOfFloatBits(0xff7fffff).floatBitsValue() == 0xff7fffff);
		Assert.assertTrue(Rational.valueOfDoubleBits(0xffefffffffffffffl).doubleBitsValue() == 0xffefffffffffffffl);
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOfQuadBits(new long[] {0xfffeffffffffffffl, 0xffffffffffffffffl,}).quadBitsValue(), new long[] {0xfffeffffffffffffl, 0xffffffffffffffffl,}));

		// smallest non-subnormal number
		Assert.assertTrue(Rational.valueOfHalfBits((short)0x0400).halfBitsValue() == (short)0x0400);
		Assert.assertTrue(Rational.valueOfFloatBits(0x00800000).floatBitsValue() == 0x00800000);
		Assert.assertTrue(Rational.valueOfDoubleBits(0x0010000000000000l).doubleBitsValue() == 0x0010000000000000l);
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOfQuadBits(new long[] {0x0001000000000000l, 0x0000000000000000l,}).quadBitsValue(), new long[] {0x0001000000000000l, 0x0000000000000000l,}));
		// largest subnormal number
		Assert.assertTrue(Rational.valueOfHalfBits((short)0x02ff).halfBitsValue() == (short)0x02ff);
		Assert.assertTrue(Rational.valueOfFloatBits(0x004fffff).floatBitsValue() == 0x004fffff);
		Assert.assertTrue(Rational.valueOfDoubleBits(0x0008ffffffffffffl).doubleBitsValue() == 0x0008ffffffffffffl);
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOfQuadBits(new long[] {0x00008fffffffffffl, 0xffffffffffffffffl,}).quadBitsValue(), new long[] {0x00008fffffffffffl, 0xffffffffffffffffl,}));
		// largest subnormal number with one bit set only
		Assert.assertTrue(Rational.valueOfHalfBits((short)0x0200).halfBitsValue() == (short)0x0200);
		Assert.assertTrue(Rational.valueOfFloatBits(0x00400000).floatBitsValue() == 0x00400000);
		Assert.assertTrue(Rational.valueOfDoubleBits(0x0008000000000000l).doubleBitsValue() == 0x0008000000000000l);
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOfQuadBits(new long[] {0x0000800000000000l, 0x0000000000000000l,}).quadBitsValue(), new long[] {0x0000800000000000l, 0x0000000000000000l,}));
		// one less of above
		Assert.assertTrue(Rational.valueOfHalfBits((short)0x01ff).halfBitsValue() == (short)0x01ff);
		Assert.assertTrue(Rational.valueOfFloatBits(0x003fffff).floatBitsValue() == 0x003fffff);
		Assert.assertTrue(Rational.valueOfDoubleBits(0x0007ffffffffffffl).doubleBitsValue() == 0x0007ffffffffffffl);
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOfQuadBits(new long[] {0x00007fffffffffffl, 0xffffffffffffffffl,}).quadBitsValue(), new long[] {0x00007fffffffffffl, 0xffffffffffffffffl,}));
		// half of above
		Assert.assertTrue(Rational.valueOfHalfBits((short)0x0100).halfBitsValue() == (short)0x0100);
		Assert.assertTrue(Rational.valueOfFloatBits(0x00200000).floatBitsValue() == 0x00200000);
		Assert.assertTrue(Rational.valueOfDoubleBits(0x0004000000000000l).doubleBitsValue() == 0x0004000000000000l);
		Assert.assertTrue(Rational.quadBitsEqual(Rational.valueOfQuadBits(new long[] {0x0000400000000000l, 0x0000000000000000l,}).quadBitsValue(), new long[] {0x0000400000000000l, 0x0000000000000000l,}));

		// round-off vs. exact
		Assert.assertTrue(Rational.valueOfHalfBits(Rational.valueOf("0.125").halfBitsValue()).equals(Rational.valueOf("0.125")));
		Assert.assertTrue(Rational.valueOfFloatBits(Rational.valueOf("0.125").floatBitsValue()).equals(Rational.valueOf("0.125")));
		Assert.assertTrue(Rational.valueOfDoubleBits(Rational.valueOf("0.125").doubleBitsValue()).equals(Rational.valueOf("0.125")));
		Assert.assertTrue(Rational.valueOfQuadBits(Rational.valueOf("0.125").quadBitsValue()).equals(Rational.valueOf("0.125")));
		Assert.assertTrue(!Rational.valueOfHalfBits(Rational.valueOf("0.1").halfBitsValue()).equals(Rational.valueOf("0.1")));
		Assert.assertTrue(!Rational.valueOfFloatBits(Rational.valueOf("0.1").floatBitsValue()).equals(Rational.valueOf("0.1")));
		Assert.assertTrue(!Rational.valueOfDoubleBits(Rational.valueOf("0.1").doubleBitsValue()).equals(Rational.valueOf("0.1")));
		Assert.assertTrue(!Rational.valueOfQuadBits(Rational.valueOf("0.1").quadBitsValue()).equals(Rational.valueOf("0.1")));
	}
}
