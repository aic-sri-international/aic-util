package com.sri.ai.util.graph2d.api.functions;

import static com.sri.ai.util.Util.myAssert;
import static com.sri.ai.util.graph2d.api.variables.SetOfVariables.singletonSet;

import java.util.HashSet;
import java.util.Set;

import com.sri.ai.util.graph2d.api.variables.Assignment;
import com.sri.ai.util.graph2d.api.variables.SetOfVariables;
import com.sri.ai.util.graph2d.api.variables.Value;
import com.sri.ai.util.graph2d.api.variables.Variable;

/**
 * A class implementing the projection of a {@link Function}
 * (see {@link Function#project(Variable, Assignment)}).
 * 
 */
public class ProjectionSingleInputFunction implements SingleInputFunction {

	private Function base;
	private Variable variable;
	private Assignment assignmentToRemainingVariables;
	
	public ProjectionSingleInputFunction(
			Function base, 
			Variable variable,
			Assignment assignmentToRemainingVariables) {
		
		super();
		this.base = base;
		this.variable = variable;
		this.assignmentToRemainingVariables = assignmentToRemainingVariables;
		myAssert(() -> checkIfVariablesCoincideWithBaseInputVariables(), () -> "Base function's input variables are not coinciding with the union of the variable and the remaining variables");
	}
	
	private boolean checkIfVariablesCoincideWithBaseInputVariables() {
		Set<Variable> baseVariables = new HashSet<Variable>(base.getInputVariables().getVariables());
		Set<Variable> variables = new HashSet<Variable>(assignmentToRemainingVariables.getSetOfVariables().getVariables());
		variables.add(variable);
		boolean check = baseVariables.equals(variables);
		return check;
	}

	@Override
	public Variable getOutputVariable() {
		return base.getOutputVariable();
	}

	@Override
	public SetOfVariables getInputVariables() {
		return singletonSet(variable);
	}

	@Override
	public Value evaluate(Assignment inputVariableValues) {
		Value valueOfVariable = inputVariableValues.get(variable);
		Assignment assignmentToAllVariables = assignmentToRemainingVariables.extend(variable, valueOfVariable);
		Value result = base.evaluate(assignmentToAllVariables);
		return result;
	}

	@Override
	public String getName() {
		return "Projection of " + base + " on " + assignmentToRemainingVariables;
	}

	////////////////////// REMAINING METHODS ARE NOT SUPPORTED
	
	@Override
	public SingleInputFunction project(Variable variable, Assignment assignmentToRemainingVariables) {
		throw new Error("Cannot project " + getClass());
	}
}
