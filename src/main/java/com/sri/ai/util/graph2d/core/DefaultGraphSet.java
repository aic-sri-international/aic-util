package com.sri.ai.util.graph2d.core;

import java.util.ArrayList;
import java.util.List;

import com.sri.ai.util.function.api.functions.Functions;
import com.sri.ai.util.graph2d.api.GraphPlot;
import com.sri.ai.util.graph2d.api.GraphSet;

public class DefaultGraphSet implements GraphSet {
  private Functions functions;
  private List<? extends GraphPlot> graphPlots = new ArrayList<>();

  public DefaultGraphSet(Functions functions) {
    this.functions = functions;
  }

  @Override
  public Functions getFunctions() {
    return functions;
  }

  @Override
  public List<? extends GraphPlot> getGraphPlots() {
    return graphPlots;
  }

  @Override
  public void add(GraphPlot plot) {
    addPlot(plot);
  }

  @SuppressWarnings("unchecked")
  private void addPlot(GraphPlot plot) {
    ((List<GraphPlot>) graphPlots).add(plot);
  }

  @Override
  public String toString() {
    return "DefaultGraphSet{" +
        "graphPlots=" + graphPlots +
        '}';
  }
}
