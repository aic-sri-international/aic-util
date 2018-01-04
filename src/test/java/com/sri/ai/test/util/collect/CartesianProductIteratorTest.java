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

import static com.sri.ai.util.Util.arrayList;
import static com.sri.ai.util.Util.arrayListFrom;
import static com.sri.ai.util.Util.iterator;
import static com.sri.ai.util.Util.join;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Test;

import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.collect.CartesianProductIterator;

public class CartesianProductIteratorTest {

	@Test
	public void test() {
		ArrayList<NullaryFunction<Iterator<String>>> iteratorMakers;
		String expected;

		iteratorMakers =
				arrayList(
						() -> iterator("small", "medium", "big"),
						() -> iterator("red", "green", "blue"),
						() -> iterator("square", "circle")
						);
		expected =
				"[small, red, square]\n" +
				"[small, red, circle]\n" + 
				"[small, green, square]\n" + 
				"[small, green, circle]\n" + 
				"[small, blue, square]\n" + 
				"[small, blue, circle]\n" + 
				"[medium, red, square]\n" + 
				"[medium, red, circle]\n" + 
				"[medium, green, square]\n" + 
				"[medium, green, circle]\n" + 
				"[medium, blue, square]\n" + 
				"[medium, blue, circle]\n" + 
				"[big, red, square]\n" + 
				"[big, red, circle]\n" + 
				"[big, green, square]\n" + 
				"[big, green, circle]\n" + 
				"[big, blue, square]\n" + 
				"[big, blue, circle]";
		runTest(iteratorMakers, expected);

		
		iteratorMakers =
				arrayList(
						() -> iterator(),
						() -> iterator("red", "green", "blue")
						);
		expected = "";
		runTest(iteratorMakers, expected);

		
		iteratorMakers =
				arrayList(
						() -> iterator("red", "green", "blue"),
						() -> iterator()
						);
		expected = "";
		runTest(iteratorMakers, expected);

		
		iteratorMakers =
				arrayList(
						() -> iterator("one-size-fits-all"),
						() -> iterator("red", "green", "blue")
						);
		expected =
				"[one-size-fits-all, red]\n" + 
				"[one-size-fits-all, green]\n" + 
				"[one-size-fits-all, blue]";
		runTest(iteratorMakers, expected);

		
		iteratorMakers =
				arrayList(() -> iterator("one-size-fits-all"));
		expected = "[one-size-fits-all]";
		runTest(iteratorMakers, expected);

		
		iteratorMakers = arrayList();
		expected = "[]";
		runTest(iteratorMakers, expected);
	}

	/**
	 * @param iteratorMakers
	 * @param expected
	 */
	protected void runTest(ArrayList<NullaryFunction<Iterator<String>>> iteratorMakers, String expected) {
		Iterator<ArrayList<String>> iterator;
		ArrayList<ArrayList<String>> list;
		String description;
		iterator = new CartesianProductIterator<String>(iteratorMakers);
		list = arrayListFrom(iterator);
		description = join("\n", list);
		assertEquals(expected, description);
	}
}
