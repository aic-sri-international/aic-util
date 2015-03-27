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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import com.sri.ai.util.Util;
import com.sri.ai.util.collect.ArrayHashSet;
import com.sri.ai.util.collect.BackListIterator;

public class ArrayHashSetTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		
		ArrayHashSet<String> set;
		
		set = new ArrayHashSet<String>();
		assertTrue(set.isEmpty());
		assertEquals(set.size(), 0);
		
		assertEquals(Util.list(), Util.listFrom(set.iterator())); // tests iteration order

		set.add("tree");
		assertFalse(set.isEmpty());
		assertEquals(set.size(), 1);
		assertEquals("tree", set.get(0));
		
		set.add("almond");
		assertFalse(set.isEmpty());
		assertEquals(set.size(), 2);
		assertEquals("tree", set.get(0));
		assertEquals("almond", set.get(1));

		assertEquals(Util.list("tree", "almond"), Util.listFrom(set.iterator())); // tests iteration order
		
		set.clear();
		assertTrue(set.isEmpty());
		assertEquals(set.size(), 0);
		
		set.add("almond");
		assertFalse(set.isEmpty());
		assertEquals(set.size(), 1);
		assertEquals("almond", set.get(0));
		
		set.add("tree");
		assertFalse(set.isEmpty());
		assertEquals(set.size(), 2);
		assertEquals("almond", set.get(0));
		assertEquals("tree", set.get(1));
		
		set.add("leaf");
		assertFalse(set.isEmpty());
		assertEquals(set.size(), 3);
		assertEquals("almond", set.get(0));
		assertEquals("tree", set.get(1));
		assertEquals("leaf", set.get(2));

		assertEquals(Util.list("almond", "tree", "leaf"), Util.listFrom(set.iterator())); // tests iteration order
		
		set.remove("tree");
		assertFalse(set.isEmpty());
		assertEquals(set.size(), 2);
		assertEquals("almond", set.get(0));
		assertEquals("leaf", set.get(1));

		assertEquals(Util.list("almond", "leaf"), Util.listFrom(set.iterator())); // tests iteration order

		set.remove("leaf");
		assertFalse(set.isEmpty());
		assertEquals(set.size(), 1);
		assertEquals("almond", set.get(0));

		assertEquals(Util.list("almond"), Util.listFrom(set.iterator())); // tests iteration order

		set.remove("almond");
		assertTrue(set.isEmpty());
		assertEquals(set.size(), 0);

		assertEquals(Util.list(), Util.listFrom(set.iterator())); // tests iteration order

		set.add("almond");
		set.add("tree");
		set.add("tree");
		set.add("almond");
		set.add("leaf");
		set.add("tree");

		assertEquals("almond", set.get(0));
		assertEquals("tree", set.get(1));
		assertEquals("leaf", set.get(2));
		assertEquals(Util.list("almond", "tree", "leaf"), Util.listFrom(set.iterator())); // tests iteration order

		set.set(2, "trunk");
		assertEquals("almond", set.get(0));
		assertEquals("tree", set.get(1));
		assertEquals("trunk", set.get(2));
		assertEquals(Util.list("almond", "tree", "trunk"), Util.listFrom(set.iterator())); // tests iteration order
		
		set.set(2, "leaf");
		
		assertEquals("almond", set.get(0));
		assertEquals("tree", set.get(1));
		assertEquals("leaf", set.get(2));
		assertEquals(Util.list("almond", "tree", "leaf"), Util.listFrom(set.iterator())); // tests iteration order
		
		set.set(2, "leaf");
		
		assertEquals("almond", set.get(0));
		assertEquals("tree", set.get(1));
		assertEquals("leaf", set.get(2));
		assertEquals(Util.list("almond", "tree", "leaf"), Util.listFrom(set.iterator())); // tests iteration order
		
		try {
			set.set(2, "almond"); // cannot add an already-present element in a different position
			fail("Should have thrown an exception");
		}
		catch (IllegalArgumentException e) {
			// good, did what it had to do
		}
		catch (Exception e) {
			fail("Should have thrown an IllegalArgumentException but threw " + e);
		}
		
		try {
			set.set(3, "almond"); // cannot set a non-existing position
			fail("Should have thrown an exception");
		}
		catch (NoSuchElementException e) {
			// good, did what it had to do
		}
		catch (Exception e) {
			fail("Should have thrown an NoSuchElementException but threw " + e);
		}

		// should remain the same after failed operations
		assertEquals("almond", set.get(0));
		assertEquals("tree", set.get(1));
		assertEquals("leaf", set.get(2));
		assertEquals(Util.list("almond", "tree", "leaf"), Util.listFrom(set.iterator())); // tests iteration order

		assertEquals(Util.list("leaf", "tree", "almond"), Util.listFrom(new BackListIterator<String>(set.listIterator(set.size())))); // tests back-iteration order
	}
}
