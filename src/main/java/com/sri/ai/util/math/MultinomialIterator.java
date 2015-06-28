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

import java.util.Iterator;

import com.sri.ai.util.collect.EZIterator;

/**
 * An {@link Iterator} over {@link Multinomial}s.
 * Provided multinomial is internally kept by iterator and modifiable,
 * so operations on it will reflect on future operations with the iterator.
 */
public class MultinomialIterator extends EZIterator<Multinomial> {

	private Multinomial multinomial;
	
	/**
	 * Creates an iterator around a given multinomial.
	 * @param multinomial the multinomial to iterate
	 */
	public MultinomialIterator(Multinomial multinomial) {
		this.multinomial = multinomial;
		this.onNext = true;
		this.next = multinomial;
	}
	
	/**
	 * Creates an iterator around a multinomial constructed with {@link Multinomial#Multinomial(int, int)}.
	 * @param n the number of elements being distributed
	 * @param m the number of classes into which elements are being distributed
	 */
	public MultinomialIterator(int n, int m) {
		this(new Multinomial(n, m));
	}
	
	/**
	 * Creates an iterator around a multinomial constructed with {@link Multinomial#Multinomial(int[])}.
	 * @param counters an array of class counters to construct a multinomial from.
	 */
	public MultinomialIterator(int[] counters) {
		this(new Multinomial(counters));
	}
	
	@Override
	protected Multinomial calculateNext() {
		Multinomial result;
		boolean hasSuccessor = multinomial.iterate();
		if (hasSuccessor) {
			result = multinomial;
		}
		else {
			result = null;
		}
		return result;
	}
}
