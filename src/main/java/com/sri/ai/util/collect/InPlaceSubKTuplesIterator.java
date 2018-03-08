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

import static com.sri.ai.util.Util.fill;
import static com.sri.ai.util.Util.iterator;
import static com.sri.ai.util.Util.myAssert;
import static com.sri.ai.util.Util.set;
import static com.sri.ai.util.collect.FunctionIterator.functionIterator;
import static com.sri.ai.util.collect.PredicateIterator.predicateIterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.sri.ai.util.base.NullaryFunction;


/**
 * An iterator over sub-tuples of k integers from 0 to n - 1, using the same collection instance to store all sub-tuples.
 * 
 * @author braz
 */
@Beta
public class InPlaceSubKTuplesIterator<E> extends NonDeterministicIterator<List<E>> {

	public InPlaceSubKTuplesIterator(ArrayList<E> array, int k) {
		super(makeLazyTreeOverChoicesOfElementsFromArrayWithLeavesStoppingAtKChoices(array, k));
	}

	private static <E> DefaultLazyTree<List<E>> makeLazyTreeOverChoicesOfElementsFromArrayWithLeavesStoppingAtKChoices(ArrayList<E> array, int k) {
		return new DefaultLazyTree<List<E>>(makeIteratorOverBranchesDependingOnFirstElementToBeChosen(array, k));
	}

	private static <E> Iterator<NullaryFunction<LazyTree<List<E>>>> makeIteratorOverBranchesDependingOnFirstElementToBeChosen(ArrayList<E> arrayToChosenFrom, int numberOfChoices) {
		int numberOfChoicesMadeSoFar = 0;
		LinkedHashSet<E> elementsAlreadyChosen = set();
		ArrayList<E> arrayStoringIndicesOfKChoices = fill(numberOfChoices, (E) null);
		return 
				makeIteratorOverEitherNextElementToBeChosenOrArrayOfFinalKChoices(
						numberOfChoicesMadeSoFar,
						arrayToChosenFrom,
						numberOfChoices,
						elementsAlreadyChosen,
						arrayStoringIndicesOfKChoices);
	}

	private static <T> Iterator<NullaryFunction<LazyTree<List<T>>>> makeIteratorOverEitherNextElementToBeChosenOrArrayOfFinalKChoices(
			int numberOfChoicesMadeSoFar, 
			ArrayList<T> arrayToChoseFrom, 
			int numberOfChoices,
			Collection<T> elementsAlreadyChosen, 
			ArrayList<T> arrayStoringIndicesOfKChoices) {
		
		myAssert(
				() -> numberOfChoices <= arrayToChoseFrom.size(),
				() -> InPlaceSubKTuplesIterator.class + " requires k to be at most the array size, but we got instead k = " + numberOfChoices + " and array size = " + arrayToChoseFrom);
		
		Iterator<NullaryFunction<LazyTree<List<T>>>> result;
		
		if (numberOfChoicesMadeSoFar == numberOfChoices) {
			result = makeIteratorOverSingleNodeContainingArrayOfFinalKChoices(arrayStoringIndicesOfKChoices);
		}
		else {
			result =
					functionIterator(
							availableElements(arrayToChoseFrom, elementsAlreadyChosen),
							commitToChosenElementAndReturnMakerOfLaterChoices(numberOfChoicesMadeSoFar, arrayToChoseFrom, numberOfChoices, elementsAlreadyChosen, arrayStoringIndicesOfKChoices));
		}
		
		return result;
	}

	private static <T> Iterator<NullaryFunction<LazyTree<List<T>>>> makeIteratorOverSingleNodeContainingArrayOfFinalKChoices(ArrayList<T> arrayStoringIndicesOfKChoices) {
		return iterator(() -> new DefaultLazyTree<>(arrayStoringIndicesOfKChoices));
	}

	private static <T> Function<T, NullaryFunction<LazyTree<List<T>>>> commitToChosenElementAndReturnMakerOfLaterChoices(int numberOfChoicesMadeSoFar, ArrayList<T> arrayToChoseFrom, int numberOfChoices, Collection<T> elementsAlreadyChosen, ArrayList<T> arrayStoringIndicesOfKChoices) {
		return j -> () -> {
			arrayStoringIndicesOfKChoices.set(numberOfChoicesMadeSoFar, j);
			Collection<T> newElementsAlreadyChosen = new LinkedList<T>(elementsAlreadyChosen); // maybe we can optimize to eliminate this copy without much extra search cost.
			newElementsAlreadyChosen.add(j);
			return makeLazyTreeOverSubsequentChoices(numberOfChoicesMadeSoFar, arrayToChoseFrom, numberOfChoices, newElementsAlreadyChosen, arrayStoringIndicesOfKChoices);
		};
	}

	private static <T> PredicateIterator<T> availableElements(ArrayList<T> arrayToChoseFrom, Collection<T> elementsAlreadyChosen) {
		return predicateIterator(arrayToChoseFrom, elementIsStillAvailable(elementsAlreadyChosen));
	}

	private static <T> Predicate<T> elementIsStillAvailable(Collection<T> elementsAlreadyChosen) {
		return j -> ! elementsAlreadyChosen.contains(j);
	}

	private static <T> DefaultLazyTree<List<T>> makeLazyTreeOverSubsequentChoices(int numberOfChoicesMadeSoFar, ArrayList<T> arrayToChoseFrom, int numberOfChoices, Collection<T> newElementsAlreadyChosen, ArrayList<T> arrayStoringIndicesOfKChoices) {
		return new DefaultLazyTree<List<T>>(makeIteratorOverEitherNextElementToBeChosenOrArrayOfFinalKChoices(numberOfChoicesMadeSoFar + 1, arrayToChoseFrom, numberOfChoices, newElementsAlreadyChosen, arrayStoringIndicesOfKChoices));
	}
}
