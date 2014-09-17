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

/* <b>Note:</b> Closely based on freely available version 'BigRational.java', developed
 * by Eric Laroche, which can be found at: <a
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
package com.sri.ai.util.math;

import java.math.BigInteger;

import com.google.common.annotations.Beta;

/**
 * Rational implements dynamically sized arbitrary precision immutable rational
 * numbers.<br>
 * <br>
 * 
 * <p>
 * Dynamically sized means that (provided enough memory) the Rational numbers
 * can't overflow (nor underflow); this characteristic is different from Java's
 * data types int and long, but common with BigInteger (which implements only
 * integer numbers, i.e. no fractions) and BigDecimal (which only implements
 * precisely rational numbers with denominators that are products of 2 and 5
 * [and implied 10]). Arbitrary precision means that there is no loss of
 * precision with common arithmetic operations such as addition, subtraction,
 * multiplication, and division (BigDecimal loses precision with division, if
 * factors other than 2 or 5 are involved). [Doubles and floats can overflow and
 * underflow, have a limited precision, and only implement precisely rationals
 * with a denominator that is a power of 2.]
 * 
 * <p>
 * Rational provides most operations needed in rational number space
 * calculations, including addition, subtraction, multiplication, division,
 * integer power, remainder/modulus, comparison, and different roundings.
 * 
 * <p>
 * Rational provides conversion methods to and from the native types long, int,
 * short, and byte (including support for unsigned values), double (binary64),
 * float (binary32), quad (binary128, quadruple), half (binary16), and
 * BigInteger. Rational can parse and print many string representations:
 * rational, dot notations, with exponent, even mixtures thereof, and supports
 * different radixes/bases (2 to typically 36 [limited by BigInteger parsing
 * implementation]).
 * 
 * <p>
 * Rational uses java.math.BigInteger (JDK 1.1 and later) internally.
 * 
 * <p>
 * Binary operations (e.g. add, multiply) calculate their results from a
 * Rational object ('this') and one [Rational or long] argument, returning a new
 * immutable Rational object. Both the original object and the argument are left
 * unchanged (hence immutable). Unary operations (e.g. negate, invert) calculate
 * their result from the Rational object ('this'), returning a new immutable
 * Rational object. The original object is left unchanged.
 * 
 * <p>
 * Most operations are precise (i.e. without loss of precision); exceptions are
 * the conversion methods to limited precision types (doubleValue, floatValue),
 * rounding (round), truncation (bigIntegerValue, floor, ceiling, truncate), as
 * well as obviously the printing methods that include a precision parameter
 * (toStringDot, toStringDotRelative, toStringExponent).
 * 
 * <p>
 * Rational doesn't provide a notion of "infinity" ([+-]Infinity) and
 * "not a number" (NaN); IEEE 754 floating point Infinity and NaN are rejected
 * (throwing a NumberFormatException). Operations such as 0/0 result in an
 * ArithmeticException.
 * 
 * <p>
 * Rational internally uses private proxy functions for BigInteger
 * functionality, including scanning and multiplying, to enhance speed and to
 * realize fast checks for common values (1, 0, etc.).
 * 
 * <p>
 * Constructor samples: normal rational form, abbreviated form, fixed point
 * form, abbreviated fixed point form, [exponential] floating point form,
 * different radixes/bases different from 10, doubles/floats:
 * 
 * <pre>
 * 	Rational("-21/35")              : rational -3/5
 * 	Rational("/3")                  : rational 1/3
 * 	Rational("3.4")                 : rational 17/5
 * 	Rational(".7")                  : 0.7, rational 7/10
 * 	Rational("-65.4E-3")            : -327/5000
 * 	Rational("f/37", 0x10)          : 3/11
 * 	Rational("f.37", 0x10)          : 3895/256
 * 	Rational("-dcba.efgh", 23)      : -46112938320/279841
 * 	Rational("1011101011010110", 2) : 47830
 * 	Rational(StrictMath.E)          : 6121026514868073/2251799813685248
 * 	Rational((float)StrictMath.E)   : 2850325/1048576
 * </pre>
 * 
 * <p>
 * Also accepted are denormalized representations such as:
 * 
 * <pre>
 * 	Rational("2.5/-3.5"): -5/7
 * 	Rational("12.34E-1/-56.78E-1")  : -617/2839
 * </pre>
 * 
 * <p>
 * Printing: rational form, fixed point (dot) forms with different absolute
 * precisions (including negative precision), with relative precisions,
 * exponential form, different radix:
 * 
 * <pre>
 * 	Rational("1234.5678")                            : "6172839/5000"
 * 	Rational("1234.5678").toStringDot(6)             : "1234.567800"
 * 	Rational("1234.5678").toStringDot(2)             : "1234.57"
 * 	Rational("1234.5678").toStringDot(-2)            : "1200"
 * 	Rational("1234.5678").toStringDotRelative(6)     : "1234.57"
 * 	Rational("0.00012345678").toStringDotRelative(3) : "0.000123"
 * 	Rational("1234.5678").toStringExponent(2)        : "1.2E3"
 * 	Rational("1011101011010110", 2).toString(0x10)   : "bad6"
 * </pre>
 * 
 * <p>
 * Usage: Rational operations can be conveniently chained (sample from Rational
 * internal conversion from IEEE 754 bits):
 * 
 * <pre>
 * Rational.valueOf(2).power(exponent)
 * 		.multiply(fraction.add(Rational.valueOfUnsigned(mantissa)))
 * 		.multiply(sign);
 * </pre>
 * 
 * @author Eric Laroche &lt;laroche@lrdev.com&gt;
 * @author oreilly
 * 
 */
@Beta
public class Rational extends Number implements Cloneable, Comparable<Object> {
	//
	// PRIVATE CONSTANTS
	// Note: Need to declare first as some of the public
	// constants are dependent on these being initialized beforehand.
	private static final long serialVersionUID = 1L;

	//
	// Note: following constants can't be constructed using
	// Rational.bigIntegerValueOf().
	// That one _uses_ the constants (avoid circular dependencies).
	/**
	 * Constant internally used, for convenience and speed. Used as zero
	 * numerator. Used for fast checks.
	 */
	private final static BigInteger BIG_INTEGER_ZERO = BigInteger.valueOf(0);

	/**
	 * Constant internally used, for convenience and speed. Used as neutral
	 * denominator. Used for fast checks.
	 */
	private final static BigInteger BIG_INTEGER_ONE = BigInteger.valueOf(1);

	/**
	 * Constant internally used, for convenience and speed. Used for fast
	 * checks.
	 */
	private final static BigInteger BIG_INTEGER_MINUS_ONE = BigInteger
			.valueOf(-1);

	/**
	 * Constant internally used, for convenience and speed. Used in rounding
	 * zero numerator. _Not_ used for checks.
	 */
	private final static BigInteger BIG_INTEGER_TWO = BigInteger.valueOf(2);

	/**
	 * Constant internally used, for convenience and speed. _Not_ used for
	 * checks.
	 */
	private final static BigInteger BIG_INTEGER_MINUS_TWO = BigInteger
			.valueOf(-2);

	/**
	 * Constant internally used, for convenience and speed. Corresponds to
	 * DEFAULT_RADIX, used in reading, scaling and printing. _Not_ used for
	 * checks.
	 */
	private final static BigInteger BIG_INTEGER_TEN = BigInteger.valueOf(10);

	/**
	 * Constant internally used, for convenience and speed. Used in reading,
	 * scaling and printing. _Not_ used for checks.
	 */
	private final static BigInteger BIG_INTEGER_SIXTEEN = BigInteger
			.valueOf(16);

	/**
	 * The constant two to the power of 64 (18446744073709551616). Used is
	 * slicing larger [than double size] IEEE 754 values.
	 */
	private final static BigInteger BIG_INTEGER_TWO_POWER_64 = BigInteger
			.valueOf(2).pow(64);

	// some more constants, often used as radixes/bases

	/**
	 * The constant two (2).
	 */
	private final static Rational TWO = new Rational(2);

	/**
	 * The constant ten (10).
	 */
	private final static Rational TEN = new Rational(10);

	/**
	 * The constant sixteen (16).
	 */
	private final static Rational SIXTEEN = new Rational(16);

	/**
	 * The constant two to the power of 64 (18446744073709551616). Used is
	 * slicing larger [than double size] IEEE 754 values.
	 */
	private final static Rational TWO_POWER_64 = new Rational(
			BIG_INTEGER_TWO_POWER_64);

	/**
	 * Constant internally used, for speed.
	 */
	// calculated via Rational((float)(StrictMath.log(10)/StrictMath.log(2)))
	// note: don't use float/double operations in this code though (except for
	// test())
	private final static Rational LOGARITHM_TEN_GUESS = new Rational(1741647,
			524288);

	/**
	 * Constant internally used, for speed.
	 */
	private final static Rational LOGARITHM_SIXTEEN = new Rational(4);

	/**
	 * Number of explicit fraction bits in an IEEE 754 double (binary64) float,
	 * 52.
	 */
	private final static int DOUBLE_FLOAT_FRACTION_SIZE = 52;

	/**
	 * Number of exponent bits in an IEEE 754 double (binary64) float, 11.
	 */
	private final static int DOUBLE_FLOAT_EXPONENT_SIZE = 11;

	/**
	 * Number of explicit fraction bits in an IEEE 754 single (binary32) float,
	 * 23.
	 */
	private final static int SINGLE_FLOAT_FRACTION_SIZE = 23;

	/**
	 * Number of exponent bits in an IEEE 754 single (binary32) float, 8.
	 */
	private final static int SINGLE_FLOAT_EXPONENT_SIZE = 8;

	/**
	 * Number of explicit fraction bits in an IEEE 754 half (binary16) float,
	 * 10.
	 */
	private final static int HALF_FLOAT_FRACTION_SIZE = 10;

	/**
	 * Number of exponent bits in an IEEE 754 half (binary16) float, 5.
	 */
	private final static int HALF_FLOAT_EXPONENT_SIZE = 5;

	/**
	 * Number of explicit fraction bits in an IEEE 754 quad (binary128,
	 * quadruple) float, 112.
	 */
	private final static int QUAD_FLOAT_FRACTION_SIZE = 112;

	/**
	 * Number of exponent bits in an IEEE 754 quad (binary128, quadruple) float,
	 * 15.
	 */
	private final static int QUAD_FLOAT_EXPONENT_SIZE = 15;

	//
	//
	/**
	 * Numerator. Numerator may be negative. Numerator may be zero, in which
	 * case m_q must be one. [Conditions are put in place by normalize().]
	 */
	private BigInteger numerator;
	/**
	 * Denominator (quotient). Denominator is never negative and never zero.
	 * [Conditions are put in place by normalize().]
	 */
	private BigInteger denominator;
	// optimization, as instances are immmutable only
	// calculate once when needed
	private int hashCode = 0;

	//
	// PUBLIC CONSTANTS
	//
	/**
	 * Default radix, used in string printing and scanning, 10 (i.e. decimal by
	 * default).
	 */
	public final static int DEFAULT_RADIX = 10;

	// Note: don't use valueOf() here; valueOf implementations use the constants

	/**
	 * The constant zero (0).
	 */
	// [Constant name: see class BigInteger.]
	public final static Rational ZERO = new Rational(0);

	/**
	 * The constant one (1).
	 */
	// [Name: see class BigInteger.]
	public final static Rational ONE = new Rational(1);

	/**
	 * The constant minus-one (-1).
	 */
	public final static Rational MINUS_ONE = new Rational(-1);

	/**
	 * Rounding mode to round away from zero.
	 */
	public final static int ROUND_UP = 0;

	/**
	 * Rounding mode to round towards zero.
	 */
	public final static int ROUND_DOWN = 1;

	/**
	 * Rounding mode to round towards positive infinity.
	 */
	public final static int ROUND_CEILING = 2;

	/**
	 * Rounding mode to round towards negative infinity.
	 */
	public final static int ROUND_FLOOR = 3;

	/**
	 * Rounding mode to round towards nearest neighbor unless both neighbors are
	 * equidistant, in which case to round up.
	 */
	public final static int ROUND_HALF_UP = 4;

	/**
	 * Rounding mode to round towards nearest neighbor unless both neighbors are
	 * equidistant, in which case to round down.
	 */
	public final static int ROUND_HALF_DOWN = 5;

	/**
	 * Rounding mode to round towards the nearest neighbor unless both neighbors
	 * are equidistant, in which case to round towards the even neighbor.
	 */
	public final static int ROUND_HALF_EVEN = 6;

	/**
	 * Rounding mode to assert that the requested operation has an exact result,
	 * hence no rounding is necessary. If this rounding mode is specified on an
	 * operation that yields an inexact result, an ArithmeticException is
	 * thrown.
	 */
	public final static int ROUND_UNNECESSARY = 7;

	/**
	 * Rounding mode to round towards nearest neighbor unless both neighbors are
	 * equidistant, in which case to round ceiling.
	 */
	public final static int ROUND_HALF_CEILING = 8;

	/**
	 * Rounding mode to round towards nearest neighbor unless both neighbors are
	 * equidistant, in which case to round floor.
	 */
	public final static int ROUND_HALF_FLOOR = 9;

	/**
	 * Rounding mode to round towards the nearest neighbor unless both neighbors
	 * are equidistant, in which case to round towards the odd neighbor.
	 */
	public final static int ROUND_HALF_ODD = 10;

	/**
	 * Default round mode, ROUND_HALF_UP.
	 */
	public final static int DEFAULT_ROUND_MODE = ROUND_HALF_UP;

	//
	// PUBLIC METHODS
	//

	//
	// START - Constructors

