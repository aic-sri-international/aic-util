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

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.google.common.annotations.Beta;

/**
 * A comparator lexicographically composed by a list of comparators. The first
 * comparator in the list is used first and, if there is a tie, the next ones
 * are used successively.
 * 
 * @author braz
 */
@Beta
public class LexicographicComparator<T> implements Comparator<T> {
	
	private List<Comparator<T>> comparators;

	public LexicographicComparator(List<Comparator<T>> comparators) {
		this.comparators = comparators;
	}

	public LexicographicComparator(Comparator<T>... comparators) {
		this.comparators = Arrays.asList(comparators);
	}

	@Override
	public int compare(T o1, T o2) {
		return compare(o1, o2, 0);
	}

	private int compare(T o1, T o2, int i) {
		if (i == comparators.size()) {
			return 0;
		}
		Comparator<T> nextComparator = comparators.get(i);
		int resultForNextComparator = nextComparator.compare(o1, o2);
		if (resultForNextComparator == 0) {
			return compare(o1, o2, i+1);
		}
		return resultForNextComparator;
	}
}
