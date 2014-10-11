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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.sri.ai.util.Util;
import com.sri.ai.util.functionalsequence.AbstractFunctionalRefiner;
import com.sri.ai.util.functionalsequence.IteratorRefiner;
import com.sri.ai.util.functionalsequence.Refiner;
import com.sri.ai.util.functionalsequence.RefinerIterator;

public class FunctionalRefinerTest {

	/** Returns the received arguments in an iterator refiner. */
	public static IteratorRefiner<String> argument(String... elements) {
		return new IteratorRefiner<String>(Arrays.asList(elements).iterator());
	}

	public static <T extends Refiner<String>> void addAll(List<String> list, T refiner) {
		Util.addAll(list, new RefinerIterator<String>(refiner));
	}
	
	public static class AllMonarchs extends AbstractFunctionalRefiner<String, String> {

		public AllMonarchs() {
			super("Pre-monarchy state");
		}

		@SuppressWarnings("unchecked")
		private List<IteratorRefiner<String>> arguments =
		Util.list(
				argument("Henry III", "Henry IV", "Henry V"),
				argument("Louis XI", "Louis XII", "Louis XIII", "Louis XIV"),
				argument("Isabella I", "Isabella II")
				);

		protected List<IteratorRefiner<String>> getArguments() {
			return arguments;
			// the reason we do not construct the value of 'arguments' inside here is
			// that they should be constructed only once per AbstractFunctionalRefiner instance object.
		}

		@Override
		protected String computeFunction() {
			List<String> result = new LinkedList<String>();
			for (Refiner<String> argument : getArguments()) {
				result.add(getCurrentArgumentValue(argument));
			}
			return Util.join(result);
		}

	}

	public static class AllMonarchsWithFinalValueDetectionOnHenryIVLouisXIIsabellaI extends AllMonarchs {
		@Override
		protected String computeFunction() {
			String result = super.computeFunction();
			if (result.equals("Henry IV, Louis XI, Isabella I")) {
				knownToBeOver = true;
			}
			return result;
		}
	}
	
	public static class AllMonarchsWithNoMonarchs extends AllMonarchs {
		@SuppressWarnings("unchecked")
		private List<IteratorRefiner<String>> arguments =
				Util.list(
						argument("none"),
						argument("none"),
						argument("none")
						);

		@Override
		protected List<IteratorRefiner<String>> getArguments() {
			return arguments;
		}
	}
	
	public static class AllMonarchsWithOnlyOneDinasty extends AllMonarchs {
		@SuppressWarnings("unchecked")
		private List<IteratorRefiner<String>> arguments =
				Util.list(
						argument("Henry III", "Henry IV", "Henry V"),
						argument("none"),
						argument("none")
						);
		// even though we have the Henrys, there will be no computed values because
		// we have no values for the remaining arguments.

		@Override
		protected List<IteratorRefiner<String>> getArguments() {
			return arguments;
		}
	}
	
	@Test
	public void testFunctionalRefiners() {
		AllMonarchs allMonarchs;
		List<String> range;

		allMonarchs = new AllMonarchs();
		range = new LinkedList<String>();
		addAll(range, allMonarchs);
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
		addAll(range, allMonarchs);
		assertEquals(
				Util.list(
						"Pre-monarchy state",
						"Henry III, Louis XI, Isabella I",
						"Henry IV, Louis XI, Isabella I"),
				range
		);

		allMonarchs = new AllMonarchsWithNoMonarchs();
		range = new LinkedList<String>();
		addAll(range, allMonarchs);
		assertEquals(
				Util.list("Pre-monarchy state",
						"none, none, none"),
				range
		);

		allMonarchs = new AllMonarchsWithOnlyOneDinasty();
		range = new LinkedList<String>();
		addAll(range, allMonarchs);
		assertEquals(
				Util.list("Pre-monarchy state",
						"Henry III, none, none",
						"Henry IV, none, none",
						"Henry V, none, none"),
				range
		);
	}
}
