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
import java.util.Iterator;
import java.util.List;

import com.google.common.annotations.Beta;
import com.sri.ai.util.base.NullaryFunction;

/**
 * An iterator over subsets of k integers from 0 to n - 1, using the same collection instance to store all subsets.
 * 
 * @author braz
 */
@Beta
public class InPlaceSubsetsOfKIterator<E> extends NonDeterministicIterator<List<E>> {

	public InPlaceSubsetsOfKIterator(ArrayList<E> array, int k) {
		super(new DefaultLazyTree<List<E>>(makeChoicesIterator(0, new ArrayList<E>(k), array, k)));
	}

	@SuppressWarnings("unchecked")
	private static <T> Iterator<NullaryFunction<LazyTree<List<T>>>> makeChoicesIterator(int i, ArrayList<T> chosen, ArrayList<T> array, int k) {
		
		myAssert(
				() -> k <= array.size(),
				() -> InPlaceSubsetsOfKIterator.class + " requires k to be at most the array size, but we got instead k = " + k + " and array size = " + array.size());
		
		Iterator<NullaryFunction<LazyTree<List<T>>>> result;
		
		if (chosen.size() + (array.size() - i) < k) {
			result = iterator(); // fail, no possible ways of choosing k elements
		}
		else if (chosen.size() == k) {
			result = iterator(() -> new DefaultLazyTree(chosen));
		}
		else {
			result =
					iterator(
							() -> {
								ArrayList<T> chosenIncludingITh = new ArrayList<>(chosen);
								chosenIncludingITh.add(array.get(i));
								return new DefaultLazyTree<List<T>>(makeChoicesIterator(i + 1, chosenIncludingITh, array, k));
							},
							() -> {
								// choose not to include i, simply move on with the same list
								return new DefaultLazyTree<List<T>>(makeChoicesIterator(i + 1, chosen, array, k));
							}
							);
		}
		
		return result;
	}
	
//	public static void main(String[] args) {
//		InPlaceSubsetsOfKIterator<String> iterator = new InPlaceSubsetsOfKIterator<>(arrayList("apple", "banana", "orange", "pineapple"), 2);
//		while (iterator.hasNext()) {
//			List<String> subset = iterator.next();
//			System.out.println(subset);	
//		}
//		
//		System.out.println("---");	
//		iterator = new InPlaceSubsetsOfKIterator<>(arrayList(), 0);
//		while (iterator.hasNext()) {
//			List<String> subset = iterator.next();
//			System.out.println(subset);	
//		}
//		
//		long start = System.currentTimeMillis();
//		InPlaceSubsetsOfKIterator<Integer> integerIterator = new InPlaceSubsetsOfKIterator<Integer>(arrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), 4);
//		while (integerIterator.hasNext()) {
//			integerIterator.next();
//		}
//		System.out.println(System.currentTimeMillis() - start + " ms");	
//	}
}