	/**
	 * Construct a Rational from numerator and denominator. Both numerator and
	 * denominator may be negative. numerator/denominator may be denormalized
	 * (i.e. have common factors, or denominator being negative).
	 * 
	 * @param numerator
	 *            the rational's numerator.
	 * @param denominator
	 *            the rational's denominator (quotient)
	 */
	public Rational(BigInteger numerator, BigInteger denominator) {
		// note: check for denominator==null done later
		if (denominator != null && bigIntegerIsZero(denominator)) {
			throw new NumberFormatException("Denominator zero");
		}

		normalizeFrom(numerator, denominator);
	}

	/**
	 * Construct a Rational from a numerator only, denominator is defaulted to
	 * 1.
	 * 
	 * @param numerator
	 *            the rational's numerator.
	 */
	public Rational(BigInteger numerator) {
		this(numerator, BIG_INTEGER_ONE);
	}

	/**
	 * Construct a Rational from long fix number integers representing numerator
	 * and denominator.
	 * 
	 * @param numerator
	 *            the rational's numerator.
	 * @param denominator
	 *            the rational's denominator (quotient)
	 */
	public Rational(long numerator, long denominator) {
		this(bigIntegerValueOf(numerator), bigIntegerValueOf(denominator));
	}

	/**
	 * Construct a Rational from a long fix number integer representing
	 * numerator, denominator is defaulted to 1.
	 * 
	 * @param numerator
	 *            the rational's numerator.
	 */
	public Rational(long numerator) {
		this(bigIntegerValueOf(numerator), BIG_INTEGER_ONE);
	}

	/**
	 * Clone a Rational.
	 * <p>
	 * [As Rationals are immutable, this copy-constructor is not that useful.]
	 * 
	 * @param that
	 *        a rational value that the newly constructed rational is to be a clone of.
	 */
	public Rational(Rational that) {
		normalizeFrom(that);
	}

	/**
	 * Construct a Rational from a string representation.
	 * 
	 * <pre>
	 * The supported string formats are:
	 * "[+-]numerator" 
	 * "[+-]numerator/[+-]denominator" 
	 * "[+-]i.f"
	 * "[+-]i" 
	 * "[+-]iE[+-]e" 
	 * "[+-]i.fE[+-]e" 
	 * (latter two only with radix &lt;= 10, due to possible ambiguities); 
	 * numerator and denominator can be any of the
	 * latter (i.e. mixed representations such as "-1.2E-3/-4.5E-6" are
	 * supported).
	 * 
	 * Samples: "-21/35", "3.4", "-65.4E-3", "f/37" (base 16),
	 * "1011101011010110" (base 2).
	 * </pre>
	 * 
	 * @param strRational
	 *            a string prepresentation of a Rational number.
	 * @param radix
	 *            the radix the string representation is meant to be in.
	 */
	public Rational(String strRational, int radix) {
		if (strRational == null) {
			throw new NumberFormatException("null");
		}
		
		// For simplicity remove leading and trailing white spaces.
		strRational = strRational.trim();
		
		// Within AIC-SMF we do not want rational to consider these
		// as special legal default formats (original BigRational allowed these).
		if (strRational.equals("+") || strRational.equals("-") || strRational.equals("/") ||
			strRational.equals(".") || strRational.equals("") || 
			strRational.startsWith("E") || strRational.startsWith("e")) {
			throw new NumberFormatException("underspecificed rational:"+strRational);
		}

		// '/': least precedence, and left-to-right associative
		// (therefore lastIndexOf and not indexOf: last slash has least
		// precedence)
		final int slash = strRational.lastIndexOf('/');
		if (slash != -1) {

			// "[+-]numerator/[+-]denominator"
			String strNumerator = strRational.substring(0, slash);
			String strDenominator = strRational.substring(slash + 1);

			// suppress recursion: make stack-overflow attacks infeasible
			if (strNumerator.indexOf('/') != -1) {
				throw new NumberFormatException("can't nest '/'");
			}

			// handle "/x" as "1/x"
			// [note: "1" and "-1" are treated specially and optimized
			// in Rational.bigIntegerValueOf(String,int).]
			if (strNumerator.equals("") || strNumerator.equals("+")) {
				strNumerator = "1";
			} else if (strNumerator.equals("-")) {
				strNumerator = "-1";
			}

			// handle "x/" as "x"
			// [note: "1" and "-1" are treated special and optimized
			// in Rational.bigIntegerValueOf(String,int).]
			if (strDenominator.equals("") || strDenominator.equals("+")) {
				strDenominator = "1";
			} else if (strDenominator.equals("-")) {
				strDenominator = "-1";
			}

			// [recursion]
			// [divide()'s outcome is already normalized,
			// so there would be no need to normalize [again]]
			normalizeFrom((new Rational(strNumerator, radix))
					.divide(new Rational(strDenominator, radix)));

			return;
		}

		checkRadix(radix);

		// catch Java's string representations of
		// doubles/floats unsupported by Rational
		checkNaNAndInfinity(strRational, radix);

		// [if radix<=10:] 'E': next higher precedence, not associative
		// or right-to-left associative
		int exp = -1;
		// note: a distinction of exponent-'E' from large-base-digit-'e'
		// would be unintuitive, since case doesn't matter with both uses
		if (radix <= 10) {
			// handle both [upper/lower] cases
			final int exp1 = strRational.indexOf('E');
			final int exp2 = strRational.indexOf('e');
			exp = (exp1 == -1 || (exp2 != -1 && exp2 < exp1) ? exp2 : exp1);
		}

		if (exp != -1) {
			String strMantissa = strRational.substring(0, exp);
			String strExponent = strRational.substring(exp + 1);

			// suppress recursion: make stack-overflow attacks infeasible
			if (strExponent.indexOf('E') != -1
					|| strExponent.indexOf('e') != -1) {
				throw new NumberFormatException("can't nest 'E'");
			}

			// skip '+'
			if (strExponent.length() > 0 && strExponent.charAt(0) == '+') {
				strExponent = strExponent.substring(1);
			}

			// handle '-'
			boolean negateTheExponent = false;
			if (strExponent.length() > 0 && strExponent.charAt(0) == '-') {
				negateTheExponent = true;
				strExponent = strExponent.substring(1);
			}

			// handle "xE", "xE+", "xE-", as "xE0" aka "x"
			if (strExponent.equals("")) {
				strExponent = "0";
			}

			// [recursion]
			Rational exponent = new Rational(strExponent, radix);

			final int iexponent;
			// transform possible [overflow/fraction] exception
			try {
				iexponent = exponent.intValueExact();
			} catch (ArithmeticException e) {
				final NumberFormatException e2 = new NumberFormatException(
						e.getMessage());
				// make sure this operation doesn't shadow the exception to be
				// thrown
				try {
					e2.initCause(e);
				} catch (Throwable e3) {
					throw e2;
				}
				throw e2;
			}
			exponent = valueOf(radix).pow(iexponent);
			if (negateTheExponent) {
				exponent = exponent.invert();
			}

			// handle "Ex", "+Ex", "-Ex", as "1Ex"
			if (strMantissa.equals("") || strMantissa.equals("+")) {
				strMantissa = "1";
			} else if (strMantissa.equals("-")) {
				strMantissa = "-1";
			}

			// [multiply()'s outcome is already normalized,
			// so there would be no need to normalize [again]]
			normalizeFrom((new Rational(strMantissa, radix)).multiply(exponent));

			return;
		}

		// '.': next higher precedence, not associative
		// (can't have more than one dot)
		String strIntegerPart, strFractionPart;
		final int dot = strRational.indexOf('.');
		if (dot != -1) {
			// "[+-]i.f"
			strIntegerPart = strRational.substring(0, dot);
			strFractionPart = strRational.substring(dot + 1);
		} 
		else {
			// "[+-]i". [not just delegating to BigInteger.]
			strIntegerPart = strRational;
			strFractionPart = "";
		}

		// check for multiple signs or embedded signs
		checkNumberFormat(strIntegerPart);

		// skip '+'
		// skip '+'. [BigInteger [likely] doesn't handle these.]
		if (strIntegerPart.length() > 0 && strIntegerPart.charAt(0) == '+') {
			strIntegerPart = strIntegerPart.substring(1);
		}

		// handle '-'
		boolean negativeIntegerPart = false;
		if (strIntegerPart.length() > 0 && strIntegerPart.charAt(0) == '-') {
			negativeIntegerPart = true;
			strIntegerPart = strIntegerPart.substring(1);
		}

		// handle ".x" as "0.x" ("." as "0.0")
		// handle "" as "0"
		// note: "0" is treated specially and optimized
		// in Rational.bigIntegerValueOf(String,int).
		if (strIntegerPart.equals("")) {
			strIntegerPart = "0";
		}

		BigInteger numerator = bigIntegerValueOf(strIntegerPart, radix);
		BigInteger denominator;

		// includes the cases "x." and "."
		if (!strFractionPart.equals("")) {
			// check for signs
			checkFractionFormat(strFractionPart);

			final BigInteger fraction = bigIntegerValueOf(strFractionPart,
					radix);
			final int scale = strFractionPart.length();
			denominator = bigIntegerPower(bigIntegerValueOf(radix), scale);
			numerator = bigIntegerMultiply(numerator, denominator)
					.add(fraction);
		} 
		else {
			denominator = BIG_INTEGER_ONE;
		}

		if (negativeIntegerPart) {
			numerator = numerator.negate();
		}

		normalizeFrom(numerator, denominator);
	}

	/**
	 * Construct a Rational from a string representation using DEFAULT_RADIX.
	 * 
	 * <pre>
	 * The supported string formats are:
	 * "[+-]numerator" 
	 * "[+-]numerator/[+-]denominator" 
	 * "[+-]i.f"
	 * "[+-]i" 
	 * "[+-]iE[+-]e" 
	 * "[+-]i.fE[+-]e" 
	 * (latter two only with radix &lt;= 10, due to possible ambiguities); 
	 * numerator and denominator can be any of the
	 * latter (i.e. mixed representations such as "-1.2E-3/-4.5E-6" are
	 * supported).
	 * 
	 * Samples: "-21/35", "3.4", "-65.4E-3", "f/37" (base 16),
	 * "1011101011010110" (base 2).
	 * </pre>
	 * 
	 * @param strRational
	 *            a string prepresentation of a Rational number.
	 */
	public Rational(String strRational) {
		this(strRational, DEFAULT_RADIX);
	}

	/**
	 * Construct a Rational from an unscaled value and a scale value.
	 * 
	 * @param unscaledValue
	 *            an unscaled value representation of a Rational.
	 * @param scale
	 *            the scale to be associated with the unscaledValue
	 * @param radix
	 *            the radix the rational is meant to be in.
	 */
	public Rational(BigInteger unscaledValue, int scale, int radix) {
		if (unscaledValue == null) {
			throw new NumberFormatException("null");
		}

		final boolean negate = (scale < 0);
		if (negate) {
			scale = -scale;
		}

		checkRadix(radix);
		final BigInteger scaleValue = bigIntegerPower(bigIntegerValueOf(radix),
				scale);

		normalizeFrom((negate ? bigIntegerMultiply(unscaledValue, scaleValue)
				: unscaledValue), (negate ? BIG_INTEGER_ONE : scaleValue));
	}

	/**
	 * Construct a Rational from an unscaled value and a scale value, default
	 * radix (10).
	 * 
	 * @param unscaledValue
	 *            an unscaled value representation of a Rational.
	 * @param scale
	 *            the scale to be associated with the unscaledValue
	 */
	public Rational(BigInteger unscaledValue, int scale) {
		this(unscaledValue, scale, DEFAULT_RADIX);
	}

	/**
	 * Construct a Rational from an unscaled fix number value and a scale value.
	 * 
	 * @param unscaledValue
	 *            an unscaled value representation of a Rational.
	 * @param scale
	 *            the scale to be associated with the unscaledValue
	 * @param radix
	 *            the radix the rational is meant to be in.
	 */
	public Rational(long unscaledValue, int scale, int radix) {
		this(bigIntegerValueOf(unscaledValue), scale, radix);
	}

	/**
	 * Construct a Rational from a [IEEE 754] double [size/precision] floating
	 * point number.
	 * 
	 * @param value
	 *            double value from which a Rational is to be constructed.
	 */
	public Rational(double value) {
		normalizeFrom(valueOfDoubleBits(Double.doubleToLongBits(value)));
	}

	/**
	 * Construct a Rational from a [IEEE 754] single [size/precision] floating
	 * point number.
	 * 
	 * @param value
	 *            double value from which a Rational is to be constructed.
	 */
	public Rational(float value) {
		normalizeFrom(valueOfFloatBits(Float.floatToIntBits(value)));
	}

	// END - Constructors
	//

	/**
	 * Positive predicate.
	 * <p>
	 * Indicates whether this Rational is larger than zero. Zero is not
	 * positive.
	 * <p>
	 * [For convenience.]
	 * 
	 * @return true if the rational is larger than zero.
	 */
	public boolean isPositive() {
		return (signum() > 0);
	}

	/**
	 * Negative predicate.
	 * <p>
	 * Indicates whether this Rational is smaller than zero. Zero isn't negative
	 * either.
	 * <p>
	 * [For convenience.]
	 * 
	 * @return true is the rational is smaller than zero.
	 */
	public boolean isNegative() {
		return (signum() < 0);
	}

	/**
	 * Zero predicate.
	 * <p>
	 * Indicates whether this Rational is zero.
	 * <p>
	 * [For convenience and speed.]
	 * 
	 * @return true if the rational is zero.
	 */
	public boolean isZero() {
		// optimization, first test is for speed.
		if (this == ZERO || numerator == BIG_INTEGER_ZERO) {
			return true;
		}

		// well, this is also optimized for speed a bit.
		return (signum() == 0);
	}

