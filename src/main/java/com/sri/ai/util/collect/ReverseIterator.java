package com.sri.ai.util.collect;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * A {@link Iterator} that inverts the direction of a base list iterator,
 * that is, moves the base iterator backwards when iterating.
 * This is useful for operating on lists backwards while using methods that operate forward (using {@link Iterator#next()}),
 * and vice-versa.
 * 
 * @author braz
 *
 * @param <E>
 */
public class ReverseIterator<E> implements Iterator<E> {

	private ListIterator<E> backIterator;
	
	public ReverseIterator(ListIterator<E> backIterator) {
		this.backIterator = backIterator;
	}

	@Override
	public boolean hasNext() {
		return backIterator.hasPrevious();
	}

	@Override
	public E next() {
		return backIterator.previous();
	}

	@Override
	public void remove() {
		backIterator.remove();
	}

}
