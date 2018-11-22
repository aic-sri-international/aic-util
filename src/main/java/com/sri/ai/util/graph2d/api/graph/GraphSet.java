package com.sri.ai.util.graph2d.api.graph;

import java.util.List;

import com.sri.ai.util.graph2d.api.functions.Functions;

/**
 * An interface for collections of 2D graph plots generated from a {@link Functions} object
 * and a chosen variable,
 * as well as any other useful information is associated with them
 * (such as the {@link Functions} object from which it was generated).
 * 
 * @author braz
 *
 */
public interface GraphSet {

	Functions getFunctions();
	
	List<? extends GraphPlot> getGraphPlots();

	static GraphSet graphSet(Functions functions) {
		DefaultGraphSet defaultGraphSet = new DefaultGraphSet(functions);
		return defaultGraphSet;
	}

	void add(GraphPlot plot);
}
