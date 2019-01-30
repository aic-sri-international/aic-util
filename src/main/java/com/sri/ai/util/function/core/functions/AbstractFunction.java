package com.sri.ai.util.function.core.functions;

import java.lang.reflect.Method;

import com.sri.ai.util.function.api.functions.Function;
import com.sri.ai.util.function.api.functions.SingleInputFunction;
import com.sri.ai.util.function.api.variables.Assignment;
import com.sri.ai.util.function.api.variables.Variable;

/**
 * An abstract {@link Function} that checks if an assignment is empty before
 * computing projection with an abstract {@link Method} {@link #projectIfNeeded(Variable, Assignment)}.
 * If it is empty, returns this object itself, possibly wrapping it in {@link FunctionToSingleInputFunctionAdapter} first.
 * 
 * @author braz
 *
 */
public abstract class AbstractFunction implements Function {

	abstract protected SingleInputFunction projectIfNeeded(Variable variable, Assignment assignmentToRemainingVariables);
	
	@Override
	public SingleInputFunction project(Variable variable, Assignment assignmentToRemainingVariables) {
		if (assignmentToRemainingVariables.size() != 0) {
			return projectIfNeeded(variable, assignmentToRemainingVariables);
		}
		else if (this instanceof SingleInputFunction) {
			return (SingleInputFunction) this;
		}
		else {
			return new FunctionToSingleInputFunctionAdapter(this);
		}
	}

}
