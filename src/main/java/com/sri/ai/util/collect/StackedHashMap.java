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

import static com.sri.ai.util.Util.map;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.annotations.Beta;

/**
 * An implementation of {@link AbstractStackedMap} using HashMap and HashSet. 
 * <p>
 * IMPORTANT: see notes on super class documentation regarding incomplete implementation.
 *
 * @author braz
 */
@Beta
public class StackedHashMap<K, V> extends AbstractStackedMap<K, V> {
	public StackedHashMap() {
		super();
	}

	public StackedHashMap(Map<K, V> base) {
		super(base);
	}

	public StackedHashMap(Map<K, V> top, Map<K, V> base) {
		super(top, base);
	}

	/**
	 * Convenience for {@link #StackedHashMap(Map)}.
	 * @param base the base map
	 * @return a stacked map with the base and an empty top map
	 */
	public static <K, V> StackedHashMap<K, V> stackedHashMap(Map<K, V> base) {
		return new StackedHashMap<K, V>(base);
	}

	/**
	 * Convenience for {@link #StackedHashMap(Map, Map)}.
	 * @param top the top map
	 * @param base the base map
	 * @return a stacked map with the given top and base maps
	 */
	public static <K, V> StackedHashMap<K, V> stackedHashMap(Map<K, V> top, Map<K, V> base) {
		return new StackedHashMap<K, V>(top, base);
	}


	/**
	 * Convenience for making a stacked map with the given key and value.
	 * @param key the key
	 * @param value the value
	 * @param base the base map
	 * @return a stacked map with the a top map with the given key and value, and given base map.
	 */
	public static <K, V> StackedHashMap<K, V> stackedHashMap(K key, V value, Map<K, V> base) {
		return new StackedHashMap<K, V>(map(key, value), base);
	}

	@Override
	Map<K, V> makeMap() {
		return new LinkedHashMap<K, V>();
	}

	@Override
	Set<K> makeSetForKeySet() {
		return new LinkedHashSet<K>();
	}
}
