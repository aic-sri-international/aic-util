package com.sri.ai.util.collect;

import com.sri.ai.util.base.NullaryFunction;

/**
 * A valued computed only when needed and then stored permanently.
 * 
 * @author braz
 * 
 */
public class LazyConstant<T> {
	
	private T value;
	private NullaryFunction<T> maker;
	
	public LazyConstant(NullaryFunction<T> maker) {
		this.maker = maker;
	}
	
	public T get() {
		if (value == null) {
			value = maker.apply();
		}
		return value;
	}
}