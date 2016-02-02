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
 * Neither the name of the aic-expresso nor the names of its
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
package com.sri.ai.util.base;

import static com.sri.ai.util.base.PairOf.pairOf;

import com.google.common.annotations.Beta;
import com.sri.ai.util.collect.EZIterator;

/**
 * A cloneable iterator over ordered pairs of integers less than <code>n</code>.
 * <p>
 * One of the constructors takes the initial indices as parameters as well.
 * <p>
 * If <code>n</code> is 0 or 1, no pairs are provided (naturally).
 * <p>
 * If <code>n</code> is 2 or greater but the initial indices don't lead to the create of ordered pairs, no pairs are provided.
 * 
 * @author braz
 *
 */
@Beta
public class OrderedPairsOfIntegersIterator extends EZIterator<PairOf<Integer>> implements CloneableIterator<PairOf<Integer>> {

	private int n;
	private int i;
	private int j;
	
	private boolean hadPreviousAndItWasLastOfRowForPreviousIAndJ;

	public OrderedPairsOfIntegersIterator(int n) {
		this(n, 0, 1);
	}

	public OrderedPairsOfIntegersIterator(int n, int i, int j) {
		super();
		if (initialIIsValid(n, i) && initialJIsValid(n, j)) {
			this.i = i;
			this.j = j;
			this.onNext = true;
			this.next = pairOf(i, j);
		}
		else {
			// no pairs available
			this.onNext = true;
			this.next = null;
		}
		
		this.n = n;
		this.hadPreviousAndItWasLastOfRowForPreviousIAndJ = false; // has not had a previous element yet
	}

	private boolean initialIIsValid(int n, int i) {
		return i >= 0 && i < n - 1;
	}

	private boolean initialJIsValid(int n, int j) {
		return j >= 0 && j < n;
	}

	/**
	 * A cloning method delegating to super.clone().
	 */
	@Override
	public OrderedPairsOfIntegersIterator clone() {
		try {
			return (OrderedPairsOfIntegersIterator) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error("Trying to clone " + getClass() + " but cloning is not supported for this class.");
		}
	}
	
	@Override
	protected PairOf<Integer> calculateNext() {
		recordWhetherHadPreviousAndItWasLastOfRowBeforeUpdatingIAndJ();
		
		if ( ! increment()) {
			return null;
		}
		return next; // relies on the fact that increment sets 'next'
	}

	/**
	 * If there is a next pair, move to it and returns true. Otherwise, returns false.
	 * @return whether it was possible to increment to a next pair.
	 */
	public boolean increment() {
		recordWhetherHadPreviousAndItWasLastOfRowBeforeUpdatingIAndJ();
		if (j == n - 1) {
			if ( ! makeSureToBeAtRowBeginning()) {
				return false;
			}
		}
		else {
			j++;
			next = pairOf(i, j);
			onNext = true;
		}
		return true;
	}

	/**
	 * Iterate, if needed, until next element is at beginning of row
	 * (that is, with a position (i, i + 1) for some i),
	 * or return false if that is not possible.
	 * @return whether it succeeded
	 */
	public boolean makeSureToBeAtRowBeginning() {
		recordWhetherHadPreviousAndItWasLastOfRowBeforeUpdatingIAndJ();
		if (onNext && j == i + 1) { // already at beginning of row
			return true;
		}
		else { // not at row beginning
			if (i == n - 2) { // try to move forward but not possible
				return false;
			}
			i++; // move forward
			j = i + 1;
			next = pairOf(i, j);
			onNext = true;
			return true;
		}
	}
	
	private void recordWhetherHadPreviousAndItWasLastOfRowBeforeUpdatingIAndJ() {
		// at this point, we are about to *calculate* next pair (i', j') to current (i, j), so we will lose this information (current (i, j)).
		// Therefore, we must record the information of whether current (i, j) is at the end of a row,
		// because if hadPreviousAndItWasLastOfRow is invoked *before* (i', j') gets returned (that is, becomes the "previous"ly returned element),
		// (i, j) remains the current "previous" and hadPreviousAndItWasLastOfRow needs to inform about it. 
		hadPreviousAndItWasLastOfRowForPreviousIAndJ = j == n - 1;
	}

	/**
	 * Returns whether there is a next element and it is at the beginning of a row.
	 * @return has next and it is at row beginning
	 */
	public boolean hasNextAndItIsAtRowBeginning() {
		if (hasNext()) {
			boolean result = j == i + 1;
			return result;
		}
		else {
			return false;
		}
	}
	
	public boolean hadPreviousAndItWasLastOfRow() {
		if ( ! onNext) {
			// (i, j) still points to previously returned element, so we check if it is at the end of a row			
			boolean result = j == n - 1;
			return result;
		}
		else {
			// if we have already computed 'next', current (i, j) no longer refer to the previously returned element. Therefore we must return the information recorded when (i, j) were still at the previously returned element
			return hadPreviousAndItWasLastOfRowForPreviousIAndJ;
		}
	}
}