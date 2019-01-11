package com.sri.ai.util.graph2d.core;

import com.sri.ai.util.graph2d.api.GraphPlot;

public class DefaultBarGraphPlotter extends AbstractExternalGraphPlotter {

  @Override
  public GraphPlot plot() {
    DefaultGraphPlot defaultGraphPlot = new DefaultGraphPlot();

    fromVariableToSetOfValues
        .forEach((key, value) -> System.err.println(key.toString() + " : " + value.toString()));

    return defaultGraphPlot;
  }
}
