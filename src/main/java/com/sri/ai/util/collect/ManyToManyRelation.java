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
import java.util.Iterator;

import com.sri.ai.util.base.Pair;

public interface ManyToManyRelation<A, B> {

	public void add(A a, B b);

	public void remove(A a, B b);

	/**
	 * Remove all tuples with <code>a</code> as first dimension.
	 * 
	 * @param a
	 *            the item used in the removal logic.
	 */
	public void removeA(A a);

	/**
	 * Remove all tuples with elements in collection <code>as</code> as first
	 * dimension.
	 * 
	 * @param as
	 *            the items used in the removal logic.
	 */
	public void removeAllAs(Collection<A> as);

	/**
	 * Indicates whether an element <code>a</code> is present in the first
	 * dimension.
	 * 
	 * @param a
	 *            the item to test if its in the first dimension.
	 * @return true if the given argument is in the first dimension.
	 */
	public boolean containsA(A a);

	/**
	 * Remove all tuples with <code>b</code> as second dimension.
	 * 
	 * @param b
	 *            the argument to match on.
	 */
	public void removeB(B b);

	/**
	 * Remove all tuples with elements in collection <code>bs</code> as second
	 * dimension.
	 * 
	 * @param bs
	 *            the arguments to match on.
	 */
	public void removeAllBs(Collection<B> bs);

	/**
	 * @param b
	 *        element to test for containment.
	 * @return true if element <code>b</code> is present in the second dimension. 
	 */
	public boolean containsB(B b);

	public void clear();

	public Collection<A> getAs();

	public Collection<B> getBs();

	/**
	 * @param a
	 *            the element to relate to.
	 * @return collection of second-dimension elements relating to
	 *         <code>a</code>.
	 */
	public Collection<B> getBsOfA(A a);

	/**
	 * @param b
	 *         the element to relate to. 
	 * @return collection of first-dimension elements relating to <code>b</code>. 
	 */
	public Collection<A> getAsOfB(B b);

	/**
	 * @return an iterator over the pairs in this relationship; the iterator is
	 *         <i>not</i> supported by the underlying relationship storage.
	 */
	public Iterator<Pair<A, B>> iterator();

}