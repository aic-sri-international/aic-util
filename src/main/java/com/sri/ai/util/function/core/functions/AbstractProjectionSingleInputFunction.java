package com.sri.ai.util.function.core.functions;

import static com.sri.ai.util.Util.myAssert;
import static com.sri.ai.util.function.api.variables.SetOfVariables.singletonSet;

import java.util.HashSet;
import java.util.Set;

import com.sri.ai.util.function.api.functions.Function;
import com.sri.ai.util.function.api.functions.SingleInputFunction;
import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.api.variables.Assignment;
import com.sri.ai.util.function.api.variables.SetOfVariables;
import com.sri.ai.util.function.api.variables.Variable;

public abstract class AbstractProjectionSingleInputFunction implements SingleInputFunction {

	@Override
	public abstract Value evaluate(Assignment inputVariableValue);

	//////////////////////
	
	protected Function projectedFunction;
	protected Variable variable;
	protected Assignment assignmentToRemainingVariables;

	//////////////////////
	
	public AbstractProjectionSingleInputFunction(Function projectedFunction, Variable variable, Assignment assignmentToRemainingVariables) {
		this.projectedFunction = projectedFunction;
		this.variable = variable;
		this.assignmentToRemainingVariables = assignmentToRemainingVariables;
		checkIfVariablesCoincideWithBaseInputVariables();
	}

	//////////////////////
	
	public Function getProjectedFunction() {
		return projectedFunction;
	}

	public Variable getVariable() {
		return variable;
	}

	public Assignment getAssignmentToRemainingVariables() {
		return assignmentToRemainingVariables;
	}

	//////////////////////
	
	protected void checkIfVariablesCoincideWithBaseInputVariables() {
		Set<Variable> projectedVariables = getProjectedVariables();
		Set<Variable> variablesFromAssignmentPlusProjectionVariable = makeVariablesFromAssignmentPlusProjectionVariable();
		boolean valid = projectedVariables.equals(variablesFromAssignmentPlusProjectionVariable);
		myAssert(valid, () -> "Projected function's input variables " + getProjectedVariables() + " are not coinciding with the union of the projection variable " + getVariable() + " and the remaining variables defined in the assignment " + getAssignmentToRemainingVariables());
	}

	private HashSet<Variable> getProjectedVariables() {
		return new HashSet<Variable>(getProjectedFunction().getSetOfInputVariables().getVariables());
	}

	private Set<Variable> makeVariablesFromAssignmentPlusProjectionVariable() {
		Set<Variable> variablesFromAssignmentPlusProjectionVariable = new HashSet<Variable>(getAssignmentToRemainingVariables().getSetOfVariables().getVariables());
		variablesFromAssignmentPlusProjectionVariable.add(getVariable());
		return variablesFromAssignmentPlusProjectionVariable;
	}

	//////////////////////
	
	@Override
	public Variable getOutputVariable() {
		return getProjectedFunction().getOutputVariable();
	}

	@Override
	public SetOfVariables getSetOfInputVariables() {
		return singletonSet(getVariable());
	}

	@Override
	public String getName() {
		return "Projection of " + getProjectedFunction() + " on " + getAssignmentToRemainingVariables();
	}

	////////////////////// REMAINING METHODS ARE NOT SUPPORTED

	@Override
	public SingleInputFunction project(Variable variable, Assignment assignmentToRemainingVariables) {
		throw new Error("Cannot project " + getClass());
	}

}