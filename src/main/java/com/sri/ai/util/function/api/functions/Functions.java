package com.sri.ai.util.function.api.functions;

import static com.sri.ai.util.function.api.functions.SingleInputFunctions.singleInputFunctions;

import java.util.List;

import com.sri.ai.util.function.api.variables.Assignment;
import com.sri.ai.util.function.api.variables.SetOfVariables;
import com.sri.ai.util.function.api.variables.Variable;
import com.sri.ai.util.function.core.functions.DefaultFunctions;

/**
 * A collection of {@link Function}s sharing the same set of input variables
 *
 */
public interface Functions {

	static Functions functions(Function... functions) {
		return new DefaultFunctions(functions);
	}
	
	List<? extends Function> getFunctions();

	SetOfVariables getAllInputVariables();
	
	/**
	 * Creates a {@link SingleInputFunctions} object containing the projections of each of its constituent functions
	 * (see {@link Function#project(Variable, Assignment)}).
	 * @param variable
	 * @param assignmentToRemainingVariables
	 * @return
	 */
	default SingleInputFunctions project(Variable variable, Assignment assignmentToRemainingVariables) {
		SingleInputFunctions result = singleInputFunctions();
		for (Function function: getFunctions()) {
			SingleInputFunction functionOnVariableGivenFixedAssignmentForRemainingVariables = 
					function.project(variable, assignmentToRemainingVariables);
			result.add(functionOnVariableGivenFixedAssignmentForRemainingVariables);
		}
		return result;
	}

}
