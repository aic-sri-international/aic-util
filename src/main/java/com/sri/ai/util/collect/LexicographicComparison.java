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

import java.util.Comparator;
import java.util.Iterator;

import com.google.common.annotations.Beta;
import com.google.common.collect.Ordering;

/**
 * A generic implementation of lexicographical comparison using iterators.
 * This is similar to Guava's {@link Ordering#lexicographical()}, but that method requires {@link Iterable}s,
 * which prevents its (proper) use with iterators.
 * 
 * Given two iterators over ranges a1, a2, ..., an and b1, b2, ..., bm,
 * defines
 * <pre>
 * lex(a1..an, b1..bm) = -1 if (n = 0 and m &gt; 0) or a1 &lt; b1
 *                     =  1 if (m = 0 and n &gt; 0) or b1 &gt; a1
 *                     = lex(a2..an, b2..bm) if n = m = 0 or a1 == b1
 * </pre>
 *                     
 * The operator &lt; can be provided as a comparator, or the given base type default comparison is used.
 * 
 * This class does not extend {@link Comparator} because it has the side effect of exhausting the iterators. 
 *                     
 * @author braz
 */
@Beta
public class LexicographicComparison<T extends Comparable> {
	private Comparator<T> baseComparator;
	
	public LexicographicComparison() {
		this.baseComparator = Ordering.natural();
	}
	
	public LexicographicComparison(Comparator<T> baseComparator) {
		this.baseComparator = baseComparator;
	}
	
	public int compare(Iterator<T> a, Iterator<T> b) {
		int result;
		
		if ( !a.hasNext()) {
			if ( !b.hasNext()) {
				result = 0; // two empty iterators
			}
			else {
				result = -1; // a is empty, b is not, so a is less than b.
			}
		}
		else if ( !b.hasNext()) {
			result = +1; // a is not empty, b is empty, so a is more than b.
		}
		else { // both are not empty, so both have a first element
			T aFirst = a.next();
			T bFirst = b.next();
			int bc = baseComparator.compare(aFirst, bFirst);
			if (bc == 0) { // first elements are equal, so defer decision to remaining elements
				result = compare(a,b);
			}
			else {
				result = bc; // first elements are not equal, so they determine entire comparison
			}
		}
		
		return result;
	}
}
