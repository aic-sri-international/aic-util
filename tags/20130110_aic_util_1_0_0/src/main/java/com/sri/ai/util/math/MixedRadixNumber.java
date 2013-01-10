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
package com.sri.ai.util.math;

import java.math.BigInteger;
import java.util.List;

import com.google.common.annotations.Beta;

/**
 * For details on <a
 * href="http://demonstrations.wolfram.com/MixedRadixNumberRepresentations/"
 * >Mixed Radix Number Representations.</a>
 * 
 * Will proceed from right to left in geometric sequence based on the radix
 * values provided.
 * 
 * @author Ciaran O'Reilly
 */
@Beta
public class MixedRadixNumber extends Number {
	//
	private static final long serialVersionUID = 1L;
	//
	private BigInteger value = BigInteger.ZERO;
	private BigInteger maxValue = BigInteger.ZERO;
	private int[] radices = null;
	private int[] currentNumeralValue = null;
	private BigInteger[] cachedRadixValues = null;
	private boolean recalculateValue = false;

	/**
	 * Constructs a mixed radix number with a specified value and a specified
	 * array of radices.
	 * 
	 * @param value
	 *            the value of the mixed radix number
	 * @param radices
	 *            the radices used to represent the value of the mixed radix
	 *            number
	 */
	public MixedRadixNumber(BigInteger value, int[] radices) {
		this.value = value;
		this.radices = new int[radices.length];
		System.arraycopy(radices, 0, this.radices, 0, radices.length);
		initialize();
	}

	/**
	 * Constructs a mixed radix number with a specified value and a specified
	 * list of radices.
	 * 
	 * @param value
	 *            the value of the mixed radix number
	 * @param radices
	 *            the radices used to represent the value of the mixed radix
	 *            number
	 */
	public MixedRadixNumber(BigInteger value, List<Integer> radices) {
		this.value = value;
		this.radices = new int[radices.size()];
		for (int i = 0; i < radices.size(); i++) {
			this.radices[i] = radices.get(i);
		}
		initialize();
	}

	/**
	 * Constructs a mixed radix number with a specified array of numerals and a
	 * specified array of radices.
	 * 
	 * @param radixValues
	 *            the numerals of the mixed radix number
	 * @param radices
	 *            the radices of the mixed radix number
	 */
	public MixedRadixNumber(int[] radixValues, int[] radices) {
		this(BigInteger.ZERO, radices);
		setCurrentValueFor(radixValues);
	}

	/**
	 * 
	 * @return return the current value of the mixed radix number as a
	 *         BigInteger.
	 */
	public BigInteger getValue() {
		if (recalculateValue) {
			value = getValueFor(currentNumeralValue);
			recalculateValue = false;
		}
		return value;
	}

	/**
	 * Returns the maximum value which can be represented by the current array
	 * of radices.
	 * 
	 * @return the maximum value which can be represented by the current array
	 *         of radices.
	 */
	public BigInteger getMaxAllowedValue() {
		return maxValue;
	}

	/**
	 * Returns the value of the mixed radix number with the specified array of
	 * numerals and the current array of radices.
	 * 
	 * @return the value of the mixed radix number
	 * 
	 * @throws IllegalArgumentException
	 *             if any of the specified numerals is less than zero, or if any
	 *             of the specified numerals is greater than it's corresponding
	 *             radix.
	 */
	public BigInteger getValueFor(int[] radixValues) {
		if (radixValues.length != radices.length) {
			throw new IllegalArgumentException(
					"Radix values not same size as Radices.");
		}

		BigInteger cvalue = BigInteger.ZERO;
		BigInteger mvalue = BigInteger.ONE;
		for (int i = radixValues.length-1; i >= 0; i--) {
			if (radixValues[i] < 0 || radixValues[i] > radices[i]) {
				throw new IllegalArgumentException("Radix value " + i
						+ " is out of range for radix at this position");
			}
			if (i != radixValues.length-1) {
				mvalue = mvalue.multiply(cachedRadixValues[radices[i + 1]]);
			}
			cvalue = cvalue.add(mvalue
					.multiply(cachedRadixValues[radixValues[i]]));
		}
		return cvalue;
	}

	/**
	 * Sets the value of the mixed radix number with the specified array of
	 * numerals and the current array of radices.
	 * 
	 * @param radixValues
	 *            the numerals of the mixed radix number
	 */
	public void setCurrentValueFor(int[] radixValues) {
		this.value = getValueFor(radixValues);
		System.arraycopy(radixValues, 0, this.currentNumeralValue, 0,
				radixValues.length);
	}

	/**
	 * 
	 * @return true if you can increment by 1 the current value, false
	 *         otherwise.
	 */
	public boolean canIncrement() {
		boolean canIncrement = true;
		int numberAtMaxValue = 0;
		for (int i = 0; i < radices.length; i++) {
			if (currentNumeralValue[i] == radices[i] - 1) {
				numberAtMaxValue++;
			}
		}
		if (numberAtMaxValue == radices.length) {
			canIncrement = false;
		}
		return canIncrement;
	}

