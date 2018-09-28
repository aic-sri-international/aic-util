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
			GraphPlot plot = makePlot(assignmentToNonAxisVariables, xAxisVariable);
			graphSet.add(plot);
		}
		
		return graphSet;
	}
	
	default TupleOfVariables getNonAxisVariables(Variable xAxisVariable) {
		TupleOfVariables nonAxisVariables = getFunctions().getAllInputVariables().minus(xAxisVariable);
		return nonAxisVariables;
	}
	
	default GraphPlot makePlot(Assignment assignmentToNonAxisVariables, Variable xAxisVariable) {
		
		ExternalGraphPlotter graphMaker = externalGraphMaker();
		graphMaker.setTitle(assignmentToNonAxisVariables.toString()); // there should be other settings here, such as units, etc.
		SingleInputFunctions plottedSingleInputFunctions = getFunctions().project(xAxisVariable, assignmentToNonAxisVariables);
		graphMaker.setFunctions(plottedSingleInputFunctions);
		return graphMaker.plot();
		
	}

}
