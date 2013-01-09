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
package com.sri.ai.util.cache;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.cache.CacheStats;
import com.sri.ai.util.base.BinaryFunction;
import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.base.Pair;
import com.sri.ai.util.collect.FunctionIterator;

/**
 * A {@link CacheMap} that offers more flexibility than {@link DefaultCacheMap}
 * with respect to what exactly it stores and returns. It works as a
 * {@link CacheMap} between keys of type <code>K</code> and values of type
 * <code>V</code>. However, it actually stores <i>internal</i> keys of type
 * <code>K_</code> and values of type <code>V_</code>.
 * <p>
 * Such functions are by the user to provide abstractions in the sets of values
 * stored and recovered.
 * <p>
 * The relationship between keys and values and their stored counterparts is
 * given by user-defined functions. See constructors parameters names to see
 * what these functions should take as input and output.
 * 
 * @author braz
 */
@Beta
public class FlexibleCacheMap<K,V,K_,V_> implements CacheMap<K, V> {

	private Function<K, K_>                    fromKeyToStoredKey;
	private BinaryFunction<K, V, Pair<K_, V_>> fromPairToStoredPair;
	private BinaryFunction<K, V_, V>           fromKeyAndStoredValueToValue;
	//
	private DefaultCacheMap<K_, V_>      innerCacheMap;
	private NullaryFunction<Iterator<K>> reachableObjectIteratorMaker;

	public FlexibleCacheMap(long maximumSize,
			Function<K, K_>                    fromKeyToStoredKey,
			BinaryFunction<K, V, Pair<K_, V_>> fromPairToStoredPair,
			BinaryFunction<K, V_, V>           fromKeyAndStoredValueToValue,
			NullaryFunction<Iterator<K>>       reachableObjectIteratorMaker,
			int garbageCollectionPeriod) {
		this.fromKeyToStoredKey           = fromKeyToStoredKey;
		this.fromPairToStoredPair         = fromPairToStoredPair;
		this.fromKeyAndStoredValueToValue = fromKeyAndStoredValueToValue;
		this.reachableObjectIteratorMaker = reachableObjectIteratorMaker;
		//
		NullaryFunction<Iterator<K_>> storedReachableObjectIteratorMaker = makeReachableStoredObjectsIteratorMaker(reachableObjectIteratorMaker);
		this.innerCacheMap = new DefaultCacheMap<K_, V_>(maximumSize, storedReachableObjectIteratorMaker, garbageCollectionPeriod);
	}
	
	@Override
	public void garbageCollect(Iterator<K> reacheableObjectsIterator) {
		Iterator<K_> reacheableStoredKeysIterator =
			new FunctionIterator<K, K_>(reacheableObjectsIterator, fromKeyToStoredKey);
		innerCacheMap.garbageCollect(reacheableStoredKeysIterator);
	}
	
	@Override
	public void clear() {
		innerCacheMap.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		throw new Error("containsKey() not supported by FlexibleCacheMap");
	}

	@Override
	public boolean containsValue(Object value) {
		throw new Error("containsValue() not supported by FlexibleCacheMap");
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		throw new Error("entrySet() not supported by FlexibleCacheMap");
	}

	@Override
	public Set<K> keySet() {
		throw new Error("keySet() not supported by FlexibleCacheMap");
	}
	
	@Override
	public Collection<V> values() {
		throw new Error("values() not supported by FlexibleCacheMap");
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		K_ storedKey   = fromKeyToStoredKey.apply((K)key);
		V_ storedValue = innerCacheMap.get(storedKey);
		V value = storedValue == null ? null : fromKeyAndStoredValueToValue.apply((K) key, storedValue);
		return value;
	}

	@Override
	public boolean isEmpty() {
		return innerCacheMap.isEmpty();
	}

	@Override
	public V put(K key, V value) {
		Pair<K_,V_> storedPair = fromPairToStoredPair.apply(key, value);
		V_ storedValueReturned = innerCacheMap.put(storedPair.first, storedPair.second);
		V result = storedValueReturned == null? null : fromKeyAndStoredValueToValue.apply(key, storedValueReturned);
		return result;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> t) {
		for (Map.Entry<? extends K, ? extends V> entry : t.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public V remove(Object key) {
		K_ storedKey           = fromKeyToStoredKey.apply((K) key);
		V_ storedValueReturned = innerCacheMap.remove(storedKey);
		V result = storedValueReturned == null? null : fromKeyAndStoredValueToValue.apply((K) key, storedValueReturned);
		return result;
	}

	@Override
	public int size() {
		return innerCacheMap.size();
	}

	@Override
	public int getGarbageCollectionPeriod() {
		return innerCacheMap.getGarbageCollectionPeriod();
	}

	@Override
	public NullaryFunction<Iterator<K>> getReachableObjectIteratorMaker() {
		return reachableObjectIteratorMaker;
	}

	@Override
	public void setGarbageCollectionPeriod(int period) {
		innerCacheMap.setGarbageCollectionPeriod(period);
	}

	@Override
	public void setReachableObjectIteratorMaker(NullaryFunction<Iterator<K>> reachableObjectIteratorMaker) {
		this.reachableObjectIteratorMaker = reachableObjectIteratorMaker;
		NullaryFunction<Iterator<K_>> storedReachableObjectIteratorMaker = makeReachableStoredObjectsIteratorMaker(reachableObjectIteratorMaker);
		innerCacheMap.setReachableObjectIteratorMaker(storedReachableObjectIteratorMaker);
	}
	
	@Override
	public CacheStats stats() {
		return innerCacheMap.stats();
	}
	
	//
	// PRIVATE METHODS
	//
	private NullaryFunction<Iterator<K_>> makeReachableStoredObjectsIteratorMaker(
			NullaryFunction<Iterator<K>> reachableObjectIteratorMaker) {
		NullaryFunction<Iterator<K_>> storedReachableObjectIteratorMaker =
			new ReachableStoredObjectIteratorMaker(reachableObjectIteratorMaker);
		return storedReachableObjectIteratorMaker;
	}

	private class ReachableStoredObjectIteratorMaker implements NullaryFunction<Iterator<K_>> {
		private NullaryFunction<Iterator<K>> reachableObjectIteratorMaker;
		
		public ReachableStoredObjectIteratorMaker(NullaryFunction<Iterator<K>> reachableObjectIteratorMaker) {
			super();
			this.reachableObjectIteratorMaker = reachableObjectIteratorMaker;
		}

		@Override
		public Iterator<K_> apply() {
			Iterator<K> objectsIterator = reachableObjectIteratorMaker.apply();
			Iterator<K_> storedKeyIterator =
				new FunctionIterator<K, K_>(objectsIterator, fromKeyToStoredKey);
			return storedKeyIterator;
		}
	}
}
