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

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import com.google.common.annotations.Beta;


/**
 * An iterator based on an array of sub-iterators that ranges
 * over all i-th elements of each sub-iterator before ranging over the (i+1)-th elements.
 * 
 * @author braz
 */
@Beta
public class BreadthFirstIterator<T> extends EZIteratorWithNull<T> {

	LinkedList<Iterator<T>> subIterators;
	ListIterator<Iterator<T>> subIteratorsIterator;
	
	@SafeVarargs
	public BreadthFirstIterator(Iterator<T>... subIterators) {
		this.subIterators = new LinkedList<Iterator<T>>(Arrays.asList(subIterators));
		this.subIteratorsIterator = this.subIterators.listIterator();
	}

	@Override
	protected T calculateNext() {
		while (true) {

			// look for next sub-iterator that has next element in this round, if any
			Iterator<T> subIteratorWhichHasNext = null;
			while (subIteratorWhichHasNext == null && subIteratorsIterator.hasNext()) {
				subIteratorWhichHasNext = subIteratorsIterator.next();
				if ( ! subIteratorWhichHasNext.hasNext()) {
					subIteratorsIterator.remove(); // discard depleted sub-iterator
					subIteratorWhichHasNext = null; // discard empty sub-iterator as current sub-iterator
				}
			}

			// if there was no sub-iterator with next element in this round,
			// check if round yielded anything, or if whole iterator is over
			if (subIteratorWhichHasNext != null){
				// found iterator with next element, return next element
				return subIteratorWhichHasNext.next();
			}
			else if ( ! subIterators.isEmpty()) { // must have gone over all sub-iterators, and only non-empty ones remain if any
				// there are still sub-iterators with next elements; go for another round
				subIteratorsIterator = subIterators.listIterator();
			}
			else {
				// must have gone over all sub-iterators and they are all depleted -- iterator is over
				endOfRange();
				return null;
			}
		}
	}
}
