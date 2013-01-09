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

import com.google.common.annotations.Beta;

/**
 * An iterator over a given integer interval
 * {@code [start, [end, (end+increment-1)]]} with a specified increment over the
 * interval.
 * 
 * @author braz
 * 
 */
@Beta
public class IntegerIterator extends EZIterator<Integer> {

	private int i;
	private int end;
	private int increment;

	/**
	 * Constructor.
	 * 
	 * @param start
	 *            the starting integer, inclusive.
	 * @param end
	 *            the ending integer in the range, inclusive if can increment to
	 *            it or past it.
	 * @param increment
	 *            the amount to increment on each iteration.
	 */
	public IntegerIterator(int start, int end, int increment) {
		this.i = start;
		this.end = end;
		this.increment = increment;
	}

	/**
	 * Constructor with a default increment of 1.
	 * 
	 * @param start
	 *            the starting integer, inclusive.
	 * @param end
	 *            the ending integer in the range, inclusive if can increment to
	 *            it or past it.
	 */
	public IntegerIterator(int start, int end) {
		this(start, end, 1);
	}

	/**
	 * Constructor starting at 0 and a default increment of 1.
	 * 
	 * @param end
	 *            the ending integer in the range, inclusive if can increment to
	 *            it or past it.
	 */
	public IntegerIterator(int end) {
		this(0, end, 1);
	}

	@Override
	protected Integer calculateNext() {
		if (i < end) {
			Integer next = Integer.valueOf(i);
			i += increment;
			return next;
		}
		return null;
	}
}
