package com.sri.ai.util.graph2d.core;

import java.util.Arrays;
import java.util.List;

import com.sri.ai.util.graph2d.api.functions.Function;
import com.sri.ai.util.graph2d.api.functions.Functions;
import com.sri.ai.util.graph2d.api.variables.SetOfVariables;

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
		return null; // TODO: implement
	}

}
