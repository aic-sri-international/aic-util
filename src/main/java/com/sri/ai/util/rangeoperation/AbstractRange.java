package com.sri.ai.util.rangeoperation;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.sri.ai.util.base.BinaryProcedure;

/**
 * Provides basic Range functionality, only leaving to the user the task of defining
 * {@link #evaluate()}, which should provide a new iterator over a range of values.
 */
public abstract class AbstractRange implements Range {
	/** Builds a range with a given variable name. */
	public AbstractRange(String name) {
		this.name = name;
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public void setEnvironment(DependencyAwareEnvironment environment) {
		this.environment = environment;
	}
	@Override
	public void initialize() {
		iterator = (Iterator<?>) apply();
	}
	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}
	@Override
	public void next() {
		Object value = iterator.next();
		environment.put(name, value);
		for (BinaryProcedure<String, Object> listener : listeners) {
			listener.apply(name, value);
		}
	}
	@Override
	public void addIterationListener(BinaryProcedure<String, Object> listener) {
		listeners.add(listener);
	}
	protected String name;
	protected Iterator<?> iterator;
	protected DependencyAwareEnvironment environment;
	protected Collection<BinaryProcedure<String, Object>> listeners = new LinkedList<BinaryProcedure<String, Object>>();
}