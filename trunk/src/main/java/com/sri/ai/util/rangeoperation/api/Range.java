package com.sri.ai.util.rangeoperation.api;

import com.sri.ai.util.base.BinaryProcedure;
import com.sri.ai.util.base.NullaryFunction;

/**
 * A range can be thought of as a "virtual list" of values for a variable, based on a generator of values or an actual collection.
 * Technically, it is a {@link NullaryFunction} producing a new iterator upon request.
 * It must keep a {@link DependencyAwareEnvironment}
 * set by {@link #setEnvironment(DependencyAwareEnvironment)}
 * that is updated by the iterator's method {@link #next()}.
 * It may also receive {@link BinaryProcedure} listeners to be called
 * with variable and value pairs whenever {@link #next()} is invoked.
 */
public interface Range extends NullaryFunction {
	/** The variable set by this range. */
	public String getName();
	/** Informs the range which environment to use. */
	public void setEnvironment(DependencyAwareEnvironment environment);
	/** Makes range ready for iteration over variable values. */
	public void initialize();
	/** Indicates that there are still values to be iterated over. */
	public boolean hasNext();
	/** Go to the next value. */
	public void next();
	/** Add a binary procedure to be invoke upon iteration, with variable and value as parameters. */
	public void addIterationListener(BinaryProcedure<String, Object> listener);
}