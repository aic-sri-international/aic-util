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

import java.util.Iterator;

import com.google.common.annotations.Beta;
import com.sri.ai.util.base.Pair;

/**
 * A PairIterator is constructed on two iterators, and iterates over pairs of
 * their next elements until one of them runs out of elements. For example, if
 * given iterators for collections (1,2,3), and (x, y), it will range over lists
 * (1,x) and (2,y).
 * 
 * @author braz
 */
@Beta
public class PairIterator<T1, T2> extends EZIteratorWithNull<Pair<T1, T2>> {

	Iterator<T1> iterator1;
	Iterator<T2> iterator2;

	public PairIterator(Iterator<T1> iterator1, Iterator<T2> iterator2) {
		this.iterator1 = iterator1;
		this.iterator2 = iterator2;
	}

	@Override
	protected Pair<T1, T2> calculateNext() {
		Pair<T1, T2> next = new Pair<T1, T2>();
		if (iterator1.hasNext()) {
			T1 nextFromIterator = iterator1.next();
			next.first = nextFromIterator;
		} 
		else {
			endOfRange();
			return null;
		}
		if (iterator2.hasNext()) {
			T2 nextFromIterator = iterator2.next();
			next.second = nextFromIterator;
		} 
		else {
			endOfRange();
			return null;
		}
		return next;
	}
}
