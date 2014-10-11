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
package com.sri.ai.test.util.functionalsequence;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.sri.ai.util.Util;
import com.sri.ai.util.functionalsequence.AbstractFunctionalSequence;

public class FunctionalSequenceTest {

	public static class AllMonarchs extends AbstractFunctionalSequence<String, String> {

		@SuppressWarnings("unchecked")
		private List<Iterator<String>> arguments =
				Util.list(
						Util.iterator("Henry III", "Henry IV", "Henry V"),
						Util.iterator("Louis XI", "Louis XII", "Louis XIII", "Louis XIV"),
						Util.iterator("Isabella I", "Isabella II")
						);

		protected List<Iterator<String>> getArguments() {
			return arguments;
			// the reason we do not construct the value of 'arguments' inside here is
			// that they should be constructed only once per AbstractFunctionalSequence instance object.
		}

		@Override
		protected String initialValue() {
			return "Pre-monarchy state";
		}

		@Override
		protected String computeFunction() {
			List<String> result = new LinkedList<String>();
			for (Iterator<String> argument : getArguments()) {
				result.add(getCurrentArgumentValue(argument));
			}
			return Util.join(result);
		}
		
	}
	
	public static class AllMonarchsWithFinalValueDetectionOnHenryIVLouisXIIsabellaI extends AllMonarchs {
		@Override
		protected boolean isFinalValue(String value) {
			boolean result = value.equals("Henry IV, Louis XI, Isabella I");
			return result;
		}
	}
	
	public static class AllMonarchsWithNoPreMonarchyState extends AllMonarchs {
		@Override
		protected String initialValue() {
			return null;
		}
	}
	
	public static class AllMonarchsWithNoMonarchs extends AllMonarchs {
		@SuppressWarnings("unchecked")
		private List<Iterator<String>> arguments =
				Util.list(
						Util.<String>iterator(),
						Util.<String>iterator(),
						Util.<String>iterator()
						);

		@Override
		protected List<Iterator<String>> getArguments() {
			return arguments;
		}
	}
	
	public static class AllMonarchsWithNoPreMonarchyStateAndNoMonarchs extends AllMonarchs {
		@Override
		protected String initialValue() {
			return null;
		}

		@SuppressWarnings("unchecked")
		private List<Iterator<String>> arguments =
				Util.list(
						Util.<String>iterator(),
						Util.<String>iterator(),
						Util.<String>iterator()
						);

		@Override
		protected List<Iterator<String>> getArguments() {
			return arguments;
		}
	}
	
	public static class AllMonarchsWithOnlyOneDinasty extends AllMonarchs {
		@SuppressWarnings("unchecked")
		private List<Iterator<String>> arguments =
				Util.list(
						Util.iterator("Henry III", "Henry IV", "Henry V"),
						Util.<String>iterator(),
						Util.<String>iterator()
						);
		// even though we have the Henrys, there will be no computed values because
		// we have no values for the remaining arguments.

		@Override
		protected List<Iterator<String>> getArguments() {
			return arguments;
		}
	}
	
	@Test
	public void testFunctionalSequence() {
		AllMonarchs allMonarchs;
		List<String> range;

		allMonarchs = new AllMonarchs();
		range = new LinkedList<String>();
		Util.addAll(range, allMonarchs);
		assertEquals(
				Util.list(
						"Pre-monarchy state",
						"Henry III, Louis XI, Isabella I",
						"Henry IV, Louis XI, Isabella I",
						"Henry V, Louis XI, Isabella I",
						"Henry V, Louis XII, Isabella I",
						"Henry V, Louis XIII, Isabella I",
						"Henry V, Louis XIV, Isabella I",
						"Henry V, Louis XIV, Isabella II"),
				range
		);

		allMonarchs = new AllMonarchsWithFinalValueDetectionOnHenryIVLouisXIIsabellaI();
		range = new LinkedList<String>();
		Util.addAll(range, allMonarchs);
		assertEquals(
				Util.list(
						"Pre-monarchy state",
						"Henry III, Louis XI, Isabella I",
						"Henry IV, Louis XI, Isabella I"),
				range
		);

		allMonarchs = new AllMonarchsWithNoPreMonarchyState();
		range = new LinkedList<String>();
		Util.addAll(range, allMonarchs);
		assertEquals(
				Util.list(
//						"Pre-monarchy state", // No pre-monarchy state
						"Henry III, Louis XI, Isabella I",
						"Henry IV, Louis XI, Isabella I",
						"Henry V, Louis XI, Isabella I",
						"Henry V, Louis XII, Isabella I",
						"Henry V, Louis XIII, Isabella I",
						"Henry V, Louis XIV, Isabella I",
						"Henry V, Louis XIV, Isabella II"),
				range
		);

		allMonarchs = new AllMonarchsWithNoMonarchs();
		range = new LinkedList<String>();
		Util.addAll(range, allMonarchs);
		assertEquals(
				Util.list("Pre-monarchy state"),
				range
		);

		allMonarchs = new AllMonarchsWithNoPreMonarchyStateAndNoMonarchs();
		range = new LinkedList<String>();
		Util.addAll(range, allMonarchs);
		List<String> emptyList = Collections.emptyList();
		assertEquals(
				emptyList,
				range
		);

		allMonarchs = new AllMonarchsWithOnlyOneDinasty();
		range = new LinkedList<String>();
		Util.addAll(range, allMonarchs);
		assertEquals(
				Util.list("Pre-monarchy state"),
				range
		);
	}
}
