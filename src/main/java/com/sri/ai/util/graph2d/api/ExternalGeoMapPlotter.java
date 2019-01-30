package com.sri.ai.util.graph2d.api;

import com.sri.ai.util.function.api.functions.SingleInputFunctions;

/** 
 * Interface for external libraries used for maps.
 * Implementations of this interface must dispatch the task to those libraries.
 *
 */
public interface ExternalGeoMapPlotter {
	boolean isValid();
	GraphPlot plotGeoMap(SingleInputFunctions singleInputFunctionsToBePlotted);
}
