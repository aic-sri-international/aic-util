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

import static com.sri.ai.util.Util.map;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.annotations.Beta;
import com.sri.ai.util.base.NullaryFunction;

/**
 * A iterator over the Cartesian product of the ranges of a map of iterators,
 * indexed by an arbitrary type K.
 * The constructor takes a map of iterator <i>makers</i>
 * because each dimension but the first one will need to be iterated multiple times,
 * so a new iterator will be needed for each sweeping.
 * An iterator maker for a given key is required to make iterators over the same range every time.
 * 
 * @author braz
 * 
 */
@Beta
public class CartesianProductIterator<K,V> extends EZIterator<Map<K,V>> {

	private Map<K, NullaryFunction<Iterator<V>>> iteratorMakers;
	private Map<K, Iterator<V>> iterators;
	
	public CartesianProductIterator(Map<K, NullaryFunction<Iterator<V>>> iteratorMakers) {
		this.iteratorMakers = iteratorMakers;
		this.iterators = new LinkedHashMap<K, Iterator<V>>(iteratorMakers.size());
		for (Map.Entry<K, NullaryFunction<Iterator<V>>> entry : iteratorMakers.entrySet()) {
			iterators.put(entry.getKey(), entry.getValue().apply());
		}
		
		next = map();
		for (Map.Entry<K, Iterator<V>> entry : iterators.entrySet()) {
			K key = entry.getKey();
			Iterator<V> iterator = entry.getValue();
			if (iterator.hasNext()) {
				next.put(key, iterator.next());
			}
			else { // cartesian product is empty because this component is empty
				next = null;
				break;
			}
		}
		onNext = true;
	}
	
	@Override
	protected Map<K, V> calculateNext() {
		boolean iterated = false;
		for (Map.Entry<K, Iterator<V>> entry : iterators.entrySet()) {
			K key = entry.getKey();
			Iterator<V> iterator = entry.getValue();
			if (iterator.hasNext()) {
				next.put(key, iterator.next());
				iterated = true;
				break; // iterated one component, done.
			}
			else {
				iterator = iteratorMakers.get(key).apply();
				iterators.put(key, iterator);
				next.put(key, iterator.next());
				// no need to check hasNext because iteratorMakers are required to make iterators over the same range every time.
			}
		}
		
		if (!iterated) {
			next = null;
		}
		else {
			next = new LinkedHashMap<K,V>(next);
		}
		
		return next;
	}
}
