package com.sri.ai.util.function.core.functions;

import static com.sri.ai.util.Util.myAssert;

import com.sri.ai.util.function.api.functions.Function;
import com.sri.ai.util.function.api.functions.SingleInputFunction;
import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.api.variables.Assignment;
import com.sri.ai.util.function.api.variables.SetOfVariables;
import com.sri.ai.util.function.api.variables.Variable;

public class FunctionToSingleInputFunctionAdapter extends AbstractFunction implements SingleInputFunction {

	private Function base;
	
	public FunctionToSingleInputFunctionAdapter(Function base) {
		this.base = base;
		myAssert(base.getSetOfInputVariables().size() == 1, () -> getClass() + " requires base function to have a single input variable, but got base function " + base + " with " + base.getSetOfInputVariables().size() + " input variables, namely " + base.getSetOfInputVariables());
	}
	
	public Function getBase() {
		return base;
	}

	@Override
	public String getName() {
		return base.getName();
	}

	@Override
	public Variable getOutputVariable() {
		return base.getOutputVariable();
	}

	@Override
	public SetOfVariables getSetOfInputVariables() {
		return base.getSetOfInputVariables();
	}

	@Override
	public Value evaluate(Assignment assignmentToInputVariables) {
		return base.evaluate(assignmentToInputVariables);
	}
	
	@Override
	protected SingleInputFunction projectIfNeeded(Variable variable, Assignment assignmentToRemainingVariables) {
		throw new Error("Cannot project a function to one variable that already has only one variable");
	}

}