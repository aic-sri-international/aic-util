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
package com.sri.ai.test.util.collect;

import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.listFrom;
import static com.sri.ai.util.collect.FunctionIterator.functionIterator;
import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Test;

import com.google.common.base.Function;
import com.sri.ai.util.base.Mutable;
import com.sri.ai.util.collect.FunctionIterator;
import com.sri.ai.util.collect.LazyIterator;

public class FunctionIteratorTest {

	@Test
	public void test() {
		
		FunctionIterator<Integer, String> functionIterator;
		LinkedList<String> expected;
		
		functionIterator = functionIterator(list(1, 2, 3), i -> i.toString());
		expected = list("1", "2", "3");
		assertEquals(expected, listFrom(functionIterator));
		
		functionIterator = functionIterator(list(), i -> i.toString());
		expected = list();
		assertEquals(expected, listFrom(functionIterator));
	}

	@Test
	public void testLazy() {
		
		LazyIterator<String> lazyIterator;
		String current;
		Mutable<Boolean> functionRan = new Mutable<>(false);
			
		Function<Integer, String> function =
				i -> {
					functionRan.value = true;
					return i.toString();
				};
				
		lazyIterator = functionIterator(list(1, 2, 3), function);
		assertTrue(lazyIterator.hasNext());
		assertFalse(functionRan.value);

		lazyIterator.goToNextWithoutComputingCurrent();
		assertFalse(functionRan.value);
		
		lazyIterator.goToNextWithoutComputingCurrent();
		assertFalse(functionRan.value);
		
		current = lazyIterator.computeCurrent();
		assertEquals("2", current);
		assertTrue(functionRan.value);
		
		functionRan.value = false;
		current = lazyIterator.next();
		assertTrue(functionRan.value);
		assertEquals("3", current);
		
		assertFalse(lazyIterator.hasNext());
	}
}
