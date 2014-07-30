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
 * <code>RangeOperationApplication</code>s is a facility for
 * easily writing and efficiently computing expressions with nested cumulative operators
 * (for example, <code>\sum_i \prod_j f(i,j)</code>).
 * <p>
 * It allows easy writing of these expressions through its method {@link #run(Object...)},
 * best explained by example:
 * <p>
 * <code>
 * Object result = RangeOperations.run(Averaging("x", 0, 10), X());
 * </code>
 * <p>
 * computes the average of integers from 0 to 10.
 * <p>
 * {@link #run(Object...)} receives a variable number of arguments,
 * which can be, among other types (see below), {@link DefaultRangeOperation} and {@link AbstractDAEFunction}. 
 * {@link #Averaging()} is a method returning an instance of {@link #Averaging},
 * an extension of {@link DefaultRangeOperation}.
 * {@link #X()} is a method returning an instance of {@link #X}, an extension of {@link AbstractDAEFunction}
 * returning the value of variable <code>"x"</code> in an implicit environment (of type {@link DependencyAwareEnvironment})
 * which is passed to the {@link AbstractDAEFunction}. 
 * <p>
 * Therefore, <code>run(Averaging("x", 0, 10), X())</code> denotes an operation
 * setting variable <code>"x"</code> to values from 0 to 10 and averaging over them,
 * and returns that value.
 * <p>
 * Another example is:
 * <p>
 * <code>
 * Object result = RangeOperations.run(Axis("x", 0, 20), Averaging("y", 0, 10), new F());
 * </code>
 * <p>
 * computes a list with 21 elements,
 * where the x-th element contains the average, over values of <code>"y"</code> from 0 to 10,
 * of <code>F()</code> evaluated on an environment with current values for <code>"x"</code> and <code>"y"</code>. 
 * <p>
 * An arbitrary number of range operations can be used, and will be performed in the order they are given.
 * User-defined extensions of {@link DefaultRangeOperation} and {@link AbstractDAEFunction} can be used as well.
 * See the documentation on {@link DefaultRangeOperation} for details on how to use it.
 * <p>
 * An important feature of this framework is automatic caching.
 * Suppose <code>F</code> above only depends on x.
 * It would then be wasteful to recalculate it for every new value of y, which is being used as a counter only.
 * This does not happen, however, because {@link DependencyAwareEnvironment} keeps track of such dependencies
 * automatically.
 * IMPORTANT: for this automatic dependency management to occur even for sub-functions inside F,
 * they must be calculated with the {@link DependencyAwareEnvironment#getResultOrRecompute(AbstractDAEFunction)} method.
 * The function will always be recomputed if {@link AbstractDAEFunction#isRandom()} returns <code>true</code>,
 * or an ancestor function is random.
 * <p>
 * If {@link #run(Object...)} receives Strings as arguments, they are assumed to be variables to be
 * put in the environment with the object right after them as value.
 * If an {@link DependencyAwareEnvironment} is found, it replaces the default (initially empty) environment,
 * removing previous variable values. Subsequent variables are added to this environment.
 * <p>
 * As a convenience, this class already provides a few {@link DefaultRangeOperation} extensions:
 * {@link #Averaging(String, int, int, int)}, {@link #Axis(String, int, int, int)} and 
 * {@link #Summation(String, int, int)}.
 *
 * @author braz
 */
@Beta
public class RangeOperationsApplication {

	protected ArrayList<? extends RangeOperation> rangeOperations;
	protected AbstractDAEFunction function;
	protected DependencyAwareEnvironment environment;

	private <T extends RangeOperation> RangeOperationsApplication(DependencyAwareEnvironment environment, List<T> rangeOperations, AbstractDAEFunction function) {
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
	public static Object run(Object ... arguments) {
		List<DefaultRangeOperation> rangeOperations = new LinkedList<DefaultRangeOperation>();
		DependencyAwareEnvironment environment = new DefaultDependencyAwareEnvironment();
		AbstractDAEFunction function = null;
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
				function = (AbstractDAEFunction) argument;
			}
		}

		RangeOperationsApplication rangeOperationsObject = new RangeOperationsApplication(environment, rangeOperations, function);
		Object result = rangeOperationsObject.run();
		return result;
	}

	private Object run() {
		return run(0);
	}

	private Object run(int i) {
		if (i == rangeOperations.size()) {
			return environment.getResultOrRecompute(function);
		}

		RangeOperation rangeOp = rangeOperations.get(i);
		rangeOp.getOperator().initialize();
		for (rangeOp.initialize(); rangeOp.hasNext(); ) {
			rangeOp.next();
			Object subresult = run(i+1); 
			rangeOp.getOperator().increment(subresult);
		}
		
		return rangeOp.getOperator().getResult();
	}
}
