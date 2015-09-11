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

import java.util.Iterator;
import java.util.Stack;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.sri.ai.util.Util;
import com.sri.ai.util.base.NullaryFunction;


/**
 * An abstract iterator ranging over elements on the leaves of a virtual tree.
 * <p>
 * The choice tree is either a information tree (containing an element of the iterator's range),
 * or a non-information tree containing an iterator over sub-tree makers, which are thunks.
 * These thunks are allowed to perform whatever bookkeeping necessary,
 * and then return a sub-tree. 
 * <p>
 * The class is, admittedly, a little subtle to use.
 * Here's an example of code using it:
 * <pre>
 * 	
	private static Iterator<NullaryFunction<LazyTree<String>>> makeIterator(String prefix, int depth) {
		if (depth == 2) {
			return iterator(() -> new DefaultLazyTree(prefix));
		}
		Function<String, NullaryFunction<LazyTree<String>>> fromStringToSubTreeMaker =
				s -> () -> new DefaultLazyTree<String>(makeIterator(prefix + s, depth + 1));
		Iterator<NullaryFunction<LazyTree<String>>> result = 
				FunctionIterator.make(iterator("a", "b", "c"), fromStringToSubTreeMaker);
		return result;
	}
	
	public static void main(String[] args) {
		DefaultLazyTree<String> root = new DefaultLazyTree<String>(makeIterator("info: ", 0));
		NonDeterministicIterator<String> it = new NonDeterministicIterator<String>(root);
		System.out.println(Util.join(it));
	}
</pre>
 * 
 * @author braz
 */
@Beta
public class NonDeterministicIterator<E> extends EZIterator<E> {

	private Stack<Iterator<NullaryFunction<LazyTree<E>>>> stack;
	
	@SuppressWarnings("unchecked")
	public NonDeterministicIterator(NullaryFunction<LazyTree<E>> root) {
		stack = new Stack<>();
		stack.push(iterator(root));
	}
	
	public NonDeterministicIterator(LazyTree<E> root) {
		this(() -> root);
	}
	
	@Override
	protected E calculateNext() {
		E result = null;
		while (result == null && !stack.isEmpty()) {
			Iterator<NullaryFunction<LazyTree<E>>> branchingPoint = stack.peek();
			if (branchingPoint.hasNext()) {
				NullaryFunction<LazyTree<E>> branchMaker = branchingPoint.next();
				LazyTree<E> branchResult = branchMaker.apply();
				Iterator<NullaryFunction<LazyTree<E>>> subTreeMakers = branchResult.getSubTreeMakers();
				if (!subTreeMakers.hasNext()) {
					result = branchResult.getInformation();
				}
				else {
					stack.push(subTreeMakers);
				}
			}
			else {
				stack.pop();
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private static Iterator<NullaryFunction<LazyTree<String>>> makeIterator(String prefix, int depth) {
		if (depth == 2) {
			return iterator(() -> new DefaultLazyTree(prefix));
		}
		Function<String, NullaryFunction<LazyTree<String>>> fromStringToSubTreeMaker =
				s -> () -> new DefaultLazyTree<String>(makeIterator(prefix + s, depth + 1));
		Iterator<NullaryFunction<LazyTree<String>>> result = 
				FunctionIterator.make(iterator("a", "b", "c"), fromStringToSubTreeMaker);
		return result;
	}
	
	public static void main(String[] args) {
		DefaultLazyTree<String> root = new DefaultLazyTree<String>(makeIterator("info: ", 0));
		NonDeterministicIterator<String> it = new NonDeterministicIterator<String>(root);
		System.out.println(Util.join(it));
	}
}
