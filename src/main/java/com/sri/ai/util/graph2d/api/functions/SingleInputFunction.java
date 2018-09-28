package com.sri.ai.util.graph2d.api.functions;

import static com.sri.ai.util.Util.arrayList;
import static com.sri.ai.util.graph2d.api.variables.Assignment.assignment;
import static com.sri.ai.util.graph2d.api.variables.SetOfVariables.singletonSet;

import java.util.ArrayList;

import com.sri.ai.util.graph2d.api.variables.Assignment;
import com.sri.ai.util.graph2d.api.variables.SetOfVariables;
import com.sri.ai.util.graph2d.api.variables.Value;
import com.sri.ai.util.graph2d.api.variables.Variable;

/**
 * A {@link Function} with a single input variable.
 * 
 * @author braz
 *
 */
public interface SingleInputFunction extends Function {

	default Variable getInputVariable() {
		return getInputVariables().getFirst();
	}
	
	default void setInputVariable(Variable inputVariable) {
		setInputVariables(singletonSet(inputVariable));
	}

	default Value computeOutputVariableValue(Value inputVariableValue) {
		Assignment assignmentToInputVariable = makeAssignmentToInputVariable(inputVariableValue);
		Value result = computeOutputVariableValue(assignmentToInputVariable);
		return result;
	}

	default Assignment makeAssignmentToInputVariable(Value inputVariableValue) {
		SetOfVariables variables = singletonSet(getInputVariable());
		ArrayList<Value> values = arrayList(inputVariableValue);
		Assignment result = assignment(variables, values);
		return result;
	}
}
