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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.annotations.Beta;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheStats;
import com.google.common.collect.ForwardingMap;
import com.sri.ai.util.AICUtilConfiguration;
import com.sri.ai.util.base.NullaryFunction;


/**
 * A default implementation for {@link CacheMap} that, upon garbage collection,
 * creates a new internal map and moves entries with reachable entries to it,
 * discarding all other entries.
 * 
 * If a garbage collection period <code>n</code> and a reachable object iterator
 * maker are provided, then garbage collection happens automatically after every
 * <code>n</code> {@link #put(Object, Object)} operations.
 * 
 * @author braz
 * @author oreilly
 */
@Beta
public class DefaultCacheMap<K, V> extends ForwardingMap<K, V> implements CacheMap<K, V> {
	
	// Configuration attributes
	private boolean                      weakKeys                     = false;
	private long                         maximumSize                  = NO_MAXIMUM_SIZE;
	private NullaryFunction<Iterator<K>> reachableObjectIteratorMaker = null;
	private int                          garbageCollectionPeriod      = NO_GARBAGE_COLLECTION;
	// Working attributes
	private Cache<K, V> storage                                = null;
	private Map<K, V>   delegate                               = null;
	private int         numberOfPutsSinceLastGarbageCollection = 0;
	private Lock        garbageCollectLock                     = new ReentrantLock();
	
	public DefaultCacheMap() {
		initStorage();
	}
	
	public DefaultCacheMap(boolean weakKeys) {
		this(weakKeys, NO_MAXIMUM_SIZE);
	}
	
	public DefaultCacheMap(long maximumSize) {
		this(false, maximumSize);
	}
	
	public DefaultCacheMap(boolean weakKeys, long maximumSize) {
		this.weakKeys    = weakKeys;
		this.maximumSize = maximumSize;
		initStorage();
	}
	
	public DefaultCacheMap(long maximumSize, NullaryFunction<Iterator<K>> reachableObjectIteratorMaker, int garbageCollectionPeriod) {
		this(false, maximumSize, reachableObjectIteratorMaker, garbageCollectionPeriod);
	}
	
	public DefaultCacheMap(boolean weakKeys, long maximumSize, NullaryFunction<Iterator<K>> reachableObjectIteratorMaker, int garbageCollectionPeriod) {
		this.weakKeys                     = weakKeys;
		this.maximumSize                  = maximumSize;
		this.reachableObjectIteratorMaker = reachableObjectIteratorMaker;
		this.garbageCollectionPeriod      = garbageCollectionPeriod;
		initStorage();
	}
	
	//
	// START-MAP Interface
	@Override
	public void clear() {
		storage.invalidateAll();
		storage.cleanUp();
	}
	
	@Override
	public V get(Object key) {
		return storage.getIfPresent(key);
	}
	
	@Override
	public V put(K key, V value) {
		storage.put(key, value);
		numberOfPutsSinceLastGarbageCollection++;
		checkDoGarbageCollect();
		return value;
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends V> t) {
		storage.putAll(t);
		numberOfPutsSinceLastGarbageCollection += t.size();
		checkDoGarbageCollect();
	}
	
	// END-MAP Interface
	//

	//
	// START-CacheMap
	@Override
	public void garbageCollect(Iterator<K> reachableObjectsIterator) {
		// To ensure we don't garbage collect repeatedly across threads.
		if (reachableObjectsIterator != null && garbageCollectLock.tryLock()) {
			try {
				if (isGarbageCollection() && numberOfPutsSinceLastGarbageCollection >= getGarbageCollectionPeriod()) {
					Set<K> reachableObjects = setFrom(reachableObjectsIterator);
					delegate.keySet().retainAll(reachableObjects);
					storage.cleanUp();
					numberOfPutsSinceLastGarbageCollection = 0;
				}
			} finally {
				garbageCollectLock.unlock();
			}
		}
	}

	private HashSet<K> setFrom(Iterator<K> reachableObjectsIterator) {
		HashSet<K> reachableObjects = new HashSet<>();
		while (reachableObjectsIterator.hasNext()) {
			reachableObjects.add(reachableObjectsIterator.next());
		}
		return reachableObjects;
	}

	@Override
	public int getGarbageCollectionPeriod() {
		return garbageCollectionPeriod;
	}

	@Override
	public NullaryFunction<Iterator<K>> getReachableObjectIteratorMaker() {
		return reachableObjectIteratorMaker;
	}

	@Override
	public void setGarbageCollectionPeriod(int period) {
		this.garbageCollectionPeriod = period;
	}

	@Override
	public void setReachableObjectIteratorMaker(NullaryFunction<Iterator<K>> iteratorMaker) {
		this.reachableObjectIteratorMaker = iteratorMaker;
	}
	
	@Override
	public CacheStats stats() {
		return storage.stats();
	}
	
	// END-CacheMap
	//
	
	//
	// PROTECTED METHODS
	//
	@Override
	protected Map<K,V> delegate() {
		return delegate;
	}
	
	//
	// PRIVATE METHODS
	//
	private void initStorage() {
		CacheBuilder<Object, Object> cb = CacheBuilder.newBuilder();
		
		if (weakKeys) {
			cb.weakKeys();
		}
		// Note: a maximumSize of 
		// < 0 means no size restrictions
		// = 0 means no cache
		// > 0 means maximum size of cache
		if (maximumSize >= 0L) {		
			cb.maximumSize(maximumSize);
		}
		if (AICUtilConfiguration.isRecordCacheStatistics()) {
			cb.recordStats();
		}
		
		storage  = cb.build();
		delegate = storage.asMap();
	}
	
	private void checkDoGarbageCollect() {
		if (isGarbageCollection() && numberOfPutsSinceLastGarbageCollection >= getGarbageCollectionPeriod()) {
			garbageCollect(reachableObjectIteratorMaker.apply());
		}
	}
	
	private boolean isGarbageCollection() {
		return getGarbageCollectionPeriod() != NO_GARBAGE_COLLECTION && reachableObjectIteratorMaker != null;
	}
}
