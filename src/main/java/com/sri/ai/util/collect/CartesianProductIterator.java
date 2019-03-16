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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.google.common.annotations.Beta;
import com.sri.ai.util.base.NullaryFunction;

/**
 * A iterator over the Cartesian product of the ranges of a list of iterators.
 * The constructor takes a list of iterator <i>makers</i>
 * because each dimension but the first one will need to be iterated multiple times,
 * so a new iterator will be needed for each sweeping.
 * An iterator maker for a given key is required to make iterators over the same range every time.
 * 
 * @author braz
 * 
 */
@Beta
public class CartesianProductIterator<E> extends EZIterator<ArrayList<E>> {

	private List<? extends NullaryFunction<Iterator<? extends E>>> iteratorMakers;
	private List<Iterator<? extends E>> iterators;
	
	/**
	 * Constructs a Cartesian product iterator given a sequence of iterator makers (one per dimension).
	 * @param iteratorMakers iterator makers
	 */
	@SafeVarargs
	public CartesianProductIterator(NullaryFunction<Iterator<? extends E>>... iteratorMakers) {
		this(Arrays.asList(iteratorMakers));
	}
	
	public CartesianProductIterator(List<? extends NullaryFunction<Iterator<? extends E>>> iteratorMakers) {
		this.iteratorMakers = iteratorMakers;
		this.iterators = new ArrayList<Iterator<? extends E>>(iteratorMakers.size());
		for (NullaryFunction<Iterator<? extends E>> maker : iteratorMakers) {
			iterators.add(maker.apply());
		}
		
		next = new ArrayList<E>(iterators.size());
		for (Iterator<? extends E> iterator : iterators) {
			if (iterator.hasNext()) {
				next.add(iterator.next());
			}
			else { // Cartesian product is empty because this component is empty
				next = null;
				break;
			}
		}
		onNext = true;
	}
	
	@Override
	protected ArrayList<E> calculateNext() {
		boolean iterated = false;
		next = new ArrayList<E>(next);
		for (int indexPlusOne = iterators.size(); indexPlusOne != 0; indexPlusOne--) {
			// we iterate backwards to make the rightmost iterator the least significant one,
			// which is arguably the more standard way to do it because rightmost digits are the least significant ones.
			int index = indexPlusOne - 1;
			Iterator<? extends E> iterator = iterators.get(index);
			if (iterator.hasNext()) {
				next.set(index, iterator.next());
				iterated = true;
				break; // iterated one component, done.
			}
			else {
				iterator = iteratorMakers.get(index).apply();
				iterators.set(index, iterator);
				next.set(index, iterator.next());
				// no need to check hasNext because iteratorMakers are required to make iterators over the same range every time.
			}
		}
		
		if ( ! iterated) { // ran out of possible arrays
			next = null;
		}
		
		return next;
	}
}
