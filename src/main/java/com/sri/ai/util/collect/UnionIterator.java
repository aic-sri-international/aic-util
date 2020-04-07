package com.sri.ai.util.collect;

import static com.sri.ai.util.collect.FunctionIterator.functionIterator;

import java.util.Iterator;

/**
 * Iterates over the elements of a sequence of iterators.
 * 
 * @author braz
 */
public class UnionIterator<T> extends EZIterator<T> {

	private Iterator<? extends Iterator<? extends T>> iteratorsIterator;
	private Iterator<? extends T> currentIterator;

	public UnionIterator(Iterator<? extends Iterator<? extends T>> iteratorsIterator) {
		this.iteratorsIterator = iteratorsIterator;
		this.currentIterator = null;
	}
	
	public UnionIterator(Iterable<? extends Iterable<? extends T>> iterablesIterable) {
		this(functionIterator(iterablesIterable, iterable -> iterable.iterator()));
	}
	
	@Override
	protected T calculateNext() {
		while (currentIterator == null || !currentIterator.hasNext()) {
			if (iteratorsIterator.hasNext()) {
				currentIterator = iteratorsIterator.next();
			}
			else {
				return null;
			}
		}
		return currentIterator.next();
	}

}
