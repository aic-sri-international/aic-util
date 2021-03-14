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
package com.sri.ai.util.collect;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.common.annotations.Beta;
import com.sri.ai.util.math.MixedRadixNumber;

/**
 * A utility class for enumerating over the cartesian product of lists of lists
 * of elements. For example, if you have 2 lists each of which has two elements:
 * 
 * <pre>
 * [list_1_Element_1, list_1_Element_2]
 * [list_2_Element_1, list_2_Element_2]
 * </pre>
 * 
 * and you enumerate fastest from right to left (the default), the cross product
 * elements will come out in the following order:
 * 
 * <pre>
 * [list_1_Element_1, list_2_Element_1]
 * [list_1_Element_1, list_2_Element_2]
 * [list_1_Element_2, list_2_Element_1]
 * [list_1_Element_2, list_2_Element_2]
 * </pre>
 * 
 * alternatively if you enumerate fastest from left to right, the cross product
 * elements will come out in the following order:
 * 
 * <pre>
 * [list_1_Element_1, list_2_Element_1]
 * [list_1_Element_2, list_2_Element_1]
 * [list_1_Element_1, list_2_Element_2]
 * [list_1_Element_2, list_2_Element_2]
 * </pre>
 * 
 * @author oreilly
 * 
 * @param <E>
 *            the type of the elements in the Cartesian Product being enumerated
 *            over.
 */
@Beta
public class CartesianProductEnumeration<E> implements Enumeration<List<E>> {

	private boolean          enumerateFastestFromRightToLeft = true;
	private MixedRadixNumber mixedRadixNumber                = null;
	private List<E>          currentElements                 = new ArrayList<E>();
	private List<List<E>>    elementValues                   = new ArrayList<List<E>>();
	private BigInteger       sizeOfCrossProduct              = null;
	private boolean          firstTime                       = true;

	public CartesianProductEnumeration(
			List<? extends List<E>> listOfListsOfElements) {
		this(listOfListsOfElements, true);
	}

	public CartesianProductEnumeration(
			List<? extends List<E>> listOfListsOfElements,
			boolean enumerateFastestFromRightToLeft) {
		this.enumerateFastestFromRightToLeft = enumerateFastestFromRightToLeft;
		if (enumerateFastestFromRightToLeft) {
			for (int i = 0; i < listOfListsOfElements.size(); i++) {
				elementValues.add(listOfListsOfElements.get(i));
			}
		} 
		else {
			for (int i = listOfListsOfElements.size() - 1; i >= 0; i--) {
				elementValues.add(listOfListsOfElements.get(i));
			}
		}

		int[] radices = new int[elementValues.size()];
		for (int i = 0; i < elementValues.size(); i++) {
			int size = elementValues.get(i).size();
			if (size == 0) {
				throw new IllegalArgumentException("List " + i
						+ " has no elements in it.");
			}
			radices[i] = size;
		}
		mixedRadixNumber = new MixedRadixNumber(BigInteger.ZERO, radices);
		
		// Add 1 to the max value that can be represented in the 
		// mixed radix number to get the size
		sizeOfCrossProduct = BigInteger.ONE.add(mixedRadixNumber.getMaxAllowedValue());
	}

	/**
	 * 
	 * @return The number of elements that will be enumerated over.
	 */
	public BigInteger size() {
		return sizeOfCrossProduct;
	}

	//
	// START - Enumeration Interface
	@Override
	public boolean hasMoreElements() {
		return firstTime || mixedRadixNumber.canIncrement();
	}

	@Override
	public List<E> nextElement() {
		if (!hasMoreElements()) {
			throw new NoSuchElementException("No more elements.");
		}
		if (firstTime) {
			firstTime = false;
			// First time in, collect the elements from
			// the first row.
			if (enumerateFastestFromRightToLeft) {
				for (int i = 0; i < elementValues.size(); i++) {
					currentElements.add(elementValues.get(i).get(0));
				}
			} 
			else {
				for (int i = elementValues.size() -1; i >= 0; i--) {
					currentElements.add(elementValues.get(i).get(0));
				}
			}
		} 
		else {
			mixedRadixNumber.increment();
			currentElements.clear();
			if (enumerateFastestFromRightToLeft) {
				for (int i = 0; i < elementValues.size(); i++) {
					currentElements.add(
							elementValues.get(i).get(
									mixedRadixNumber.getCurrentNumeralValue(i)));
				}
			} 
			else {
				for (int i = elementValues.size() -1; i >= 0; i--) {
					currentElements.add(
							elementValues.get(i).get(
									mixedRadixNumber.getCurrentNumeralValue(i)));
				}
			}
		}

		return Collections.unmodifiableList(currentElements);
	}

	// END - Enumerate Interface
	//
}
