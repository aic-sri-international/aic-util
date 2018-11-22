package com.sri.ai.util.graph2d.api.functions;

import com.sri.ai.util.graph2d.api.variables.SetOfVariables;
import com.sri.ai.util.graph2d.api.variables.Variable;
import java.util.List;

/**
 * An extension of {@link Functions} containing {@link SingleInputFunction}s only.
 *
 * @author braz
 *
 */
public interface SingleInputFunctions extends Functions {

	@Override
	List<? extends SingleInputFunction> getFunctions();

	public static SingleInputFunctions singleInputFunctions() {
		return new DefaultSingleInputFunctions();
	}
	
	void add(SingleInputFunction singleInputFunction);

	@Override
	default SetOfVariables getAllInputVariables() {
		Variable variable = getInputVariable();
		SetOfVariables setOfVariables = variable == null ? null :  SetOfVariables.setOfVariables(variable);
		return setOfVariables;
	}
	/**
	 *  All SingleInputFunction's must have the same input variable
	 * @return input Variable or null if no functions have been added
	 */
	default Variable getInputVariable() {
		List<? extends SingleInputFunction> functions = getFunctions();
		SingleInputFunction singleInputFunction
				= functions != null && !functions.isEmpty() ? functions.get(0) : null;
		return singleInputFunction == null ? null : singleInputFunction.getInputVariable();
	}
}
