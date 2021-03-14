package com.sri.ai.util.base;

import com.google.common.base.Function;

/**
 * A {@link Function} that always returns <code>null</code>.
 * 
 * @author braz
 *
 * @param <A>
 *            the argument type for the function.
 * @param <R>
 *            the return type for the function.
 */
public class Null<A, R> implements Function<A, R> {

	@Override
	public R apply(A input) {
		return null;
	}
}
