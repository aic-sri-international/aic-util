package com.sri.ai.util.graph2d.api.graph;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.sri.ai.util.graph2d.api.functions.SingleInputFunctions;
import com.sri.ai.util.graph2d.api.variables.SetOfValues;
import com.sri.ai.util.graph2d.api.variables.Variable;
import java.util.function.Function;

/** 
 * Interface for external libraries making images of graph plots.
 * Implementations of this interface must dispatch the task to those libraries.
 * Different types of graphs (bar graphs, line graphs) will be different implementations of this interface.
 * 
 * @author braz
 *
 */
public interface ExternalGraphPlotter {

	static ExternalGraphPlotter externalGraphMaker(Function<Variable, SetOfValues> setOfValuesForVariable) {
    AbstractExternalGraphPlotter abstractExternalGraphPlotter = new DefaultLineGraphPlotter();
    abstractExternalGraphPlotter.setSetOfValuesForVariable(setOfValuesForVariable);
		return abstractExternalGraphPlotter;
	}
	
	/** The functions to be plotted. They must be single-input because the graphs are 2D. */
	SingleInputFunctions getFunctions();
	void setFunctions(SingleInputFunctions functions);
	
	String getTitle();
	void setTitle(String title);

	/**
	 * Receives the values to be used for the plot.
	 */
	void setFromVariableToSetOfValues(Map<Variable, SetOfValues> fromVariableToSetOfValues);
	
	GraphPlot plot();

	default File createFileForImage() {
		File imageFile;
		try {
			imageFile = File.createTempFile("graph2d-", ".png");
		} catch (IOException e) {
			throw new RuntimeException("Cannot create tmpfile for Image", e);
		}
		return imageFile;
	}

}
