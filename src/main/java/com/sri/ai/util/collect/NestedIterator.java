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
import java.util.Collections;
import java.util.Iterator;

import com.google.common.annotations.Beta;
import com.sri.ai.util.base.NullaryFunction;


/**
 * An iterator that does not range over its elements, but over ranges of
 * sub-iterators computed from its elements. By default, the sub-iterators of
 * elements are:
 * <ul>
 * <li>the iterator over the collection if the element is a
 * collection
 * <li> the iterator itself if the element is an iterator,
 * <li> the sub-iterator of an object <code>O</code> if the element is a {@link NullaryFunction} returning <code>O</code>
 *      (this can be used to create collections or iterators lazily, only when this point is reached), and
 * <li> the iterator over the singleton composed by the element if it is neither a
 * collection nor a iterator not a {@link NullaryFunction}.
 * </ul>
 * <p>
 * For example,
 * <code>NestedIterator(list.iterator())</code> will range over
 * <code>"a", "b", "c", ...</code> if <code>list</code> is
 * <code>list("a", list("b", "c"), (NullaryFunction) () -&gt; iterator("e", list("f", "g")), ...)</code>,
 * where <code>list</code> and <code>iterator</code> are functions creating lists and iterators from a variable number of arguments.
 * <p>
 * The method {@link #determineSubIterator(Object)} can be overridden in order to customize
 * the decision of when to recurse or not.
 * 
 * @author braz
 */
@Beta
public class NestedIterator<E> extends EZIteratorWithNull<E> {

	private Iterator<? extends Object> baseIterator;
	
	private Iterator<E> subIterator = null;
	
	protected NestedIterator() {
	}
	
	public NestedIterator(Iterator<Object> baseIterator) {
		this.baseIterator = baseIterator;
	}
	
	public NestedIterator(Iterable<Object> baseCollection) {
		this(baseCollection.iterator());
	}
	
	public NestedIterator(Object... objects) {
		this(Arrays.asList(objects).iterator());
	}
	
	public static <E> NestedIterator<E> make(Iterator<E> baseIterator) {
		return new NestedIterator<E>(baseIterator);
	}
	
	public static <E> NestedIterator<E> make(Iterable<E> baseCollection) {
		return new NestedIterator<E>(baseCollection);
	}
	
	public static <E> NestedIterator<E> make(Object... objects) {
		return new NestedIterator<E>(objects);
	}

	
	
	public static <E> NestedIterator<E> nestedIterator(Iterator<E> baseIterator) {
		return new NestedIterator<E>(baseIterator);
	}
	
	public static <E> NestedIterator<E> nestedIterator(Iterable<E> baseCollection) {
		return new NestedIterator<E>(baseCollection);
	}
	
	public static <E> NestedIterator<E> nestedIterator(Object... objects) {
		return new NestedIterator<E>(objects);
	}
	
	/** 
	 * Resets NestedIterator to operate over a new base iterator.
	 * 
	 * @param baseIterator
	 *        the new base iterator to operate over.
	 */
	public void setBaseIterator(Iterator<Object> baseIterator) {
		this.baseIterator = baseIterator;
		subIterator = null;
	}

	@SuppressWarnings("unchecked")
	public Iterator<E> determineSubIterator(Object element) {
		if (element instanceof Iterable) {
			return new NestedIterator(((Iterable<E>)element).iterator());
		}
		else if (element instanceof Iterator) {
			return new NestedIterator((Iterator<E>) element);
		}
		else if (element instanceof NullaryFunction) {
			return determineSubIterator(((NullaryFunction) element).apply());
		}
		return Collections.singletonList((E) element).iterator();
	}
	
	@Override
	protected E calculateNext() {
		while (subIterator != null || baseIterator.hasNext()) {
			if (subIterator == null) {
				Object next = baseIterator.next();
				subIterator = determineSubIterator(next);
			}
			
			if (subIterator.hasNext()) {
				return subIterator.next();
			}
			else {
				subIterator = null;
			}
		}
		endOfRange(); // EZIteratorWithNull requires calling this method when range is over, as opposed to just returning null as in EZIterator.
		return null;
	}
}
