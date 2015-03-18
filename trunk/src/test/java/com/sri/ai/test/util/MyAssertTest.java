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

import static org.junit.Assert.fail;

import org.junit.Test;

import com.sri.ai.util.Util;

public class MyAssertTest {

	@Test
	public void testMyAssert() {
		System.clearProperty(Util.MY_ASSERT);
		regular();
		System.setProperty(Util.MY_ASSERT, "true");
		regular();
	}

	private void regular() {
		try {
			Util.myAssert(true,  "error message 1");
			Util.myAssert(false, "error message 2");
			if (System.getProperty(Util.MY_ASSERT) != null) {
				fail("Should have thrown an AssertionError but did not. System.getProperty(Util.MY_ASSERT) = " + System.getProperty(Util.MY_ASSERT));
			}
		}
		catch (AssertionError error) {
			if (error.getMessage().equals("error message 2")) {
				if (System.getProperty(Util.MY_ASSERT) != null) {
					// good, passed the unit test
				}
				else {
					fail("Threw assertion error when it should not have.");
				}
			}
			else {
				fail("Threw the wrong assertion error.");
			}
		}
		// good, passed the unit test
	}


	@Test
	public void testMyAssertWithNullaryFunctions() {
		System.clearProperty(Util.MY_ASSERT);
		nullary();
		System.setProperty(Util.MY_ASSERT, "true");
		nullary();
	}


	private void nullary() {
		try {
			Util.myAssert(() -> true , () -> "error message 1");
			Util.myAssert(() -> false, () -> "error message 2");
			if (System.getProperty(Util.MY_ASSERT) != null) {
				fail("Should have thrown an AssertionError but did not. System.getProperty(Util.MY_ASSERT) = " + System.getProperty(Util.MY_ASSERT));
			}
		}
		catch (AssertionError error) {
			if (error.getMessage().equals("error message 2")) {
				if (System.getProperty(Util.MY_ASSERT) != null) {
					// good, passed the unit test
				}
				else {
					fail("Threw assertion error when it should not have.");
				}
			}
			else {
				fail("Threw the wrong assertion error.");
			}
		}
		// good, passed the unit test
	}
}
