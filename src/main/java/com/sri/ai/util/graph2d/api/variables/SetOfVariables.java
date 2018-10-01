package com.sri.ai.util.graph2d.api.variables;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * An interface for immutable sets of variables.
 * We recommend implementations to be based on collections that are order-preserving (such as {@link LinkedHashSet}).
 *
 */
public interface SetOfVariables {
	
	List<? extends Variable> getVariables();
	
	Variable getFirst();

	/** Returns the result of removing a given variable from the set of variables */
	SetOfVariables minus(Variable variable); // implement with Util.removeNonDestructively(Set<E>, E) since this object should be immutable
	
	public static SetOfVariables setOfVariables(List<? extends Variable> variables) {
		// TODO implement default implementation class and create instance here
		return null;
	}

	public static SetOfVariables setOfVariables(Variable... variables) {
		return setOfVariables(Arrays.asList(variables));
	}

	public static SetOfVariables singletonSet(Variable variable) {
		// TODO implement default implementation class and create instance here
		return null;
	}

}
