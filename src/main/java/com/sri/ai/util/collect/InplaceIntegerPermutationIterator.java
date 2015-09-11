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
 * An iterator over permutations of integers from 1 to n, using the same array list to store all permutations
 * 
 * @author braz
 */
@Beta
public class InplaceIntegerPermutationIterator extends NonDeterministicIterator<List<Integer>> {

	public InplaceIntegerPermutationIterator(int n) {
		super(new DefaultLazyTree<List<Integer>>(makeChoicesIterator(0, n, new LinkedHashSet<Integer>(), Util.fill(n, -1))));
	}

	@SuppressWarnings("unchecked")
	private static Iterator<NullaryFunction<LazyTree<List<Integer>>>> makeChoicesIterator(int i, int n, Collection<Integer> alreadyTaken, ArrayList<Integer> list) {
		
		Iterator<NullaryFunction<LazyTree<List<Integer>>>> result;
		
		if (i == n) {
			result = iterator(() -> new DefaultLazyTree(list));
		}
		else {
			PredicateIterator<Integer> available = PredicateIterator.make(
					new IntegerIterator(n), 
					j -> ! alreadyTaken.contains(j)
					);
			result =
					FunctionIterator.make(
							available,
							j -> () -> {
								list.set(i, j);
								Collection<Integer> newAlreadyTaken = new LinkedList<Integer>(alreadyTaken); // maybe we can optimize to eliminate this copy without much extra search cost.
								newAlreadyTaken.add(j);
								return new DefaultLazyTree<List<Integer>>(makeChoicesIterator(i + 1, n, newAlreadyTaken, list));
							});
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		InplaceIntegerPermutationIterator iterator = new InplaceIntegerPermutationIterator(3);
		while (iterator.hasNext()) {
			List<Integer> permutation = iterator.next();
			System.out.println(permutation);	
		}
		
		System.out.println("---");	
		iterator = new InplaceIntegerPermutationIterator(0);
		while (iterator.hasNext()) {
			List<Integer> permutation = iterator.next();
			System.out.println(permutation);	
		}
		
		long start = System.currentTimeMillis();
		iterator = new InplaceIntegerPermutationIterator(10);
		while (iterator.hasNext()) {
			iterator.next();
		}
		System.out.println(System.currentTimeMillis() - start + " ms");	
	}
}
