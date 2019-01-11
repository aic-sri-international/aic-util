package com.sri.ai.util.graph2d.api;

import static com.sri.ai.util.graph2d.api.ExternalGraphPlotter.externalGraphMaker;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sri.ai.util.function.api.functions.Functions;
import com.sri.ai.util.function.api.functions.SingleInputFunction;
import com.sri.ai.util.function.api.functions.SingleInputFunctions;
import com.sri.ai.util.function.api.variables.Assignment;
import com.sri.ai.util.function.api.variables.SetOfValues;
import com.sri.ai.util.function.api.variables.SetOfVariables;
import com.sri.ai.util.function.api.variables.Variable;
import com.sri.ai.util.graph2d.core.DefaultGraphSetMaker;
import com.sri.ai.util.graph2d.core.jfreechart.GraphSettings;

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

	GraphSettings getGraphSettings();

	void setGraphSettings(GraphSettings graphSettings);

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
		String names = singleInputFunctions.stream()
				.map(f -> f.getOutputVariable().getName())
				.collect(Collectors.joining(delimiter));

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
		graphMaker.setGraphSettings(getGraphSettings());
		graphMaker.setTitle(title);
		graphMaker.setFunctions(singleInputFunctionsToBePlotted);
		graphMaker.setFromVariableToSetOfValues(getFromVariableToSetOfValues());
		return graphMaker.plot();
	}

}
