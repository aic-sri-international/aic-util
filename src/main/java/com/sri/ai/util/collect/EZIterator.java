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

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.common.annotations.Beta;

/**
 * An abstract class meant to make the implementation of iterators easier. One
 * needs only to extend it and define {@link #calculateNext()}, which must
 * calculate the next object in the sequence, or <code>null</code> if it is
 * over. EZIterator defines the Iterator interface based on this method (without
 * supporting the {@link #remove()} method.
 * If the constructor already computes the next (thus, the first) element,
 * the protected field {@link #onNext} must be set to <code>true</code>.
 * If the constructor determines there is no next element, it must set
 * {@link #onNext} to <code>true</code> and {@link #next} to <code>null</code>.
 * 
 * @author braz
 */
@Beta
public abstract class EZIterator<E> implements Iterator<E> {

	/** Field indicating if next element has already been computed. */
	protected boolean onNext = false;

	/** The next element if already computed. */
	protected E next;
	
	/**
	 * Method responsible for calculating next element in sequence, returning
	 * <code>null</code> if there are no more elements.
	 * 
	 * @return the next calculated element in sequence, null if there are no
	 *         more elements.
	 */
	protected abstract E calculateNext();

	/**
	 * A default constructor that assumes {@link next} needs to be computed.
	 */
	public EZIterator() {
		this(false);
	}
	
	/**
	 * A constructor indicating that the field {@link next} already contains
	 * the next value to be provided by {@link #next} right at construction.
	 * @param onNext whether the constructor (of an extending class) already computes a <code>next</code> value. 
	 */
	public EZIterator(boolean onNext) {
		this.onNext = onNext;
	}
	
	private void ensureBeingOnNext() {
		if (!onNext) {
			next = calculateNext();
			onNext = true;
		}
	}

	@Override
	public boolean hasNext() {
		ensureBeingOnNext();
		return next != null;
	}

	@Override
	public E next() {
		ensureBeingOnNext();
		if (next == null) {
			throw new NoSuchElementException();
		}
		onNext = false;
		return next;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
