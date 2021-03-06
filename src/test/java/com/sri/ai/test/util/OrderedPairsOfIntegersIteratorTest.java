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
package com.sri.ai.test.util;

import static com.sri.ai.util.Util.addAllToList;
import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.pair;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.sri.ai.util.base.OrderedPairsOfIntegersIterator;
import com.sri.ai.util.base.Pair;
import com.sri.ai.util.base.PairOf;

public class OrderedPairsOfIntegersIteratorTest {
	

	@Test
	public void test() {
		int n;
		int i;
		int j;
		List<Pair<Integer, Integer>> expected;
		Pair<Integer, Integer> pair;
		
		n = 0;
		i = 0;
		j = 1;
		expected = list();
		runTest(n, i, j, expected);
		
		n = 1;
		i = 0;
		j = 1;
		expected = list();
		runTest(n, i, j, expected);
		
		n = 2;
		i = 0;
		j = 1;
		expected = list(pair(0,1));
		runTest(n, i, j, expected);
		
		n = 2;
		i = 1; // invalid
		j = 1;
		expected = list();
		runTest(n, i, j, expected);
		
		n = 2;
		i = 1; // invalid
		j = 2; // invalid
		expected = list();
		runTest(n, i, j, expected);
		
		n = 3;
		i = 0;
		j = 1;
		expected = list(pair(0,1), pair(0,2), pair(1,2));
		runTest(n, i, j, expected);
		
		n = 2;
		i = 0;
		j = 1;
		expected = list(pair(0,1));
		runTest(n, i, j, expected);
		
		n = 4;
		i = 1;
		j = 2;
		expected = list(pair(1,2), pair(1,3), pair(2,3));
		runTest(n, i, j, expected);
		
		n = 4;
		OrderedPairsOfIntegersIterator iterator = new OrderedPairsOfIntegersIterator(n);
		assertEquals(true, iterator.hasNext());
		assertTrue(iterator.hasNextAndItIsAtRowBeginning());
		assertFalse(iterator.hadPreviousAndItWasLastOfRow()); // has not had a previous yet
		pair = iterator.next();
		assertEquals(pair(0, 1), pair);
		assertFalse(iterator.hadPreviousAndItWasLastOfRow());
		
		iterator.increment();
		assertEquals(true, iterator.hasNext());
		assertFalse(iterator.hasNextAndItIsAtRowBeginning());
		assertFalse(iterator.hadPreviousAndItWasLastOfRow()); // has had a previous but it was not last in row
		pair = iterator.next();
		assertEquals(pair(0, 2), pair);
		assertFalse(iterator.hadPreviousAndItWasLastOfRow()); // has had a previous but it was not last in row
		
		iterator.makeSureToBeAtRowBeginning();
		assertEquals(true, iterator.hasNext());
		assertTrue(iterator.hasNextAndItIsAtRowBeginning());
		assertFalse(iterator.hadPreviousAndItWasLastOfRow()); // same previous, so same result here as last call
		pair = iterator.next();
		assertEquals(pair(1, 2), pair);
		assertFalse(iterator.hadPreviousAndItWasLastOfRow()); // has had a previous but it was not last in row
		
		assertEquals(true, iterator.hasNext());
		assertFalse(iterator.hasNextAndItIsAtRowBeginning());
		pair = iterator.next();
		assertEquals(pair(1, 3), pair);
		assertTrue(iterator.hadPreviousAndItWasLastOfRow()); // has had a previous *and* it was last in row

		iterator.makeSureToBeAtRowBeginning();
		assertEquals(true, iterator.hasNext());
		assertTrue(iterator.hasNextAndItIsAtRowBeginning());
		assertTrue(iterator.hadPreviousAndItWasLastOfRow()); // same previous, so same result here as last call
		pair = iterator.next();
		assertEquals(pair(2, 3), pair);
		assertTrue(iterator.hadPreviousAndItWasLastOfRow()); // has had a previous *and* it was last in row

		assertEquals(false, iterator.hasNext());
		assertFalse(iterator.hasNextAndItIsAtRowBeginning());
		assertTrue(iterator.hadPreviousAndItWasLastOfRow()); // same previous, so same result here as last call

		iterator.makeSureToBeAtRowBeginning();
		assertEquals(false, iterator.hasNext());
		assertTrue(iterator.hadPreviousAndItWasLastOfRow()); // same previous, so same result here as last call
		assertFalse(iterator.hasNextAndItIsAtRowBeginning());

		iterator.increment();
		assertEquals(false, iterator.hasNext());
		assertFalse(iterator.hasNextAndItIsAtRowBeginning());
}

	public void runTest(int n, int i, int j, List<Pair<Integer, Integer>> expected) {
		OrderedPairsOfIntegersIterator iterator = new OrderedPairsOfIntegersIterator(n, i, j);
		List<PairOf<Integer>> actual = addAllToList(iterator);
		assertEquals(expected, actual);
	}
}