	/**
	 * One predicate.
	 * <p>
	 * Indicates whether this Rational is 1.
	 * <p>
	 * [For convenience and speed.]
	 * 
	 * @return true if the rational is 1.
	 */
	public boolean isOne() {
		// optimization
		// first test is for speed.
		if (this == ONE) {
			return true;
		}

		return equals(ONE);
	}

	/**
	 * Minus-one predicate.
	 * <p>
	 * Indicates whether this Rational is -1.
	 * <p>
	 * [For convenience and speed.]
	 * 
	 * @return true if the rational is -1;
	 */
	public boolean isMinusOne() {
		// optimization
		// first test is for speed.
		if (this == MINUS_ONE) {
			return true;
		}

		return equals(MINUS_ONE);
	}

	/**
	 * Integer predicate.
	 * <p>
	 * Indicates whether this Rational convertible to a BigInteger without loss
	 * of precision. True iff denominator/quotient is one.
	 * 
	 * @return true if the rational is an integer.
	 */
	public boolean isInteger() {
		return bigIntegerIsOne(denominator);
	}

	/**
	 * Rational string representation, format "[-]numerator[/denominator]".
	 * <p>
	 * Sample output: "6172839/5000".
	 * 
	 * @param radix
	 *        the radix to use.
	 * @return a string representation of the rational.
	 */
	public String toString(int radix) {
		checkRadixArgument(radix);
		final String s = stringValueOf(numerator, radix);

		if (isInteger()) {
			return s;
		}

		return s + "/" + stringValueOf(denominator, radix);
	}

	/**
	 * Rational string representation, format "[-]numerator[/denominator]",
	 * default radix (10).
	 * <p>
	 * Default string representation, as rational, not using an exponent.
	 * <p>
	 * Sample output: "6172839/5000".
	 * <p>
	 * Overwrites Object.toString().
	 * 
	 * @return a default string representation of the rational.
	 */
	@Override
	public String toString() {
		return toString(DEFAULT_RADIX);
	}

	/**
	 * Fixed dot-format "[-]i.f" string representation, with a precision.
	 * <p>
	 * Precision may be negative, in which case the rounding affects digits left
	 * of the dot, i.e. the integer part of the number, as well.
	 * <p>
	 * Sample output: "1234.567800".
	 * <p>
	 * Possible loss of precision.
	 * 
	 * @param precision
	 *        the precision to use.
	 * @param radix
	 *        the radix to use.
	 * @return a fixed dot-format string representation of the rational.
	 */
	// @PrecisionLoss
	public String toStringDot(int precision, int radix) {
		return toStringDot(precision, radix, false);
	}

	/**
	 * Dot-format "[-]i.f" string representation, with a precision, default
	 * radix (10). Precision may be negative.
	 * <p>
	 * Sample output: "1234.567800".
	 * <p>
	 * Possible loss of precision.
	 * 
	 * @param precision
	 *        the precision to use.
	 * @return a fixed dot-format string representation of the rational.
	 */
	// @PrecisionLoss
	public String toStringDot(int precision) {
		// [possible loss of precision step]
		return toStringDot(precision, DEFAULT_RADIX, false);
	}

	// note: there is no 'default' precision.

	/**
	 * Dot-format "[-]i.f" string representation, with a relative precision.
	 * <p>
	 * If the relative precision is zero or negative, "0" will be returned (i.e.
	 * total loss of precision).
	 * <p>
	 * Possible loss of precision.
	 * 
	 * @param precision
	 *        the precision to use.
	 * @param radix
	 *        the radix to use.
	 * @return a fixed dot-format string representation of the rational.
	 */
	// @PrecisionLoss
	public String toStringDotRelative(int precision, int radix) {
		// kind of expensive, due to expensive logarithm implementation
		// (with unusual radixes), and post processing

		checkRadixArgument(radix);

		// zero doesn't have any significant digits
		if (isZero()) {
			return "0";
		}

		// relative precision zero [or less means]: no significant digits at
		// all, i.e. 0
		// [loss of precision step]
		if (precision <= 0) {
			return "0";
		}

		// guessed [see below: rounding issues] length: sign doesn't matter;
		// one digit more than logarithm
		final int guessedLength = abs().logarithm(radix) + 1;
		// [possible loss of precision step]
		String s = toStringDot(precision - guessedLength, radix);

		// [floor of] logarithm and [arithmetic] rounding [half-up]
		// need post-processing:

		// find first significant digit and check for dot
		boolean dot = false;
		int i;
		for (i = 0; i < s.length(); i++) {
			final char c = s.charAt(i);
			if (c == '.') {
				dot = true;
			}
			// expecting nothing than '-', '.', and digits
			if (c != '-' && c != '.' && c != '0') {
				break;
			}
		}

		// count digits / [still] check for dot
		int digits = 0;
		for (; i < s.length(); i++) {
			if (s.charAt(i) == '.') {
				dot = true;
			} 
			else {
				digits++;
			}
		}

		// cut excess zeros
		// expecting at most 1 excess zero, e.g. for "0.0099999"
		final int excess = digits - precision;
		if (dot && excess > 0) {
			s = s.substring(0, s.length() - excess);
		}

		return s;
	}

	/**
	 * Dot-format "[-]i.f" string representation, with a relative precision,
	 * default radix (10).
	 * <p>
	 * If the relative precision is zero or negative, "0" will be returned (i.e.
	 * total loss of precision).
	 * <p>
	 * Possible loss of precision.
	 * 
	 * @param precision
	 *        the precision to use.
	 * @return a fixed dot-format string representation of the rational.
	 */
	// @PrecisionLoss
	public String toStringDotRelative(int precision) {
		// [possible loss of precision step]
		return toStringDotRelative(precision, DEFAULT_RADIX);
	}

	/**
	 * Exponent-format string representation, with a relative precision,
	 * "[-]i[.f]E[-]e" (where i is one digit other than 0 exactly; f has no
	 * trailing 0);
	 * <p>
	 * Sample output: "1.2E3".
	 * <p>
	 * Possible loss of precision.
	 * @param precision
	 *        the precision to use.
	 * @param radix
	 *        the radix to use.
	 * @return an exponent-format string representation of the rational.
	 */
	// @PrecisionLoss
	public String toStringExponent(int precision, int radix) {
		checkRadixArgument(radix);

		// zero doesn't have any significant digits
		if (isZero()) {
			return "0";
		}

		// relative precision zero [or less means]: no significant digits at
		// all, i.e. 0
		// [loss of precision step]
		if (precision <= 0) {
			return "0";
		}

		// guessed [see below: rounding issues] length: sign doesn't matter;
		// one digit more than logarithm
		final int guessedLength = abs().logarithm(radix) + 1;
		// [possible loss of precision step]
		final String s = toStringDot(precision - guessedLength, radix, true);
		return toExponentRepresentation(s, radix);
	}

	/**
	 * Exponent-format string representation, with a relative precision, default
	 * radix (10), "[-]i[.f]E[-]e" (where i is one digit other than 0 exactly; f
	 * has no trailing 0);
	 * <p>
	 * Sample output: "1.2E3".
	 * <p>
	 * Possible loss of precision.
	 * @param precision
	 *        the precision to use.
	 * @return an exponent-format string representation of the rational.

	 */
	// @PrecisionLoss
	public String toStringExponent(int precision) {
		// [possible loss of precision step]
		return toStringExponent(precision, DEFAULT_RADIX);
	}

	/**
	 * Add a Rational to this Rational and return a new Rational.
	 * <p>
	 * If one of the operands is zero, [as an optimization] the other Rational
	 * is returned.
	 * 
	 * @param that
	 *        the rational to be added to this rational.
	 * @return a new Rational that is the addition of this and that.
	 */
	// [Name: see class BigInteger.]
	public Rational add(Rational that) {
		// optimization: second operand is zero (i.e. neutral element).
		if (that.isZero()) {
			return this;
		}

		// optimization: first operand is zero (i.e. neutral element).
		if (isZero()) {
			return that;
		}

		// note: not checking for that.equals(negate()),
		// since that would involve creation of a temporary object

		// note: the calculated numerator/denominator may be denormalized,
		// implicit normalize() is needed.

		// optimization: same denominator.
		if (bigIntegerEquals(denominator, that.denominator)) {
			return new Rational(numerator.add(that.numerator), denominator);
		}

		// optimization: second operand is an integer.
		if (that.isInteger()) {
			return new Rational(numerator.add(that.numerator
					.multiply(denominator)), denominator);
		}

		// optimization: first operand is an integer.
		if (isInteger()) {
			return new Rational(numerator.multiply(that.denominator).add(
					that.numerator), that.denominator);
		}

		// default case. [this would handle all cases.]
		return new Rational(numerator.multiply(that.denominator).add(
				that.numerator.multiply(denominator)),
				denominator.multiply(that.denominator));
	}

	/**
	 * Add a long fix number integer to this Rational and return a new Rational.
	 * 
	 * @param that
	 *        a long to be added to this rational.
	 * @return a new Rational that is the addition of this and that.
	 */
	public Rational add(long that) {
		return add(valueOf(that));
	}

	/**
	 * Subtract a Rational from this Rational and return a new Rational.
	 * <p>
	 * If the second operand is zero, [as an optimization] this Rational is
	 * returned.
	 * 
	 * @param that
	 *        the rational to be subtracted from this rational.
	 * @return a new Rational that is the result of subtracting that from this.
	 */
	// [Name: see class BigInteger.]
	public Rational subtract(Rational that) {
		// optimization: second operand is zero.
		if (that.isZero()) {
			return this;
		}

		// optimization: first operand is zero
		if (isZero()) {
			return that.negate();
		}

		// optimization: operands are equal
		if (equals(that)) {
			return ZERO;
		}

		// note: the calculated n/q may be denormalized,
		// implicit normalize() is needed.

		// optimization: same denominator.
		if (bigIntegerEquals(denominator, that.denominator)) {
			return new Rational(numerator.subtract(that.numerator), denominator);
		}

		// optimization: second operand is an integer.
		if (that.isInteger()) {
			return new Rational(numerator.subtract(that.numerator
					.multiply(denominator)), denominator);
		}

		// optimization: first operand is an integer.
		if (isInteger()) {
			return new Rational(numerator.multiply(that.denominator).subtract(
					that.numerator), that.denominator);
		}

		// default case. [this would handle all cases.]
		return new Rational(numerator.multiply(that.denominator).subtract(
				that.numerator.multiply(denominator)),
				denominator.multiply(that.denominator));
	}

	/**
	 * Subtract a long fix number integer from this Rational and return a new
	 * Rational.
	 *
	 * @param that
	 *        the long value to be subtracted from this rational.
	 * @return a new Rational that is the result of subtracting that from this.
	 */
	public Rational subtract(long that) {
		return subtract(valueOf(that));
	}

	/**
	 * Multiply a Rational to this Rational and return a new Rational.
	 * <p>
	 * If one of the operands is one, [as an optimization] the other Rational is
	 * returned.
	 * 
	 * @param that
	 *        the rational to be multiplied with this rational.
	 * @return a new Rational that is the result of multiplying this with that.
	 */
	// [Name: see class BigInteger.]
	public Rational multiply(Rational that) {
		// optimization: one or both operands are zero.
		if (that.isZero() || isZero()) {
			return ZERO;
		}

		// optimization: second operand is 1.
		if (that.isOne()) {
			return this;
		}

		// optimization: first operand is 1.
		if (isOne()) {
			return that;
		}

		// optimization: second operand is -1.
		if (that.isMinusOne()) {
			return negate();
		}

		// optimization: first operand is -1.
		if (isMinusOne()) {
			return that.negate();
		}

		// note: the calculated numerator/denominator may be denormalized,
		// implicit normalize() is needed.

		return new Rational(bigIntegerMultiply(numerator, that.numerator),
				bigIntegerMultiply(denominator, that.denominator));
	}

	/**
	 * Multiply a long fix number integer to this Rational and return a new
	 * Rational.
	 * 
	 * @param that
	 *        the long to be multiplied with this rational.
	 * @return a new Rational that is the result of multiplying this with that.
	 */
	public Rational multiply(long that) {
		return multiply(valueOf(that));
	}

	/**
	 * Divide this Rational through another Rational and return a new Rational.
	 * <p>
	 * If the second operand is one, [as an optimization] this Rational is
	 * returned.
	 * 
	 * @param that
	 *        the rational to be divided by this rational.
	 * @return a new Rational that is the result of dividing that with this.
	 */
	// [Name: see class BigInteger.]
	public Rational divide(Rational that) {
		if (that.isZero()) {
			throw new ArithmeticException("division by zero");
		}

		// optimization: first operand is zero.
		if (isZero()) {
			return ZERO;
		}

		// optimization: second operand is 1.
		if (that.isOne()) {
			return this;
		}

		// optimization: first operand is 1.
		if (isOne()) {
			return that.invert();
		}

		// optimization: second operand is -1.
		if (that.isMinusOne()) {
			return negate();
		}

		// optimization: first operand is -1.
		if (isMinusOne()) {
			return that.invert().negate();
		}

		// note: the calculated numerator/denominator may be denormalized,
		// implicit normalize() is needed.

		return new Rational(bigIntegerMultiply(numerator, that.denominator),
				bigIntegerMultiply(denominator, that.numerator));
	}

	/**
	 * Divide this Rational through a long fix number integer and return a new
	 * Rational.
	 * 
	 * @param that
	 *        the long to be divided by this rational.
	 * @return a new Rational that is the result of dividing that with this.
	 */
	public Rational divide(long that) {
		return divide(valueOf(that));
	}

