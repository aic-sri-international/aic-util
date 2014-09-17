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
package com.sri.ai.util.collect;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.annotations.Beta;

/**
 * A basic implementation of a disjoint-set data structure for maintaining a
 * collection of disjoint dynamic sets. This is based on the algorithm
 * description in Chapter 21 of 'Introduction to Algorithm 2nd Edition' (by
 * Cormen, Leriserson, Rivest, and Stein) for Disjoint-sets taking into account
 * some of the heuristic ideas described. However, this implementation relies on
 * the constant time performance of the HashMap's get and put operations, and
 * HashSet's add, remove, contains and size operations as an alternative
 * implementation approach. This provides a cleaner separation between the
 * elements and the implementation (i.e. the idea of a representative for a
 * particular disjoint set is not used) and an easier to use API (i.e. direct
 * access to the disjoint set that an element belongs to and no need to
 * understand how the internals work with respect to a representative) but will
 * not perform as fast (proper analysis required but likely O(m + n lg n) as
 * detailed in Theorem 21.1 on page 504 of 'Introduction to Algorithm 2nd
 * Edition').
 * 
 * Note: the internal implementation of this class can likely be improved to use
 * all the techniques outlined in section 21.3 in order to get optimal
 * performance.
 * 
 * @author oreilly
 * 
 * @param <E>
 *            the type of elements to be contained by the disjoint sets.
 */
@Beta
public class DisjointSets<E> {

	private Map<E, Set<E>> elementToSet = new LinkedHashMap<E, Set<E>>();
	private Set<Set<E>>    disjointSets = new LinkedHashSet<Set<E>>();

	/**
	 * Default Constructor.
	 */
	public DisjointSets() {

	}

	/**
	 * Constructor.
	 * 
	 * @param initialElements
	 *            a collection of elements, each of which will be assigned to
	 *            their own disjoint set via makeSet().
	 */
	public DisjointSets(Collection<E> initialElements) {
		for (E element : initialElements) {
			makeSet(element);
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param elements
	 *            a collection of elements, each of which will be assigned to
	 *            their own disjoint set via makeSet().
	 */
	public DisjointSets(E... elements) {
		for (E element : elements) {
			makeSet(element);
		}
	}

	/**
	 * Create a disjoint set for the element passed in. This method should be
	 * called for all elements before any of the other API methods are called
	 * (i.e. construct a disjoint set for each element first and then union them
	 * together as appropriate).
	 * 
	 * @param element
	 *            the element for which a new disjoint set should be
	 *            constructed.
	 */
	public void makeSet(E element) {
		if (!elementToSet.containsKey(element)) {
			// Note: It is necessary to use an identity based hash set
			// whose equal and hashCode method are based on the Sets
			// identity and not its elements as we are adding
			// this set to a set but changing its values as unions
			// occur.
			Set<E> set = new IdentityHashSet<E>();
			set.add(element);
			elementToSet.put(element, set);
			disjointSets.add(set);
		}
	}

	/**
	 * Union two disjoint sets together if the arguments currently belong to two
	 * different sets.
	 * 
	 * @param element1
	 *        the first element to union together.
	 * @param element2
	 *        the second element to union together.
	 * @throws IllegalArgumentException
	 *             if element1 or element 2 is not already associated with a
	 *             disjoint set (i.e. makeSet() was not called for the argument
	 *             beforehand).
	 */
	public void union(E element1, E element2) {
		Set<E> set1 = elementToSet.get(element1);
		if (set1 == null) {
			throw new IllegalArgumentException(
					"element 1 is not associated with a disjoint set, call makeSet() first.");
		}
		Set<E> set2 = elementToSet.get(element2);
		if (set2 == null) {
			throw new IllegalArgumentException(
					"element 2 is not associated with a disjoint set, call makeSet() first.");
		}
		if (set1 != set2) {
			// simple weighted union heuristic
			if (set1.size() < set2.size()) {
				set2.addAll(set1);
				for (E element : set1) {
					disjointSets.remove(elementToSet.put(element, set2));
				}
			} 
			else {
				// i.e. set1 >= set2
				set1.addAll(set2);
				for (E element : set2) {
					Set<E> pe = elementToSet.put(element, set1);
					disjointSets.remove(pe);
				}
			}
		}
	}

	/**
	 * Find the disjoint set that an element belongs to.
	 * 
	 * @param element
	 *            the element whose disjoint set is being sought.
	 * @return the disjoint set for the element or null if makeSet(element) was
	 *         not previously called.
	 */
	public Set<E> find(E element) {
		// Note: Instantiate normal sets to ensure IdentityHashSet
		// is not exposed outside of this class.
		// This also ensures the internal logic cannot
		// be corrupted externally due to changing sets.
		return new LinkedHashSet<E>(elementToSet.get(element));
	}

	/**
	 * 
	 * @return a map for each element and the corresponding disjoint set that it
	 *         belongs to.
	 */
	public Map<E, Set<E>> getElementToDisjointSet() {
		// Note: Instantiate normal sets to ensure IdentityHashSet
		// is not exposed outside of this class.
		// This also ensures the internal logic cannot
		// be corrupted externally due to changing sets.
		Map<E, Set<E>> result = new LinkedHashMap<E, Set<E>>();
		for (Map.Entry<E, Set<E>> entry : elementToSet.entrySet()) {
			result.put(entry.getKey(), new LinkedHashSet<E>(entry.getValue()));
		}
		return result;
	}

	/**
	 * 
	 * @return the set of disjoint sets being maintained.
	 */
	public Set<Set<E>> getDisjointSets() {
		// Note: Instantiate normal sets to ensure IdentityHashSet
		// is not exposed outside of this class.
		// This also ensures the internal logic cannot
		// be corrupted externally due to changing sets.
		Set<Set<E>> result = new LinkedHashSet<Set<E>>();
		Iterator<Set<E>> it = disjointSets.iterator();
		while (it.hasNext()) {
			result.add(new LinkedHashSet<E>(it.next()));
		}
		return result;
	}

	/**
	 * 
	 * @return the number of disjoint sets.
	 */
	public int numberDisjointSets() {
		return disjointSets.size();
	}

	/**
	 * Remove all the disjoint sets.
	 */
	public void clear() {
		elementToSet.clear();
		disjointSets.clear();
	}

	//
	// PRIVATE METHODS
	//

	// Override hashCode and equals so that
	// the Set itself and not its elements
	// are used to determine its hashCode
	// and equality.
	private class IdentityHashSet<H> extends HashSet<H> {
		private static final long serialVersionUID = 1L;

		@Override
		public int hashCode() {
			return System.identityHashCode(this);
		}

		@Override
		public boolean equals(Object o) {
			return this == o;
		}
	}
}
