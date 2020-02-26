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

import static com.sri.ai.util.Util.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.ListIterator;

import org.junit.Assert;
import org.junit.Test;

import com.sri.ai.util.Util;
import com.sri.ai.util.base.BinaryPredicate;

public class UtilTest {

	@Test
	public void testCastOrThrowError() {
		Integer i = 10;
		Object iObject = i;
		Integer j = Util.castOrThrowError(Integer.class, iObject, "Got %s, which should have been an instance of %s but is instead an instance of %s");
		Assert.assertEquals(i, j);

		try {
			Util.castOrThrowError(String.class, iObject, "Got %s, which should have been an instance of %s but is instead an instance of %s");
		} catch (Error e) {
			Assert.assertEquals("Got 10, which should have been an instance of String but is instead an instance of Integer", e.getMessage());
			return;
		}
		
		Assert.fail("Util.castOrThrowError should have throw error when attempting to cast Integer into String but did not.");
	}
	
	@Test
	public void testFindListIterator() {
		
		ListIterator<String> iterator;
		
		iterator = Util.find(list("Bob", "Mary", "John"), s -> s.contains("o"));
		assertTrue(iterator.hasNext());
		assertEquals("Bob", iterator.previous());
		
		iterator = Util.find(list("Bob", "Mary", "John"), s -> s.contains("e"));
		assertTrue(iterator == null);

		iterator = Util.find(list("Bob", "Mary", "John"), s -> s.contains("John"));
		assertFalse(iterator.hasNext());
		assertEquals("John", iterator.previous());
	}
	
	@Test
	public void testRatioisInOnePlusOrMinusEpsilon() {
		assertTrue(Util.ratioisInOnePlusOrMinusEpsilon(1, 1, 0.001));
		assertTrue(Util.ratioisInOnePlusOrMinusEpsilon(1.00001, 1, 0.001));
		assertTrue(Util.ratioisInOnePlusOrMinusEpsilon(1, 1.000001, 0.001));
		assertFalse(Util.ratioisInOnePlusOrMinusEpsilon(2, 1, 0.001));
		assertFalse(Util.ratioisInOnePlusOrMinusEpsilon(1, 2, 0.001));

		assertFalse(Util.ratioisInOnePlusOrMinusEpsilon(0, 2, 0.001));
		assertFalse(Util.ratioisInOnePlusOrMinusEpsilon(2, 0, 0.001));
		assertTrue(Util.ratioisInOnePlusOrMinusEpsilon(0, 0.0001, 0.001));
		assertTrue(Util.ratioisInOnePlusOrMinusEpsilon(0.0001, 0, 0.001));
	}
	
	@Test
	public void testThereIsAOneToOneMatching() {
		
		BinaryPredicate<String, String> sameInitial = (s1, s2) -> s1.startsWith(s2.substring(0, 1));

		runOneToOneMatchingTest(true, list(), list(), sameInitial);
		runOneToOneMatchingTest(false, list(), list("Mary", "Bob", "Joe"), sameInitial);

		runOneToOneMatchingTest(false, list("Alice", "Finn", "Carl"), list("Mary", "Bob", "Joe"), sameInitial);

		runOneToOneMatchingTest(true, list("John", "Mary", "Bob"),  list("Mary", "Bob", "Joe"), sameInitial);
		runOneToOneMatchingTest(true, list("John", "Mary", "Joe"),  list("Joe", "Mary", "Joe"), sameInitial);
		runOneToOneMatchingTest(false, list("John", "Mary", "Joe"), list("Joe", "Mary", "Bob"), sameInitial);
		runOneToOneMatchingTest(false, list("John", "Mary"),        list("Mary", "Bob", "Joe"), sameInitial);
		runOneToOneMatchingTest(false, list("John", "Mary", "Bob"), list("Mary", "Bob"), sameInitial);
	}

	private void runOneToOneMatchingTest(
			boolean expected,
			LinkedList<String> list1,
			LinkedList<String> list2,
			BinaryPredicate<String, String> sameInitial) {
		
		assertEquals(expected, Util.thereIsAOneToOneMatching(list1, list2, sameInitial));
		assertEquals(expected, Util.thereIsAOneToOneMatching(list2, list1, sameInitial));
	}
}
