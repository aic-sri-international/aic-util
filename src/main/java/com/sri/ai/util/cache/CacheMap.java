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

import java.util.Iterator;
import java.util.Map;

import com.google.common.annotations.Beta;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheStats;
import com.sri.ai.util.base.NullaryFunction;

/**
 * Interface for maps working as a cache with garbage collection.
 * 
 * Garbage collection is activated by providing an iterator going over objects
 * that need to be kept in memory. The cache must eliminate references to all
 * other objects.
 * 
 * Optionally, the user can provide a {@link NullaryFunction} that evaluates to
 * an iterator over reachable objects, and a period <code>n</code> between
 * automatic garbage collections. Automatic garbage collection is then run with
 * the iterator provided by the function, every <code>n</code> calls to the
 * cache.
 * 
 * @author braz
 */
@Beta
public interface CacheMap<K, V> extends Map<K, V> {
	
	Long     NO_MAXIMUM_SIZE       = new Long(-1L);
	Integer  NO_GARBAGE_COLLECTION = new Integer(-1);

	/**
	 * Performs garbage collection by going over iterator range and considering
	 * these objects the ones that need to be kept, eliminating references for any
	 * other kept objects (which will then, if not referenced anywhere else,
	 * be garbage-collected by the Java virtual machine).
	 */
	void garbageCollect(Iterator<K> reacheableObjectsIterator);

	/**
	 * Returns the current number of calls to the cache made
	 * between one automatic garbage collection to the other.
	 * The automatic garbage collection is done by using the reachable objects iterator
	 * provided by {@link #getReachableObjectIteratorMaker()} function.
	 * A value of <code>-1</code> indicates that no automatic garbage collection
	 * takes place.
	 */
	int getGarbageCollectionPeriod();

	/**
	 * Allows the user to set the number of cache calls between
	 * automatic garbage collections.
	 * A value of <code>-1</code> indicates that no automatic garbage collection
	 * takes place.
	 */
	void setGarbageCollectionPeriod(int period);

	/**
	 * Returns the current reachable objects iterator maker.
	 * A <code>null</code> value indicates that no automatic garbage collection
	 * takes place.
	 */
	NullaryFunction<Iterator<K>> getReachableObjectIteratorMaker();
	
	/**
	 * Allows the user to provide a new reachable objects iterator maker.
	 * A <code>null</code> value indicates that no automatic garbage collection
	 * takes place.
	 */
	void setReachableObjectIteratorMaker(NullaryFunction<Iterator<K>> iteratorMaker);
	
	/**
	 * 
	 * @see Cache#stats()
	 */
	CacheStats stats();
}
