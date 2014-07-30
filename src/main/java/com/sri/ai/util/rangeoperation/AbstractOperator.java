package com.sri.ai.util.rangeoperation;

/** A basic implementation of {@link Operator} keeping a field for the current result. */
public abstract class AbstractOperator implements Operator {
	@Override
	public Object getResult() { return result; }
	protected Object result;
}