	/**
	 * Calculate this Rational's integer power and return a new Rational.
	 * <p>
	 * The integer exponent may be negative.
	 * <p>
	 * If the exponent is one, [as an optimization] this Rational is returned.
	 * 
	 * @param exponent
	 *            the exponent to raise this rational by
	 * @return a new Rational that is the result of raising this rational by the
	 *         given power.
	 */
	// [Name: see classes Math, BigInteger.]
	public Rational pow(int exponent) {
		final boolean zero = isZero();

		if (zero) {
			if (exponent == 0) {
				throw new ArithmeticException("zero exp zero");
			}

			if (exponent < 0) {
				throw new ArithmeticException("division by zero");
			}
		}

		// optimization
		if (exponent == 0) {
			return ONE;
		}

		// optimization
		// test for exponent<=0 already done
		if (zero) {
			return ZERO;
		}

		// optimization
		if (exponent == 1) {
			return this;
		}

		// optimization
		if (exponent == -1) {
			return invert();
		}

		final boolean negate = (exponent < 0);
		if (negate) {
			exponent = -exponent;
		}

		final BigInteger numerator = bigIntegerPower(this.numerator, exponent);
		final BigInteger denominator = bigIntegerPower(this.denominator,
				exponent);

		// note: the calculated numerator/denominator are not denormalized in
		// the sense of having common factors, but numerator might be negative
		// (and become denominator below)

		return new Rational((negate ? denominator : numerator),
				(negate ? numerator : denominator));
	}

	/**
	 * Calculate the remainder of this Rational and another Rational and return
	 * a new Rational.
	 * <p>
	 * The remainder result may be negative.
	 * <p>
	 * The remainder is based on round down (towards zero) / truncate. 5/3 == 1
	 * + 2/3 (remainder 2), 5/-3 == -1 + 2/-3 (remainder 2), -5/3 == -1 + -2/3
	 * (remainder -2), -5/-3 == 1 + -2/-3 (remainder -2).
	 * 
	 * @param that
	 *            the rational to be divided by this rational to get a
	 *            remainder.
	 * @return a new Rational that is the result of obtaining the remainder from
	 *         dividing that with this.
	 */
	// [Name: see class BigInteger.]
	public Rational remainder(Rational that) {
		final int thisSignum = signum();
		final int thatSignum = that.signum();

		if (thatSignum == 0) {
			throw new ArithmeticException("division by zero");
		}

		Rational a = this;
		if (thisSignum < 0) {
			a = a.negate();
		}

		// divisor's sign doesn't matter, as stated above.
		// this is also BigInteger's behavior, but don't let us be
		// dependent of a change in that.
		Rational b = that;
		if (thatSignum < 0) {
			b = b.negate();
		}

		Rational r = a.remainderOrModulusOfPositive(b);

		if (thisSignum < 0) {
			r = r.negate();
		}

		return r;
	}

	/**
	 * Calculate the remainder of this Rational and a long fix number integer
	 * and return a new Rational.
	 * 
	 * @param that
	 *            the long to be divided by this rational to get a
	 *            remainder.
	 * @return a new Rational that is the result of obtaining the remainder from
	 *         dividing that with this.
	 */
	public Rational remainder(long that) {
		return remainder(valueOf(that));
	}

	/**
	 * Calculate the modulus of this Rational and another Rational and return a
	 * new Rational.
	 * <p>
	 * The modulus result may be negative.
	 * <p>
	 * Modulus is based on round floor (towards negative). 5/3 == 1 + 2/3
	 * (modulus 2), 5/-3 == -2 + -1/-3 (modulus -1), -5/3 == -2 + 1/3 (modulus
	 * 1), -5/-3 == 1 + -2/-3 (modulus -2).
	 * 
	 * @param that
	 *            the rational to be divided by this rational to get a
	 *            mod.
	 * @return a new Rational that is the result of obtaining the mod from
	 *         dividing that with this.
	 */
	// [Name: see class BigInteger.]
	public Rational mod(Rational that) {
		final int thisSignum = signum();
		final int thatSignum = that.signum();

		if (thatSignum == 0) {
			throw new ArithmeticException("division by zero");
		}

		Rational a = this;
		if (thisSignum < 0) {
			a = a.negate();
		}

		Rational b = that;
		if (thatSignum < 0) {
			b = b.negate();
		}

		Rational r = a.remainderOrModulusOfPositive(b);

		if (thisSignum < 0 && thatSignum < 0) {
			r = r.negate();
		} else if (thatSignum < 0) {
			r = r.subtract(b);
		} else if (thisSignum < 0) {
			r = b.subtract(r);
		}

		return r;
	}

	/**
	 * Calculate the modulus of this Rational and a long fix number integer and
	 * return a new Rational.
	 * 
	 * @param that
	 *            the long to be divided by this rational to get a
	 *            mod.
	 * @return a new Rational that is the result of obtaining the mod from
	 *         dividing that with this.
	 */
	public Rational mod(long that) {
		return mod(valueOf(that));
	}

	/**
	 * Signum. -1, 0, or 1.
	 * 
	 * @return If this Rational is negative, -1 is returned; if it is zero, 0 is
	 * returned; if it is positive, 1 is returned.
	 */
	// [Name: see class BigInteger.]
	public int signum() {
		// note: denominator is positive.
		return numerator.signum();
	}

	/**
	 * @return a new Rational with the absolute value of this Rational.
	 * If this Rational is zero or positive, [as an optimization] this Rational
	 * is returned.
	 */
	// [Name: see classes Math, BigInteger.]
	public Rational abs() {
		if (signum() >= 0) {
			return this;
		}

		// optimization
		if (isMinusOne()) {
			return ONE;
		}

		// note: the calculated numerator/denominator are not denormalized,
		// implicit normalize() would not be needed.

		return new Rational(numerator.negate(), denominator);
	}

	/**
	 * @return a new Rational with the negative value of this.
	 * 
	 */
	// [Name: see class BigInteger.]
	public Rational negate() {
		// optimization
		if (isZero()) {
			return ZERO;
		}

		// optimization
		if (isOne()) {
			return MINUS_ONE;
		}

		// optimization
		if (isMinusOne()) {
			return ONE;
		}

		// note: the calculated numerator/denominator are not denormalized,
		// implicit normalize() would not be needed.

		return new Rational(numerator.negate(), denominator);
	}

	/**
	 * @return a new Rational with the inverted (reciprocal) value of this.
	 */
	public Rational invert() {
		if (isZero()) {
			throw new ArithmeticException("division by zero");
		}

		// optimization
		if (isOne() || isMinusOne()) {
			return this;
		}

		// note: the calculated numerator/denominator are not denormalized in
		// the sense of having common factors, but numerator might be negative
		// (and become denominator below)

		return new Rational(denominator, numerator);
	}

	/**
	 * Calculate the minimal value of two Rationals.
	 * 
	 * @param that
	 *        the other rational to test.
	 * @return the minimal value of two Rationals.
	 */
	// [Name: see classes Math, BigInteger.]
	public Rational min(Rational that) {
		return (compareTo(that) <= 0 ? this : that);
	}

	/**
	 * Return the minimal value of a Rational and a long fix number integer.
	 * 
	 * @param that
	 *        the other long to test.
	 * @return the minimal value of this and that.
	 */
	public Rational min(long that) {
		return min(valueOf(that));
	}

	/**
	 * Return the maximal value of two Rationals.
	 * 
	 * @param that
	 *        the other rational to test. 
	 * @return the maximal value of two Rationals.
	 */
	// [Name: see classes Math, BigInteger.]
	public Rational max(Rational that) {
		return (compareTo(that) >= 0 ? this : that);
	}

	/**
	 * Return the maximum value of a Rational and a long fix number integer.
	 * @param that
	 *        the other long to test.
	 * @return the maximum value of a Rational and a long fix number integer.
	 */
	public Rational max(long that) {
		return max(valueOf(that));
	}

	/**
	 * Compare object for equality. Overwrites Object.equals(). Semantics is
	 * that only Rationals can be equal. Never throws.
	 * <p>
	 * Overwrites Object.equals(Object).
	 * 
	 * @param object
	 *            the object to be compared with this for equality.
	 * @return true if object is a rational and this and the object have the
	 *         same value, false otherwise.
	 */
	@Override
	public boolean equals(Object object) {
		// optimization
		if (object == this) {
			return true;
		}

		// test includes null
		if (!(object instanceof Rational)) {
			return false;
		}
		final Rational that = (Rational) object;

		// optimization
		if (that.numerator == numerator && that.denominator == denominator) {
			return true;
		}

		boolean result =
				bigIntegerEquals(that.numerator,   numerator) &&
				bigIntegerEquals(that.denominator, denominator);

		return result;
	}

	/**
	 * Hash code. Overwrites Object.hashCode().
	 * <p>
	 * Overwrites Object.hashCode().
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		// lazy init for optimization
		if (hashCode == 0) {
			hashCode = ((numerator.hashCode() + 1) * (denominator.hashCode() + 2));
		}
		return hashCode;
	}

	/**
	 * Compare this Rational to another Rational.
	 * 
	 * @param that
	 *            the Rational value to be compared to.
	 * @return a negative integer, zero, or a positive integer as this object is
	 *         less than, equal to, or greater than the specified object.
	 */
	public int compareTo(Rational that) {
		// optimization
		if (that == this) {
			return 0;
		}

		final int thisSignum = signum();
		final int thatSignum = that.signum();

		if (thisSignum != thatSignum) {
			return (thisSignum < thatSignum ? -1 : 1);
		}

		// optimization: both zero.
		if (thisSignum == 0) {
			return 0;
		}

		// note: both denominators are positive.
		return bigIntegerMultiply(numerator, that.denominator)
				.compareTo(
				bigIntegerMultiply(that.numerator, denominator));
	}

	/**
	 * Compare this Rational to a BigInteger.
	 * 
	 * @param that
	 *            the BigInteger value to be compared to.
	 * @return a negative integer, zero, or a positive integer as this object is
	 *         less than, equal to, or greater than the specified object.
	 */
	public int compareTo(BigInteger that) {
		return compareTo(valueOf(that));
	}

	/**
	 * Compare this Rational to a long.
	 * 
	 * @param that
	 *            the long value to be compared to.
	 * @return a negative integer, zero, or a positive integer as this object is
	 *         less than, equal to, or greater than the specified object.
	 */
	public int compareTo(long that) {
		return compareTo(valueOf(that));
	}

	/**
	 * Compare this Rational to an Object.
	 * <p>
	 * Object can be Rational/BigInteger/Long/Integer/Short/Byte.
	 * <p>
	 * Implements Comparable.compareTo(Object) (JDK 1.2 and later).
	 * <p>
	 * A sample use is with a sorted map or set, e.g. TreeSet.
	 * <p>
	 * Only Rational/BigInteger/Long/Integer objects allowed, method will throw
	 * otherwise.
	 * <p>
	 * For backward compatibility reasons we keep compareTo(Object) additionally
	 * to compareTo(Rational). Comparable&lt;Object&gt; is declared to be
	 * implemented rather than Comparable&lt;Rational&gt;.
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public int compareTo(Object object) {
		if (object instanceof Byte) {
			return compareTo(((Byte) object).longValue());
		}

		if (object instanceof Short) {
			return compareTo(((Short) object).longValue());
		}

		if (object instanceof Integer) {
			return compareTo(((Integer) object).longValue());
		}

		if (object instanceof Long) {
			return compareTo(((Long) object).longValue());
		}

		if (object instanceof BigInteger) {
			return compareTo((BigInteger) object);
		}

		// now assuming that it's either 'instanceof Rational'
		// or it'll throw a ClassCastException.
		return compareTo((Rational) object);
	}

	/**
	 * Convert to BigInteger, by rounding.
	 * <p>
	 * Possible loss of precision.
	 * 
	 * @return a BigInteger representation of this rational, with possible rounding.
	 */
	// @PrecisionLoss
	public BigInteger bigIntegerValue() {
		// [rounding step, possible loss of precision step]
		return round().numerator;
	}

	/**
	 * Convert to long, by rounding and delegating to BigInteger. Implements
	 * Number.longValue(). As described with BigInteger.longValue(), this just
	 * returns the low-order [64] bits (losing information about magnitude and
	 * sign).
	 * <p>
	 * Possible loss of precision.
	 * <p>
	 * Overwrites Number.longValue().
	 * 
	 * @return a long representation of this rational, with possible rounding.
	 */
	// @PrecisionLoss
	@Override
	public long longValue() {
		// delegate to BigInteger.
		// [rounding step, possible loss of precision step]
		return bigIntegerValue().longValue();
	}

	/**
	 * Convert to int, by rounding and delegating to BigInteger. Implements
	 * Number.intValue(). As described with BigInteger.longValue(), this just
	 * returns the low-order [32] bits (losing information about magnitude and
	 * sign).
	 * <p>
	 * Possible loss of precision.
	 * <p>
	 * Overwrites Number.intValue().
	 * 
	 * @return an int representation of this rational, with possible rounding.
	 */
	// @PrecisionLoss
	@Override
	public int intValue() {
		// delegate to BigInteger.
		// [rounding step, possible loss of precision step]
		return bigIntegerValue().intValue();
	}

	/**
	 * Convert to double floating point value. Implements Number.doubleValue().
	 * <p>
	 * Possible loss of precision.
	 * <p>
	 * Overwrites Number.doubleValue().
	 * 
	 * @return a double representation of this rational, with possible rounding.
	 */
	// @PrecisionLoss
	@Override
	public double doubleValue() {
		return Double.longBitsToDouble(
		// [rounding step, possible loss of precision step]
				doubleBitsValue());
	}

