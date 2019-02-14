package com.sri.ai.util.function.core.functions;

import com.sri.ai.util.function.api.functions.SingleInputFunction;
import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.api.variables.Assignment;
import com.sri.ai.util.function.api.variables.SetOfVariables;
import com.sri.ai.util.function.api.variables.Variable;

public class DefaultFunction extends AbstractFunctionWithVariablesProvidedByConstructor {
	
	private String name;
	private java.util.function.Function<Assignment, Value> javaFunction;

	public DefaultFunction(String name, Variable outputVariable, SetOfVariables inputVariables, JavaFunction<Assignment, Value> javaFunction) {
		super(outputVariable, inputVariables);
		this.name = name;
		this.javaFunction = javaFunction;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Value evaluate(Assignment inputVariableValues) {
		return javaFunction.apply(inputVariableValues);
	}

	@Override
	protected SingleInputFunction projectIfNeeded(Variable variable, Assignment assignmentToRemainingVariables) {
		return new DefaultProjectionSingleInputFunction(this, variable, assignmentToRemainingVariables);
	}
}
