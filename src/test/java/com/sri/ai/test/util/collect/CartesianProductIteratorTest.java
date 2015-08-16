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

import static com.sri.ai.util.Util.iterator;
import static com.sri.ai.util.Util.join;
import static com.sri.ai.util.Util.listFrom;
import static com.sri.ai.util.Util.map;
import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.collect.CartesianProductIterator;

public class CartesianProductIteratorTest {

	@Test
	public void test() {
		Map<String, NullaryFunction<Iterator<String>>> iteratorMakers;
		String expected;

		iteratorMakers =
				map(
						"size",  (NullaryFunction<Iterator<String>>) () -> iterator("small", "medium", "big"),
						"color", (NullaryFunction<Iterator<String>>) () -> iterator("red", "green", "blue"),
						"shape", (NullaryFunction<Iterator<String>>) () -> iterator("square", "circle")
						);
		expected =
				"{size=medium, color=red, shape=square}\n" + 
				"{size=big, color=red, shape=square}\n" + 
				"{size=small, color=green, shape=square}\n" + 
				"{size=medium, color=green, shape=square}\n" + 
				"{size=big, color=green, shape=square}\n" + 
				"{size=small, color=blue, shape=square}\n" + 
				"{size=medium, color=blue, shape=square}\n" + 
				"{size=big, color=blue, shape=square}\n" + 
				"{size=small, color=red, shape=circle}\n" + 
				"{size=medium, color=red, shape=circle}\n" + 
				"{size=big, color=red, shape=circle}\n" + 
				"{size=small, color=green, shape=circle}\n" + 
				"{size=medium, color=green, shape=circle}\n" + 
				"{size=big, color=green, shape=circle}\n" + 
				"{size=small, color=blue, shape=circle}\n" + 
				"{size=medium, color=blue, shape=circle}\n" + 
				"{size=big, color=blue, shape=circle}\n" + 
				"{size=small, color=red, shape=square}";
		runTest(iteratorMakers, expected);

		
		iteratorMakers =
				map(
						"size",  (NullaryFunction<Iterator<String>>) () -> iterator(),
						"color", (NullaryFunction<Iterator<String>>) () -> iterator("red", "green", "blue")
						);
		expected = "";
		runTest(iteratorMakers, expected);

		
		iteratorMakers =
				map(
						"color", (NullaryFunction<Iterator<String>>) () -> iterator("red", "green", "blue"),
						"size",  (NullaryFunction<Iterator<String>>) () -> iterator()
						);
		expected = "";
		runTest(iteratorMakers, expected);

		
		iteratorMakers =
				map(
						"size",  (NullaryFunction<Iterator<String>>) () -> iterator("one-size-fits-all"),
						"color", (NullaryFunction<Iterator<String>>) () -> iterator("red", "green", "blue")
						);
		expected =
				"{size=one-size-fits-all, color=green}\n" + 
				"{size=one-size-fits-all, color=blue}\n" + 
				"{size=one-size-fits-all, color=red}";
		runTest(iteratorMakers, expected);

		
		iteratorMakers =
				map("size",  (NullaryFunction<Iterator<String>>) () -> iterator("one-size-fits-all"));
		expected =
				"{size=one-size-fits-all}";
		runTest(iteratorMakers, expected);

		
		iteratorMakers = map();
		expected = "{}";
		runTest(iteratorMakers, expected);
	}

	/**
	 * @param iteratorMakers
	 * @param expected
	 */
	protected void runTest(Map<String, NullaryFunction<Iterator<String>>> iteratorMakers, String expected) {
		Iterator<Map<String, String>> iterator;
		List<Map<String, String>> list;
		String description;
		iterator = new CartesianProductIterator<>(iteratorMakers);
		list = listFrom(iterator);
		description = join("\n", list);
		assertEquals(expected, description);
	}
}
