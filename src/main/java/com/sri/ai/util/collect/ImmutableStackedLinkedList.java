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
import static com.sri.ai.util.Util.join;
import static com.sri.ai.util.collect.NestedIterator.nestedIterator;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.google.common.annotations.Beta;
import com.sri.ai.util.Util;

/**
 * An implementation of an immutable {@link List}
 * sharing elements with a pre-existing given list.
 * The immutability allows a faster implementation
 * through caching, as opposed to non-immutable {@link StackedLinkedList}.
 *
 * @author braz
 */
@Beta
public class ImmutableStackedLinkedList<E> implements List<E> {
	
	private List<E> base;
	private List<E> extension;
	
	public ImmutableStackedLinkedList(E top, List<E> base) {
		this.base = base;
		this.extension = new LinkedList<E>();
		this.extension.add(top);
	}
	
	public ImmutableStackedLinkedList(List<E> top, List<E> base) {
		this.base = base;
		this.extension = top;
	}
	
	public List<E> getBase() {
		return base;
	}

	private boolean sizeIsCached = false;
	private int cachedSize;
	
	@Override
	public int size() {
		if ( ! sizeIsCached) {
			cachedSize = base.size() + extension.size();
			sizeIsCached = true;
		}
		return cachedSize;
	}

	private boolean isEmptyIsCached = false;
	private boolean cachedIsEmpty;
	
	@Override
	public boolean isEmpty() {
		if ( ! isEmptyIsCached) {
			cachedIsEmpty = base.isEmpty() && extension.isEmpty();
			isEmptyIsCached = true;
		}
		return cachedIsEmpty;
	}

	@Override
	public boolean contains(Object o) {
		boolean result = extension.contains(o) || base.contains(o);
		return result;
	}

	@Override
	public Iterator<E> iterator() {
		return new NestedIterator<E>(base, extension);
	}

	private Object[] array;
	
	@Override
	public Object[] toArray() {
		if (array == null) {
			array = new Object[size()];
			base.toArray(array);
			int i = base.size();
			for (E element : extension) {
				array[i++] = element;
			}
		}
		return array;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		T[] result;
		
		if (a.length < size()) {
			result = (T[]) new Object[size()];
		}
		else {
			result = a;
		}
		
		base.toArray(result);
		int i = base.size();
		for (E element : extension) {
			result[i++] = (T) element;
		}
		
		for (; i != result.length; i++) {
			result[i] = null;
		}
		
		return result;
	}

	@Override
	public boolean add(E e) {
		throw new UnsupportedOperationException("add method not supported for immutable stacked linked lists.");
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("remove method not supported for immutable stacked linked lists.");
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		boolean result = forAll(c, this::contains);
		return result;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException("addAll method not supported for immutable stacked linked lists.");
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException("addAll method not supported for immutable stacked linked lists.");
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("removeAll method not supported for immutable stacked linked lists.");
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("retainAll not supported for " + getClass());
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException("clear method not supported for immutable stacked linked lists.");
	}

	@Override
	public E get(int index) {
		E result;
		if (index < base.size()) {
			result = base.get(index);
		}
		else {
			result = extension.get(index - base.size());
		}
		return result;
	}

	@Override
	public E set(int index, E element) {
		throw new UnsupportedOperationException("set method not supported for immutable stacked linked lists.");
	}

	@Override
	public void add(int index, E element) {
		throw new UnsupportedOperationException("add method not supported for immutable stacked linked lists.");
	}

	@Override
	public E remove(int index) {
		throw new UnsupportedOperationException("remove method not supported for immutable stacked linked lists.");
	}

	@Override
	public int indexOf(Object o) {
		int result = base.indexOf(o);
		if (result == -1) {
			result = extension.indexOf(o) + base.size();
		}
		return result;
	}

	@Override
	public int lastIndexOf(Object o) {
		int result = extension.indexOf(o);
		if (result == -1) {
			result = base.indexOf(o);
		}
		else {
			result = result + base.size();
		}
		return result;
	}

	@Override
	public ListIterator<E> listIterator() {
		throw new UnsupportedOperationException("List iterators not supported for " + getClass());
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		throw new UnsupportedOperationException("List iterators not supported for " + getClass());
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException("subList not supported for " + getClass());
	}
	
	@Override
	public String toString() {
		return "[" + join(", ", nestedIterator(base, extension)) + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		
		if ( !(o instanceof List)) {
			return false;
		}
		
		@SuppressWarnings("unchecked")
		List<E> oList = (List<E>) o;
		
		if (oList.size() != size()) {
			return false;
		}

		Iterator<E> i1 = iterator();
		Iterator<E> i2 = oList.iterator();
		while (i1.hasNext()) {
			if ( ! Util.equals(i1.next(), i2.next())) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
	     int hashCode = 1;
	     for (E e : this) {
	         hashCode = 31*hashCode + (e == null ? 0 : e.hashCode());
	     }
	     return hashCode;
	}
}
