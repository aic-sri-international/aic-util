/*
 * Copyright (c) 2015, SRI International
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
 * Neither the name of the aic-praise nor the names of its
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
package com.sri.ai.util.computation.treecomputation.anytime.core;

import static com.sri.ai.util.Util.mapIntoArrayList;
import static com.sri.ai.util.Util.myAssert;

import java.util.ArrayList;
import java.util.List;

import com.sri.ai.util.collect.EZIterator;
import com.sri.ai.util.collect.RoundRobinIterator;
import com.sri.ai.util.computation.anytime.api.Anytime;
import com.sri.ai.util.computation.anytime.api.Approximation;
import com.sri.ai.util.computation.treecomputation.anytime.api.AnytimeEagerTreeComputation;

/**
 * An abstract implementation of {@link AnytimeEagerTreeComputation}.
 * <p>
 * Essentially, this creates an anytime tree computation by
 * lazily expanding, node by node, a tree computation as specified by the implementation of method {@link #makeSubs()}.
 * <p>
 * Initially, the value of the anytime root computation is one provided at construction.
 * This value is meant to represent total ignorance since it is provided before any computation
 * is done. For example, if the result is a variable with a certain range,
 * then the initial value should be the variable's entire range.
 * <p>
 * Then, in order to create the next approximation,
 * this class creates sub-anytime approximations with method {@link #makeSubs()}.
 * The sub-anytime tree computations's approximations
 * are then used by {@link #function(List)} to compute the current approximation.
 * <p>
 * To update an even better next approximation,
 * the class picks the next anytime sub-computation
 * by using the abstract method {@link #pickNextSubToIterate()},
 * which is also to be specified by extensions.
 * <p>
 * It then iterates the picked next anytime sub-computation,
 * obtaining a new approximation to the corresponding argument,
 * and delegates again to {@link #function(List)}.
 * <p>
 * Extending classes must override {@link #makeSubs()},
 * {@link #pickNextSubToIterate()} to indicate how the next sub-tree computation
 * to be iterated is computed,
 * {@link #informativeApproximationRequiresAllSubsToBeInformative()}
 * to indicate whether a single sub-approximation with total ignorance nullifies other arguments,
 * and {@link #function(List)} to specify how new approximations are computed.
 *
 * @author braz
 *
 * @param <T> the type of the values being approximated
 */
public abstract class AbstractAnytimeEagerTreeComputation<T> extends EZIterator<Approximation<T>> implements AnytimeEagerTreeComputation<T> {
	
	////////////// ABSTRACT METHODS
	
	protected abstract ArrayList<? extends Anytime<T>> makeSubs();
	
	/**
	 * A suggestion for a default strategy for implementing this method is to use a {@link RoundRobinIterator} field.
	 */
	@Override
	public abstract Anytime<T> pickNextSubToIterate();

	@Override
	public abstract boolean informativeApproximationRequiresAllSubsToBeInformative();

	@Override
	public abstract boolean informativeApproximationRequiresThatNotAllSubsAreNonInformative();

	@Override
	public abstract Approximation<T> function(List<Approximation<T>> subsApproximations);

	////////////// DATA MEMBERS
	
	private Approximation<T> totalIgnorance;
	private ArrayList<? extends Anytime<T>> subs;
	private Approximation<T> currentApproximation;
	
	////////////// CONSTRUCTOR
	
	public AbstractAnytimeEagerTreeComputation(Approximation<T> totalIgnorance) {
		super(totalIgnorance);
		this.totalIgnorance = totalIgnorance;
		this.subs = null;
		this.currentApproximation = totalIgnorance;
	}

	////////////// GETTERS AND SETTERS
	
	/**
	 * Hook for when sub computes its current approximation, with a default <code>return sub.getCurrentApproximation();</code>.
	 */
	protected Approximation<T> getCurrentApproximationForSub(Anytime<T> sub) {
		return sub.getCurrentApproximation();
	}

	@Override
	public ArrayList<? extends Anytime<T>> getSubs() {
		if (subs == null) {
			subs = makeSubs();
		}
		return subs;
	}

	@Override
	public Approximation<T> getCurrentApproximation() {
		return currentApproximation;
	}

	@Override
	public void setCurrentApproximation(Approximation<T> newCurrentApproximation) {
		currentApproximation = newCurrentApproximation;
	}
	
	@Override
	public Approximation<T> getTotalIgnorance() {
		return totalIgnorance;
	}

	///////////// MAKING AND GETTING SUBS
	
	@Override
	public boolean subsHaveNotYetBeenMade() {
		return subs == null;
	}

