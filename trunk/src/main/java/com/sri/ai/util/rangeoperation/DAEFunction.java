package com.sri.ai.util.rangeoperation;

import com.google.common.base.Function;

/**
 * An abstract class for functions computing entries of a {@link DependencyAwareEnvironment}.
 * 
 * @author braz
 */
public abstract class DAEFunction implements Function<DependencyAwareEnvironment, Object> {

	public abstract Object apply(DependencyAwareEnvironment environment);

	/**
	 * Indicates whether the DAEFunction is random (default implementation returns <code>false</code>,
	 * which requires it to be recomputed every time it is requested.
	 */
	public boolean isRandom() {
		return false;
	}
}
