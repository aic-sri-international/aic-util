package com.sri.ai.util.function.api.functions;

import static com.sri.ai.util.Util.arrayList;
import static com.sri.ai.util.function.api.variables.Assignment.assignment;
import static com.sri.ai.util.function.api.variables.SetOfVariables.singletonSet;

import java.util.ArrayList;

import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.api.variables.Assignment;
import com.sri.ai.util.function.api.variables.SetOfVariables;
import com.sri.ai.util.function.api.variables.Variable;

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
	
	default Value computeOutputVariableValue(Value inputVariableValue) {
		Assignment assignmentToInputVariable = makeAssignmentToInputVariable(inputVariableValue);
		Value result = evaluate(assignmentToInputVariable);
		return result;
	}

	default Assignment makeAssignmentToInputVariable(Value inputVariableValue) {
		SetOfVariables variables = singletonSet(getInputVariable());
		ArrayList<Value> values = arrayList(inputVariableValue);
		Assignment result = assignment(variables, values);
		return result;
	}
}
