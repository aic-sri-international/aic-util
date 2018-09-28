package com.sri.ai.util.graph2d.api.variables;

import java.util.ArrayList;

public interface Assignment {

	public static Assignment assignment(TupleOfVariables variables, ArrayList<Value> values) {
		// TODO implement default implementation class and create instance here
		return null;
	}
	
	TupleOfVariables getTupleOfVariables();
	
	/** Returns the value of the variable in this assignment */
	Value get(Variable variable);
	
	/**
	 * Returns a new assignment which includes the new variable -> value assignment
	 * Throws an error if variable already has an assigned value in this assignment.
	 */
	Assignment extend(Variable variable, Value value);
	
}
