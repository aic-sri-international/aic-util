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
package com.sri.ai.util.collect;

import java.math.BigDecimal;

import org.apache.commons.lang3.Validate;

import com.google.common.annotations.Beta;

/**
 * An iterator over a given BigDecimal interval
 * {@code [start, end[} (with inclusive start and exclusive end)
 * or
 * {@code [start, end]} (with inclusive start and inclusive end)
 * with a specified increment over the interval.
 * 
 */
@Beta
public class BigDecimalIterator extends EZIterator<BigDecimal> {
	private static final BigDecimal DEFAULT_INCREMENT = BigDecimal.ONE;
	private BigDecimal end;
	private boolean exclusive;
	private boolean rangeIsEmpty;
	private boolean rangeIsSingleton;
	private BigDecimal increment = DEFAULT_INCREMENT;
	private boolean infinite;

	/**
	 * 		this.end = end;

	 *
	 * @param start
	 *            the starting BigDecimal, inclusive.
	 * @param end
	 *            the ending BigDecimal in the range.
	 * @param exclusive 
	 *            whether the end limit is exclusive or not
	 * @param increment
	 *            the amount to increment on each iteration.
	 */
	public BigDecimalIterator(BigDecimal start, BigDecimal end, boolean exclusive, BigDecimal increment) {
		this.next = Validate.notNull(start, "start value cannot be null");
		this.rangeIsEmpty = exclusive? start.compareTo(end) >= 0 : start.compareTo(end) > 0;
		this.rangeIsSingleton = !exclusive && start.compareTo(end) == 0;
		this.onNext = ! rangeIsEmpty;
		this.end = Validate.notNull(end, "end value cannot be null");
		this.exclusive = exclusive;
		this.increment = Validate.notNull(increment, "increment value cannot be null");

		Validate.isTrue(rangeIsEmpty || rangeIsSingleton || increment.compareTo(BigDecimal.ZERO) > 0,
				"increment value must be greater than zero if range is greater than a point");
	}

	/**
	 * Constructor with a default increment of 1.
	 *
	 * @param start the starting BigDecimal, inclusive.
	 * @param end the ending BigDecimal in the range, exclusive
	 * @param exclusive whether the end limit is exclusive or not
	 */
	public BigDecimalIterator(BigDecimal start, BigDecimal end, boolean exclusive) {
		this(start, end, true, DEFAULT_INCREMENT);
	}

	/**
	 * Constructor starting at a given BigDecimal and incrementing indefinitely with a value of 1.
	 *
	 * @param start the initial value (there is no end value)
	 */
	private BigDecimalIterator(BigDecimal start) {
		this.next = Validate.notNull(start, "start value cannot be null");
		this.onNext = true;
		this.infinite = true;
		this.rangeIsEmpty = false;
		this.rangeIsSingleton = false;
	}

	public static BigDecimalIterator fromThisValueOnForever(BigDecimal start) {
		return new BigDecimalIterator(start);
	}

	@Override
	protected BigDecimal calculateNext() {
		if (rangeIsEmpty || rangeIsSingleton) {
			next = null;
		}
		else {
			next = next.add(increment);

			if (!infinite && finiteButBeyondEnd()) {
				next = null;
			}
		}
		return next;
	}

	public boolean finiteButBeyondEnd() {
		boolean result = 
				exclusive? 
						next.compareTo(end) >= 0 
						: next.compareTo(end) > 0;
		return result;
	}
}
