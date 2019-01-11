package com.sri.ai.util.function.core.functions;

import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.function.api.variables.SetOfVariables.setOfVariables;

import java.util.Arrays;
import java.util.List;

import com.sri.ai.util.function.api.functions.Function;
import com.sri.ai.util.function.api.functions.Functions;
import com.sri.ai.util.function.api.variables.SetOfVariables;
import com.sri.ai.util.function.api.variables.Variable;

public class DefaultFunctions implements Functions {

	private List<? extends Function> functions;
	
	public DefaultFunctions(List<? extends Function> functions) {
		this.functions = functions;
	}

	public DefaultFunctions(Function... functions) {
		this(Arrays.asList(functions));
	}

	@Override
	public List<? extends Function> getFunctions() {
		return functions;
	}

	@Override
	public SetOfVariables getAllInputVariables() {
		List<Variable> allInputVariables = list();
		for (Function function: getFunctions()) {
			allInputVariables.addAll(function.getInputVariables().getVariables());
		}
		SetOfVariables result = setOfVariables(allInputVariables);
		return result;
	}
}
