package com.sri.ai.util.graph2d.core.functions;

import com.sri.ai.util.graph2d.api.variables.Assignment;
import com.sri.ai.util.graph2d.api.variables.SetOfVariables;
import com.sri.ai.util.graph2d.api.variables.Value;
import com.sri.ai.util.graph2d.api.variables.Variable;

public class DefaultFunction extends AbstractFunction {
	
	private String name;
	private java.util.function.Function<Assignment, Value> javaFunction;

	public DefaultFunction(String name, Variable outputVariable, SetOfVariables inputVariables, JavaFunction<Assignment, Value> javaFunction) {
		super(outputVariable, inputVariables);
		this.name = name;
		this.javaFunction = javaFunction;
	}

	public String getName() {
		return name;
	}

	@Override
	public Value evaluate(Assignment inputVariableValues) {
		return javaFunction.apply(inputVariableValues);
	}

}
