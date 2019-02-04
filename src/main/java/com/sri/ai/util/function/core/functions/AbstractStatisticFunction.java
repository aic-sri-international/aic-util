package com.sri.ai.util.function.core.functions;

import com.sri.ai.util.function.api.functions.Function;
import com.sri.ai.util.function.api.functions.SingleInputFunction;
import com.sri.ai.util.function.api.variables.Assignment;
import com.sri.ai.util.function.api.variables.SetOfVariables;
import com.sri.ai.util.function.api.variables.Variable;

public abstract class AbstractStatisticFunction extends AbstractFunction implements Function {

	private Function function;
	private Variable variable;
	private String statisticName;
	
	public AbstractStatisticFunction(String statisticName, Variable variable, Function function) {
		this.statisticName = statisticName;
		this.variable = variable;
		this.function = function;
	}

	public Function getFunction() {
		return function;
	}

	public Variable getVariable() {
		return variable;
	}

	public String getStatisticName() {
		return statisticName;
	}

	@Override
	public String getName() {
		return statisticName + " of " + function.getName();
	}

	@Override
	public Variable getOutputVariable() {
		return function.getOutputVariable();
	}

	private SetOfVariables setOfVariables = null;
	
	@Override
	public SetOfVariables getSetOfInputVariables() {
		if (setOfVariables == null) {
			setOfVariables = function.getSetOfInputVariables().minus(variable);
		}
		return setOfVariables;
	}

	@Override
	protected SingleInputFunction projectIfNeeded(Variable variable, Assignment assignmentToRemainingVariables) {
		return new DefaultProjectionSingleInputFunction(this, variable, assignmentToRemainingVariables);
	}

}
