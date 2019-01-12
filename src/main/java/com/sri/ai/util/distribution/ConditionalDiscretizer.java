package com.sri.ai.util.distribution;

import static com.sri.ai.util.Util.arrayList;
import static com.sri.ai.util.Util.getFirstSatisfyingPredicateOrNull;
import static com.sri.ai.util.Util.myAssert;

import java.util.ArrayList;
import java.util.List;

import com.sri.ai.util.base.Pair;
import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.api.variables.SetOfVariables;
import com.sri.ai.util.function.api.variables.Variable;

/**
 * A class that provides, given an array of values, the indices of these values according to a predefined set of variables and their ranges.
 * It also uses the notion of a query variable so that indices of values are provided separately for query and non-query (remaining) variables.
 * This is done for efficiency, assuming that the client will want a separate array of value indices for the remaining variables,
 * so it is created already in this form as opposed to creating an array of indices for all variables and forcing the client to copy the
 * indices of values of remaining variables to a new array.
 * 
 * @author braz
 *
 */
public class ConditionalDiscretizer {

	protected SetOfVariables setOfVariablesWithRange;
	protected Variable queryVariable;
	protected int queryVariableIndex;

	protected ConditionalDiscretizer(SetOfVariables setOfVariablesWithRange, int queryVariableIndex) {
		
		assertVariablesAllHaveADefinedSetOfValues(setOfVariablesWithRange);
		
		this.setOfVariablesWithRange = setOfVariablesWithRange;
		this.queryVariable = getVariables().get(queryVariableIndex);
		this.queryVariableIndex = queryVariableIndex;

	}
	
	/**
	 * Returns the index of the value of the query, and the indices of values of remaining variables.
	 * If index of value of query is -1 (meaning the value is out of range), the indices of values of remaining variables are not calculated (array is empty).
	 * @param valueObjects
	 * @return
	 */
	public Pair<Integer, ArrayList<Integer>> getValueIndices(ArrayList<Object> valueObjects) {
		int queryValueIndex = getIndexOfValueOfVariableAt(queryVariableIndex, valueObjects);
		ArrayList<Integer> nonQueryValueIndices = makeNonQueryValueIndices(valueObjects, queryValueIndex);
		return new Pair<>(queryValueIndex, nonQueryValueIndices);
	}

	private ArrayList<Integer> makeNonQueryValueIndices(ArrayList<Object> valueObjects, int queryValueIndex) {
		ArrayList<Integer> nonQueryValueIndices;
		if (queryValueIndex != -1) { // is in range
			nonQueryValueIndices = getNonQueryValueIndices(valueObjects);
		}
		else {
			nonQueryValueIndices = arrayList();
		}
		return nonQueryValueIndices;
	}

	private ArrayList<Integer> getNonQueryValueIndices(ArrayList<Object> valueObjects) {
		ArrayList<Integer> result = arrayList(numberOfVariables() - 1);
		for (int i = 0; i != numberOfVariables(); i++) {
			if (i != queryVariableIndex) {
				int indexOfValueOfIthVariable = getIndexOfValueOfVariableAt(i, valueObjects);
				result.add(indexOfValueOfIthVariable);
			}
		}
		return result;
	}

	//////////////////////////////

	private int getIndexOfValueOfVariableAt(int i, ArrayList<Object> valueObjects) {
		Object valueObject = getValueOfVariableAt(i, valueObjects);
		int indexOfValueOfIthVariable = getIndexOfValue(getVariable(i), valueObject);
		return indexOfValueOfIthVariable;
	}

	private Object getValueOfVariableAt(int variableIndex, ArrayList<Object> valueObjects) {
		Object valueObject = valueObjects.get(variableIndex);
		myAssert(valueObject != null, () -> "Value not available for " + setOfVariablesWithRange.get(variableIndex));
		return valueObject;
	}

	private int getIndexOfValue(Variable variable, Object valueObject) {
		Value value = Value.value(valueObject);
		int result = variable.getSetOfValuesOrNull().getIndex(value);
		return result;
	}

	//////////////////////////////

	protected List<? extends Variable> getVariables() {
		return setOfVariablesWithRange.getVariables();
	}

	protected int numberOfVariables() {
		return setOfVariablesWithRange.size();
	}

	protected Variable getVariable(int i) {
		return getVariables().get(i);
	}

	private void assertVariablesAllHaveADefinedSetOfValues(SetOfVariables inputVariablesWithRange) throws Error {
		List<? extends Variable> variables = inputVariablesWithRange.getVariables();
		Variable withoutSetOfValues = getFirstSatisfyingPredicateOrNull(variables, v -> v.getSetOfValuesOrNull() == null);
		if (withoutSetOfValues != null) {
			throw new Error(getClass() + " requires that all variables have a defined set of values, but " + withoutSetOfValues + " does not");
		}
	}

}