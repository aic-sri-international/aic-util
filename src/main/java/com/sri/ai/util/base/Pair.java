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
package com.sri.ai.util.base;

import com.google.common.annotations.Beta;

/**
 * A pair of objects with hash code and equality reduced to its components.
 * 
 * @author braz
 */
@Beta
public class Pair<T1, T2> {
	public T1 first;
	public T2 second;

	public Pair() {
	}

	public Pair(T1 first, T2 second) {
		this.first = first;
		this.second = second;
	}

	public static <T1, T2> Pair<T1, T2> make(T1 first, T2 second) {
		return new Pair<T1, T2>(first, second);
	}

	public static <T1, T2> Pair<T1, T2> pair(T1 first, T2 second) {
		return new Pair<T1, T2>(first, second);
	}

	public static <T> Pair<T, T> pair(Iterable<? extends T> iterable) {
		var iterator = iterable.iterator();
		return pair(iterator.next(), iterator.next());
	}

	@Override
	public int hashCode() {
		// Subtract different primes to ensure re-orderings do not generate the same hash codes.
		return (first.hashCode() - 11) + (second.hashCode() - 17);
	}

	@Override
	public boolean equals(Object another) {
		if (another instanceof Pair) {
			Pair<?, ?> anotherPair = (Pair<?, ?>) another;
			return first.equals(anotherPair.first)
					&& second.equals(anotherPair.second);
		}
		return false;
	}

	@Override
	public String toString() {
		return "(" + first + ", " + second + ")";
	}
}
