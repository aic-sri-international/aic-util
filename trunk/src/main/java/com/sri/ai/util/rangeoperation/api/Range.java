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
package com.sri.ai.util.rangeoperation.api;

import java.util.Iterator;

import com.google.common.annotations.Beta;
import com.sri.ai.util.base.BinaryProcedure;
import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.rangeoperation.api.DependencyAwareEnvironment;

/**
 * A range can be thought of as a "virtual list" of values for a named variable,
 * based on a generator of values or an actual collection. Technically, it is a
 * {@link NullaryFunction} producing a new iterator upon request. It must keep a
 * {@link DependencyAwareEnvironment} set by
 * {@link #setEnvironment(DependencyAwareEnvironment)} that is updated by the
 * iterator's method {@link #next()} every time the variable is updated. It may
 * also receive {@link BinaryProcedure} listeners to be called with variable and
 * value pairs whenever {@link #next()} is invoked.
 * 
 * @author braz
 */
@Beta
public interface Range<T> extends NullaryFunction<Iterator<T>> {
	/** @return The variable set by this range. */
	public String getName();

	/**
	 * Informs the range which environment to use.
	 * 
	 * @param environment
	 *            the environment the range is to use.
	 */
	public void setEnvironment(DependencyAwareEnvironment environment);

	/** Makes range ready for iteration over variable values. */
	public void initialize();

	/** @return true if there are still values to be iterated over. */
	public boolean hasNext();

	/** Go to the next value. */
	public void next();

	/**
	 * Add a binary procedure to be invoke upon iteration, with variable and
	 * value as parameters.
	 * 
	 * @param listener
	 *            the listener to be added.
	 */
	public void addIterationListener(BinaryProcedure<String, T> listener);
}