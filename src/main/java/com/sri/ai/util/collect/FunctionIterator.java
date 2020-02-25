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
import java.util.Collection;
import java.util.Iterator;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.sri.ai.util.Util;

/**
 * Iterator ranging over the results of an unary function applied to the
 * elements of the range of a base iterator.
 * 
 * @author braz
 */
@Beta
public class FunctionIterator<F, T> implements LazyIterator<T> {

	private Iterator<? extends F> base;
	private Function<? super F, ? extends T> function;

	public FunctionIterator(Function<? super F, ? extends T> function, Iterator<? extends F> base) {
		this.base     = base;
		this.function = function;
	}

	public FunctionIterator(Iterator<? extends F> base, Function<? super F, ? extends T> function) {
		this.base     = base;
		this.function = function;
	}

	public FunctionIterator(Collection<? extends F> base, Function<? super F, ? extends T> function) {
		this(base.iterator(), function);
	}

	public FunctionIterator(Function<? super F, ? extends T> function, Collection<? extends F> base) {
		this(base.iterator(), function);
	}

	protected Iterator<? extends F> getBase() {
		return base;
	}

	public Function<? super F, ? extends T> getFunction() {
		return function;
	}

	public static <F1, T1> FunctionIterator<F1, T1> make(Function<? super F1, ? extends T1> function, Iterator<? extends F1> base) {
		return new FunctionIterator<F1, T1>(function, base);
	}

	public static <F, T> FunctionIterator<F, T> make(Function<? super F, ? extends T> function, Iterable<? extends F> base) {
		return new FunctionIterator<F, T>(function, base.iterator());
	}

	public static <F, T> FunctionIterator<F, T> make(Function<? super F, ? extends T> function, Collection<F> base) {
		return new FunctionIterator<F, T>(function, base);
	}

	public static <F, T> FunctionIterator<F, T> make(Function<? super F, ? extends T> function, F[] base) {
		return new FunctionIterator<F, T>(function, Arrays.asList(base));
	}

	public static <F, T> FunctionIterator<F, T> make(Iterator<? extends F> base, Function<? super F, ? extends T> function) {
		return new FunctionIterator<F, T>(function, base);
	}

	public static <F, T> FunctionIterator<F, T> make(Iterable<? extends F> base, Function<? super F, ? extends T> function) {
		return new FunctionIterator<F, T>(function, base.iterator());
	}

	public static <F, T> FunctionIterator<F, T> make(Collection<F> base, Function<? super F, ? extends T> function) {
		return new FunctionIterator<F, T>(function, base);
	}

	public static <F, T> FunctionIterator<F, T> make(F[] base, Function<? super F, ? extends T> function) {
		return new FunctionIterator<F, T>(function, Arrays.asList(base));
	}

	public static <F, T> FunctionIterator<F, T> functionIterator(Function<? super F, ? extends T> function, Iterator<? extends F> base) {
		return new FunctionIterator<F, T>(function, base);
	}

	public static <F, T> FunctionIterator<F, T> functionIterator(Function<? super F, ? extends T> function, Iterable<? extends F> base) {
		return new FunctionIterator<F, T>(function, base.iterator());
	}

	public static <F, T> FunctionIterator<F, T> functionIterator(Function<? super F, ? extends T> function, Collection<F> base) {
		return new FunctionIterator<F, T>(function, base);
	}

	public static <F, T> FunctionIterator<F, T> functionIterator(Function<? super F, ? extends T> function, F[] base) {
		return new FunctionIterator<F, T>(function, Arrays.asList(base));
	}

	public static <F, T> FunctionIterator<F, T> functionIterator(Iterator<? extends F> base, Function<? super F, ? extends T> function) {
		return new FunctionIterator<F, T>(function, base);
	}

	public static <F, T> FunctionIterator<F, T> functionIterator(Iterable<? extends F> base, Function<? super F, ? extends T> function) {
		return new FunctionIterator<>(function, base.iterator());
	}

	public static <F, T> FunctionIterator<F, T> functionIterator(Collection<F> base, Function<? super F, ? extends T> function) {
		return new FunctionIterator<F, T>(function, base);
	}

	public static <F, T> FunctionIterator<F, T> functionIterator(F[] base, Function<? super F, ? extends T> function) {
		return new FunctionIterator<F, T>(function, Arrays.asList(base));
	}

	@Override
	public boolean hasNext() {
		boolean result = base.hasNext();
		return result;
	}
	
	private boolean baseCurrentIsInitialized = false;
	private F baseCurrent;
	
	@Override
	public void goToNextWithoutComputingCurrent() {
		baseCurrent = base.next();
		baseCurrentIsInitialized = true;
	}

	@Override
	public T computeCurrent() {
		Util.myAssert(baseCurrentIsInitialized, () -> "No current element defined for iterator.");
		T result = function.apply(baseCurrent);
		return result;
	}
}
