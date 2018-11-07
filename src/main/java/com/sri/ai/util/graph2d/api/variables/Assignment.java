package com.sri.ai.util.graph2d.api.variables;

import java.util.ArrayList;

public interface Assignment {

	static Assignment assignment(SetOfVariables variables, ArrayList<? extends Value> values) {
		return new DefaultAssignment(variables, values);
	}
	
	SetOfVariables getSetOfVariables();
	
	/** Returns the value of the variable in this assignment */
	Value get(Variable variable);
	
	/**
	 * Returns a new assignment which includes the new variable -> value assignment
	 * Throws an error if variable already has an assigned value in this assignment.
	 */
	Assignment extend(Variable variable, Value value);

  /**
   * Returns a text string that contains each variable name and its associated string value
   * formatted so that it is suitable to be used as part of the graph's title.
   * @return formatted text
   */
	String toDisplayFormat();
	
}
