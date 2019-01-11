package com.sri.ai.util.graph2d.api;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

import com.sri.ai.util.function.api.functions.SingleInputFunctions;
import com.sri.ai.util.function.api.variables.SetOfValues;
import com.sri.ai.util.function.api.variables.Variable;
import com.sri.ai.util.graph2d.core.AbstractExternalGraphPlotter;
import com.sri.ai.util.graph2d.core.DefaultLineGraphPlotter;
import com.sri.ai.util.graph2d.core.jfreechart.GraphSettings;

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

	GraphSettings getGraphSettings();

	void setGraphSettings(GraphSettings graphSettings);

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
