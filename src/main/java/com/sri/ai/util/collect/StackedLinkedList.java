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
import static com.sri.ai.util.Util.thereExists;
import static com.sri.ai.util.collect.NestedIterator.nestedIterator;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.google.common.annotations.Beta;

/**
 * An implementation of {@link List}
 * sharing elements with a pre-existing given list.
 * <p>
 * This works by receiving the base list at construction time and keeping a linked list of additional elements.
 * <p>
 * Modification of the base list is not allowed and
 * will throw a {@link UnsupportedOperationException}.
 *
 * @author braz
 */
@Beta
public class StackedLinkedList<E> implements List<E> {

	private List<E> base;
	private List<E> extension;
	
	public StackedLinkedList(List<E> base) {
		this.base = base;
		this.extension = new LinkedList<E>();
	}
	
	public StackedLinkedList(E top, List<E> base) {
		this(base);
		this.extension.add(top);
	}
	
	public List<E> getBase() {
		return base;
	}
	
	@Override
	public int size() {
		return base.size() + extension.size();
	}

	@Override
	public boolean isEmpty() {
		boolean result = base.isEmpty() && extension.isEmpty();
		return result;
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

	@Override
	public Object[] toArray() {
		@SuppressWarnings("unchecked")
		E[] result = (E[]) new Object[size()];
		base.toArray(result);
		int i = base.size();
		for (E element : extension) {
			result[i++] = element;
		}
		return result;
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
		boolean result = extension.add(e);
		return result;
	}

	@Override
	public boolean remove(Object o) {
		if (base.contains(o)) {
			throw new UnsupportedOperationException("Cannot remove objects in base list of " + getClass());
		}
		boolean result = extension.remove(o);
		return result;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		boolean result = forAll(c, this::contains);
		return result;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean result = extension.addAll(c);
		return result;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		if (index < base.size()) {
			throw new UnsupportedOperationException("Cannot add objects to base list of " + getClass() + ". Base size is " + base.size() + " and given index is " + index);
		}
		boolean result = extension.addAll(index - base.size(), c);
		return result;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean result = thereExists(c, this::remove);
		return result;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("retailAll not supported for " + getClass());
	}

	@Override
	public void clear() {
		if (base.isEmpty()) {
			extension.clear();
		}
		else {
			throw new UnsupportedOperationException("Cannot clear a " + getClass() + " if base list is not empty.");
		}
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
		E result;
		if (index < base.size()) {
			throw new UnsupportedOperationException("Cannot set elements to base list of " + getClass() + ". Base size is " + base.size() + " and given index is " + index);
		}
		else {
			result = extension.set(index - base.size(), element);
		}
		return result;
	}

	@Override
	public void add(int index, E element) {
		if (index < base.size()) {
			throw new UnsupportedOperationException("Cannot add elements to base list of " + getClass() + ". Base size is " + base.size() + " and given index is " + index);
		}
		else {
			extension.add(index - base.size(), element);
		}
	}

	@Override
	public E remove(int index) {
		E result;
		if (index < base.size()) {
			throw new UnsupportedOperationException("Cannot remove elements from base list of " + getClass() + ". Base size is " + base.size() + " and given index is " + index);
		}
		else {
			result = extension.remove(index - base.size());
			return result;
		}
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
}
