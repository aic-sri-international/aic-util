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

import static com.sri.ai.util.Util.iterator;
import static com.sri.ai.util.Util.myAssert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import com.google.common.annotations.Beta;
import com.sri.ai.util.Util;
import com.sri.ai.util.base.NullaryFunction;


/**
 * An iterator over sub-tuples of k integers from 0 to n - 1, using the same collection instance to store all sub-tuples.
 * 
 * @author braz
 */
@Beta
public class InPlaceSubKTuplesIterator<E> extends NonDeterministicIterator<List<E>> {

	public InPlaceSubKTuplesIterator(ArrayList<E> array, int k) {
		super(new DefaultLazyTree<List<E>>(makeChoicesIterator(0, array, k, new LinkedHashSet<E>(), Util.fill(k, null))));
	}

	@SuppressWarnings("unchecked")
	private static <T> Iterator<NullaryFunction<LazyTree<List<T>>>> makeChoicesIterator(int i, ArrayList<T> array, int k, Collection<T> alreadyTaken, ArrayList<T> kTuple) {
		
		myAssert(
				() -> k <= array.size(),
				() -> InPlaceSubKTuplesIterator.class + " requires k to be at most the array size, but we got instead k = " + k + " and array size = " + array);
		
		Iterator<NullaryFunction<LazyTree<List<T>>>> result;
		
		if (i == k) {
			result = iterator(() -> new DefaultLazyTree(kTuple));
		}
		else {
			PredicateIterator<T> available = PredicateIterator.make(
					array, 
					j -> ! alreadyTaken.contains(j)
					);
			result =
					FunctionIterator.make(
							available,
							j -> () -> {
								kTuple.set(i, j);
								Collection<T> newAlreadyTaken = new LinkedList<T>(alreadyTaken); // maybe we can optimize to eliminate this copy without much extra search cost.
								newAlreadyTaken.add(j);
								return new DefaultLazyTree<List<T>>(makeChoicesIterator(i + 1, array, k, newAlreadyTaken, kTuple));
							});
		}
		
		return result;
	}
	
//	public static void main(String[] args) {
//		Iterator<List<String>> iterator = new InPlaceSubKTuplesIterator<String>(arrayList("apple", "banana", "orange", "pineapple"), 3);
//		while (iterator.hasNext()) {
//			List<String> subTuple = iterator.next();
//			System.out.println(subTuple);	
//		}
//		
//		System.out.println("---");	
//		iterator = new InPlaceSubKTuplesIterator<String>(arrayList(), 0);
//		while (iterator.hasNext()) {
//			List<String> subTuple = iterator.next();
//			System.out.println(subTuple);	
//		}
//		
//		long start = System.currentTimeMillis();
//		Iterator<List<Integer>> integerIterator = new InPlaceSubKTuplesIterator<Integer>(arrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), 4);
//		while (integerIterator.hasNext()) {
//			integerIterator.next();
//		}
//		System.out.println(System.currentTimeMillis() - start + " ms");	
//	}
}
