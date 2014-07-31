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
package com.sri.ai.util.rangeoperation.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.common.annotations.Beta;
import com.sri.ai.util.rangeoperation.api.DAEFunction;
import com.sri.ai.util.rangeoperation.api.DependencyAwareEnvironment;
import com.sri.ai.util.rangeoperation.api.RangeOperation;

/**
 * {@link RangeOperationInterpreter} is a facility for
 * easily writing and efficiently computing expressions with nested aggregate operators
 * (for example, <code>\sum_i \prod_j f(i,j)</code>).
 * <p>
 * It works by having its {@link #apply(Object...)} method invoked with
 * a sequence of {@link RangeOperation}s and a {@link DAEFunction} <code>function</code>,
 * iterating over the {@link RangeOperation}s variables, updating an internal
 * {@link DependencyAwareEnvironment} with their current values, and executing, at each iteration,
 * <code>function</code>.
 * The result is a matrix or single value which is the result of the specified operations.
 * <p>
 * More precisely, let <code>r1, ..., rn, function</code>
 * be the ranging operations and function received, with a current <code>env</code> environment.
 * If <code>n = 0</code>, the returned value <code>I(r1, ..., rn, function, env)</code>
 *    is <code>function(env)</code>.
 * If <code>n > 0</code>, the returned value <code>I(r1, ..., rn, function, env)</code>
 *    is <code>I(r2, ..., rn, function, env + var = val1) op ... op I(r2, ..., rn, function, env + var = valm)</code>,
 *    where <code>op</code>, <code>var</code> and <code>val1,...,valm</code> are
 *    the aggregate operator, variable, and range of <code>r1</code>, respectively.
 * <p>
 * Examples:
 * <p>
 * <code>
 * Object result = RangeOperationInterpreter.apply(new Dimension("x", 1, 10), new Dimension("y", 1, 20), xPlusY());
 * </code><br>
 * generates a list of 10 lists, each with 20 elements, containing the sums of the row and column indices.
 * <p>
 * <code>
 * Object result = RangeOperationInterpreter.apply(new Dimension("x", 1, 10), new Averaging(1, 20), gaussianWithMeanX());
 * </code><br>
 * generates a list of 10 elements, the x-th element being the average of 20 samples of a Gaussian of mean x.
 * Note that gaussianWithMeanX needs to have its method {@link DAEFunction#isRandom()} return <code>true</code>,
 * or it will incorrectly be computed only once per value of x.
 * <p>
 * An arbitrary number of range operations can be used, and will be performed in the order they are given.
 * User-defined extensions of {@link DefaultRangeOperation} and {@link DAEFunction} can be used as well.
 * See the documentation on {@link RangeOperation} for details on how to use it.
 * <p>
 * An important feature of this framework is automatic caching.
 * Suppose <code>function</code> above only depends on a variable "x".
 * It would then be wasteful to recalculate it for every new value of another variable "y",
 * which may be, for example, only a counter.
 * This does not happen, however, because {@link DependencyAwareEnvironment} keeps track of such dependencies
 * automatically.<br>
 * IMPORTANT: for this automatic dependency management to occur even for sub-functions inside the {@link DAEFunction},
 * they must be calculated with the {@link DependencyAwareEnvironment#getResultOrRecompute(DAEFunction)} method.
 * The function will always be recomputed if {@link DAEFunction#isRandom()} returns <code>true</code>,
 * or an ancestor function is random.
 * <p>
 * If {@link #apply(Object...)} receives Strings as arguments, they are assumed to be variables to be
 * put in the environment with the object right after them as value.
 * If an {@link DependencyAwareEnvironment} is found, it replaces the default (initially empty) environment,
 * removing previous variable values. Subsequent variables are added to this environment.
 *
 * @author braz
 */
@Beta
public class RangeOperationsInterpreter {

	protected ArrayList<? extends RangeOperation> rangeOperations;
	protected DAEFunction function;
	protected DependencyAwareEnvironment environment;

	private <T extends RangeOperation>
	RangeOperationsInterpreter(
			DependencyAwareEnvironment environment, List<T> rangeOperations, DAEFunction function) {
		
		this.environment = environment;
		this.rangeOperations = new ArrayList<T>(rangeOperations);
		this.function = function;
		for (RangeOperation range : rangeOperations) {
			range.getRange().setEnvironment(environment);
		}
	}

	/**
	 * Evaluates the range operations and {@DAEFunction} present in a list of arguments. 
	 */
	public static Object apply(Object ... arguments) {
		List<DefaultRangeOperation> rangeOperations = new LinkedList<DefaultRangeOperation>();
		DependencyAwareEnvironment environment = new DefaultDependencyAwareEnvironment();
		DAEFunction function = null;
		for (int i = 0; i < arguments.length; i++) {
			Object argument = arguments[i];
			if (argument instanceof RangeOperation) {
				rangeOperations.add((DefaultRangeOperation) argument);
			}
			else if (argument instanceof DependencyAwareEnvironment) {
				environment = (DependencyAwareEnvironment) argument;
			}
			else if (argument instanceof String) {
				String variable = (String) argument;
				Object value = arguments[++i];
				environment.put(variable, value);
			}
			else if (argument instanceof DAEFunction) {
				function = (DAEFunction) argument;
			}
		}

		RangeOperationsInterpreter rangeOperationsObject = new RangeOperationsInterpreter(environment, rangeOperations, function);
		Object result = rangeOperationsObject.apply();
		return result;
	}

	private Object apply() {
		return apply(0);
	}

	@SuppressWarnings("unchecked")
	private Object apply(int i) {
		if (i == rangeOperations.size()) {
			return environment.getResultOrRecompute(function);
		}

		RangeOperation rangeOperation = rangeOperations.get(i);
		rangeOperation.getOperator().initialize();
		for (rangeOperation.initialize(); rangeOperation.hasNext(); ) {
			rangeOperation.next();
			Object subresult = apply(i+1); 
			rangeOperation.getOperator().increment(subresult);
		}
		
		Object result = rangeOperation.getOperator().getResult();
		return result;
	}
}
