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
 * Neither the name of the aic-expresso nor the names of its
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
package com.sri.ai.util.base;

import static com.sri.ai.util.Util.myAssert;
import static com.sri.ai.util.base.PairOf.pairOf;

import com.google.common.annotations.Beta;
import com.sri.ai.util.collect.EZIterator;

/**
 * A cloneable iterator over ordered pairs of integers less than <code>n</code>.
 * 
 * @author braz
 *
 */
@Beta
public class OrderedPairsOfIntegersIterator extends EZIterator<PairOf<Integer>> implements CloneableIterator<PairOf<Integer>> {

	private int n;
	private int i;
	private int j;
	
	public OrderedPairsOfIntegersIterator(int n) {
		this(n, 0, 1, true);
	}

	public OrderedPairsOfIntegersIterator(int n, int i, int j) {
		this(n, i, j, true);
	}

	OrderedPairsOfIntegersIterator(int n, int i, int j, boolean onNext) {
		super();
		myAssert(() -> i >= 0 && i < n - 1, () -> "i must be in [0, n - 1] but was " + i + " whereas n is " + n);
		myAssert(() -> j >= 0 && i < n    , () -> "j must be in [0, n]     but was " + j + " whereas n is " + n);
		this.n = n;
		this.i = i;
		this.j = j;
		this.onNext = onNext;
		this.next = pairOf(i, j);
	}

	@Override
	public OrderedPairsOfIntegersIterator clone() {
		return new OrderedPairsOfIntegersIterator(n, i, j, onNext);
	}

	@Override
	protected PairOf<Integer> calculateNext() {
		if ( ! increment()) {
			return null;
		}
		return next; // relies on the fact that increment sets 'next'
	}

	/**
	 * Moves to next pair, unless there is none, in which case returns false.
	 * @return
	 */
	public boolean increment() {
		if (j == n - 1) {
			if ( ! incrementI()) {
				return false;
			}
		}
		else {
			j++;
			next = pairOf(i, j);
			onNext = true;
		}
		return true;
	}

	/**
	 * Increments i and sets j to i + 1, unless there is no such position, in which case returns false.
	 * @return
	 */
	public boolean incrementI() {
		if (i == n - 2) {
			return false;
		}
		i++;
		j = i + 1;
		next = pairOf(i, j);
		onNext = true;
		return true;
	}
}