package com.sri.ai.util.graph2d.api.graph;

import static com.sri.ai.util.graph2d.api.graph.ExternalGraphPlotter.externalGraphMaker;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sri.ai.util.graph2d.api.functions.Function;
import com.sri.ai.util.graph2d.api.functions.Functions;
import com.sri.ai.util.graph2d.api.functions.SingleInputFunction;
import com.sri.ai.util.graph2d.api.functions.SingleInputFunctions;
import com.sri.ai.util.graph2d.api.variables.Assignment;
import com.sri.ai.util.graph2d.api.variables.SetOfValues;
import com.sri.ai.util.graph2d.api.variables.SetOfVariables;
import com.sri.ai.util.graph2d.api.variables.Variable;
import com.sri.ai.util.graph2d.core.DefaultGraphSetMaker;

/**
 * An interface for classes generating sets of {@link GraphSet}s from {@link Functions}.
 * 
 * @author braz
 *
 */
public interface GraphSetMaker {
	
	static GraphSetMaker graphSetMaker() {
		return new DefaultGraphSetMaker();
	}
	
	Functions getFunctions();
	void setFunctions(Functions functions);

	Map<Variable, SetOfValues> getFromVariableToSetOfValues();
	void setFromVariableToSetOfValues(Map<Variable, SetOfValues> fromVariableToSetOfValues);

	SetOfValues valuesForVariable(Variable variable);
	
	Iterable<Assignment> assignments(SetOfVariables setOfVariables);

	default GraphSet make(Variable xAxisVariable) {
		
		GraphSet graphSet = GraphSet.graphSet(getFunctions());
		
		SetOfVariables nonAxisVariables = getNonAxisVariables(xAxisVariable);
		
		for (Assignment assignmentToNonAxisVariables: assignments(nonAxisVariables)) {
			GraphPlot plot = plot(assignmentToNonAxisVariables, xAxisVariable);
			graphSet.add(plot);
		}
		
		return graphSet;
	}
	
	default SetOfVariables getNonAxisVariables(Variable xAxisVariable) {
		SetOfVariables nonAxisVariables = getFunctions().getAllInputVariables().minus(xAxisVariable);
		return nonAxisVariables;
	}

	default String buildTitle(Assignment assignmentToNonAxisVariables,
														SingleInputFunctions singleInputFunctionsToBePlotted) {
		List<? extends SingleInputFunction> singleInputFunctions
				= singleInputFunctionsToBePlotted.getFunctions();

		final String delimiter = singleInputFunctions.size() == 2 ? " & " : ", ";
		String names = singleInputFunctions.stream().map(Function::getName).collect(
				Collectors.joining(delimiter));

		return names + " by " + singleInputFunctionsToBePlotted.getInputVariable().getName() + " for " +
				assignmentToNonAxisVariables.toDisplayFormat();
	}

	default GraphPlot plot(Assignment assignmentToNonAxisVariables, Variable xAxisVariable) {
		SingleInputFunctions singleInputFunctionsToBePlotted
				= getFunctions().project(xAxisVariable, assignmentToNonAxisVariables);

		String title = buildTitle(assignmentToNonAxisVariables, singleInputFunctionsToBePlotted);
		return plot(title, singleInputFunctionsToBePlotted);
		
	}
	
	default GraphPlot plot(String title, SingleInputFunctions singleInputFunctionsToBePlotted) {
		// This needs to be improved with more settings, such as units etc.
		ExternalGraphPlotter graphMaker = externalGraphMaker(this::valuesForVariable);
		graphMaker.setTitle(title);
		graphMaker.setFunctions(singleInputFunctionsToBePlotted);
		graphMaker.setFromVariableToSetOfValues(getFromVariableToSetOfValues());
		return graphMaker.plot();
	}

}
