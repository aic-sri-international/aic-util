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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sri.ai.util.collect.AbstractStackedMap;
import com.sri.ai.util.collect.StackedHashMap;

public class StackedMapTest {

	private String abc  = "abc";
	private String def  = "def";
	private String ghi  = "ghi";

	private Integer n123 = 123;
	private Integer n456 = 456;
	private Integer n789 = 789;

	private AbstractStackedMap<String, Integer> base    = new StackedHashMap<String, Integer>();
	private AbstractStackedMap<String, Integer> map     = new StackedHashMap<String, Integer>(base);
	private HashMap<String, Integer>            regular = new LinkedHashMap<String, Integer>();
	private HashMap<String, Integer>            other   = new LinkedHashMap<String, Integer>();

	@Before
	public void setUp() {
		base.put(abc,n123);
		map.put(def,n456);
		map.put(abc,n789);
		base.put(ghi,null);

		regular.put(def,n456);
		regular.put(abc,n789);
		regular.put(ghi,null);

		other.put("This is ", 0);
	}

	@Test
	public void test() {
		AbstractStackedMap<String, Integer> base = new StackedHashMap<String, Integer>();
		base.put("abc", 123);
		AbstractStackedMap<String, Integer> map  = new StackedHashMap<String, Integer>(base);
		map.put("def", 456);
		Assert.assertSame(map.getBase(), base);
		Assert.assertEquals(map.get("abc"), new Integer(123));
		Assert.assertEquals(map.get("def"), new Integer(456));
		map.put("abc", new Integer(789));
		Assert.assertEquals(map.get("abc"), new Integer(789));
		base.put("ghi", null);
		Assert.assertSame(map.get("ghi"), null);
		Assert.assertTrue(map.containsKey("ghi"));
	}

	@Test
	public void testEquals() {
		Assert.assertEquals(map, regular);
		Assert.assertEquals(map.hashCode(), regular.hashCode());
		Assert.assertEquals(map.size(), regular.size());
		Assert.assertEquals(map.entrySet(), regular.entrySet());
		Assert.assertEquals(map.keySet(), regular.keySet());
		Set<Integer> mapValues = new LinkedHashSet<Integer>();
		mapValues.addAll(map.values());
		Set<Integer> regularValues = new LinkedHashSet<Integer>();
		regularValues.addAll(regular.values());
		Assert.assertEquals(mapValues, regularValues);
		Assert.assertTrue(map.hashCode() != other.hashCode());
	}

	@Test
	public void testPutAll() {
		Map<String, Integer> clone = new StackedHashMap<String, Integer>(map);
		clone.clear();
		Assert.assertTrue(clone.isEmpty());
		clone.putAll(regular);
		Assert.assertEquals(clone, regular);
		Assert.assertEquals(clone.size(), regular.size());
		Assert.assertEquals(clone.entrySet(), regular.entrySet());
		Assert.assertEquals(clone.keySet(), regular.keySet());
		Set<Integer> cloneValues = new LinkedHashSet<Integer>();
		cloneValues.addAll(clone.values());
		Set<Integer> regularValues = new LinkedHashSet<Integer>();
		regularValues.addAll(regular.values());
		Assert.assertEquals(cloneValues, regularValues);
		Assert.assertEquals(clone.hashCode(), regular.hashCode());
	}
}
