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
package com.sri.ai.test.util.cache;

import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.sri.ai.util.base.Mutable;
import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.cache.CacheMap;
import com.sri.ai.util.cache.DefaultCacheMap;
import com.sri.ai.util.collect.EZIterator;


public class CacheMapTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCacheCentral() {
		final int GARBAGE_COLLECTION_PERIOD = 200000;

		Runtime.getRuntime().gc();
		System.out.println("Free memory in the beginning                      : " + Runtime.getRuntime().freeMemory());

		CacheMap<Integer, String> cache1;
		CacheMap<Integer, Integer> cache2;
		
		final Mutable<Integer> counter = new Mutable<Integer>(0);
		
		cache1 = new DefaultCacheMap<Integer, String>(CacheMap.NO_MAXIMUM_SIZE, new NullaryFunction<Iterator<Integer>>() { public Iterator<Integer> apply() { return new EvenIntegerIterator(counter.value.intValue()); }}, GARBAGE_COLLECTION_PERIOD);
		cache2 = new DefaultCacheMap<Integer, Integer>(CacheMap.NO_MAXIMUM_SIZE, new NullaryFunction<Iterator<Integer>>() { public Iterator<Integer> apply() { return new EvenIntegerIterator(counter.value.intValue()); }}, GARBAGE_COLLECTION_PERIOD);
		
		long mem1;
		long mem2;
		long mem3;
		
		Runtime.getRuntime().gc();
		System.out.println("Free memory before storage                        : " + Runtime.getRuntime().freeMemory());

		for (int i = 0; i != GARBAGE_COLLECTION_PERIOD - 1; i++) {
			counter.value = i;
			cache1.put(i, Integer.toString(i));
			cache2.put(i, i);
		}
		
		Runtime.getRuntime().gc();
		System.out.println("Free memory before CacheMap.put garbage collection: " + (mem1 = Runtime.getRuntime().freeMemory()));

		cache1.put(GARBAGE_COLLECTION_PERIOD, Integer.toString(GARBAGE_COLLECTION_PERIOD));
		cache2.put(GARBAGE_COLLECTION_PERIOD, GARBAGE_COLLECTION_PERIOD);

		Runtime.getRuntime().gc();
		System.out.println("Free memory  after CacheMap.put garbage collection: " + (mem2 = Runtime.getRuntime().freeMemory()));
		
		cache1.clear();
		cache2.clear();

		Runtime.getRuntime().gc();
		System.out.println("Free memory  after clearing caches                : " + (mem3 = Runtime.getRuntime().freeMemory()));

		assertTrue(mem1 < mem2);
		assertTrue(mem2 < mem3);
	}
	
	public static class EvenIntegerIterator extends EZIterator<Integer> {

		int i = 0;
		int end;
		
		public EvenIntegerIterator(int end) {
			this.end = end;
		}
		
		@Override
		protected Integer calculateNext() {
			i += 2;
			if (i < end) {
				return i;
			}
			return null;
		}
	}
}
