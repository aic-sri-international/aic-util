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
package com.sri.ai.util.collect;

import static com.sri.ai.util.base.PairOf.makePairOf;

import java.util.Iterator;
import java.util.List;

import com.google.common.annotations.Beta;
import com.sri.ai.util.base.PairOf;

/**
 * An iterator over pairs of distinct elements in an {@link java.util.List},
 * such that each pair's first element occurs before the second in the list.
 * It is highly advisable that the list be an {@link java.util.ArrayList}
 * since the class heavily uses random access.
 * 
 * @param <E> the type of elements
 *
 * @author braz
 */
@Beta
public class PairOfElementsInListIterator<E> extends EZIterator<PairOf<E>> {

	private List<E> list;
	private int i; // invariant: next = PairOf.makePairOf(list.get(i), list.get(j))
	private int j;
	
	public PairOfElementsInListIterator(List<E> list) {
		this.list = list;
		if (list.size() < 2) {
			next = null;
			onNext = true;
		}
		else {
			this.i = 0;
			this.j = 1;
			next = makePairOf(list.get(i), list.get(j));
			onNext = true;
		}
	}
	
	public static <E> Iterator<PairOf<E>> make(List<E> list) {
		return new PairOfElementsInListIterator<E>(list);
	}

	@Override
	protected PairOf<E> calculateNext() {
		j++;
		if (j != list.size()) {
			next = makePairOf(list.get(i), list.get(j));
		}
		else {
			i++;
			if (i != list.size() - 1) {
				j = i + 1;
				next = makePairOf(list.get(i), list.get(j));
			}
			else {
				next = null;
			}
		}

		return next;
	}
}
