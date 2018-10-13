package com.sri.ai.util.graph2d.api.graph;

public class DefaultLineGraphPlotter extends AbstractExternalGraphPlotter {

  @Override
  public GraphPlot plot() {
    DefaultGraphPlot defaultGraphPlot = new DefaultGraphPlot();
    System.err.println(" --Start plot()--");
    System.err.println(singleInputFunctionsToBePlotted.getAllInputVariables().getVariables());
    System.err.println("============================================");
    fromVariableToSetOfValues
        .forEach((key, value) -> System.err.println("Key=" + key.toString() + " : Value=" + value.toString()));
    System.err.println(" --End plot()--");

    return defaultGraphPlot;
  }
}
