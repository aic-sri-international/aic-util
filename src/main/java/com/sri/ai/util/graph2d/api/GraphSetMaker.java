package com.sri.ai.util.graph2d.api;

import static com.sri.ai.util.Util.join;
import static com.sri.ai.util.Util.println;
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
import com.sri.ai.util.graph2d.core.DefaultExternalGeoMapPlotter;
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
	
	SetOfValues getValuesForVariable(Variable variable);
	void setValuesForVariable(Variable variable, SetOfValues setOfValues);

	/** 
	 * The base for the file pathnames to use for each plot; 
	 * the actual pathname will be the concatenation of the base and the plot's title.
	 * If not provided, temp files will be created.
	 */
	String getFilePathnameBase();
	void setFilePathnameBase(String filePathname);

	
	Iterable<Assignment> assignments(SetOfVariables setOfVariables);

	default GraphSet make(Variable xAxisVariable) {
		
		GraphSet graphSet = GraphSet.graphSet(getFunctions());
		
		SetOfVariables nonAxisVariables = getNonAxisVariables(xAxisVariable);
		
		for (Assignment assignmentToNonAxisVariables: assignments(nonAxisVariables)) {
			println(assignmentToNonAxisVariables);
			GraphPlot plot = plot(assignmentToNonAxisVariables, xAxisVariable);
			graphSet.add(plot);
		}
		
		return graphSet;
	}
	
	default SetOfVariables getNonAxisVariables(Variable xAxisVariable) {
		SetOfVariables nonAxisVariables = getFunctions().getAllInputVariables().minus(xAxisVariable);
		return nonAxisVariables;
	}

	default GraphPlot plot(Assignment assignmentToNonAxisVariables, Variable xAxisVariable) {
		SingleInputFunctions singleInputFunctionsToBePlotted
				= getFunctions().project(xAxisVariable, assignmentToNonAxisVariables);

		SetOfValues oldXAxisSetOfValues = useValuesFromCurrentlyGivenXAxisVariableIfProvidedEvenIfDifferentFromOriginalVariable(xAxisVariable);
		
		String title = buildTitle(assignmentToNonAxisVariables, singleInputFunctionsToBePlotted);
		
		GraphPlot result = plot(title, singleInputFunctionsToBePlotted, assignmentToNonAxisVariables);
		
		setValuesForVariable(xAxisVariable, oldXAxisSetOfValues);
		
		return result;
		
	}

	default SetOfValues useValuesFromCurrentlyGivenXAxisVariableIfProvidedEvenIfDifferentFromOriginalVariable(Variable currentXAxisVariable) {
		// this method covers the case in which a variable is specified for the x-axis
		// that has the same name as the variable in the function, but a different set of values
		SetOfValues old = getValuesForVariable(currentXAxisVariable);
		SetOfValues currentXAxisVariableSetOfValuesOrNull = currentXAxisVariable.getSetOfValuesOrNull();
		if (currentXAxisVariableSetOfValuesOrNull != null) {
			setValuesForVariable(currentXAxisVariable, currentXAxisVariableSetOfValuesOrNull);
		}
		return old;
	}
	
	default String buildTitle(Assignment assignmentToNonAxisVariables,
														SingleInputFunctions singleInputFunctionsToBePlotted) {
		List<? extends SingleInputFunction> singleInputFunctions
				= singleInputFunctionsToBePlotted.getFunctions();

		final String delimiter = singleInputFunctions.size() == 2 ? " & " : ", ";
		String names = singleInputFunctions.stream()
				.map(f -> f.getOutputVariable().getName())
				.collect(Collectors.joining(delimiter));

//		String inputVariableName = singleInputFunctionsToBePlotted.getInputVariable().getName();
//		String xAxisDescription = inputVariableName.length() > 1? " by " + inputVariableName : "";
//      usually in the output variable name already anyway 
		String xAxisDescription = "";
		
		int numberOfRemainingVariables = assignmentToNonAxisVariables.size();
		String remainingVariablesDescription = numberOfRemainingVariables > 0? " for " + assignmentToNonAxisVariables.toDisplayFormat() : "";
		
		return names + xAxisDescription + remainingVariablesDescription;
	}
	
	default GraphPlot plot(String title, SingleInputFunctions singleInputFunctionsToBePlotted, Assignment assignment) {
		SetOfValues setOfValues = singleInputFunctionsToBePlotted.getInputVariable().getSetOfValuesOrNull();
		DefaultExternalGeoMapPlotter externalGeoMapPlotter = new DefaultExternalGeoMapPlotter(setOfValues);
		if (externalGeoMapPlotter.isValid()) {
			return externalGeoMapPlotter.plotGeoMap(singleInputFunctionsToBePlotted);
		} else {
			return plotLineChart(title, singleInputFunctionsToBePlotted, assignment);
		}
	}

	default GraphPlot plotLineChart(String title, SingleInputFunctions singleInputFunctionsToBePlotted, Assignment assignment) {
    ExternalGraphPlotter graphMaker = externalGraphMaker(this::getValuesForVariable);
    graphMaker.setGraphSettings(getGraphSettings());
    graphMaker.setTitle(title);
    graphMaker.setFunctions(singleInputFunctionsToBePlotted);
    graphMaker.setFromVariableToSetOfValues(getFromVariableToSetOfValues());
    String filePathname;
    if (getFilePathnameBase() == "") {
      filePathname = "";
    }
    else {
      String assignmentInFileName = join("", assignment.indices(this::getValuesForVariable));
      filePathname = getFilePathnameBase() + assignmentInFileName;
    }
    graphMaker.setFilePathname(filePathname);
    return graphMaker.plot();
  }
}
