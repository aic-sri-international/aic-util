package com.sri.ai.util.function.core.functions;

import com.sri.ai.util.function.api.functions.Function;
import com.sri.ai.util.function.api.variables.SetOfVariables;
import com.sri.ai.util.function.api.variables.Variable;

public abstract class AbstractFunction implements Function {
	
	protected Variable outputVariable;
	
	protected SetOfVariables inputVariables;
	
	public AbstractFunction(Variable outputVariable, SetOfVariables inputVariables) {
		this.outputVariable = outputVariable;
		this.inputVariables = inputVariables;
	}

	@Override
	public Variable getOutputVariable() {
		return outputVariable;
	}

	@Override
	public SetOfVariables getInputVariables() {
		return inputVariables;
	}

	public void setOutputVariable(Variable outputVariable) {
		this.outputVariable = outputVariable;
	}

	public void setInputVariables(SetOfVariables inputVariables) {
		this.inputVariables = inputVariables;
	}

}