	/**
	 * Convert to single floating point value. Implements Number.floatValue().
	 * <p>
	 * Note that Rational's [implicit] [default] rounding mode that applies
	 * [too] on indirect double to Rational to float rounding (round-half-up)
	 * may differ from what's done in a direct cast/coercion from double to
	 * float (e.g. round-half-even).
	 * <p>
	 * Possible loss of precision.
	 * <p>
	 * Overwrites Number.floatValue().
	 * 
	 * @return a float representation of this rational, with possible rounding.
	 */
	// @PrecisionLoss
	@Override
	public float floatValue() {
		return Float.intBitsToFloat(
		// [rounding step, possible loss of precision step]
				floatBitsValue());
	}

	/**
	 * Convert to IEEE 754 double float bits. The bits can be converted to a
	 * double by Double.longBitsToDouble().
	 * <p>
	 * Possible loss of precision.
	 * 
	 * @return a double bits value representation of this rational, with possible rounding.
	 */
	// @PrecisionLoss
	public long doubleBitsValue() {
		// [rounding step, possible loss of precision step]
		return (toIEEE754(this, DOUBLE_FLOAT_FRACTION_SIZE,
				DOUBLE_FLOAT_EXPONENT_SIZE)[0]);
	}

	/**
	 * Convert to IEEE 754 single float bits. The bits can be converted to a
	 * float by Float.intBitsToFloat().
	 * <p>
	 * Possible loss of precision.
	 * 
	 * @return a float bits value representation of this rational, with possible rounding.
	 */
	// @PrecisionLoss
	public int floatBitsValue() {
		// [rounding step, possible loss of precision step]
		return (int) (toIEEE754(this, SINGLE_FLOAT_FRACTION_SIZE,
				SINGLE_FLOAT_EXPONENT_SIZE)[0]);
	}

	/**
	 * Convert this Rational to IEEE 754 half float (binary16) bits.
	 * <p>
	 * As a short value is returned rather than a int, care has to be taken no
	 * unwanted sign expansion happens in subsequent operations, e.g. by masking
	 * (x.halfBitsValue()&amp;0xffffl) or similar
	 * (x.halfBitsValue()==(short)0xbc00).
	 * <p>
	 * Possible loss of precision.
	 * 
	 * @return a half bits representation of this rational, with possible rounding.
	 */
	// @PrecisionLoss
	public short halfBitsValue() {
		// [rounding step, possible loss of precision step]
		return (short) (toIEEE754(this, HALF_FLOAT_FRACTION_SIZE,
				HALF_FLOAT_EXPONENT_SIZE)[0]);
	}

	/**
	 * Convert this Rational to IEEE 754 quad float (binary128, quadruple) bits.
	 * <p>
	 * The bits are returned in an array of two longs, big endian (higher
	 * significant long first).
	 * <p>
	 * Possible loss of precision.
	 * 
	 * @return a quad bits representation of this rational, with possible rounding.
	 */
	// @PrecisionLoss
	public long[] quadBitsValue() {
		// [rounding step, possible loss of precision step]
		return toIEEE754(this, QUAD_FLOAT_FRACTION_SIZE,
				QUAD_FLOAT_EXPONENT_SIZE);
	}

	/**
	 * Convert this Rational to a long integer, either returning an exact result
	 * (no rounding or truncation needed), or throw an ArithmeticException.
	 * 
	 * @return an exact long representation of this rational.
	 */
	public long longValueExact() {
		final long i = longValue();
		// test is kind-of costly
		if (!equals(valueOf(i))) {
			throw new ArithmeticException(isInteger() ? "overflow"
					: "rounding necessary");
		}
		return i;
	}

	/**
	 * Convert this Rational to an int, either returning an exact result (no
	 * rounding or truncation needed), or throw an ArithmeticException.
	 * 
	 * @return an exact int representation of this rational.
	 */
	public int intValueExact() {
		final int i = intValue();
		// test is kind-of costly
		if (!equals(valueOf(i))) {
			throw new ArithmeticException(isInteger() ? "overflow"
					: "rounding necessary");
		}
		return i;
	}

	/**
	 * Convert this Rational to its constant (ONE, ZERO, MINUS_ONE) if possible.
	 * 
	 * @param value
	 *            the value to be converted if possible.
	 * @return the constrant representation of the given value if one exists,
	 *         other the input value.
	 */
	public static Rational valueOf(Rational value) {
		if (value == null) {
			throw new NumberFormatException("null");
		}

		// note: these tests are quite expensive,
		// but they are minimized to a reasonable amount.

		// priority in the tests: 1, 0, -1;

		// two phase testing.
		// cheap tests first.

		// optimization
		if (value == ONE) {
			return value;
		}

		// optimization
		if (value == ZERO) {
			return value;
		}

		// optimization
		if (value == MINUS_ONE) {
			return value;
		}

		// more expensive tests later.

		// optimization
		if (value.equals(ONE)) {
			return ONE;
		}

		// optimization
		if (value.equals(ZERO)) {
			return ZERO;
		}

		// optimization
		if (value.equals(MINUS_ONE)) {
			return MINUS_ONE;
		}

		// not a known constant
		return value;
	}

	/**
	 * Build a Rational from a String.
	 * <p>
	 * [Roughly] equivalent to <CODE>new Rational(value)</CODE>.
	 * 
	 * @param value
	 *        the value to be converted to a rational.
	 * @return a rational representation of the given value.
	 */
	public static Rational valueOf(String value) {
		if (value == null) {
			throw new NumberFormatException("null");
		}

		// optimization
		if (value.equals("0")) {
			return ZERO;
		}

		// optimization
		if (value.equals("1")) {
			return ONE;
		}

		// optimization
		if (value.equals("-1")) {
			return MINUS_ONE;
		}

		return new Rational(value);
	}

	/**
	 * Build a Rational from a BigInteger.
	 * <p>
	 * Equivalent to <CODE>new Rational(value)</CODE>.
	 * 
	 * @param value
	 *        the value to be converted to a rational.
	 * @return a rational representation of the given value.
	 */
	public static Rational valueOf(BigInteger value) {
		return new Rational(value);
	}

	/**
	 * Build a Rational from a long fix number integer.
	 * <p>
	 * [Roughly] equivalent to <CODE>new Rational(value)</CODE>.
	 * <p>
	 * As an optimization, commonly used numbers are returned as a reused
	 * constant.
	 * 
	 * @param value
	 *        the value to be converted to a rational.
	 * @return a rational representation of the given value.
	 */
	public static Rational valueOf(long value) {
		// return the internal constants if possible

		// optimization
		// check whether it's outside int range.
		// actually check a much narrower range, fitting the switch below.
		if (value >= -16 && value <= 16) {
			// note: test above needed to make the cast below safe
			// jump table, for speed
			switch ((int) value) {
			case 0:
				return ZERO;
			case 1:
				return ONE;
			case -1:
				return MINUS_ONE;
			case 2:
				return TWO;
			case 10:
				return TEN;
			case 16:
				return SIXTEEN;
			}
		}

		return new Rational(value);
	}

	// note: byte/short/int implicitly upgraded to long,
	// so strictly the additional implementations aren't needed;
	// with unsigned (below) they however are

	/**
	 * Build a Rational from an int.
	 * 
	 * @param value
	 *        the value to be converted to a rational.
	 * @return a rational representation of the given value.
	 */
	public static Rational valueOf(int value) {
		return valueOf((long) value);
	}

	/**
	 * Build a Rational from a short.
	 * 
	 * @param value
	 *        the value to be converted to a rational.
	 * @return a rational representation of the given value.
	 */
	public static Rational valueOf(short value) {
		return valueOf((long) value);
	}

	/**
	 * Build a Rational from a byte.
	 * 
	 * @param value
	 *        the value to be converted to a rational.
	 * @return a rational representation of the given value.
	 */
	public static Rational valueOf(byte value) {
		return valueOf((long) value);
	}

	/**
	 * Build a Rational from a [IEEE 754] double [size/precision] floating point
	 * number.
	 * 
	 * @param value
	 *        the value to be converted to a rational.
	 * @return a rational representation of the given value.
	 */
	public static Rational valueOf(double value) {
		return new Rational(value);
	}

	/**
	 * Build a Rational from a [IEEE 754] single [size/precision] floating point
	 * number.
	 * 
	 * @param value
	 *        the value to be converted to a rational.
	 * @return a rational representation of the given value.
	 */
	public static Rational valueOf(float value) {
		return new Rational(value);
	}

	/**
	 * Build a Rational from an unsigned long fix number integer.
	 * <p>
	 * The resulting Rational is positive, i.e. the negative longs are mapped to
	 * 2**63..2**64 (exclusive).
	 * 
	 * @param value
	 *        the value to be converted to a rational.
	 * @return a rational representation of the given value.
	 */
	public static Rational valueOfUnsigned(long value) {
		final Rational b = valueOf(value);
		// mind the long being unsigned with highest significant
		// bit (bit#63) set (interpreted as negative by valueOf(long))
		return (b.isNegative() ? b.add(TWO_POWER_64) : b);
	}

	/**
	 * Build a Rational from an unsigned int.
	 * <p>
	 * The resulting Rational is positive, i.e. the negative ints are mapped to
	 * 2**31..2**32 (exclusive).
	 * 
	 * @param value
	 *        the value to be converted to a rational.
	 * @return a rational representation of the given value.
	 */
	public static Rational valueOfUnsigned(int value) {
		// masking: suppress sign expansion
		return valueOf(value & 0xffffffffl);
	}

	/**
	 * Build a Rational from an unsigned short.
	 * <p>
	 * The resulting Rational is positive, i.e. the negative shorts are mapped
	 * to 2**15..2**16 (exclusive).
	 * 
	 * @param value
	 *        the value to be converted to a rational.
	 * @return a rational representation of the given value.
	 */
	public static Rational valueOfUnsigned(short value) {
		// masking: suppress sign expansion
		return valueOf(value & 0xffffl);
	}

	/**
	 * Build a Rational from an unsigned byte.
	 * <p>
	 * The resulting Rational is positive, i.e. the negative bytes are mapped to
	 * 2**7..2**8 (exclusive).
	 * 
	 * @param value
	 *        the value to be converted to a rational.
	 * @return a rational representation of the given value.
	 */
	public static Rational valueOfUnsigned(byte value) {
		// masking: suppress sign expansion
		return valueOf(value & 0xffl);
	}

	/**
	 * Build a Rational from an IEEE 754 double size (double precision,
	 * binary64) floating point number represented as long.
	 * <p>
	 * An IEEE 754 double size (binary64) number uses 1 bit for the sign, 11
	 * bits for the exponent, and 52 bits (plus 1 implicit bit) for the
	 * fraction/mantissa. The minimal exponent encodes subnormal nubers; the
	 * maximal exponent encodes Infinities and NaNs.
	 * <p>
	 * Infinities and NaNs are not supported as Rationals.
	 * <p>
	 * The conversion from the bits to a Rational is done without loss of
	 * precision.
	 * 
	 * @param value
	 *        the value to be converted to a rational.
	 * @return a rational representation of the given value.
	 */
	public static Rational valueOfDoubleBits(long value) {
		return fromIEEE754(new long[] { value, }, DOUBLE_FLOAT_FRACTION_SIZE,
				DOUBLE_FLOAT_EXPONENT_SIZE);
	}

	/**
	 * Build a Rational from an IEEE 754 single size (single precision,
	 * binary32) floating point number represented as int.
	 * <p>
	 * An IEEE 754 single size (binary32) number uses 1 bit for the sign, 8 bits
	 * for the exponent, and 23 bits (plus 1 implicit bit) for the
	 * fraction/mantissa. The minimal exponent encodes subnormal nubers; the
	 * maximal exponent encodes Infinities and NaNs.
	 * <p>
	 * Infinities and NaNs are not supported as Rationals.
	 * <p>
	 * The conversion from the bits to a Rational is done without loss of
	 * precision.
	 * 
	 * @param value
	 *        the value to be converted to a rational.
	 * @return a rational representation of the given value.
	 */
	public static Rational valueOfFloatBits(int value) {
		// [masking: suppress sign expansion, that leads to excess bits,
		// that's not accepted by fromIeee754()]
		return fromIEEE754(new long[] { value & 0xffffffffl, },
				SINGLE_FLOAT_FRACTION_SIZE, SINGLE_FLOAT_EXPONENT_SIZE);
	}

	/**
	 * Build a Rational from an IEEE 754 half size (half precision, binary16)
	 * floating point number represented as short.
	 * <p>
	 * An IEEE 754 half size (binary16) number uses 1 bit for the sign, 5 bits
	 * for the exponent, and 10 bits (plus 1 implicit bit) for the
	 * fraction/mantissa. The minimal exponent encodes subnormal nubers; the
	 * maximal exponent encodes Infinities and NaNs.
	 * <p>
	 * Infinities and NaNs are not supported as Rationals.
	 * <p>
	 * The conversion from the bits to a Rational is done without loss of
	 * precision.
	 * 
	 * @param value
	 *        the value to be converted to a rational.
	 * @return a rational representation of the given value.
	 */
	public static Rational valueOfHalfBits(short value) {
		// [masking: suppress sign expansion, that leads to excess bits,
		// that's not accepted by fromIeee754()]
		return fromIEEE754(new long[] { value & 0xffffl, },
				HALF_FLOAT_FRACTION_SIZE, HALF_FLOAT_EXPONENT_SIZE);
	}

	/**
	 * Build a Rational from an IEEE 754 quad size (quadruple precision,
	 * binary128) floating point number represented as an array of two longs
	 * (big endian; higher significant long first).
	 * <p>
	 * An IEEE 754 quad size (binary128, quadruple) number uses 1 bit for the
	 * sign, 15 bits for the exponent, and 112 bits (plus 1 implicit bit) for
	 * the fraction/mantissa. The minimal exponent encodes subnormal nubers; the
	 * maximal exponent encodes Infinities and NaNs.
	 * <p>
	 * Infinities and NaNs are not supported as Rationals.
	 * <p>
	 * The conversion from the bits to a Rational is done without loss of
	 * precision.
	 * 
	 * @param value
	 *        the value to be converted to a rational.
	 * @return a rational representation of the given value.
	 */
	public static Rational valueOfQuadBits(long[] value) {
		return fromIEEE754(value, QUAD_FLOAT_FRACTION_SIZE,
				QUAD_FLOAT_EXPONENT_SIZE);
	}

