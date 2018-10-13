package com.sri.ai.util.graph2d.api.graph;

public class DefaultBarGraphPlotter extends AbstractExternalGraphPlotter {

  @Override
  public GraphPlot plot() {
    DefaultGraphPlot defaultGraphPlot = new DefaultGraphPlot();

    fromVariableToSetOfValues
        .forEach((key, value) -> System.err.println(key.toString() + " : " + value.toString()));

    return defaultGraphPlot;
  }
}
