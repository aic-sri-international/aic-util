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

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.sri.ai.util.collect.DisjointSets;

public class DisjointSetsTest {
	
	@Test
	public void testConstructors() {
		DisjointSets<String> disjSets = new DisjointSets<String>();
		Assert.assertEquals(0, disjSets.numberDisjointSets());
		
		disjSets = new DisjointSets<String>("a", "a", "b");
		Assert.assertEquals(2, disjSets.numberDisjointSets());
		
		disjSets = new DisjointSets<String>(Arrays.asList("a", "a", "b"));
		Assert.assertEquals(2, disjSets.numberDisjointSets());
	}
	
	@Test
	public void testMakeSet() {
		DisjointSets<String> disjSets = new DisjointSets<String>();
		
		disjSets.makeSet("a");
		Assert.assertEquals(1, disjSets.numberDisjointSets());
		
		disjSets.makeSet("a");
		Assert.assertEquals(1, disjSets.numberDisjointSets());
		
		disjSets.makeSet("b");
		Assert.assertEquals(2, disjSets.numberDisjointSets());
	}
	
	@Test
	public void testUnion() {
		DisjointSets<String> disjSets = new DisjointSets<String>(
				"a", "b", "c", "d");
		Assert.assertEquals(4, disjSets.numberDisjointSets());
		
		disjSets.union("a", "b");
		Assert.assertEquals(3, disjSets.numberDisjointSets());
		Assert.assertEquals(disjSets.find("a"), disjSets.find("b"));
		
		disjSets.union("c", "d");
		Assert.assertEquals(2, disjSets.numberDisjointSets());
		Assert.assertEquals(disjSets.find("c"), disjSets.find("d"));
		
		disjSets.union("b", "c");
		Assert.assertEquals(1, disjSets.numberDisjointSets());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testUnionIllegalArgumentException1() {
		DisjointSets<String> disjSets = new DisjointSets<String>(
				"a");
		disjSets.union("b", "a");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testUnionIllegalArgumentException2() {
		DisjointSets<String> disjSets = new DisjointSets<String>(
				"a");
		disjSets.union("a", "b");
	}

	/**
	 * Note: This is based on the example given in Figure 21.1 of 'Introduction
	 * to Algorithm 2nd Edition' (by Cormen, Leriserson, Rivest, and Stein)
	 */
	@Test
	public void testWorkedExample() {
		// Should be the following when finished:
		// {a, b, c, d}, {e, f, g}, {h, i}, and {j}
		
		// 1. initial sets
		DisjointSets<String> disjSets = new DisjointSets<String>(
				"a", "b", "c", "d", "e", "f", "g", "h", "i", "j");
		
		Assert.assertEquals(10, disjSets.numberDisjointSets());
		Assert.assertEquals(1, disjSets.find("a").size());
		Assert.assertEquals(1, disjSets.find("b").size());
		Assert.assertEquals(1, disjSets.find("c").size());
		Assert.assertEquals(1, disjSets.find("d").size());
		Assert.assertEquals(1, disjSets.find("e").size());
		Assert.assertEquals(1, disjSets.find("f").size());
		Assert.assertEquals(1, disjSets.find("g").size());
		Assert.assertEquals(1, disjSets.find("h").size());
		Assert.assertEquals(1, disjSets.find("i").size());
		Assert.assertEquals(1, disjSets.find("j").size());
		
		// 2. (b, d)
		disjSets.union("b", "d");
		Assert.assertEquals(9, disjSets.numberDisjointSets());
		Assert.assertEquals(disjSets.find("b"), disjSets.find("d"));
		
		// 3. (e, g)
		disjSets.union("e", "g");		
		Assert.assertEquals(8, disjSets.numberDisjointSets());
		Assert.assertEquals(disjSets.find("e"), disjSets.find("g"));
		
		// 4. (a, c)
		disjSets.union("a", "c");		
		Assert.assertEquals(7, disjSets.numberDisjointSets());
		Assert.assertEquals(disjSets.find("a"), disjSets.find("c"));
		
		// 5. (h, i)
		disjSets.union("h", "i");		
		Assert.assertEquals(6, disjSets.numberDisjointSets());
		Assert.assertEquals(disjSets.find("h"), disjSets.find("i"));
		
		// 6. (a, b)
		disjSets.union("a", "b");
		Assert.assertEquals(5, disjSets.numberDisjointSets());
		Assert.assertEquals(disjSets.find("a"), disjSets.find("b"));
		Assert.assertEquals(disjSets.find("b"), disjSets.find("c"));
		Assert.assertEquals(disjSets.find("c"), disjSets.find("d"));
		
		// 7. (e, f)
		disjSets.union("e", "f");
		Assert.assertEquals(4, disjSets.numberDisjointSets());
		Assert.assertEquals(disjSets.find("e"), disjSets.find("f"));
		Assert.assertEquals(disjSets.find("f"), disjSets.find("g"));
		
		// 8. (b, c)
		disjSets.union("b", "c");
		Assert.assertEquals(4, disjSets.numberDisjointSets());
		Assert.assertEquals(disjSets.find("a"), disjSets.find("b"));
		Assert.assertEquals(disjSets.find("b"), disjSets.find("c"));
		Assert.assertEquals(disjSets.find("c"), disjSets.find("d"));
	}
}
