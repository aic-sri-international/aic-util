package com.sri.ai.util.graph2d.api.variables;

import static com.sri.ai.util.Util.in;
import static com.sri.ai.util.Util.mapIntoList;
import static com.sri.ai.util.collect.FunctionIterator.functionIterator;
import static com.sri.ai.util.graph2d.api.variables.Assignment.assignment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Function;
import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.collect.CartesianProductIterator;

/**
 * An interface for immutable sets of variables.
 * We recommend implementations to be based on collections that are order-preserving (such as {@link LinkedHashSet}).
 *
 */
public interface SetOfVariables {
	
	List<? extends Variable> getVariables();
	
	Variable getFirst();
	
	default Iterable<Assignment> assignments() {
		
		List<NullaryFunction<Iterator<Value>>> iteratorMakers = mapIntoList(getVariables(), makeIteratorMaker());
		
		Iterator<ArrayList<Value>> cartesianProductIterator = new CartesianProductIterator<>(iteratorMakers);
		
		Iterator<Assignment> assignmentsIterator = functionIterator(cartesianProductIterator, valuesArray -> assignment(this, valuesArray));
		
		Iterable<Assignment> assignmentsIterable = in(assignmentsIterator);
		
		return assignmentsIterable;
	}

	default Function<Variable, NullaryFunction<Iterator<Value>>> makeIteratorMaker() {
		return v -> () -> v.valuesIterator();
	}

	/** Returns the result of removing a given variable from the set of variables */
	SetOfVariables minus(Variable variable); // implement with Util.removeNonDestructively(Set<E>, E) since this object should be immutable
	
	public static SetOfVariables setOfVariables(List<? extends Variable> variables) {
		// TODO implement default implementation class and create instance here
		return null;
	}

	public static SetOfVariables singletonSet(Variable variable) {
		// TODO implement default implementation class and create instance here
		return null;
	}

}