	private void iterateAllSubs() {
		for (Anytime<T> sub : getSubs()) {
			sub.next();
		}
	}

	////////////// (RE-)COMPUTING CURRENT APPROXIMATION
	
	@Override
	public void refreshFromWithin() {
		Approximation<T> result;
		if (subsHaveNotYetBeenMade()) {
			result = getTotalIgnorance();
		}
		else {
			result = eval(getSubs());
		}
		setCurrentApproximation(result);
	}

	private Approximation<T> eval(ArrayList<? extends Anytime<T>> subs) {
		var subsApproximations = mapIntoArrayList(subs, s -> getCurrentApproximationForSub(s)); 
		var result = function(subsApproximations);
		return result;
	}

	////////////// ITERATION
	
	@Override
	public Approximation<T> calculateNext() {
		boolean thereWasANextApproximation = computeApproximationBasedOnSubsNextCollectiveApproximationIfAny();
		if (thereWasANextApproximation) {
			return getCurrentApproximation();
		}
		else {
			return null;
		}
	}

	private boolean computeApproximationBasedOnSubsNextCollectiveApproximationIfAny() {
		boolean subsIteratedToTheirNextApproximation = iterateSubsToTheirNextCollectiveApproximationIfAny();
		if (subsIteratedToTheirNextApproximation) {
			refreshFromWithin();
		}
		return subsIteratedToTheirNextApproximation;
	}

	private boolean iterateSubsToTheirNextCollectiveApproximationIfAny() {
		boolean subsIteratedToTheirNextApproximation;
		if (subsHaveNotYetBeenMade()) {
			subsIteratedToTheirNextApproximation = iterateYetToBeMadeSubsToTheirFirstUsefulCollectiveApproximationIfAny();
		}
		else {
			subsIteratedToTheirNextApproximation = iterateAlreadyMadeSubsToTheirNextUsefulCollectiveApproximationIfAny();
		}
		return subsIteratedToTheirNextApproximation;
	}

	private boolean iterateYetToBeMadeSubsToTheirFirstUsefulCollectiveApproximationIfAny() {
		iterateYetToBeMadeSubsToTheirFirstUsefulCollectiveApproximation();
		boolean subsIteratedToTheirFirstUsefulApproximation = true;
		return subsIteratedToTheirFirstUsefulApproximation;
	}

	private void iterateYetToBeMadeSubsToTheirFirstUsefulCollectiveApproximation() {
		iterateAllSubs(); // this will make and bring subs to their first values, which is totalIgnorance and therefore not useful
		iterateAlreadyMadeSubsToTheirNextUsefulCollectiveApproximationIfAnyAssumingTheyAreAllAtTotalIgnorance();
	}

	private
	void
	iterateAlreadyMadeSubsToTheirNextUsefulCollectiveApproximationIfAnyAssumingTheyAreAllAtTotalIgnorance() {
		if (informativeApproximationRequiresAllSubsToBeInformative()) {
			iterateAllSubs();
		}
		else if (!getSubs().isEmpty() && informativeApproximationRequiresThatNotAllSubsAreNonInformative()) {
			boolean hadNextUsefulCollectiveApproximation = iterateAlreadyMadeSubsToTheirNextUsefulCollectiveApproximationIfAny();
			myAssert(hadNextUsefulCollectiveApproximation, () -> "Anytime subs should have had next useful collective approximation since they were just made and were at total ignorance (no computation performed yet), but did not.");
		}
		else {
			// we are not required to have informative subs to obtain a useful approximation, so we refrain from expansion for now.
		}
	}

	private boolean iterateAlreadyMadeSubsToTheirNextUsefulCollectiveApproximationIfAny() {
		boolean subsIteratedToTheirNextCollectiveApproximation;
		Anytime<T> nextSub = pickNextSubToIterate();
		boolean foundSubWithNext = (nextSub != null);
		if (foundSubWithNext) {
			nextSub.next();
			refreshFromWithoutSiblingSubsGivenChangeInExternalContext(nextSub);
			subsIteratedToTheirNextCollectiveApproximation = true;
		}
		else {
			subsIteratedToTheirNextCollectiveApproximation = false;
		}
		return subsIteratedToTheirNextCollectiveApproximation;
	}

	private void refreshFromWithoutSiblingSubsGivenChangeInExternalContext(Anytime<T> someSub) {
		if (getSubs().size() > 1) {
			for (var sub : getSubs()) {
				if (sub != someSub) {
					sub.refreshFromWithout();
				}
			}
		}
		// we might want to expand {@link Anytime} to indicate whether its evaluation
		// has actually changed the external context for the remaining subs
		// in order to avoid unnecessary rounds of this notification.
	}

}