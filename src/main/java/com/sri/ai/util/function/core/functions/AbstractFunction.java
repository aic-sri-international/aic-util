package com.sri.ai.util.function.core.functions;

import com.sri.ai.util.function.api.functions.Function;
import com.sri.ai.util.function.api.variables.SetOfVariables;
import com.sri.ai.util.function.api.variables.Variable;

public abstract class AbstractFunction implements Function {
	
	protected Variable outputVariable;
	
	protected SetOfVariables setOfInputVariables;
	
	public AbstractFunction(Variable outputVariable, SetOfVariables inputVariables) {
		this.outputVariable = outputVariable;
		this.setOfInputVariables = inputVariables;
	}

	@Override
	public Variable getOutputVariable() {
		return outputVariable;
	}

	@Override
	public SetOfVariables getSetOfInputVariables() {
		return setOfInputVariables;
	}

	public void setOutputVariable(Variable outputVariable) {
		this.outputVariable = outputVariable;
	}

	public void setSetOfInputVariables(SetOfVariables setOfInputVariables) {
		this.setOfInputVariables = setOfInputVariables;
	}

}