	/**
	 * Compare two IEEE 754 quad size (quadruple precision, binary128) floating
	 * point numbers (each represented as two longs). NaNs are not considered;
	 * comparison is done by bits. [Convenience method.]
	 * 
	 * @param a 
	 *        the first value to compare.
	 * @param b
	 *        the second value to compare.
	 * @return true if both values are the same.
	 */
	// note: especially due the NaN issue commented above
	// (a NaN maps to many bits representations),
	// we call this method quadBitsEqual rather than quadEqual
	public static boolean quadBitsEqual(long[] a, long[] b) {
		if (a == null || b == null) {
			throw new NumberFormatException("null");
		}
		if (a.length != 2 || b.length != 2) {
			throw new NumberFormatException("not a quad");
		}
		return (a[1] == b[1] && a[0] == b[0]);
	}

	/**
	 * Round.
	 * <p>
	 * Round mode is one of {
	 * <code>ROUND_UP, ROUND_DOWN, ROUND_CEILING, ROUND_FLOOR,
	 * ROUND_HALF_UP, ROUND_HALF_DOWN, ROUND_HALF_EVEN,
	 * ROUND_HALF_CEILING, ROUND_HALF_FLOOR, ROUND_HALF_ODD,
	 * ROUND_UNNECESSARY, DEFAULT_ROUND_MODE (==ROUND_HALF_UP)</code> .
	 * <p>
	 * If rounding isn't necessary, i.e. this Rational is an integer, [as an
	 * optimization] this Rational is returned.
	 * <p>
	 * Possible loss of precision.
	 * 
	 * @param roundMode
	 *            the rounding mode to use.
	 * @return a new Rational based on rounding this rational using the given
	 *         rounding mode.
	 */
	// @PrecisionLoss
	public Rational round(int roundMode) {
		// optimization
		// return self if we don't need to round, independent of rounding mode
		if (isInteger()) {
			return this;
		}

		return new Rational(
		// [rounding step, possible loss of precision step]
				roundToBigInteger(roundMode));
	}

	/**
	 * Round by default mode (ROUND_HALF_UP).
	 * <p>
	 * Possible loss of precision.
	 * 
	 * @return a new Rational based on rounding this rational using the ROUND_HALF_UP
	 *         rounding mode.
	 */
	// @PrecisionLoss
	public Rational round() {
		// [rounding step, possible loss of precision step]
		return round(DEFAULT_ROUND_MODE);
	}

	/**
	 * Floor, round towards negative infinity.
	 * <p>
	 * Possible loss of precision.
	 * 
	 * @return a new rational representing the floor value of this rational.
	 */
	// @PrecisionLoss
	public Rational floor() {
		// [rounding step, possible loss of precision step]
		return round(ROUND_FLOOR);
	}

	/**
	 * Ceiling, round towards positive infinity.
	 * <p>
	 * Possible loss of precision.
	 * 
	 * @return a new rational representing the ceiling value of this rational.
	 */
	// [Name: see class Math.]
	// @PrecisionLoss
	public Rational ceil() {
		// [rounding step, possible loss of precision step]
		return round(ROUND_CEILING);
	}

	/**
	 * Truncate, round towards zero.
	 * <p>
	 * Possible loss of precision.
	 * 
	 * @return a new rational representing the truncated value of this rational.
	 */
	// @PrecisionLoss
	public Rational truncate() {
		// [rounding step, possible loss of precision step]
		return round(ROUND_DOWN);
	}

	/**
	 * Integer part.
	 * <p>
	 * Possible loss of precision.
	 * 
     * @return a new rational representing the integer part value of this rational.
	 */
	// @PrecisionLoss
	public Rational integerPart() {
		// [rounding step, possible loss of precision step]
		return round(ROUND_DOWN);
	}

	/**
	 * Fractional part.
	 * <p>
	 * Possible loss of precision.
	 * 
	 * @return a new rational representing the fractional part of this rational.
	 */
	// @PrecisionLoss
	public Rational fractionalPart() {
		// this==ip+fp; sign(fp)==sign(this)
		// [possible loss of precision step]
		return subtract(integerPart());
	}

	/**
	 * Return an array of Rationals with both integer and fractional part.
	 * <p>
	 * Integer part is returned at offset 0; fractional part at offset 1.
	 * 
     * @return an array of Rationals with both integer and fractional part.
	 */
	public Rational[] integerAndFractionalPart() {
		// note: this duplicates fractionalPart() code, for speed.

		final Rational[] pp = new Rational[2];
		final Rational ip = integerPart();
		pp[0] = ip;
		pp[1] = subtract(ip);

		return pp;
	}
	
	/**
	 * Clone the current Rational.
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public Rational clone() throws CloneNotSupportedException {
		return (Rational) super.clone();
	}

	//
	// PRIVATE METHODS
	//

	/**
	 * Normalize Rational. Denominator will be positive, numerator and
	 * denominator will have no common divisor. BigIntegers -1, 0, 1 will be set
	 * to constants for later comparison speed.
	 */
	private void normalize() {
		// note: don't call anything that depends on a normalized this.
		// i.e.: don't call most (or all) of the Rational methods.

		if (numerator == null || denominator == null) {
			throw new NumberFormatException("null");
		}

		// [these are typically cheap.]
		int numeratorSignum = numerator.signum();
		int denominatorSignum = denominator.signum();

		// note: we don't throw on denominatorSignum==0. that'll be done
		// elsewhere.
		// if (denominatorSignum == 0) {
		// throw new NumberFormatException("quotient zero");
		// }

		if (numeratorSignum == 0 && denominatorSignum == 0) {
			// [typically not reached, due to earlier tests.]
			// [both for speed]
			numerator = BIG_INTEGER_ZERO;
			denominator = BIG_INTEGER_ZERO;
			return;
		}

		if (numeratorSignum == 0) {
			denominator = BIG_INTEGER_ONE;
			// [for speed]
			numerator = BIG_INTEGER_ZERO;
			return;
		}

		if (denominatorSignum == 0) {
			// [typically not reached, due to earlier tests.]
			numerator = BIG_INTEGER_ONE;
			// [for speed]
			denominator = BIG_INTEGER_ZERO;
			return;
		}

		// optimization
		// check the frequent case of denominator==1, for speed.
		// note: this only covers the normalized-for-speed 1-case.
		if (denominator == BIG_INTEGER_ONE) {
			// [for [later] speed]
			numerator = bigIntegerValueOf(numerator);
			return;
		}

		// optimization
		// check the symmetric case too, for speed.
		// note: this only covers the normalized-for-speed 1-case.
		if ((numerator == BIG_INTEGER_ONE || numerator == BIG_INTEGER_MINUS_ONE)
				&& denominatorSignum > 0) {
			// [for [later] speed]
			denominator = bigIntegerValueOf(denominator);
			return;
		}

		// setup torn apart for speed
		BigInteger numeratorApart = numerator;
		BigInteger denominatorApart = denominator;

		if (denominatorSignum < 0) {
			numerator = numerator.negate();
			denominator = denominator.negate();
			numeratorSignum = -numeratorSignum;
			denominatorSignum = -denominatorSignum;

			denominatorApart = denominator;
			if (numeratorSignum > 0) {
				numeratorApart = numerator;
			}
		} 
		else {
			if (numeratorSignum < 0) {
				numeratorApart = numerator.negate();
			}
		}

		final BigInteger gcd = numeratorApart.gcd(denominatorApart);

		// test: optimization (body: not)
		if (!bigIntegerIsOne(gcd)) {
			numerator = numerator.divide(gcd);
			denominator = denominator.divide(gcd);
		}

		// for [later] speed, and normalization generally
		numerator = bigIntegerValueOf(numerator);
		denominator = bigIntegerValueOf(denominator);
	}

	/**
	 * Normalize Rational. [Convenience method to normalize(void).]
	 */
	private void normalizeFrom(BigInteger numerator, BigInteger denominator) {
		this.numerator = numerator;
		this.denominator = denominator;

		normalize();
	}

	/**
	 * Normalize Rational. [Convenience method to normalize(void).]
	 */
	private void normalizeFrom(Rational that) {
		if (that == null) {
			throw new NumberFormatException("null");
		}

		normalizeFrom(that.numerator, that.denominator);
	}

	/**
	 * Check constraints on radixes. Radix may not be negative or less than two.
	 */
	private static void checkRadix(int radix) {
		if (radix < 0) {
			throw new NumberFormatException("radix negative");
		}

		if (radix < 2) {
			throw new NumberFormatException("radix too small");
		}

		// note: we don't check for "radix too large";
		// that's left to BigInteger.toString(radix)
		// [i.e.: we don't really mind whether the underlying
		// system supports base36, or base62, or even more]
	}

	/**
	 * Check some of the integer format constraints.
	 */
	private static void checkNumberFormat(String strNumber) {
		// "x", "-x", "+x", "", "-", "+"

		if (strNumber == null) {
			throw new NumberFormatException("null");
		}

		// note: 'embedded sign' catches both-signs cases too.

		final int p = strNumber.indexOf('+');
		final int m = strNumber.indexOf('-');

		final int pp = (p == -1 ? -1 : strNumber.indexOf('+', p + 1));
		final int mm = (m == -1 ? -1 : strNumber.indexOf('-', m + 1));

		if ((p != -1 && p != 0) || (m != -1 && m != 0) || pp != -1 || mm != -1) {
			// embedded sign. this covers the both-signs case.
			throw new NumberFormatException("embedded sign");
		}
	}

	/**
	 * Check number format for fraction part.
	 */
	private static void checkFractionFormat(String strFraction) {
		if (strFraction == null) {
			throw new NumberFormatException("null");
		}

		if (strFraction.indexOf('+') != -1 || strFraction.indexOf('-') != -1) {
			throw new NumberFormatException("sign in fraction");
		}
	}

	/**
	 * Check number input for Java's string representations of doubles/floats
	 * that are unsupported: "NaN" and "Infinity" (with or without sign).
	 */
	private static void checkNaNAndInfinity(String strNumber, int radix) {
		// the strings may actually be valid given a large enough radix
		// (e.g. base 36), so limit the radix/check
		if (radix > 16) {
			return;
		}

		// [null and empty string check]
		final int length = (strNumber == null ? 0 : strNumber.length());
		if (length < 1) {
			return;
		}

		// optimization (String.equals and even more String.equalsIgnoreCase
		// are quite expensive, charAt and switch aren't)
		// test for last character in strings below, both cases
		switch (strNumber.charAt(length - 1)) {
		case 'N':
		case 'n':
		case 'y':
		case 'Y':
			break;
		default:
			return;
		}

		if (strNumber.equalsIgnoreCase("NaN")
				|| strNumber.equalsIgnoreCase("Infinity")
				|| strNumber.equalsIgnoreCase("+Infinity")
				|| strNumber.equalsIgnoreCase("-Infinity")) {
			throw new NumberFormatException(strNumber);
		}
	}

