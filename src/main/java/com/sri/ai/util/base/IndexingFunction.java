package com.sri.ai.util.base;

import com.google.common.base.Function;

/**
 * A stateful function that maps a value <code>v</code> to the pair <code>(v, i)</code> if this is the <code>i</code>-th time the function is used. 
 * @author braz
 *
 * @param <T>
 */
public class IndexingFunction<T> implements Function<T, Pair<T, Integer>> {
	private int index = 0;

	@Override
	public Pair<T, Integer> apply(T t) {
		return Pair.<T, Integer>pair(t, index++);
	}
}