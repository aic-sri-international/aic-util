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

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.base.Predicate;
import com.sri.ai.util.Util;

/**
 * An abstract class implementing a {@link Refiner} based on a function, the
 * arguments of which being provided by refiners as well. The specific function
 * and argument refiners are left for extensions to implement.
 * <p>
 * The generic arguments T and V correspond to the type of value produced by the
 * functional refiner, and the type of argument values, respectively.
 * <p>
 * Intuitively, a functional refiner starts with an initial arbitrary value, and
 * more refined values are obtained by applying the function to more refined
 * values of its arguments than the ones used to compute the previous value.
 * When a refinement is requested, the functional refiner checks whether there
 * are more refined argument values available and, if so, uses them to compute a
 * more refined value. If the arguments do not have a more refined value for
 * free, then a rank of arguments is used to request refinements from each of
 * them, in rank order, until one is obtained. If no argument provides a more
 * refined value, the functional refiner indicates that no further refinement of
 * its own value is possible.
 * <p>
 * Here is a more precise definition: let f : V^n -> T be a n-ary function from
 * arguments of type V to a set T and s_1, ..., s_n be n (possibly infinite)
 * sequences, with s_{i,j} the j-th element of the sequence s_i. Let k be a
 * (possibly infinite) sequence of index arrays (k_{1,1} ... k_{1,n}), ...,
 * (k_{m,1}, ..., k_{m, n}), ..., where k_{t,i} is an integer greater than 0,
 * k_{1,i} = 1 for all i, and each array k_t differs from k_{t+1} in at least
 * one element, with k_{t+1,i} >= k_{t,i} for all i. (note that this means some
 * argument sequence values can be "skipped"; at the implementation level, this
 * happens when an argument's refiner performs more than one refinement at some
 * other client's request, in between requests from this functional refiner,
 * which never gets to see the intermediary ones -- this is ok because the
 * values are always better refinements). Let f0 be an initial value in T. Then
 * we define a <i>functional refiner<i> fr(f, f0, s, k) as a refiner with the
 * following sequence of refined values: f0, f(s_{1,k_{1,1}}, ...,
 * s_{n,k_{1,n}}), ..., f(s_{1,k_{t,1}}, ..., s_{n,k_{t,n}}), ...
 * <p>
 * Extensions of {@link AbstractFunctionalRefiner} implement a functional
 * refiner by implementing the parameters f, f0, and s. The parameter k is
 * implemented in the following way: if there are any argument sequences with
 * more refined values available for free, then they are the ones being updated.
 * Otherwise, the first one (in a given ranking) with a more refined value, even
 * if at a cost, is updated. The default ranking is undefined (it will typically
 * be the order of argument updated when the function is run for the first
 * time), but extensions can provide their own ranking.
 * <p>
 * An extending class must do at least the following:
 * <ul>
 * <li>have a constructor setting an initial value;
 * <li>define a method {@link #computeFunction()} providing an implementation
 * for f and identifying the argument sequences s. The identification of
 * argument sequences is achieved by {@link #computeFunction()}'s obligation to
 * access the value of its arguments through the method {@link
 * #getCurrentArgumentValue(Refiner)}. It is up to extension to
 * create these argument sequence refiners.
 * </ul>
 * <p>
 * Optionally, an extending class may override
 * {@link #argumentUpdateRankingIterator()}, effectively implementing parameter
 * k. By default, this method picks the first argument with a more refined
 * value;
 * 
 * @author braz
 */
@Beta
public abstract class AbstractFunctionalRefiner<T, V> extends AbstractRefiner<T> {

	private Set<Refiner<V>> arguments = new LinkedHashSet<Refiner<V>>();
	private boolean firstComputationDone = false;
	
	// Methods to be implemented or overridden

	public AbstractFunctionalRefiner(T initialValue) {
		super(initialValue);
	}

	/**
	 * Computes the function from current argument values in {@link #arguments},
	 * being responsible for computing and storing the first value of an argument if it is not already there.
	 */
	abstract protected T computeFunction();

	/**
	 * Returns an iterator ranging over arguments in decreasing preference for refinement attempt.
	 * Default implementation returns an arbitrary ranking,
	 * typically in the order arguments are used the first time function is run.
	 */
	protected Iterator<Refiner<V>> argumentUpdateRankingIterator() {
		return arguments.iterator();
	}

	// End of methods to be implemented or overridden

	protected V getCurrentArgumentValue(Refiner<V> argument) {
		
		if ( ! arguments.contains(argument)) {
			arguments.add(argument);
		}
		
		V result = argument.getCurrentValue(this);

		return result;
	}
	
	@Override
	protected T refineOrNull() {
		
		T result = null;
		
		if ( ! firstComputationDone) { // first computed value
			result = computeFunction();
			firstComputationDone = true;
		}
		else { // incremental computation
			boolean hasMoreRefinedValue;
			if ( ! (hasMoreRefinedValue = Util.thereExists(arguments, new HasMoreRefinedValueSinceLastTimeAtNoCost()))) {
				hasMoreRefinedValue = tryToRefineSomeArgumentAccordingToRanking();
			}
			if (hasMoreRefinedValue) {
				result = computeFunction();
			}
		}

		return result;
	}

	private boolean tryToRefineSomeArgumentAccordingToRanking() {
		boolean result = false;
		Iterator<Refiner<V>> updateRankingIterator = argumentUpdateRankingIterator();
		while ( ! result && updateRankingIterator.hasNext()) {
			Refiner<V> argument = updateRankingIterator.next();
			result = argument.refineIfPossible();
		}
		return result;
	}

	private class HasMoreRefinedValueSinceLastTimeAtNoCost implements Predicate<Refiner<V>> {
		@Override
		public boolean apply(Refiner<V> refiner) {
			return refiner.hasMoreRefinedValueSinceLastTimeAtNoCost(AbstractFunctionalRefiner.this);
		}
	}
}
