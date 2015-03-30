package com.sri.ai.util.collect;

import java.util.Collection;
import java.util.Iterator;

import com.sri.ai.util.base.NullaryFunction;

/**
 * A {@link Collection} implementation meant to behave as a copy of a given base B, which must be a Collection type.
 * However, it only performs the copy upon modification, using the base Collection for reading meanwhile.
 * When the copy is performed, the default constructor of a given Collection implementation will be used.
 * IMPORTANT: this collection will reflect any changes made to the base Collection while it is not copied,
 * so this is best used on Collections not meant to change after being used as a base.
 */
public class CopyOnWriteCollection<E, B extends Collection<E>> implements Collection<E> {

	protected B baseCollection;
	private NullaryFunction<B> maker;
	
	public CopyOnWriteCollection(B baseCollection, NullaryFunction<B> maker) {
		this.baseCollection = baseCollection;
		this.maker = maker;
	}
	
	/**
	 * Returns the base collection being reflected, which will <i>not</i> be
	 * the originally given one if a copy has already been performed.
	 * @return
	 */
	public Collection<E> getBaseCollection() {
		return baseCollection;
	}
	
	private void copy() {
		B newBaseCollection = null;
		newBaseCollection = maker.apply();
		newBaseCollection.addAll(baseCollection);
		baseCollection = newBaseCollection;
	}
	
	@Override
	public boolean add(E e) {
		copy();
		return baseCollection.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		copy();
		return baseCollection.addAll(c);
	}

	@Override
	public void clear() {
		copy();
		baseCollection.clear();
	}

	@Override
	public boolean contains(Object o) {
		return baseCollection.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return baseCollection.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return baseCollection.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		final Iterator<E> baseIterator = baseCollection.iterator();
		Iterator<E> result = new Iterator<E>() {

			@Override
			public boolean hasNext() {
				return baseIterator.hasNext();
			}

			@Override
			public E next() {
				return baseIterator.next();
			}

			@Override
			public void remove() { // overriding for the cases in which base collection does support 'remove'.
				throw new UnsupportedOperationException("Iterator's remove method not supported for CopyOnWriteCollection instances.");
			}
		};
		return result;
	}

	@Override
	public boolean remove(Object o) {
		copy();
		return baseCollection.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		copy();
		return baseCollection.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		copy();
		return baseCollection.retainAll(c);
	}

	@Override
	public int size() {
		return baseCollection.size();
	}

	@Override
	public Object[] toArray() { //no need to copy since Collection.toArray returns a freshly instantiated array.
		return baseCollection.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return baseCollection.toArray(a);
	}
	
	@Override
	public boolean equals(Object another) {
		if (another instanceof CopyOnWriteCollection) {
			another = ((CopyOnWriteCollection) another).baseCollection;
		}
		return baseCollection.equals(another);
	}
	
	@Override
	public int hashCode() {
		return baseCollection.hashCode();
	}
	
	@Override
	public String toString() {
		return baseCollection.toString();
	}
}