package com.sri.ai.util.graph2d.api.variables;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * An interface for immutable sets of variables.
 * We recommend implementations to be based on collections that are order-preserving (such as {@link LinkedHashSet}).
 *
 */
public interface SetOfVariables extends Iterable<Variable> {
	
	List<? extends Variable> getVariables();
	
	@SuppressWarnings("unchecked")
	default Iterator<Variable> iterator() {
		return (Iterator<Variable>) getVariables().iterator();
	}
	
	Variable getFirst();

	/** Returns the result of removing a given variable from the set of variables */
	SetOfVariables minus(Variable variable); // implement with Util.removeNonDestructively(Set<E>, E) since this object should be immutable
	
	static SetOfVariables setOfVariables(List<? extends Variable> variables) {
		return new DefaultSetOfVariables(variables);
	}

	static SetOfVariables setOfVariables(Variable... variables) {
		return setOfVariables(Arrays.asList(variables));
	}

	static SetOfVariables singletonSet(Variable variable) {
		return new DefaultSetOfVariables(variable);
	}

}
