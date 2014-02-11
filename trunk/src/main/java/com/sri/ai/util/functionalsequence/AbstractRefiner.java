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
package com.sri.ai.util.functionalsequence;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.annotations.Beta;

/**
 * A basic abstract implementation of {@link Refiner}. Extensions only need to
 * implement {@link #refineOrNull()}, which must return either a refinement of
 * the current value (available at {@link #currentValue}), or <code>null</code>
 * if that is not possible.
 * <p>
 * {@link #refineOrNull()} can choose to set {@link #knownToBeOver} if it
 * determines that no further refinements in the future will be possible even if
 * the present invocation of {@link #refineOrNull()} was successful. This will
 * avoid unnecessary subsequent calls to {@link #refineOrNull()} (the flag is
 * set automatically if {@link #refineOrNull()} fails).
 * 
 * @author braz
 * 
 * @param <T>
 *            the type of the value being computed.
 */
@Beta
public abstract class AbstractRefiner<T> implements Refiner<T> {

	protected boolean     knownToBeOver       = false;
	protected T           currentValue        = null;
	private   Set<Object> upToDateSubscribers = new LinkedHashSet<Object>(0);

	public AbstractRefiner(T initialValue) {
		super();
		currentValue = initialValue;
	}

	/** Returns the next best refinement of the value being computed, or null if that is not possible. */
	protected abstract T refineOrNull();
	
	@Override
	public T getCurrentValue(Object subscriber) {
		upToDateSubscribers.add(subscriber);
		return currentValue;
	}

	@Override
	public boolean hasMoreRefinedValueSinceLastTimeAtNoCost(Object subscriber) {
		boolean result = ! upToDateSubscribers.contains(subscriber);
		return result;
	}

	@Override
	public boolean refineIfPossible() {
		if (knownToBeOver) {
			return false;
		}
		
		T newValueOrNull = refineOrNull();
		if (newValueOrNull != null) {
			currentValue = newValueOrNull;
			upToDateSubscribers.clear();
			return true;
		}
		else {
			knownToBeOver = true;
			return false;
		}
	}
}
