package com.sri.ai.util.graph2d.core.functions;

import com.sri.ai.util.graph2d.api.functions.Function;
import com.sri.ai.util.graph2d.api.variables.Assignment;
import com.sri.ai.util.graph2d.api.variables.SetOfVariables;
import com.sri.ai.util.graph2d.api.variables.Value;
import com.sri.ai.util.graph2d.api.variables.Variable;

public class DefaultFunction implements Function {
	
	private String name;
	private Variable outputVariable;
	private SetOfVariables inputVariables;
	private java.util.function.Function<Assignment, Value> javaFunction;

	public DefaultFunction(String name, Variable outputVariable, SetOfVariables inputVariables, JavaFunction<Assignment, Value> javaFunction) {
		this.name = name;
		this.outputVariable = outputVariable;
		this.inputVariables = inputVariables;
		this.javaFunction = javaFunction;
	}

	public String getName() {
		return name;
	}

	public Variable getOutputVariable() {
		return outputVariable;
	}

	public SetOfVariables getInputVariables() {
		return inputVariables;
	}

	@Override
	public Value evaluate(Assignment inputVariableValues) {
		return javaFunction.apply(inputVariableValues);
	}

}
