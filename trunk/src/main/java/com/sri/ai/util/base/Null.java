package com.sri.ai.util.base;

import com.google.common.base.Function;

/**
 * A {@link Function} that always returns <code>null</code>.
 * @author braz
 *
 * @param <T1>
 * @param <T2>
 */
public class Null<T1, T2> implements Function<T1, T2> {

	@Override
	public T2 apply(T1 input) {
		return null;
	}
}
