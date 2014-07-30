package com.sri.ai.util.rangeoperation;

/** Represents a cumulative operator by defining initializing and incrementing functions. */
public interface Operator {
	/** Prepares operator for another round of cumulative operations. */
	public void initialize();
	/** Returns current result. */
	public Object getResult();
	/** Increments result by another value. */
	public void increment(Object value);
}