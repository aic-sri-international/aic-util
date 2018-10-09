package com.sri.ai.util.graph2d.core;

import static com.sri.ai.util.Util.in;
import static com.sri.ai.util.Util.mapIntoList;
import static com.sri.ai.util.collect.FunctionIterator.functionIterator;
import static com.sri.ai.util.graph2d.api.variables.Assignment.assignment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.collect.CartesianProductIterator;
import com.sri.ai.util.graph2d.api.functions.Functions;
import com.sri.ai.util.graph2d.api.graph.GraphSetMaker;
import com.sri.ai.util.graph2d.api.variables.Assignment;
import com.sri.ai.util.graph2d.api.variables.SetOfValues;
import com.sri.ai.util.graph2d.api.variables.SetOfVariables;
import com.sri.ai.util.graph2d.api.variables.Value;
import com.sri.ai.util.graph2d.api.variables.Variable;

public class DefaultGraphSetMaker implements GraphSetMaker {

	private Functions functions;
	private Map<Variable, SetOfValues> fromVariableToSetOfValues;
	
	@Override
	public Functions getFunctions() {
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
		SetOfValues setOfValues = variable.setOfValuesOrNull();
		if (setOfValues == null && getFromVariableToSetOfValues() != null) {
			setOfValues = getFromVariableToSetOfValues().get(variable);
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

		cartesianProductIterator = new CartesianProductIterator<>(iteratorMakers);
		
		Iterator<Assignment> assignmentsIterator = functionIterator(cartesianProductIterator, valuesArray -> assignment(setOfVariables, valuesArray));
		
		Iterable<Assignment> assignmentsIterable = in(assignmentsIterator);
		
		return assignmentsIterable;
	}

	private com.google.common.base.Function<Variable, NullaryFunction<Iterator<Value>>> makeIteratorMaker() {
		return v -> () -> valuesForVariable(v).iterator();
	}
}
