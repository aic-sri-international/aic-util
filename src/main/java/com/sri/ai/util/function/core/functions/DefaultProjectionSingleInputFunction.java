package com.sri.ai.util.function.core.functions;

import static com.sri.ai.util.Util.myAssert;

import com.sri.ai.util.Enclosing;
import com.sri.ai.util.function.api.functions.Function;
import com.sri.ai.util.function.api.functions.SingleInputFunction;
import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.api.variables.Assignment;
import com.sri.ai.util.function.api.variables.Variable;

/**
 * A default {@link SingleInputFunction} implementing the projection of a {@link Function}
 * (see {@link Function#project(Variable, Assignment)})
 * by taking a projected function, a projection variable, and an assignment on the remaining projected variables,
 * and, for evaluating the projection on an assignment to the projection variable,
 * using its value to complete the originally given assignment on remaining variables
 * and using the full assignment to evaluate the projected function.
 */
public class DefaultProjectionSingleInputFunction extends AbstractProjectionSingleInputFunction {

	public DefaultProjectionSingleInputFunction(
			Function projectedFunction, 
			Variable variable,
			Assignment assignmentToRemainingVariables) {
		
		super(projectedFunction, variable, assignmentToRemainingVariables);
		this.projectedFunction = projectedFunction;
		this.variable = variable;
		this.assignmentToRemainingVariables = assignmentToRemainingVariables;
		myAssert(assignmentToRemainingVariables.get(variable) == null, () -> (new Enclosing(){}).methodName() + " got request to create a projection on " + variable + " but assignment on \"other\" variables includes this variable: " + assignmentToRemainingVariables);
		myAssert(assignmentToRemainingVariables.size() == projectedFunction.getSetOfInputVariables().size() - 1, () -> (new Enclosing(){}).methodName() + " must receive an assignment on all input variables " + projectedFunction.getSetOfInputVariables() + " excluuding " + variable + ", but got an assignment on " + assignmentToRemainingVariables.getSetOfVariables() + " instead");

	}
	
	//////////////////////
	
	@Override
	public Value evaluate(Assignment assignmentToInputVariable) {
		Value valueOfVariable = assignmentToInputVariable.get(getVariable());
		Assignment assignmentToAllVariables = getAssignmentToRemainingVariables().extend(getVariable(), valueOfVariable);
		Value result = getProjectedFunction().evaluate(assignmentToAllVariables);
		return result;
	}

}
