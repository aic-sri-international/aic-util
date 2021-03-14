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

import com.google.common.annotations.Beta;

/**
 * A Refiner is meant to lazily compute successively better versions of
 * something concurrently used by potentially many subscribers. It only refines
 * its value if explicitly asked to do so with {@link #refineIfPossible()}.
 * 
 * @author braz
 */
@Beta
public interface Refiner<T> {

	/**
	 * Returns current (latest) version of computed value, without attempting to
	 * refine it. It takes a subscriber into account so that it can later tell,
	 * with {@link #hasMoreRefinedValueSinceLastTimeAtNoCost(Object)}, whether
	 * there have been any refinements since the last time a value has been
	 * provided to the subscriber (that is, if a more refined value than the
	 * last one can be provided at no cost).
	 * 
	 * @param subscriber
	 *            a subscriber to the refiner.
	 * @return the current version of a computed value, without attempting to
	 *         refine it.
	 */
	T getCurrentValue(Object subscriber);

	/**
	 * Indicates whether there have been any refinements since the last time a
	 * value has been provided to subscriber (that is, if a more refined value
	 * than the last one can be provided at no cost).
	 * 
	 * @param subscriber
	 *            a subscriber to the refiner.
	 * @return true if there have been any refinements since the last time a
	 *         value has ben provided to the given subscriber.
	 */
	boolean hasMoreRefinedValueSinceLastTimeAtNoCost(Object subscriber);

	/**
	 * Attempts to refine the value, returning an indication of whether that has
	 * been possible.
	 * 
	 * @return true if it was possible to refine the value, false otherwise.
	 */
	boolean refineIfPossible();
}
