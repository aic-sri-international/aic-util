package com.sri.ai.util.graph2d.core.functions;

import com.sri.ai.util.graph2d.api.functions.Function;
import com.sri.ai.util.graph2d.api.variables.SetOfVariables;
import com.sri.ai.util.graph2d.api.variables.Variable;

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
