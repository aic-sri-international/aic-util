package com.sri.ai.util.graph2d.api.variables;

import static com.sri.ai.util.Util.in;
import static com.sri.ai.util.Util.mapIntoList;
import static com.sri.ai.util.collect.FunctionIterator.functionIterator;
import static com.sri.ai.util.graph2d.api.variables.Assignment.assignment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.collect.CartesianProductIterator;

/**
 * An interface for immutable tuples of variables
 *
 */
public interface TupleOfVariables {
	
	List<? extends Variable> getVariables();
	
	Variable getFirst();
	
	default Iterable<Assignment> assignments() {
		
		List<NullaryFunction<Iterator<Value>>> iteratorMakers = mapIntoList(getVariables(), v -> () -> v.valuesIterator());
		
		Iterator<ArrayList<Value>> cartesianProductIterator = new CartesianProductIterator<>(iteratorMakers);
		
		Iterator<Assignment> assignmentsIterator = functionIterator(cartesianProductIterator, values -> assignment(this, values));
		
		Iterable<Assignment> assignmentsIterable = in(assignmentsIterator);
		
		return assignmentsIterable;
	}


	/** Returns the result of removing a given variable from the tuple of variables */
	TupleOfVariables minus(Variable variable); // implement with Util.removeNonDestructively(List<E>, E) since this object is immutable
	
	public static TupleOfVariables tupleOfVariables(List<? extends Variable> variables) {
		// TODO implement default implementation class and create instance here
		return null;
	}

	public static TupleOfVariables singletonTuple(Variable variable) {
		// TODO implement default implementation class and create instance here
		return null;
	}

}
