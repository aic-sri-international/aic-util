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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.sri.ai.util.Util;
import com.sri.ai.util.collect.EZIterator;
import com.sri.ai.util.collect.HasNext;

/**
 * An abstract class implementing basic functionality for a
 * {@link FunctionalSequence}. In this class, sequences are represented by
 * iterators.
 * <p>
 * An extending class must define at least the following abstract methods:
 * <ul>
 * <li> {@link #initialValue()}
 * <li> {@link #computeFunction()}
 * </ul>
 * <p>
 * Optionally, an extending class may override following methods:
 * <ul>
 * <li> {@link #isFinalValue(Object)}, which indicates whether a given value
 * indicates the end of the sequence (default always returns false).
 * <li> {@link #nextArgumentToUpdate()}, which in this class has a default
 * implementation of picking the first argument with a next value;
 * <li> {@link #computeFunctionIncrementally(Object, Iterator, Object, Object)}, which in this class has a
 * default implementation of running {@link #computeFunction()}.
 * </ul>
 * 
 * An extension must keep track of the argument iterators (that is, iterators
 * ranging over the values of each argument sequence s_i).
 * {@link #computeFunction()}, when defined by extensions, must obtain the
 * current value of an argument sequence through the method
 * {@link #getCurrentArgumentValue(Iterator)}, which takes the argument iterator
 * as a parameter.
 * 
 * @author braz
 * 
 * @param <T>
 *        the type of the elements to iterate.
 * @param <V>
 *        the type of the argument values.
 */
@Beta
public abstract class AbstractFunctionalSequence<T, V> extends EZIterator<T>
		implements FunctionalSequence<T> {

	private Map<Iterator<V>, V> fromArgumentsToArgumentValues = new LinkedHashMap<Iterator<V>, V>();
	private boolean initialValueReturned = false;
	private boolean firstComputationDone = false;
	private boolean finalValueFoundSoNoMoreElementsToBeComputed = false;

	// Methods to be implemented or overridden

	/**
	 * @return the initial value of the functional sequence. This must return
	 *         null if we do not wish to include an arbitrary initial value, in
	 *         which case the first value will be computed from applying the
	 *         function to the first elements of the arguments sequences.
	 */
	abstract protected T initialValue();

	/**
	 * @param value
	 *            the value to test.
	 * @return whether provided value should indicate end of range for this
	 *         rewriter functional sequence after being produced by it. Default
	 *         implementation returns false every time.
	 */
	protected boolean isFinalValue(T value) {
		return false;
	}

	/**
	 * @return Computes the function from current argument values in
	 *         {@link #fromArgumentsToArgumentValues}, being responsible for
	 *         computing and storing the first value of an argument if it is not
	 *         already there.
	 */
	abstract protected T computeFunction();

	/**
	 * @return Picks next argument to be updated, returning <code>null</code> if
	 *         no argument has a next value to be updated to. The default
	 *         implementation picks the first argument used in the first
	 *         execution of the function that has a next value.
	 */
	protected Iterator<V> nextArgumentToUpdate() {
		Set<Iterator<V>> arguments = fromArgumentsToArgumentValues.keySet();
		Iterator<V> result = Util.getFirstSatisfyingPredicateOrNull(arguments,
				HAS_NEXT);
		return result;
	}

	/**
	 * Optionally implements the function for after only one argument has been
	 * updated, that is, provides an incremental version of the function,
	 * assuming that all arguments values are in
	 * {@link #fromArgumentsToArgumentValues} (including the one just updated).
	 * The default implementation simply recomputes the function from the stored
	 * argument values.
	 * 
	 * @param currentResult
	 *            the current result
	 * @param argument
	 *            an iterator over arguments.
	 * @param previousArgumentValue
	 *            the previous argument value.
	 * @param newArgumentValue
	 *            the new argument value.
	 * @return the incrementally computed value.
	 */
	protected T computeFunctionIncrementally(T currentResult,
			Iterator<V> argument, V previousArgumentValue, V newArgumentValue) {
		T result = computeFunction();
		return result;
	}

	// End of methods to be implemented or overridden

	protected V getCurrentArgumentValue(Iterator<V> argument) {
		V result = fromArgumentsToArgumentValues.get(argument);
		if (result == null) {
			if (argument.hasNext()) {
				result = argument.next();
				fromArgumentsToArgumentValues.put(argument, result);
			} 
			else {
				throw new ArgumentWithNoValues();
			}
		}
		return result;
	}

	@Override
	protected T calculateNext() {

		T result = null;

		if (finalValueFoundSoNoMoreElementsToBeComputed) {
			result = null; // end of sequence
		} 
		else if (!initialValueReturned && initialValue() != null) { // arbitrary
																		// initial
																		// value,
																		// if
																		// any
			result = initialValue();
			initialValueReturned = true;
		} 
		else if (!firstComputationDone) { // first computed value
			try {
				result = computeFunction();
				firstComputationDone = true;
			} catch (ArgumentWithNoValues e) {
				result = null; // end of sequence
			}
		} 
		else { // incremental computation
			Iterator<V> nextArgumentToUpdate = nextArgumentToUpdate();
			if (nextArgumentToUpdate != null) {
				V previousArgumentValue = fromArgumentsToArgumentValues
						.get(nextArgumentToUpdate);
				V newArgumentValue = nextArgumentToUpdate.next();
				fromArgumentsToArgumentValues.put(nextArgumentToUpdate,
						newArgumentValue);
				result = computeFunctionIncrementally(result,
						nextArgumentToUpdate, previousArgumentValue,
						newArgumentValue);
			} 
			else {
				result = null; // end of sequence
			}
		}

		if (result != null && isFinalValue(result)) {
			finalValueFoundSoNoMoreElementsToBeComputed = true;
		}

		return result;
	}

	@SuppressWarnings("serial")
	public static class ArgumentWithNoValues extends Error {
	}

	private final HasNext<V> HAS_NEXT = new HasNext<V>();
}
