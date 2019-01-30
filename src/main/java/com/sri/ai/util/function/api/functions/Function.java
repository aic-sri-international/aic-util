package com.sri.ai.util.function.api.functions;

import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.api.variables.Assignment;
import com.sri.ai.util.function.api.variables.SetOfVariables;
import com.sri.ai.util.function.api.variables.Variable;
import com.sri.ai.util.function.core.functions.DefaultFunction;
import com.sri.ai.util.function.core.functions.JavaFunction;

/**
 * A function mapping assignments to a certain set of input variables to a value of an output variable.	
 *
 */
public interface Function {
	
	static Function function(String name, Variable outputVariable, SetOfVariables inputVariables, JavaFunction<Assignment, Value> javaFunction) {
		return new DefaultFunction(name, outputVariable, inputVariables, javaFunction);
	}
	
	String getName();
	
	Variable getOutputVariable();
	
	SetOfVariables getSetOfInputVariables();
	
	Value evaluate(Assignment assignmentToInputVariables);
	
	/**
	 * Let this function is f(x_1, ..., x_n).
	 * If given <code>variable</code> is x_i,
	 * then let the remaining variables be x_1, ..., x_i-1, x_i+1, ..., x_n.
	 * Let the given <code>assignmentToRemainingVariables</code> be v_1, ..., v_i-1, v_i+1, ..., v_n.
	 * Then this method must return the function g(v) = f(v_1, ..., v_i-1,    v,    v_i+1, ..., v_n).
	 * In other words, it fixes the remaining variables to a given assignment and returns a function
	 * with a single parameter <code>variable</code> (x_i) mapping to the result
	 * of the original function using the fixed values. 
	 * @param variable
	 * @param assignmentToRemainingVariables
	 * @return
	 */
	SingleInputFunction project(Variable variable, Assignment assignmentToRemainingVariables);
	// This used to be a default method using {@link DefaultProjectionSingleInputFunction},
	// but it turned out to be a pitfall since some functions need more efficient implementations,
	// and the proxies to them may end up with a default version that does not delegate
	// to their efficient implementations.
	// By not letting it be a default, we force users to really think about how they need to project.

}
