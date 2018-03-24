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

import java.util.ArrayList;
import java.util.List;

import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.collect.EZIterator;
import com.sri.ai.util.computation.anytime.api.Anytime;
import com.sri.ai.util.computation.anytime.api.Approximation;
import com.sri.ai.util.computation.treecomputation.anytime.api.AnytimeTreeComputation;
import com.sri.ai.util.computation.treecomputation.api.TreeComputation;

/**
 * An abstract implementation of {@link AnytimeTreeComputation}
 * that borrows most functionality based on a given base {@link TreeComputation}.
 * <p>
 * Essentially, this creates an anytime tree computation by
 * lazily expanding, node by node, the base tree computation, and creating
 * anytime versions of them that are used to compute the next approximation
 * to the final root value.
 * <p>
 * Initially, the value of the anytime root computation is one provided at construction.
 * <p>
 * Then, in order to create the next approximation,
 * this class creates sub-anytime approximations
 * by requesting the base tree computation's children
 * and computing their anytime versions with
 * {@link #makeAnytimeVersion(NullaryFunction)}
 * (which must be implemented by extending classes).
 * The sub-anytime tree computations's approximations
 * are then used by {@link #function(List)} to compute the current approximation.
 * <p>
 * To update an even better next approximation,
 * the class picks the next anytime sub-computation
 * by using the abstract method {@link #pickNextSubWithNext()},
 * which is also to be specified by extensions.
 * <p>
 * It then iterates the picked next anytime sub-computation,
 * obtaining a new approximation to the corresponding argument,
 * and delegates again to {@link #function(List)}.
 * <p>
 * Extending classes must override {@link #makeAnytimeVersion(NullaryFunction sub)}
 * to indicate how to make the corresponding anytime version of a sub-tree computation,
 * {@link #pickNextSubWithNext()} to indicate how the next sub-tree computation
 * to be iterated is computed,
 * and {@link #function(List)} to specify how new approximations are computed.
 *
 * @author braz
 *
 * @param <T> the type of the values being approximated
 */
public abstract class AbstractAnytimeTreeComputation<T> extends EZIterator<Approximation<T>> implements AnytimeTreeComputation<T> {
	
	protected abstract Anytime<T> makeAnytimeVersion(NullaryFunction<T> baseSub);

	protected abstract Anytime<T> pickNextSubWithNext();

	@Override
	public abstract Approximation<T> function(List<Approximation<T>> subsApproximations);

	private Approximation<T> initialApproximation;
	private TreeComputation<T> base;
	private ArrayList<? extends Anytime<T>> subs;
	private Approximation<T> currentApproximation;
	
	public AbstractAnytimeTreeComputation(TreeComputation<T> base, Approximation<T> initialApproximation) {
		this.initialApproximation = initialApproximation;
		this.base = base;
		this.subs = null;
		this.currentApproximation = null; // initial approximation is first of iterator's elements, and since it has not been iterated yet, there is no current approximation.
	}

	@Override
	public ArrayList<? extends Anytime<T>> getSubs() {
		if (subsHaveNotYetBeenMade()) {
			makeSubsAndIterateThemToTheirInitialApproximation();
		}
		return subs;
	}

	protected void makeSubsAndIterateThemToTheirInitialApproximation() {
		subs = mapIntoArrayList(base.getSubs(), this::makeAnytimeVersion);
		iterateSubsToInitialApproximation();
	}

	private void iterateSubsToInitialApproximation() {
		for (Anytime<T> sub : subs) {
			sub.next();
		}
	}

	@Override
	public Approximation<T> calculateNext() {
		Approximation<T> result;
		
		if (firstIteration()) {
			result = initialApproximation;
		}
		else {
			result = computeNextBasedOnSubs();
		}
		
		updateCurrentApproximationIfANewValueHasBeenComputed(result);
		
		return result;
	}

	private boolean firstIteration() {
		return currentApproximation == null;
	}

	private void updateCurrentApproximationIfANewValueHasBeenComputed(Approximation<T> result) {
		if (result != null) {
			currentApproximation = result;
		}
	}

	private Approximation<T> computeNextBasedOnSubs() {
		Approximation<T> result;
		boolean subsIteratedToTheirNextApproximation = iterateSubsToTheirNextApproximationIfAny();
		if (subsIteratedToTheirNextApproximation) {
			result = computeApproximationBasedOnSubsApproximation();
		}
		else {
			result = null;
		}
		return result;
	}

	private boolean iterateSubsToTheirNextApproximationIfAny() {
		boolean subsIteratedToTheirNextApproximation;
		if (subsHaveNotYetBeenMade()) {
			subsIteratedToTheirNextApproximation = createSubsAndIterateThemToTheirFirstUsefulApproximationIfAny();
		}
		else {
			subsIteratedToTheirNextApproximation = iterateAlreadCreatedSubsToTheirNextApproximationIfAny();
		}
		return subsIteratedToTheirNextApproximation;
	}

	private boolean createSubsAndIterateThemToTheirFirstUsefulApproximationIfAny() {
		makeSubsAndIterateThemToTheirInitialApproximation();
		boolean subsIteratedToTheirNextApproximation = true;
		return subsIteratedToTheirNextApproximation;
	}

	private boolean subsHaveNotYetBeenMade() {
		return subs == null;
	}

	private boolean iterateAlreadCreatedSubsToTheirNextApproximationIfAny() {
		boolean subsIteratedToTheirNextApproximation;
		Anytime<T> nextSub = pickNextSubWithNext();
		boolean foundSubWithNext = (nextSub != null);
		if (foundSubWithNext) {
			nextSub.next();
			subsIteratedToTheirNextApproximation = true;
		}
		else {
			subsIteratedToTheirNextApproximation = false;
		}
		return subsIteratedToTheirNextApproximation;
	}

	private Approximation<T> computeApproximationBasedOnSubsApproximation() {
		
		List<Approximation<T>> subsApproximations = getSubsApproximations(); 
		
		Approximation<T> result = function(subsApproximations);
		
		return result;
	}

	private ArrayList<Approximation<T>> getSubsApproximations() {
		ArrayList<Approximation<T>> result = mapIntoArrayList(getSubs(), Anytime::getCurrentApproximation);
		return result;
	}

	public TreeComputation<T> getBase() {
		return base;
	}

	public Approximation<T> getCurrentApproximation() {
		return currentApproximation;
	}
}