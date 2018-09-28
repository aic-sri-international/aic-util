package com.sri.ai.util.graph2d.api.graph;

import com.sri.ai.util.graph2d.api.functions.SingleInputFunctions;

/** 
 * Interface for external libraries making images of graph plots.
 * Implementations of this interface must dispatch the task to those libraries.
 * Different types of graphs (bar graphs, line graphs) will be different implementations of this interface.
 * 
 * @author braz
 *
 */
public interface ExternalGraphPlotter {

	public static ExternalGraphPlotter externalGraphMaker() {
		// TODO implement default implementation class and create instance here
		return null;
	}
	
	/** The functions to be plotted. They must be single-input because the graphs are 2D. */
	SingleInputFunctions getFunctions();
	void setFunctions(SingleInputFunctions functions);
	
	String getTitle();
	void setTitle(String title);
	
	GraphPlot plot();
	
}
