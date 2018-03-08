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

import com.google.common.annotations.Beta;

/**
 * A lazy iterator that has the capacity to iterate over its range but only compute the actual values when required.
 * 
 * The motivating application for this interface was a sampling iterator.
 * While we would like the sampling iterator to be able to provide a sample at any point
 * just like a regular iterator,
 * it is also desirable to let it run for thousands of iteration without examining its values.
 * In some cases, generating the actual sampled value is more expensive than simply
 * iterating the internal variables for sampling without actually generating the value.
 * This interface allows us to run many iterations without having to generate the value
 * at each iteration, leading to significant performance improvement.
 *
 * Regular iterators do no provide this option as an interface because iteration is always accomplished by {@link Iterator#next()},
 * which must also return the value.
 * Therefore, this interface adds the method {@link #goToNextWithoutComputingCurrent()} for this purpose.
 * Then {@link #computeCurrent()} computes the current element when needed.
 * 
 * Note that, while elements are not being computed, they of course cannot be used for deciding whether to keep iterator,
 * or for deciding when to compute the element at some point.
 * For lazy iterators, these decisions will typically be based simply on the number of iterators, or
 * whether we have reached the end of the iterator's range.
 * The last option can be easily achieved with {@link #computeFinalValue()}.
 * 
 * @author braz
 */
@Beta
public interface LazyIterator<T> extends Iterator<T> {

	void goToNextWithoutComputingCurrent();

	T computeCurrent();
	
	default T computeFinalValue() {
		while (hasNext()) {
			goToNextWithoutComputingCurrent();
		}
		T result = computeCurrent();
		return result;
	}
	
	
	@Override
	/**
	 * Default implementation of {@link #next()} invokes
	 * {@link #goToNextWithoutComputingCurrent()} and then returning the result of
	 * {@link #computeCurrent()}, thus concentrating iteration in
	 * {@link #goToNextWithoutComputingCurrent()} alone and avoiding code duplication.
	 */
	default T next() {
		goToNextWithoutComputingCurrent();
		T result = computeCurrent();
		return result;
	}
}
