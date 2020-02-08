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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.annotations.Beta;
import com.sri.ai.util.Util;
import com.sri.ai.util.base.Pair;

/**
 * Represents a many-to-many relation with amortized constant-time search for
 * the tuples of a given element.
 * 
 * @author braz
 */
@Beta
public class DefaultManyToManyRelation<A,B> implements ManyToManyRelation<A, B> {
	
	private Map<A, Collection<B>> fromAToItsBs = new LinkedHashMap<A, Collection<B>>();
	private Map<B, Collection<A>> fromBToItsAs = new LinkedHashMap<B, Collection<A>>();
	
	public DefaultManyToManyRelation() {
	}
	
	/**
	 * Given an iterable <code>as</code> of <code>A1</code>s and a function <code>bsOfAMaker</code> providing an iterable to <code>B1</code>s corresponding to a given <code>A1</code>,
	 * return a {@link ManyToManyRelation} between each element in <code>as</code> and its corresponding <code>B1</code> instances.
	 * @param as
	 * @param bsOfAMaker
	 * @return 
	 */
	public static <A1, B1> ManyToManyRelation<A1, B1> manyToManyRelation(Iterable<? extends A1> as, Function<? super A1, Iterable<? extends B1>> bsOfAMaker) {
		ManyToManyRelation<A1, B1> result = new DefaultManyToManyRelation<A1, B1>();
		for (A1 a : as) {
			for (B1 b : bsOfAMaker.apply(a)) {
				result.add(a, b);
			}
		}
		return result;
	}

	@Override
	public void add(A a, B b) {
		Util.addToCollectionValuePossiblyCreatingIt(fromAToItsBs, a, b, HashSet.class);
		Util.addToCollectionValuePossiblyCreatingIt(fromBToItsAs, b, a, HashSet.class);
	}
	
	@Override
	public void remove(A a, B b) {
		removeThisAFromAsOfThisB(a, b);
		removeThisBFromBsOfThisA(a, b);
	}
	
	/**
	 * Remove all tuples with <code>a</code> as first dimension.
	 * 
	 * @param a
	 *            the item used in the removal logic.
	 */
	@Override
	public void removeA(A a) {
		Collection<B> bsOfThisA = fromAToItsBs.get(a);
		
		if (bsOfThisA == null) { // a is not present
			return;
		}
		
		fromAToItsBs.remove(a); // we remove a from the list of As, but first we saved its Bs, which need to be updated as well.
		
		// Now we notify them.
		for (B b : bsOfThisA) {
			removeThisAFromAsOfThisB(a, b);
		}
	}

	/**
	 * Remove all tuples with elements in collection <code>as</code> as first
	 * dimension.
	 * 
	 * @param as
	 *            the items used in the removal logic.
	 */
	@Override
	public void removeAllAs(Collection<A> as) {
		for (A a : as) {
			removeA(a);
		}
	}
	
	/**
	 * Indicates whether an element <code>a</code> is present in the first
	 * dimension.
	 * 
	 * @param a
	 *            the item to test if its in the first dimension.
	 * @return true if the given argument is in the first dimension.
	 */
	@Override
	public boolean containsA(A a) {
		return fromAToItsBs.containsKey(a);
	}

	/**
	 * Remove all tuples with <code>b</code> as second dimension.
	 * 
	 * @param b
	 *            the argument to match on.
	 */
	@Override
	public void removeB(B b) {
		Collection<A> asOfThisB = fromBToItsAs.get(b);

		if (asOfThisB == null) { // b is not present
			return;
		}

		fromBToItsAs.remove(b); // we remove b from the list of Bs, but first we saved its As, which need to be updated as well.
		
		// Now we notify them.
		for (A a : asOfThisB) {
			removeThisBFromBsOfThisA(a, b);
		}
	}

	/**
	 * Remove all tuples with elements in collection <code>bs</code> as second
	 * dimension.
	 * 
	 * @param bs
	 *            the arguments to match on.
	 */
	@Override
	public void removeAllBs(Collection<B> bs) {
		for (B b : bs) {
			removeB(b);
		}
	}
	
	/**
	 * @param b
	 *        element to test for containment.
	 * @return true if element <code>b</code> is present in the second dimension. 
	 */
	@Override
	public boolean containsB(B b) {
		return fromBToItsAs.containsKey(b);
	}

	@Override
	public void clear() {
		fromAToItsBs.clear();
		fromBToItsAs.clear();
	}
	
	@Override
	public Collection<A> getAs() {
		return new LinkedHashSet<A>(fromAToItsBs.keySet());
	}

	@Override
	public Collection<B> getBs() {
		return new LinkedHashSet<B>(fromBToItsAs.keySet());
	}

	/**
	 * @param a
	 *            the element to relate to.
	 * @return collection of second-dimension elements relating to
	 *         <code>a</code>.
	 */
	@Override
	public Collection<B> getBsOfA(A a) {
		Collection<B> bsOfThisA = fromAToItsBs.get(a);
		if (bsOfThisA == null) {
			return new LinkedHashSet<B>();
		}
		return new LinkedHashSet<B>(bsOfThisA);
	}
	
	/**
	 * @param b
	 *         the element to relate to. 
 	 * @return collection of first-dimension elements relating to <code>b</code>. 
 	 */
	@Override
	public Collection<A> getAsOfB(B b) {
		Collection<A> asOfThisB = fromBToItsAs.get(b);
		if (asOfThisB == null) {
			return new LinkedHashSet<A>();
		}
		return new LinkedHashSet<A>(fromBToItsAs.get(b));
	}
	
	private void removeThisAFromAsOfThisB(A a, B b) {
		Collection<A> asOfThisB = fromBToItsAs.get(b);
		if (asOfThisB == null) {
			return;
		}
		asOfThisB.remove(a);
		if (asOfThisB.isEmpty()) { // this B is not in the relation anymore, so we remove it from the list of Bs.
			fromBToItsAs.remove(b);
		}
	}

	private void removeThisBFromBsOfThisA(A a, B b) {
		Collection<B> bsOfThisA = fromAToItsBs.get(a);
		if (bsOfThisA == null) {
			return;
		}
		bsOfThisA.remove(b);

		if (bsOfThisA.isEmpty()) { // this A is not in the relation anymore, so we remove it from the list of As.
			fromAToItsBs.remove(a);
		}
	}
	
	/**
	 * @return an iterator over the pairs in this relationship; the iterator is
	 *         <i>not</i> supported by the underlying relationship storage.
	 */
	@Override
	public Iterator<Pair<A,B>> iterator() {
		List<Pair<A,B>> list = new LinkedList<Pair<A,B>>();
		for (A a : getAs()) {
			for (B b : getBsOfA(a)) {
				list.add(new Pair<A,B>(a, b));
			}
		}
		return list.iterator();
	}
	
	@Override
	public DefaultManyToManyRelation clone() {
		DefaultManyToManyRelation result;
		try {
			result = (DefaultManyToManyRelation) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
		return result;
	}
}
