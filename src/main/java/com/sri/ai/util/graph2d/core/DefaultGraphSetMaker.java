package com.sri.ai.util.graph2d.core;

import static com.sri.ai.util.Util.in;
import static com.sri.ai.util.Util.mapIntoList;
import static com.sri.ai.util.Util.myAssert;
import static com.sri.ai.util.collect.FunctionIterator.functionIterator;
import static com.sri.ai.util.function.api.variables.Assignment.assignment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.collect.CartesianProductIterator;
import com.sri.ai.util.function.api.functions.Functions;
import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.api.variables.Assignment;
import com.sri.ai.util.function.api.variables.SetOfValues;
import com.sri.ai.util.function.api.variables.SetOfVariables;
import com.sri.ai.util.function.api.variables.Variable;
import com.sri.ai.util.graph2d.api.GraphSetMaker;
import com.sri.ai.util.graph2d.core.jfreechart.GraphSettings;

public class DefaultGraphSetMaker implements GraphSetMaker {
	private GraphSettings graphSettings = new GraphSettings();
	private Functions functions;
	private Map<Variable, SetOfValues> fromVariableToSetOfValues;

	@Override
	public GraphSettings getGraphSettings() {
		return graphSettings;
	}

	@Override
	public void setGraphSettings(GraphSettings graphSettings) {
		this.graphSettings = graphSettings;
	}

	@Override
	public Functions getFunctions() {
		myAssert(functions != null, () -> getClass() + " has not yet received functions to operate on");
		return functions;
	}

	@Override
	public void setFunctions(Functions functions) {
		this.functions = functions;
	}
	
	@Override
	public Map<Variable, SetOfValues> getFromVariableToSetOfValues() {
		return fromVariableToSetOfValues;
	}

	@Override
	public void setFromVariableToSetOfValues(Map<Variable, SetOfValues> fromVariableToSetOfValues) {
		this.fromVariableToSetOfValues = fromVariableToSetOfValues;
	}

	@Override
	public SetOfValues valuesForVariable(Variable variable) {
		// If a set of values have been defined in the GraphSetMaker for the variable, use them,
		// else use the set of values defined in the variable itself.
		SetOfValues setOfValues
				= fromVariableToSetOfValues == null? null : fromVariableToSetOfValues.isEmpty() ? null : fromVariableToSetOfValues.get(variable);

		if (setOfValues == null) {
			setOfValues = variable.getSetOfValuesOrNull();
		}

		if (setOfValues == null) {
			throw new Error("Need values for " + variable + " but that is not defined either by the variable itself or by the functions " + this);
		}
		return setOfValues;
	}

	@Override
	public Iterable<Assignment> assignments(SetOfVariables setOfVariables) {

		List<NullaryFunction<Iterator<Value>>> iteratorMakers = mapIntoList(setOfVariables.getVariables(), makeIteratorMaker());

		Iterator<ArrayList<Value>> cartesianProductIterator = new CartesianProductIterator<>(iteratorMakers);

		Iterator<Assignment> assignmentsIterator = functionIterator(cartesianProductIterator, valuesArray -> assignment(setOfVariables, valuesArray));

		Iterable<Assignment> assignmentsIterable = in(assignmentsIterator);

		return assignmentsIterable;
	}

	private com.google.common.base.Function<Variable, NullaryFunction<Iterator<Value>>> makeIteratorMaker() {
		return v -> () -> valuesForVariable(v).iterator();
	}
}
