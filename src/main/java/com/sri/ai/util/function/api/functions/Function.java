package com.sri.ai.util.function.api.functions;

import static com.sri.ai.util.Util.getIndexOfFirstSatisfyingPredicateOrMinusOne;

import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.api.variables.Assignment;
import com.sri.ai.util.function.api.variables.SetOfVariables;
import com.sri.ai.util.function.api.variables.Variable;
import com.sri.ai.util.function.core.functions.DefaultFunction;
import com.sri.ai.util.function.core.functions.DefaultProjectionSingleInputFunction;
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
	
	default Variable getVariable(String variableName) {
		int index = getIndexOfFirstSatisfyingPredicateOrMinusOne(getSetOfInputVariables().getVariables(), v -> v.getName().equals(variableName));
		Variable result = getSetOfInputVariables().get(index);
		return result;
	}
	
	/**
	 * Let this function be f(x_1, ..., x_n).
	 * If given <code>variable</code> is x_i,
	 * then let the remaining variables be x_1, ..., x_i-1, x_i+1, ..., x_n.
	 * Let the given <code>assignmentToRemainingVariables</code> be v_1, ..., v_i-1, v_i+1, ..., v_n.
	 * Then this method must return the function g(v) = f(v_1, ..., v_i-1,    v,    v_i+1, ..., v_n).
	 * In other words, it fixes the remaining variables to a given assignment and returns a function
	 * with a single parameter <code>variable</code> (x_i) mapping to the result
	 * of the original function using the fixed values.
	 * <p>
	 * Note: This used to be a default method using {@link DefaultProjectionSingleInputFunction},
	 * whose {@link #evaluate(Assignment)} method for a value <code>v</code> of <code>variable</code> simply
	 * extends <code>assignmentToRemainingVariables</code> with <code>variable = v</code> and invokes the original function.
	 * However, later it became evident that sometimes we need more efficient implementations of projections;
	 * for example, if the original function is based on sampling, it will generate samples for all values of
	 * the variables in <code>assignmentToRemainingVariables</code>, 
	 * but only the actual values in <code>assignmentToRemainingVariables<code>
	 * are relevant for the projection,
	 * so in this case it makes sense to condition the sampling to the values in <code>assignmentToRemainingVariables</code>.
	 * By not letting this method be a default, we force users to really think about how they need to implement projections.
	 * 	
	 * @param variable
	 * @param assignmentToRemainingVariables
	 * @return
	 */
	SingleInputFunction project(Variable variable, Assignment assignmentToRemainingVariables);

}
