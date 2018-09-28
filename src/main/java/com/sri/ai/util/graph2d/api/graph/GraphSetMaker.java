package com.sri.ai.util.graph2d.api.graph;

import static com.sri.ai.util.graph2d.api.graph.ExternalGraphPlotter.externalGraphMaker;

import com.sri.ai.util.graph2d.api.functions.Functions;
import com.sri.ai.util.graph2d.api.functions.SingleInputFunctions;
import com.sri.ai.util.graph2d.api.variables.Assignment;
import com.sri.ai.util.graph2d.api.variables.TupleOfVariables;
import com.sri.ai.util.graph2d.api.variables.Variable;

/**
 * An interface for classes generating sets of {@link GraphSet}s from {@link Functions}.
 * 
 * @author braz
 *
 */
public interface GraphSetMaker {
	
	Functions getFunctions();
	void setFunctions(Functions functions);
	
	default GraphSet make(Variable xAxisVariable) {
		
		GraphSet graphSet = GraphSet.graphSet();
		
		TupleOfVariables nonAxisVariables = getNonAxisVariables(xAxisVariable);
		
		for (Assignment assignmentToNonAxisVariables: nonAxisVariables.assignments()) {
			GraphPlot plot = plot(assignmentToNonAxisVariables, xAxisVariable);
			graphSet.add(plot);
		}
		
		return graphSet;
	}
	
	default TupleOfVariables getNonAxisVariables(Variable xAxisVariable) {
		TupleOfVariables nonAxisVariables = getFunctions().getAllInputVariables().minus(xAxisVariable);
		return nonAxisVariables;
	}
	
	default GraphPlot plot(Assignment assignmentToNonAxisVariables, Variable xAxisVariable) {
		
		SingleInputFunctions singleInputFunctionsToBePlotted = getFunctions().project(xAxisVariable, assignmentToNonAxisVariables);
		String title = assignmentToNonAxisVariables.toString();
		return plot(title, singleInputFunctionsToBePlotted);
		
	}
	
	default GraphPlot plot(String title, SingleInputFunctions singleInputFunctionsToBePlotted) {
		// This needs to be improved with more settings, such as units etc.
		ExternalGraphPlotter graphMaker = externalGraphMaker();
		graphMaker.setTitle(title);
		graphMaker.setFunctions(singleInputFunctionsToBePlotted);
		return graphMaker.plot();
	}

}
