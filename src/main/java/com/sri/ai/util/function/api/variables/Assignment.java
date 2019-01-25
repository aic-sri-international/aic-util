package com.sri.ai.util.function.api.variables;

import static com.sri.ai.util.Util.findFirst;
import static com.sri.ai.util.Util.in;
import static com.sri.ai.util.Util.mapIntoArrayList;
import static com.sri.ai.util.Util.mapIntoList;
import static com.sri.ai.util.Util.myAssert;
import static com.sri.ai.util.collect.FunctionIterator.functionIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.collect.CartesianProductIterator;
import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.core.variables.DefaultAssignment;
import com.sri.ai.util.function.core.variables.RealVariable;

public interface Assignment {

	static Assignment assignment(SetOfVariables variables, ArrayList<? extends Value> values) {
		return new DefaultAssignment(variables, values);
	}
	
	Assignment remove(Variable variableToRemove);

	SetOfVariables getSetOfVariables();
	
	default int size() {
		return getSetOfVariables().size();
	}

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

	/**
	 * Returns an iterable to all assignments to given variables, requiring that they all have defined values.
	 * @param setOfVariables
	 * @return
	 */
	static Iterable<Assignment> assignments(SetOfVariables setOfVariables) {
		Variable variableWithoutValues = findFirst(setOfVariables.getVariables(), v -> v.getSetOfValuesOrNull() == null);
		myAssert(variableWithoutValues == null, () -> Assignment.class + ".assignments(SetOfVariables) requires that all variables have associated values but " + variableWithoutValues + " does not");
		return assignments(setOfVariables, (Variable v) -> v.getSetOfValuesOrNull());
	}

	/**
	 * Returns an iterable to all assignments to given variables, using sets of values provided by an arbitrary function.
	 * @param setOfVariables
	 * @param valuesForVariable
	 * @return
	 */
	static Iterable<Assignment> assignments(SetOfVariables setOfVariables, Function<Variable, SetOfValues> valuesForVariable) {

		com.google.common.base.Function<Variable, NullaryFunction<Iterator<Value>>> makeIteratorMaker = v -> () -> valuesForVariable.apply(v).iterator();

		List<NullaryFunction<Iterator<Value>>> iteratorMakers = mapIntoList(setOfVariables.getVariables(), makeIteratorMaker);

		Iterator<ArrayList<Value>> cartesianProductIterator = new CartesianProductIterator<>(iteratorMakers);

		Iterator<Assignment> assignmentsIterator = functionIterator(cartesianProductIterator, valuesArray -> assignment(setOfVariables, valuesArray));

		Iterable<Assignment> assignmentsIterable = in(assignmentsIterator);

		return assignmentsIterable;
	}

	/**
	 * Returns the indices of the values in this assignment, in the same iteration order as the variables in the assignment,
	 * according to the set of values attributed to each of them by a given function (this can be slow if some of the variables
	 * are {@link RealVariable} because finding their index requires floating point math.
	 * @param setOfValuesForVariable
	 * @return
	 */
	default ArrayList<Integer> indices(Function<Variable, SetOfValues> setOfValuesForVariable) {
		ArrayList<? extends Variable> variables = getSetOfVariables().getVariables();
		ArrayList<Integer> result = mapIntoArrayList(variables, v -> setOfValuesForVariable.apply(v).getIndexOf(get(v)));
		return result;
	}

}
