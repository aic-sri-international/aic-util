package com.sri.ai.util.collect;

import java.util.ListIterator;

/**
 * A {@link ListIterator} that inverts the direction of a base list iterator,
 * that is, moves the base iterator forward when moving backwards and vice-versa.
 * This is useful for operating on lists backwards while using methods that operate forward (using {@link Iterator#next()}),
 * and vice-versa.
 * 
 * @author braz
 *
 * @param <E>
 */
public class ReverseListIterator<E> extends ReverseIterator<E> implements ListIterator<E> {

	private ListIterator<E> backIterator;
	
	public ReverseListIterator(ListIterator<E> backIterator) {
		super(backIterator);
	}

	@Override
	public boolean hasPrevious() {
		return backIterator.hasNext();
	}

	@Override
	public E previous() {
		return backIterator.next();
	}

	@Override
	public int nextIndex() {
		return backIterator.previousIndex();
	}

	@Override
	public int previousIndex() {
		return backIterator.nextIndex();
	}

	@Override
	public void set(E e) {
		backIterator.set(e);
	}

	@Override
	public void add(E e) {
		backIterator.add(e);
	}

}
