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
package com.sri.ai.util.base;

import com.google.common.annotations.Beta;

/**
 * An interface for iterators that can provide a continuation iterable that will
 * generate new continuation iterators ranging
 * over the current and future values of the original iterator.
 * In other words, the iterator can make an iterable that generates a continuation of the original iterator.
 * <p>
 * The above mechanism is used to provide a default clone method, making this a {link CloneableIterator} as well.
 * 
 * @author braz
 */
@Beta
public interface ContinuationIterator<T> extends CloneableIterator<T> {
	public ContinuationIterable<T> makeContinuationIterable();
	
	/**
	 * Provides a default clone method to be used by implementations
	 * (calling this 'clone' would not work because implementations would still use their
	 * Object.clone implementation. One needs to override Object.clone, and
	 * has the option of invoking this method for convenience.
	 * @return
	 */
	default ContinuationIterator<T> continuationIteratorDefaultClone() {
		ContinuationIterable<T> iterable = makeContinuationIterable();
		ContinuationIterator<T> newIterator = iterable.iterator();
		return newIterator;
	}
	
	@Override
	ContinuationIterator<T> clone();
}
