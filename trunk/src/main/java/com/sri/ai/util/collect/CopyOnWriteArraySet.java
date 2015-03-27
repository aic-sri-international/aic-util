package com.sri.ai.util.collect;

import java.util.ListIterator;

/**
 * A specialization of {@link CopyOnWriteCollection} for {@link ArraySet}.
 */
public class CopyOnWriteArraySet<E> extends CopyOnWriteCollection<E, ArraySet<E>> implements ArraySet<E> {

	public CopyOnWriteArraySet(ArraySet<E> baseArraySet, Class classForNewlyCopiedBaseCollections) {
		super(baseArraySet, classForNewlyCopiedBaseCollections);
	}

	@Override
	public ListIterator<E> listIterator() {
		return baseCollection.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return baseCollection.listIterator(index);
	}

	@Override
	public E get(int index) {
		return baseCollection.get(index);
	}

	@Override
	public void set(int index, E element) {
		baseCollection.set(index, element);
	}
}