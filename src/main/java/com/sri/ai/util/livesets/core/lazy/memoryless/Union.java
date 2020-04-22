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
package com.sri.ai.util.livesets.core.lazy.memoryless;

import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.listWithoutElementAt;
import static com.sri.ai.util.Util.thereExists;
import static com.sri.ai.util.collect.FunctionIterator.functionIterator;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import com.google.common.base.Predicate;
import com.sri.ai.util.Util;
import com.sri.ai.util.livesets.api.LiveSet;


public class Union<T> implements LiveSet<T> {
	
	private List<? extends LiveSet<T>> liveSets;
	
	public Union(List<? extends LiveSet<T>> liveSets) {
		this.liveSets = liveSets;
	}
	
	@Override
	public boolean contains(T element) {
		boolean result = thereExists(liveSets, s -> s.contains(element));
		return result;
	}
	
	public static <T> LiveSet<T> unionOfAllButTheOneAt(List<? extends LiveSet<T>> sets, int indexOfExcluded) {
		List<LiveSet<T>> siblingsLiveSets = listWithoutElementAt(sets, indexOfExcluded);
		LiveSet<T> union = union(siblingsLiveSets);
		return union;
	}

	public static <T> LiveSet<T> union(List<? extends LiveSet<T>> liveSets) {
		return new Union<>(liveSets); 
	}
	
	public static <T> LiveSet<T> union(LiveSet<T> liveSet1, LiveSet<T> liveSet2) {
		return new Union<>(list(liveSet1, liveSet2)); 
	}

	@Override
	public boolean thereIsAnElementSatisfying(Predicate<T> predicate) {
		boolean result = thereExists(liveSets, l -> l.thereIsAnElementSatisfying(predicate));
		return result;
	}

	@Override
	public Iterator<? extends T> iterator() {
		LinkedHashSet<? extends T> union = Util.union(functionIterator(liveSets, LiveSet::getCurrentElements));
		Iterator<? extends T> iterator = union.iterator();
		return iterator;
	}
}