package com.sri.ai.util.function.core.functions;

import com.sri.ai.util.function.api.functions.Function;
import com.sri.ai.util.function.api.variables.SetOfVariables;
import com.sri.ai.util.function.api.variables.Variable;

/**
 * An abstract implementation for {@link Function} that takes care of storing
 * input and output variables received at construction (as opposed to generating them in some other way).
 * 
 * @author E26638
 *
 */
public abstract class AbstractConcreteFieldsFunction extends AbstractFunction {
	
	protected Variable outputVariable;
	
	protected SetOfVariables setOfInputVariables;
	
	public AbstractConcreteFieldsFunction(Variable outputVariable, SetOfVariables inputVariables) {
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
