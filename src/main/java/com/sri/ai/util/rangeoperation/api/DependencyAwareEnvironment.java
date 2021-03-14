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

import java.util.Map;

import com.google.common.annotations.Beta;

/**
 * An extension of {@link Map} that is able to keep track and manage
 * dependencies between its entries as computed by {@link DAEFunction}s. If a
 * variable v1 depends on v2 and v2 is changed, we want v1 to be removed so it
 * is recalculated next time its value is requested.
 * <p>
 * This is useful if we want to use the map as a store for multiple variables
 * and want to make sure that all stored values are valid and consistent.
 * <p>
 * This works by computing new values using {@link DAEFunction}s. These are
 * functions from an environment to a new value (stored under the function's
 * String representation) with the method
 * {@link #getResultOrRecompute(DAEFunction)}. This method monitors accesses
 * made by the function to the environment in order to determine which values
 * depend on which other values. This monitoring is possible due to DAEFunctions
 * only being able to access the environment through methods
 * {@link #get(String)} and {@link #getResultOrRecompute(DAEFunction)}, because
 * these methods have internal hooks that do the necessary bookkeeping
 * automatically.
 * <p>
 * If a {@link DAEFunction}'s method {@link DAEFunction#isRandom()} returns
 * <code>true</code>, or depends on a value computed by a random DAEFunction, it
 * is always recomputed since its values cannot be reused.
 *
 * @author braz
 */
@Beta
public interface DependencyAwareEnvironment extends Map<String, Object> {

	/**
	 * @param function
	 *            the function to be evaluated.
	 * @return the result of the evaluation of a {@link DAEFunction} on the
	 *         current environment, only actually evaluating it if the variables
	 *         it depends on have been changed since the last evaluation (during
	 *         which invocations of {@link #get(String)} are monitored in order
	 *         to determine such dependencies).
	 */
	public abstract Object getResultOrRecompute(DAEFunction function);

	public abstract Object get(String variable);

	@Override
	public abstract Object put(String variable, Object value);

	public abstract void remove(String variable);

	// /////////// CONVENIENCE METHODS ///////////////

	public abstract Object getOrUseDefault(String variable, Object defaultValue);

	public abstract int getInt(String variable);

}