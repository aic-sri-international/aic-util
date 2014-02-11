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
package com.sri.ai.test.util.cache;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.sri.ai.util.base.BinaryFunction;
import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.base.Pair;
import com.sri.ai.util.cache.CacheMap;
import com.sri.ai.util.cache.FlexibleCacheMap;

public class FlexibleCacheMapTest {

	@Before
	public void setUp() throws Exception {
	}

	public static class Person {
		public Person(int nationality, int genes) {
			super();
			this.nationality = nationality;
			this.genes = genes;
		}
		int nationality;
		int genes;
		public String toString() {
			return "Person(" + nationality + ", " + genes + ")";
		}
		public int hashCode() {
			return nationality + genes;
		}
		public boolean equals(Object obj) {
			try {
				Person another = (Person) obj;
				return nationality == another.nationality && genes == another.genes;
			} catch (ClassCastException e) {
				return false;
			}
		}
	};

	public static class Medicine {
		public Medicine(int nationalDistributor, int activePrinciple) {
			super();
			this.nationalDistributor = nationalDistributor;
			this.activePrinciple = activePrinciple;
		}
		int nationalDistributor;
		int activePrinciple;
		public String toString() {
			return "Medicine(" + nationalDistributor + ", " + activePrinciple + ")";
		}
		public int hashCode() {
			return nationalDistributor + activePrinciple;
		}
		public boolean equals(Object obj) {
			try {
				Medicine another = (Medicine) obj;
				return nationalDistributor == another.nationalDistributor && activePrinciple == another.activePrinciple;
			} catch (ClassCastException e) {
				return false;
			}
		}
	};

	public static class FromPersonToGenes implements Function<Person, Integer> {
		@Override
		public Integer apply(Person personObj) {
			return personObj.genes;
		}
	}

	public static class FromPersonAndMedicineToGenesAndActivePrinciple implements BinaryFunction<Person, Medicine, Pair<Integer, Integer>> {
		@Override
		public Pair<Integer, Integer> apply(Person o1, Medicine o2) {
			return new Pair<Integer, Integer>(o1.genes, o2.activePrinciple);
		}
	}

	public static class FromPersonAndActivePrincipleToMedicine implements BinaryFunction<Person, Integer, Medicine> {
		@Override
		public Medicine apply(Person o1, Integer o2) {
			Medicine result = new Medicine(o1.nationality, o2);
			return result;
		}
	}

	
	@Test
	public void test() {
		final Set<Person> payRoll = new LinkedHashSet<Person>();
		NullaryFunction<Iterator<Person>> iteratorMaker = new NullaryFunction<Iterator<Person>>() {
			@Override
			public Iterator<Person> apply() {
				return payRoll.iterator();
			}
		};
		
		FlexibleCacheMap<Person, Medicine, Integer, Integer> cache =
			new FlexibleCacheMap<Person, Medicine, Integer, Integer>(CacheMap.NO_MAXIMUM_SIZE,
					new FromPersonToGenes(),
					new FromPersonAndMedicineToGenesAndActivePrinciple(),
					new FromPersonAndActivePrincipleToMedicine(),
					iteratorMaker,
					3);
		
		Person alice = new Person(1, 1);
		Person bob   = new Person(2, 1);
		Person carol = new Person(2, 3);
		Person daren = new Person(3, 4);
		
		Medicine med1_10 = new Medicine(1,10);
		Medicine med2_10 = new Medicine(2,10);
		Medicine med2_30 = new Medicine(2,30);
		
		cache.put(alice, med1_10);
		
		Medicine med;
		med = cache.get(alice);
		assertEquals(med1_10, med);
		
		// Even though bob has not been cached, alice has the same genes, so the medicine
		// should have the same active principle. However, bob has a different nationality
		// and his medicine will have the appropriate national distribution.
		med = cache.get(bob);
		assertEquals(med2_10, med);

		// The same does not happen to carol because there are no such genes in the cache.
		med = cache.get(carol);
		assert(med == null);

		// At this point, cache has one elements (alice's genes).
		assertEquals(1, cache.size());
		
		// Completing three "puts" to cache. This should trigger garbage collection.
		// Since garbage collection is based on the pay roll set and this will contain only alice,
		// cache should contain alice only after this.
		payRoll.add(alice);
		cache.put(bob,   med2_10);
		cache.put(carol, med2_30);
		assertEquals(1, cache.size());

		payRoll.add(alice);
		payRoll.add(bob);
		payRoll.add(carol);

		cache.put(alice, med1_10);
		cache.put(bob,   med2_10);
		cache.put(carol, med2_30);
		
		// The above triggers garbage collection again, but this time all items are in payroll, so they all stay.
		// At this point, cache has two elements (two types of genes).
		assertEquals(2, cache.size());

		// Now we cause another garbage collection, but this time no gene type in cache is present in payroll, so we get an empty cache.
		payRoll.clear();
		payRoll.add(daren);
		cache.put(alice, med1_10);
		cache.put(alice, med1_10);
		cache.put(alice, med1_10);
		assertEquals(0, cache.size());
	}
}
