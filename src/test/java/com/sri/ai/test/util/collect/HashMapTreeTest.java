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

import org.junit.Before;
import org.junit.Test;

import com.sri.ai.util.Util;
import com.sri.ai.util.collect.HashMapTree;
import com.sri.ai.util.collect.StringIterator;
import com.sri.ai.util.collect.Tree;

public class HashMapTreeTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		HashMapTree tree = new HashMapTree();
		
		StringIterator i;
		boolean newNodes;
		Tree.GetResult result;
		
		i = new StringIterator("abobora");
		//System.out.println(Util.join("", i));

		i = new StringIterator("");
		result = tree.get(i);
		assertEquals("", Util.join("", result.getConsumedElements()));
		assertEquals(false, result.isValid()); // empty string is never accepted

		i = new StringIterator("abobora");
		newNodes = tree.put(i);
		//System.out.println(tree);
		assertEquals(true, newNodes);

		i = new StringIterator("abobora");
		newNodes = tree.put(i);
		assertEquals(false, newNodes);

		i = new StringIterator("abo");
		newNodes = tree.put(i);
		assertEquals(false, newNodes);

		i = new StringIterator("abacate");
		newNodes = tree.put(i);
		assertEquals(true, newNodes);

		i = new StringIterator("abacate");
		newNodes = tree.put(i);
		//System.out.println(tree);
		assertEquals(false, newNodes);

		i = new StringIterator("abobo");
		result = tree.get(i);
		assertEquals("abobo", Util.join("", result.getConsumedElements()));
		assertEquals(true, result.isValid());
		assertEquals("abo", Util.join("", result.getValidPath()));
		
		i = new StringIterator("blah");
		result = tree.get(i);
		assertEquals("b", Util.join("", result.getConsumedElements()));
		assertEquals(false, result.isValid());
		assertEquals(null, result.getValidPath());
		
		i = new StringIterator("abobora");
		result = tree.get(i);
		assertEquals("abobora", Util.join("", result.getConsumedElements()));
		assertEquals("abobora", Util.join("", result.getValidPath()));

		i = new StringIterator("abacate");
		result = tree.get(i);
		assertEquals("abacate", Util.join("", result.getConsumedElements()));
		assertEquals("abacate", Util.join("", result.getValidPath()));

		i = new StringIterator("ab");
		result = tree.get(i);
		assertEquals(false, result.isValid());
		assertEquals("ab", Util.join("", result.getConsumedElements()));

		i = new StringIterator("abacateiro");
		result = tree.get(i);
		assertEquals("abacatei", Util.join("", result.getConsumedElements()));
		assertEquals("abacate",  Util.join("", result.getValidPath()));

		i = new StringIterator("blah");
		newNodes = tree.put(i);
		assertEquals(true, newNodes);

		i = new StringIterator("blahah");
		result = tree.get(i);
		assertEquals("blaha", Util.join("", result.getConsumedElements()));
		assertEquals("blah",  Util.join("", result.getValidPath()));
	
		i = new StringIterator("");
		newNodes = tree.put(i);
		assertEquals(false, newNodes);

		i = new StringIterator("");
		result = tree.get(i);
		assertEquals("", Util.join("", result.getConsumedElements()));
		assertEquals("", Util.join("", result.getValidPath()));
	}
}
