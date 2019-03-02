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

import java.util.Collection;
import java.util.Iterator;

import com.google.common.annotations.Beta;
import com.google.common.base.Predicate;

/**
 * Iterates over range of base iterator, only letting pass elements satisfying a
 * predicate.
 * 
 * @author braz
 */
@Beta
public class PredicateIterator<E> extends FilterIterator<E> {

	protected Predicate<? super E> predicate;

	public PredicateIterator(Predicate<? super E> predicate, Iterator<? extends E> base) {
		super(base);
		this.predicate = predicate;
	}

	public PredicateIterator(Iterator<? extends E> base, Predicate<? super E> predicate) {
		super(base);
		this.predicate = predicate;
	}

	public PredicateIterator(Collection<? extends E> collection, Predicate<? super E> predicate) {
		this(collection.iterator(), predicate);
	}

	public PredicateIterator(Predicate<? super E> predicate, Collection<? extends E> collection) {
		this(collection.iterator(), predicate);
	}

	public static <E> PredicateIterator<E> make(Predicate<? super E> predicate, Iterator<? extends E> base) {
		return new PredicateIterator<E>(predicate, base);
	}

	public static <E> PredicateIterator<E> make(Iterator<? extends E> base, Predicate<? super E> predicate) {
		return new PredicateIterator<E>(predicate, base);
	}

	public static <E> PredicateIterator<E> make(Collection<? extends E> collection, Predicate<? super E> predicate) {
		return new PredicateIterator<E>(predicate, collection.iterator());
	}

	public static <E> PredicateIterator<E> make(Predicate<? super E> predicate, Collection<? extends E> collection) {
		return new PredicateIterator<E>(predicate, collection.iterator());
	}


	public static <E> PredicateIterator<E> predicateIterator(Predicate<? super E> predicate, Iterator<? extends E> base) {
		return new PredicateIterator<E>(predicate, base);
	}

	public static <E> PredicateIterator<E> predicateIterator(Iterator<? extends E> base, Predicate<? super E> predicate) {
		return new PredicateIterator<E>(predicate, base);
	}

	public static <E> PredicateIterator<E> predicateIterator(Collection<? extends E> collection, Predicate<? super E> predicate) {
		return new PredicateIterator<E>(predicate, collection.iterator());
	}

	public static <E> PredicateIterator<E> predicateIterator(Predicate<? super E> predicate, Collection<? extends E> collection) {
		return new PredicateIterator<E>(predicate, collection.iterator());
	}

	@Override
	public boolean include(E element) {
		return predicate.apply(element);
	}

}
