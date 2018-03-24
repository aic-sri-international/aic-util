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

import static com.sri.ai.util.Util.forAll;
import static com.sri.ai.util.Util.getIndexOfFirstSatisfyingPredicateOrMinusOne;
import static com.sri.ai.util.Util.getIndexOfLastSatisfyingPredicateOrMinusOne;
import static com.sri.ai.util.Util.mapIntoList;
import static com.sri.ai.util.Util.mapIntoObjectArray;
import static com.sri.ai.util.collect.FunctionIterator.functionIterator;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;

/**
 * A {@link List} that is based on a base list of elements of type <code>E</code>,
 * but behaves like a list of elements of type <code>T</code>,
 * using a pair of functions to translate between them.
 *  
 * @author braz
 */
@Beta
public class FunctionList<E,T> implements List<T> {

	private List<E> base;
	private Function<T,E> in;
	private Function<E,T> out;
	
	public FunctionList(List<E> base, Function<T, E> in, Function<E, T> out) {
		super();
		this.base = base;
		this.in = in;
		this.out = out;
	}
	
	private E in(T t) {
		return in.apply(t);
	}
	
	private T out(E e) {
		return out.apply(e);
	}
	
	@Override
	public int size() {
		return base.size();
	}
	
	@Override
	public boolean isEmpty() {
		return base.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return indexOf(o) != -1;
	}
	
	@Override
	public Iterator<T> iterator() {
		return functionIterator(out, base.iterator());
	}
	
	@Override
	public Object[] toArray() {
		return mapIntoObjectArray(base, out);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V> V[] toArray(V[] a) {
		return (V[]) mapIntoObjectArray(base, out);
	}
	
	@Override
	public boolean add(T e) {
		return base.add(in.apply(e));
	}
	
	@Override
	public boolean remove(Object o) {
		ListIterator<E> it = base.listIterator();
		while (it.hasNext()) {
			E e = it.next();
			T t = out(e);
			if (t.equals(o)) {
				it.remove();
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return forAll(c, this::contains);
	}
	
	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean result = false;
		for (T t : c) {
			result = result || add(t);
		}
		return result;
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends T> collectionOfTs) {
		List<? extends E> es = mapIntoList(collectionOfTs, in);
		return base.addAll(index, es);
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean result = false;
		ListIterator<E> it = base.listIterator();
		while (it.hasNext()) {
			E e = it.next();
			T t = out(e);
			if (c.contains(t)) {
				it.remove();
				result = true;
			}
		}
		return result;
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		boolean result = false;
		ListIterator<E> it = base.listIterator();
		while (it.hasNext()) {
			E e = it.next();
			T t = out(e);
			if (!c.contains(t)) {
				it.remove();
				result = true;
			}
		}
		return result;
	}
	
	@Override
	public void clear() {
		base.clear();
	}
	
	@Override
	public T get(int index) {
		return out(base.get(index));
	}
	
	@Override
	public T set(int index, T element) {
		return out(base.set(index, in(element)));
	}
	
	@Override
	public void add(int index, T element) {
		base.add(index, in(element));
	}
	
	@Override
	public T remove(int index) {
		return out(base.remove(index));
	}
	
	@Override
	public int indexOf(Object o) {
		return getIndexOfFirstSatisfyingPredicateOrMinusOne(base, e -> out(e).equals(o));
	}
	
	@Override
	public int lastIndexOf(Object o) {
		return getIndexOfLastSatisfyingPredicateOrMinusOne(base, e -> out(e).equals(o));
	}
	
	@Override
	public ListIterator<T> listIterator() {
		throw new UnsupportedOperationException("ListIterator for " + FunctionList.class + " not implemented yet, but it is possible to do so.");
	}
	
	@Override
	public ListIterator<T> listIterator(int index) {
		throw new UnsupportedOperationException("ListIterator for " + FunctionList.class + " not implemented yet, but it is possible to do so.");
	}
	
	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException("subList for " + FunctionList.class + " not implemented yet, but it is possible to do so.");
	}
}
