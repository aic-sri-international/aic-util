package com.sri.ai.util.graph2d.api;

import java.io.File;
import java.util.Map;

import com.sri.ai.util.function.api.functions.Functions;
import com.sri.ai.util.function.api.functions.SingleInputFunctions;

/**
 * An interface for objects containing a 2D graph plot image and whatever other useful information is associated with it,
 * such as the {@link Functions} and {@link SingleInputFunctions} objects from which it was generated,
 * and the set of values for the non-driving variables for which it corresponds
 * (that is, the variables not used in either axis of the graph plot).
 * 
 * @author braz
 *
 */
public interface GraphPlot {

  Map<String, Double> getRegionToValue();

  GraphPlot setMapRegionNameToValue(Map<String, Double> mapRegionNameToValue);

  File getImageFile();

  GraphPlot setImageFile(File imageFile);
}