	/**
	 * Increments the value of the mixed radix number, if the value is less than
	 * the maximum value which can be represented by the current array of
	 * radices.
	 * 
	 * @return <code>true</code> if the increment was successful.
	 */
	public boolean increment() {
		boolean canIncrement = canIncrement();

		if (canIncrement()) {
			for (int i = radices.length-1; i >= 0; i--) {
				if (currentNumeralValue[i] < radices[i] - 1) {
					currentNumeralValue[i] = currentNumeralValue[i] + 1;
					for (int j = i + 1; j < radices.length; j++) {
						currentNumeralValue[j] = 0;
					}
					break;
				}
			}
			recalculateValue = true;
		}

		return canIncrement;
	}

	/**
	 * 
	 * @return true if you can decrement by 1 the current value, false
	 *         otherwise.
	 */
	public boolean canDecrement() {
		boolean canDecrement = true;
		int numberAtMinValue = 0;
		for (int i = 0; i < radices.length; i++) {
			if (currentNumeralValue[i] == 0) {
				numberAtMinValue++;
			}
		}
		if (numberAtMinValue == radices.length) {
			canDecrement = false;
		}
		return canDecrement;
	}

	/**
	 * Decrements the value of the mixed radix number, if the value is greater
	 * than zero.
	 * 
	 * @return <code>true</code> if the decrement was successful.
	 */
	public boolean decrement() {
		boolean canDecrement = canDecrement();

		if (canDecrement) {
			for (int i = radices.length-1; i >= 0; i--) {
				if (currentNumeralValue[i] > 0) {
					currentNumeralValue[i] = currentNumeralValue[i] - 1;
					for (int j = i + 1; j < radices.length; j++) {
						currentNumeralValue[j] = radices[j] - 1;
					}
					break;
				}
			}
			recalculateValue = true;
		}

		return canDecrement;
	}

	/**
	 * Returns the numeral at the specified position.
	 * 
	 * @param atPosition
	 *            the position of the numeral to return
	 * @return the numeral at the specified position.
	 */
	public int getCurrentNumeralValue(int atPosition) {
		if (atPosition >= 0 && atPosition < radices.length) {
			return currentNumeralValue[atPosition];
		}
		throw new IllegalArgumentException(
				"Argument atPosition must be >=0 and < " + radices.length);
	}

	//
	// START-Number
	@Override
	public int intValue() {
		return getValue().intValue();
	}

	@Override
	public long longValue() {
		return getValue().longValue();
	}

	@Override
	public float floatValue() {
		return getValue().floatValue();
	}

	@Override
	public double doubleValue() {
		return getValue().doubleValue();
	}

	// END-Number
	//

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < radices.length; i++) {
			sb.append("[");
			sb.append(this.getCurrentNumeralValue(i));
			sb.append("]");
		}

		return sb.toString();
	}

	//
	// PRIVATE
	//

	/**
	 * Sets the maximum value which can be represented by the current array of
	 * radices and related working attributes.
	 * 
	 * @throws IllegalArgumentException
	 *             if no radices are defined, if any radix is less than one, or
	 *             if the current value is greater than the maximum value which
	 *             can be represented by the current array of radices.
	 */
	private void initialize() {
		if (0 == radices.length) {
			throw new IllegalArgumentException(
					"At least 1 radix must be defined.");
		}
		for (int i = 0; i < radices.length; i++) {
			if (radices[i] < 1) {
				throw new IllegalArgumentException(
						"Invalid radix, must be >= 1");
			}
		}

		// Cache the possible radix values
		// so we don't have to create BigIntegers
		// for the same set of values multiple times
		int maxRadixValue = 0;
		for (int i = 0; i < radices.length; i++) {
			if (radices[i] > maxRadixValue) {
				maxRadixValue = radices[i];
			}
		}
		cachedRadixValues = new BigInteger[maxRadixValue + 1];
		for (int i = 0; i <= maxRadixValue; i++) {
			cachedRadixValues[i] = BigInteger.valueOf(i);
		}

		// Calculate the maxValue allowed
		maxValue = cachedRadixValues[radices[0]];
		for (int i = 1; i < radices.length; i++) {
			maxValue = maxValue.multiply(cachedRadixValues[radices[i]]);
		}
		maxValue = maxValue.subtract(BigInteger.ONE);

		if (value.max(maxValue) != maxValue && !value.equals(maxValue)) {
			throw new IllegalArgumentException(
					"The value ["
							+ value
							+ "] cannot be represented with the radices provided, max value is "
							+ maxValue);
		}

		currentNumeralValue = new int[radices.length];
		// Now set the current numeral values based on
		// the current value of the mixed radix number
		BigInteger quotient = value;
		for (int i = radices.length -1; i >= 0; i--) {
			if (!quotient.equals(BigInteger.ZERO)) {
				currentNumeralValue[i] = quotient.mod(
						cachedRadixValues[radices[i]]).intValue();
				quotient = quotient.divide(cachedRadixValues[radices[i]]);
			} 
			else {
				currentNumeralValue[i] = 0;
			}
		}
	}
}
