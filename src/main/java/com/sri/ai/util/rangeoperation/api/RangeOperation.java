package com.sri.ai.util.rangeoperation.api;

import com.sri.ai.util.rangeoperation.library.operators.Concatenate;

/**
 * A combination of {@link Operator} and {@link Range}.
 */
public class RangeOperation {

	protected Operator operator;
	protected Range range;

	public RangeOperation(Operator operator, Range range) {
		this.operator = operator;
		this.range = range;
	}

	public RangeOperation(Range range) {
		this(new Concatenate(), range);
	}

	public Range getRange() {
		return range;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setEnvironment(DependencyAwareEnvironment environment) { 
		getRange().setEnvironment(environment); 
	}
	
	public void initialize() { 
		getRange().initialize(); 
	}
	
	public boolean hasNext() { 
		return getRange().hasNext(); 
	}
	
	public void next()       { 
		getRange().next(); 
	}
}