	/**
	 * Check constraints on radixes. [Convenience method to checkRadix(radix).]
	 */
	private static void checkRadixArgument(int radix) {
		try {
			checkRadix(radix);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	/**
	 * Proxy to BigInteger.valueOf(). Speeds up comparisons by using constants.
	 */
	private static BigInteger bigIntegerValueOf(long number) {
		// return the internal constants used for checks if possible.

		// optimization
		// check whether it's outside int range.
		// actually check a much narrower range, fitting the switch below.
		if (number >= -16 && number <= 16) {
			// note: test above needed to make the cast below safe
			// jump table, for speed
			switch ((int) number) {
			case 0:
				return BIG_INTEGER_ZERO;
			case 1:
				return BIG_INTEGER_ONE;
			case -1:
				return BIG_INTEGER_MINUS_ONE;
			case 2:
				return BIG_INTEGER_TWO;
			case -2:
				return BIG_INTEGER_MINUS_TWO;
			case 10:
				return BIG_INTEGER_TEN;
			case 16:
				return BIG_INTEGER_SIXTEEN;
			}
		}

		return BigInteger.valueOf(number);
	}

	/**
	 * Convert BigInteger to its constant if possible. Speeds up later
	 * comparisons by using constants.
	 */
	private static BigInteger bigIntegerValueOf(BigInteger number) {
		// note: these tests are quite expensive,
		// so they should be minimized to a reasonable amount.

		// priority in the tests: 1, 0, -1;

		// two phase testing.
		// cheap tests first.

		// optimization
		if (number == BIG_INTEGER_ONE) {
			return number;
		}

		// optimization
		if (number == BIG_INTEGER_ZERO) {
			// [typically not reached, since zero is handled specially.]
			return number;
		}

		// optimization
		if (number == BIG_INTEGER_MINUS_ONE) {
			return number;
		}

		// more expensive tests later.

		// optimization
		if (number.equals(BIG_INTEGER_ONE)) {
			return BIG_INTEGER_ONE;
		}

		// optimization
		if (number.equals(BIG_INTEGER_ZERO)) {
			// [typically not reached from normalize().]
			return BIG_INTEGER_ZERO;
		}

		// optimization
		if (number.equals(BIG_INTEGER_MINUS_ONE)) {
			return BIG_INTEGER_MINUS_ONE;
		}

		// note: BIG_INTEGER_TWO et al. _not_ used for checks
		// and therefore not replaced by constants_here_.
		// this speeds up tests.

		// not a known constant
		return number;
	}

	/**
	 * Proxy to (new BigInteger()). Speeds up comparisons by using constants.
	 */
	private static BigInteger bigIntegerValueOf(String strNumber, int radix) {
		// note: mind the radix.
		// however, 0/1/-1 are not a problem.

		// _often_ used strings (e.g. 0 for empty fraction and
		// 1 for empty denominator), for speed.

		// optimization
		if (strNumber.equals("1")) {
			return BIG_INTEGER_ONE;
		}

		// optimization
		if (strNumber.equals("0")) {
			return BIG_INTEGER_ZERO;
		}

		// optimization
		if (strNumber.equals("-1")) {
			// typically not reached, due to [private] usage pattern,
			// i.e. the sign is cut before
			return BIG_INTEGER_MINUS_ONE;
		}

		// note: BIG_INTEGER_TWO et al. _not_ used for checks
		// and therefore even less valuable.
		// there's a tradeoff between speeds of these tests
		// and being consistent in using all constants
		// (at least with the common radixes).

		// optimization
		if (radix > 2) {
			if (strNumber.equals("2")) {
				return BIG_INTEGER_TWO;
			}

			if (strNumber.equals("-2")) {
				// typically not reached, due to [private] usage pattern,
				// i.e. the sign is cut before
				return BIG_INTEGER_MINUS_TWO;
			}
		}

		// optimization
		if (strNumber.equals("10")) {
			switch (radix) {
			case 2:
				return BIG_INTEGER_TWO;
			case 10:
				return BIG_INTEGER_TEN;
			case 16:
				return BIG_INTEGER_SIXTEEN;
			}
		}

		// optimization
		if (radix == 10 && strNumber.equals("16")) {
			return BIG_INTEGER_SIXTEEN;
		}

		// note: not directly finding the other [radix'] representations
		// of 10 and 16 in the code above

		// use the constants if possible
		return bigIntegerValueOf(new BigInteger(strNumber, radix));
	}

	/**
	 * Proxy to BigInteger.equals(). For speed.
	 */
	private static boolean bigIntegerEquals(BigInteger n, BigInteger m) {
		// optimization first test is for speed.
		if (n == m) {
			return true;
		}

		return n.equals(m);
	}

	/**
	 * Zero (0) value predicate. [For convenience and speed.]
	 */
	private static boolean bigIntegerIsZero(BigInteger n) {
		// optimization first test is for speed.
		if (n == BIG_INTEGER_ZERO) {
			return true;
		}

		// well, this is also optimized for speed a bit.
		return (n.signum() == 0);
	}

	/**
	 * One (1) value predicate. [For convenience and speed.]
	 */
	private static boolean bigIntegerIsOne(BigInteger n) {
		// optimization first test is for speed.
		if (n == BIG_INTEGER_ONE) {
			return true;
		}

		return bigIntegerEquals(n, BIG_INTEGER_ONE);
	}

	/**
	 * Minus-one (-1) value predicate. [For convenience and speed.]
	 */
	private static boolean bigIntegerIsMinusOne(BigInteger n) {
		// optimization
		// first test is for speed.
		if (n == BIG_INTEGER_MINUS_ONE) {
			return true;
		}

		return bigIntegerEquals(n, BIG_INTEGER_MINUS_ONE);
	}

	/**
	 * Negative value predicate.
	 */
	private static boolean bigIntegerIsNegative(BigInteger n) {
		return (n.signum() < 0);
	}

	/**
	 * Proxy to BigInteger.multiply().
	 * 
	 * For speed. The more common cases of integers (denominator == 1) are
	 * optimized.
	 */
	private static BigInteger bigIntegerMultiply(BigInteger n, BigInteger m) {
		// optimization: one or both operands are zero.
		if (bigIntegerIsZero(n) || bigIntegerIsZero(m)) {
			return BIG_INTEGER_ZERO;
		}

		// optimization: second operand is one (i.e. neutral element).
		if (bigIntegerIsOne(m)) {
			return n;
		}

		// optimization: first operand is one (i.e. neutral element).
		if (bigIntegerIsOne(n)) {
			return m;
		}

		// optimization
		if (bigIntegerIsMinusOne(m)) {
			// optimization
			if (bigIntegerIsMinusOne(n)) {
				// typically not reached due to earlier test(s)
				return BIG_INTEGER_ONE;
			}

			return n.negate();
		}

		// optimization
		if (bigIntegerIsMinusOne(n)) {
			// [m is not -1, see test above]
			return m.negate();
		}

		// default case. [this would handle all cases.]
		return n.multiply(m);
	}

	/**
	 * Proxy to BigInteger.pow(). For speed.
	 */
	private static BigInteger bigIntegerPower(BigInteger n, int exponent) {
		// generally expecting exponent>=0
		// (there's nor much use in inverting in the integer domain)
		// the checks for exponent<0 below are done all the same

		// optimization jump table, for speed.
		switch (exponent) {
		case 0:
			if (bigIntegerIsZero(n)) {
				// typically not reached, due to earlier test / [private] usage
				// pattern
				throw new ArithmeticException("zero exp zero");
			}
			return BIG_INTEGER_ONE;
		case 1:
			return n;
		}

		// optimization
		if (bigIntegerIsZero(n) && exponent > 0) {
			// note: exponent==0 already handled above
			// typically not reached, due to earlier test
			return BIG_INTEGER_ZERO;
		}

		// optimization
		if (bigIntegerIsOne(n)) {
			return BIG_INTEGER_ONE;
		}

		// optimization
		if (bigIntegerIsMinusOne(n)) {
			return (exponent % 2 == 0 ? BIG_INTEGER_ONE : BIG_INTEGER_MINUS_ONE);
		}

		return n.pow(exponent);
	}

	/**
	 * Binary logarithm rounded towards floor (towards negative infinity).
	 */
	// @PrecisionLoss
	private static int bigIntegerLogarithm2(BigInteger n) {
		if (bigIntegerIsZero(n)) {
			// [typically not reached, due to [private] usage pattern]
			throw new ArithmeticException("logarithm of zero");
		}
		if (bigIntegerIsNegative(n)) {
			// [typically not reached, due to [private] usage pattern]
			throw new ArithmeticException("logarithm of negative number");
		}

		// take this as a start
		// (don't wholly rely on bitLength() having the same meaning as log2)
		int exponent = n.bitLength() - 1;
		if (exponent < 0) {
			exponent = 0;
		}

		BigInteger p = BIG_INTEGER_TWO.pow(exponent + 1);
		while (n.compareTo(p) >= 0) {
			// typically not reached
			p = p.multiply(BIG_INTEGER_TWO);
			exponent++;
		}
		p = p.divide(BIG_INTEGER_TWO);
		while (n.compareTo(p) < 0) {
			// typically not reached
			p = p.divide(BIG_INTEGER_TWO);
			exponent--;
		}

		// [possible loss of precision step]
		return exponent;
	}

	/**
	 * Proxy to BigInteger.toString(int radix).
	 */
	private static String stringValueOf(BigInteger n, int radix) {
		return n.toString(radix);
	}

	/**
	 * Proxy to stringValueOf(bigIntegerValueOf(long), radix); take the same
	 * route to format [long/bigint] integer numbers [despite the overhead].
	 */
	private static String stringValueOf(long n, int radix) {
		return stringValueOf(bigIntegerValueOf(n), radix);
	}

	/**
	 * Convert a IEEE 754 floating point number (of different sizes, as array of
	 * longs, big endian) to a Rational.
	 */
	private static Rational fromIEEE754(long[] value0, int fractionSize,
			int exponentSize) {

		if (value0 == null) {
			throw new NumberFormatException("null");
		}

		// note: the long(s) in the input array are considered unsigned,
		// so expansion operations (to e.g. Rational) and [right-] shift
		// operations
		// (unlike assignment, equality-test, narrowing, and/or operations)
		// must be appropriately chosen

		Rational fraction0 = ZERO;
		// start at the little end of the [bigendian] input
		int i = value0.length - 1;

		while (fractionSize >= 64) {
			if (i < 0) {
				throw new NumberFormatException("not enough bits");
			}
			// mind the long (value0[i]) being unsigned
			fraction0 = fraction0.add(valueOfUnsigned(value0[i])).divide(
					TWO_POWER_64);
			fractionSize -= 64;
			i--;
		}

		// the rest must now fit into value0[0] (a long),
		// i.e. we don't support exponentSize > 63 at the moment;
		// as the power() method accepts ints (not longs),
		// the restriction is actually even on <= 31 bits

		if (i < 0) {
			throw new NumberFormatException("no bits");
		}
		if (i > 0) {
			throw new NumberFormatException("excess bits");
		}

		long value = value0[0];

		// [fractionSize [now is] < 64 by loop above]
		final long fractionMask = ((long) 1 << fractionSize) - 1;
		final long rawFraction = value & fractionMask;
		value >>>= fractionSize;

		// [exponentSize < 32 by [private] usage pattern; rawExponent < 2**31]
		final int exponentMask = (1 << exponentSize) - 1;
		final int exponentBias = (1 << (exponentSize - 1)) - 1;
		final int rawExponent = (int) value & exponentMask;
		value >>>= exponentSize;

		final int signSize = 1;
		final int signMask = (1 << signSize) - 1; // 1
		final int rawSign = (int) value & signMask;
		value >>>= signSize;

		if (value != 0) {
			throw new NumberFormatException("excess bits");
		}

		// check for Infinity and NaN (IEEE 754 rawExponent at its maximum)
		if (rawExponent == exponentMask) {
			// (no fraction bits means one of the Infinities; else NaN)
			throw new NumberFormatException(rawFraction == 0
					&& fraction0.isZero() ? (rawSign == 0 ? "Infinity"
					: "-Infinity") : "NaN");
		}

		// optimization -- avoids power() calculation below
		// (isZero and zero multiply) are cheap
		// check for zero (IEEE 754 rawExponent zero and no fraction bits)
		if (rawExponent == 0 && rawFraction == 0 && fraction0.isZero()) {
			return ZERO;
		}

		// handle subnormal numbers too (with rawExponent==0)
		// [fractionSize [still is] < 64]
		final long mantissa1 = rawFraction
				| (rawExponent == 0 ? (long) 0 : (long) 1 << fractionSize);

		// mind mantissa1 being unsigned
		final Rational mantissa = fraction0.add(valueOfUnsigned(mantissa1));
		// (subnormal numbers; exponent is one off)
		// [rawExponent < 2**31; exponentBias < 2**30]
		final int exponent = rawExponent - exponentBias
				+ (rawExponent == 0 ? 1 : 0) - fractionSize;
		final int sign = (rawSign == 0 ? 1 : -1);

		return valueOf(2).pow(exponent).multiply(mantissa).multiply(sign);
	}

	/**
	 * Convert a Rational to a IEEE 754 floating point number (of different
	 * sizes, as array of longs, big endian).
	 * <p>
	 * Possible loss of precision.
	 */
	// @PrecisionLoss
	private static long[] toIEEE754(Rational value, int fractionSize,
			int exponentSize) {
		if (value == null) {
			throw new NumberFormatException("null");
		}

		// [needed size: fractionSize+exponentSize+1; round up bits to a
		// multiple of 64]
		final long[] out0 = new long[(fractionSize + exponentSize + 1 + (64 - 1)) / 64];

		if (value.isZero()) {
			// 0.0
			// note: as we don't keep a sign with our ZERO,
			// we never return IEEE 754 -0.0 here
			for (int j = 0; j < out0.length; j++) {
				out0[j] = 0;
			}
			return out0;
		}

		final boolean negate = value.isNegative();
		if (negate) {
			value = value.negate();
		}

		// need to scale to this to get the full mantissa
		int exponent = fractionSize;
		final Rational lower = valueOf(2).pow(fractionSize);
		final Rational upper = lower.multiply(2);

		// optimization, and a good guess (but not exact in all cases)
		final int scale = lower.divide(value).logarithm2();
		value = value.multiply(valueOf(2).pow(scale));
		exponent -= scale;

		while (value.compareTo(lower) < 0) {
			// [typically done zero or one time]
			value = value.multiply(2);
			exponent--;
		}

		while (value.compareTo(upper) >= 0) {
			// [typically not reached]
			value = value.divide(2);
			exponent++;
		}

		// [rounding step, possible loss of precision step]
		BigInteger mantissa = value.bigIntegerValue();
		// adjust after [unfortunate] mantissa rounding
		if (upper.compareTo(mantissa) <= 0) {
			mantissa = mantissa.divide(BIG_INTEGER_TWO);
			exponent++;
		}

		// start [to fill] at the little end of the [bigendian] output
		int i = out0.length - 1;

		int fractionSize1 = fractionSize;
		while (fractionSize1 >= 64) {
			final BigInteger[] divrem = mantissa
					.divideAndRemainder(BIG_INTEGER_TWO_POWER_64);
			// [according to BigInteger javadoc] this takes the least
			// significant 64 bits;
			// i.e. in this case the long is considered unsigned, as we want it
			out0[i] = divrem[1].longValue();

			fractionSize1 -= 64;
			mantissa = divrem[0];
			i--;
		}

		// the rest must now fit into out0[0]

		if (i < 0) {
			// not reached
			throw new NumberFormatException("too many bits");
		}
		if (i > 0) {
			// not reached
			throw new NumberFormatException("not enough bits");
		}

		long fraction = mantissa.longValue();

		final int exponentBias = (1 << (exponentSize - 1)) - 1;
		exponent += exponentBias;
		final int maximalExponent = (1 << exponentSize) - 1;

		if (exponent >= maximalExponent) {
			// overflow
			// throw new NumberFormatException("overflow");
			// [positive or negative] infinity
			exponent = maximalExponent;
			fraction = 0;
			for (int j = 1; j < out0.length; j++) {
				out0[j] = 0;
			}
			// [keep sign]

		} else if (exponent <= 0) {
			// handle subnormal numbers too
			// [with know loss of precision]

			// drop one bit, while keeping the exponent
			int s = 1;

			// [need not shift more than fractionSize]
			final int n = (-exponent > fractionSize ? fractionSize : -exponent);
			s += n;
			exponent += n;

			// [possible loss of precision step]
			fraction = shiftrx(fraction, out0, 1, s);

			boolean zero = (fraction == 0);
			for (int j = 1; zero && j < out0.length; j++) {
				zero = (out0[j] == 0);
			}

			if (zero) {
				// underflow
				// throw new NumberFormatException("underflow");
				// 0.0 or -0.0; i.e.: keep sign
				exponent = 0;
				// [nonzero == 0 implies the rest of the fraction is zero as
				// well]
			}
		}

		// cut implied most significant bit
		// [unless with subnormal numbers]
		if (exponent != 0) {
			fraction &= ~((long) 1 << fractionSize1);
		}

		long out = 0;
		out |= (negate ? 1 : 0);
		out <<= exponentSize;
		out |= exponent;
		out <<= fractionSize1;
		out |= fraction;

		out0[0] = out;
		return out0;
	}

	/**
	 * Shift right, while propagating shifted bits (long[] is bigendian).
	 */
	private static long shiftrx(long a, long[] b, int boff, int n) {
		while (n > 0) {
			final int n2 = (n < 63 ? n : 63);
			final long m = ((long) 1 << n2) - 1;
			long c = a & m;
			a >>>= n2;
			for (int i = boff; i < b.length; i++) {
				final long t = b[i] & m;
				b[i] >>>= n2;
				b[i] |= (c << (64 - n2));
				c = t;
			}
			n -= n2;
		}
		return a;
	}

	/**
	 * Fixed dot-format "[-]i.f" string representation, with a precision.
	 * <p>
	 * Precision may be negative, in which case the rounding affects digits left
	 * of the dot, i.e. the integer part of the number, as well.
	 * <p>
	 * The exponentFormat parameter allows for shorter [intermediate] string
	 * representation, an optimization, e.g. used with toStringExponent.
	 * <p>
	 * Possible loss of precision.
	 */
	// @PrecisionLoss
	private String toStringDot(int precision, int radix, boolean exponentFormat) {
		checkRadixArgument(radix);

		Rational scaleValue = new Rational(bigIntegerPower(
				bigIntegerValueOf(radix), (precision < 0 ? -precision
						: precision)));
		if (precision < 0) {
			scaleValue = scaleValue.invert();
		}

		// default round mode.
		// [rounding step, possible loss of precision step]
		Rational n = multiply(scaleValue).round();
		final boolean negt = n.isNegative();
		if (negt) {
			n = n.negate();
		}

		String s = n.toString(radix);

		if (exponentFormat) {
			// note that this is _not_ the scientific notation
			// (one digit left of the dot exactly),
			// but some intermediate representation suited for post processing
			// [leaving away the left/right padding steps
			// is more performant in time and memory space]
			s = s + "E" + stringValueOf(-precision, radix);

		} 
		else {
			if (precision >= 0) {
				// left-pad with '0'
				while (s.length() <= precision) {
					s = "0" + s;
				}

				final int dot = s.length() - precision;
				final String i = s.substring(0, dot);
				final String f = s.substring(dot);

				s = i;
				if (f.length() > 0) {
					s = s + "." + f;
				}
			} 
			else {
				if (!s.equals("0")) {
					// right-pad with '0'
					for (int i = -precision; i > 0; i--) {
						s = s + "0";
					}
				}
			}
		}

		// add sign
		if (negt) {
			s = "-" + s;
		}

		return s;
	}

	/**
	 * Transform a [intermediate] dot representation to an exponent-format
	 * representation.
	 */
	private static String toExponentRepresentation(String s, int radix) {
		// skip '+'
		if (s.length() > 0 && s.charAt(0) == '+') {
			// typically not reached, due to [private] usage pattern
			s = s.substring(1);
		}

		// handle '-'
		boolean negt = false;
		if (s.length() > 0 && s.charAt(0) == '-') {
			negt = true;
			s = s.substring(1);
		}

		// skip initial zeros
		while (s.length() > 0 && s.charAt(0) == '0') {
			s = s.substring(1);
		}

		// check for and handle exponent
		// handle only upper case 'E' (we know we use that in earlier steps);
		// this allows any base using lower case characters
		int exponent0 = 0;
		final int exp = s.indexOf('E');
		if (exp != -1) {
			final String se = s.substring(exp + 1);
			s = s.substring(0, exp);
			exponent0 = (new Rational(se, radix)).intValueExact();
		}

		String si, sf;
		int exponent;

		final int dot = s.indexOf('.');
		if (dot != -1) {
			if (dot == 0) {
				// possibly more insignificant digits
				s = s.substring(1);
				exponent = -1;
				while (s.length() > 0 && s.charAt(0) == '0') {
					s = s.substring(1);
					exponent--;
				}

				if (s.equals("")) {
					// typically not reached, due to [private] usage pattern
					return "0";
				}

				// first significant digit
				si = s.substring(0, 1);
				sf = s.substring(1);
			} 
			else {
				// initial [significant] digit
				si = s.substring(0, 1);
				sf = s.substring(1, dot);
				exponent = sf.length();

				sf = sf + s.substring(dot + 1);
			}
		} 
		else {
			// [note that we just cut the zeros above]
			if (s.equals("")) {
				return "0";
			}

			// initial [significant] digit
			si = s.substring(0, 1);
			// rest
			sf = s.substring(1);
			exponent = sf.length();
		}

		exponent += exponent0;

		// drop trailing zeros
		while (sf.length() > 0 && sf.charAt(sf.length() - 1) == '0') {
			sf = sf.substring(0, sf.length() - 1);
		}

		s = si;
		if (!sf.equals("")) {
			s = s + "." + sf;
		}
		if (exponent != 0) {
			s = s + "E" + stringValueOf(exponent, radix);
		}

		if (negt) {
			s = "-" + s;
		}

		return s;
	}

	/**
	 * Return binary logarithm rounded towards floor (towards negative
	 * infinity).
	 * <p>
	 * Possible loss of precision.
	 */
	// @PrecisionLoss
	private int logarithm2() {
		if (isZero()) {
			// [typically not reached, due to [private] usage pattern]
			throw new ArithmeticException("logarithm of zero");
		}
		if (isNegative()) {
			// [typically not reached, due to [private] usage pattern]
			throw new ArithmeticException("logarithm of negative number");
		}

		final boolean inverted = (compareTo(ONE) < 0);
		final Rational a = (inverted ? invert() : this);

		// [possible loss of precision step]
		final int log = bigIntegerLogarithm2(a.bigIntegerValue());
		return (inverted ? -(log + 1) : log);
	}

	/**
	 * Return logarithm rounded towards floor (towards negative infinity).
	 * <p>
	 * Possible loss of precision.
	 */
	// @PrecisionLoss
	private int logarithm(int base) {
		// optimization
		if (base == 2) {
			return logarithm2();
		}

		if (isZero()) {
			// [typically not reached, due to [private] usage pattern]
			throw new ArithmeticException("logarithm of zero");
		}
		if (isNegative()) {
			// [typically not reached, due to [private] usage pattern]
			throw new ArithmeticException("logarithm of negative number");
		}
		// if (base < 2) {
		// // [typically not reached, due to [private] usage pattern]
		// throw new ArithmeticException("bad base");
		// }
		if (base < 0) {
			// [typically not reached, due to [private] usage pattern]
			throw new ArithmeticException("negative base");
		}
		if (base < 2) {
			// [typically not reached, due to [private] usage pattern]
			throw new ArithmeticException("base too small");
		}

		final boolean inverted = (compareTo(ONE) < 0);
		Rational a = (inverted ? invert() : this);
		final Rational bbase = valueOf(base);

		// optimization -- we could start from n=0
		// initial guess
		// [base 2 handled earlier]
		// [unusual bases are handled a bit less performant]
		final Rational lbase = (base == 10 ? LOGARITHM_TEN_GUESS
				: base == 16 ? LOGARITHM_SIXTEEN : valueOf(ilog2(base)));
		int n = valueOf(a.logarithm2()).divide(lbase).intValue();
		a = a.divide(bbase.pow(n));

		// note that these steps are needed anyway:
		// LOGARITHM_TEN_GUESS above e.g. is (as the name suggests)
		// a guess only (since most logarithms usually can't be expressed
		// as rationals generally); odd bases or off even worse
		while (a.compareTo(bbase) >= 0) {
			a = a.divide(bbase);
			n++;
		}
		while (a.compareTo(ONE) < 0) {
			a = a.multiply(bbase);
			n--;
		}

		// [possible loss of precision step]
		return (inverted ? -(n + 1) : n);
	}

	/**
	 * Return binary logarithm of an int.
	 */
	private static int ilog2(int n) {
		if (n == 0) {
			// [typically not reached, due to [private] usage pattern]
			throw new ArithmeticException("logarithm of zero");
		}
		if (n < 0) {
			// [typically not reached, due to [private] usage pattern]
			throw new ArithmeticException("logarithm of negative number");
		}

		int i = 0;

		// as this method is used in the context of [small] bases/radixes,
		// we expect less than 8 iterations at most, so no need to optimize
		while (n > 1) {
			n /= 2;
			i++;
		}

		return i;
	}

	/**
	 * Remainder or modulus of non-negative values. Helper function to
	 * remainder() and modulus().
	 */
	private Rational remainderOrModulusOfPositive(Rational that) {
		final int thisSignum = signum();
		final int thatSignum = that.signum();

		if (thisSignum < 0 || thatSignum < 0) {
			// typically not reached, due to [private] usage pattern
			throw new IllegalArgumentException("negative values(s)");
		}

		if (thatSignum == 0) {
			// typically not reached, due to [private] usage pattern
			throw new ArithmeticException("division by zero");
		}

		// optimization
		if (thisSignum == 0) {
			return ZERO;
		}

		return new Rational(bigIntegerMultiply(numerator, that.denominator)
				.remainder(bigIntegerMultiply(denominator, that.numerator)),
				bigIntegerMultiply(denominator, that.denominator));
	}

	/**
	 * Round to BigInteger helper function. Internally used.
	 * <p>
	 * Possible loss of precision.
	 */
	// @PrecisionLoss
	private BigInteger roundToBigInteger(int roundMode) {
		// note: remainder and its duplicate are calculated for all cases.

		BigInteger numerator = this.numerator;
		final BigInteger denominator = this.denominator;

		final int signum = numerator.signum();

		// optimization
		if (signum == 0) {
			// [typically not reached due to earlier test for integerp]
			return BIG_INTEGER_ZERO;
		}

		// keep info on the sign
		final boolean isPositive = (signum > 0);

		// operate on positive values
		if (!isPositive) {
			numerator = numerator.negate();
		}

		final BigInteger[] divrem = numerator.divideAndRemainder(denominator);
		BigInteger dv = divrem[0];
		final BigInteger r = divrem[1];

		// return if we don't need to round, independent of rounding mode
		if (bigIntegerIsZero(r)) {
			// [typically not reached since remainder is not zero
			// with normalized that are not integerp]
			if (!isPositive) {
				dv = dv.negate();
			}

			return dv;
		}

		boolean up = false;
		final int comp = r.multiply(BIG_INTEGER_TWO).compareTo(denominator);

		switch (roundMode) {

		// Rounding mode to round away from zero.
		case ROUND_UP:
			up = true;
			break;

		// Rounding mode to round towards zero.
		case ROUND_DOWN:
			up = false;
			break;

		// Rounding mode to round towards positive infinity.
		case ROUND_CEILING:
			up = isPositive;
			break;

		// Rounding mode to round towards negative infinity.
		case ROUND_FLOOR:
			up = !isPositive;
			break;

		// Rounding mode to round towards "nearest neighbor" unless both
		// neighbors are equidistant, in which case round up.
		case ROUND_HALF_UP:
			up = (comp >= 0);
			break;

		// Rounding mode to round towards "nearest neighbor" unless both
		// neighbors are equidistant, in which case round down.
		case ROUND_HALF_DOWN:
			up = (comp > 0);
			break;

		case ROUND_HALF_CEILING:
			up = (comp != 0 ? comp > 0 : isPositive);
			break;

		case ROUND_HALF_FLOOR:
			up = (comp != 0 ? comp > 0 : !isPositive);
			break;

		// Rounding mode to round towards the "nearest neighbor" unless both
		// neighbors are equidistant, in which case, round towards the even
		// neighbor.
		case ROUND_HALF_EVEN:
			up = (comp != 0 ? comp > 0 : !bigIntegerIsZero(dv
					.remainder(BIG_INTEGER_TWO)));
			break;

		case ROUND_HALF_ODD:
			up = (comp != 0 ? comp > 0 : bigIntegerIsZero(dv
					.remainder(BIG_INTEGER_TWO)));
			break;

		// Rounding mode to assert that the requested operation has an exact
		// result, hence no rounding is necessary. If this rounding mode is
		// specified on an operation that yields an inexact result, an
		// ArithmeticException is thrown.
		case ROUND_UNNECESSARY:
			if (!bigIntegerIsZero(r)) {
				throw new ArithmeticException("rounding necessary");
			}
			// [typically not reached due to earlier test for integerp]
			up = false;
			break;

		default:
			throw new IllegalArgumentException("unsupported rounding mode");
		}

		if (up) {
			dv = dv.add(BIG_INTEGER_ONE);
		}

		if (!isPositive) {
			dv = dv.negate();
		}

		// [rounding step, possible loss of precision step]
		return dv;
	}
}
