package com.sri.ai.util.graph2d.core;

import com.sri.ai.util.function.api.functions.SingleInputFunction;
import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.api.variables.Assignment;
import com.sri.ai.util.function.api.variables.SetOfValues;
import com.sri.ai.util.function.api.variables.Variable;
import com.sri.ai.util.graph2d.api.GraphPlot;
import com.sri.ai.util.graph2d.core.jfreechart.BarChartFactory;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultBarGraphPlotter extends AbstractExternalGraphPlotter {

  @Override
  public GraphPlot plot() {
    Map<String, Map<String, Double>> chartRowToColumns = new LinkedHashMap<>();

    Variable xVariable = getSingleInputFunctionsVariable();
    final SetOfValues setOfValuesForX = setOfValuesForVariable.apply(xVariable);

    Variable yOutputVariable = null;

    for (SingleInputFunction yFunction : singleInputFunctionsToBePlotted.getFunctions()) {
      Map<String, Double> chartColumnToValue = new LinkedHashMap<>();

      yOutputVariable = yFunction.getOutputVariable();

      for (Value xValue : setOfValuesForX) {
        Assignment assignment = yFunction.makeAssignmentToInputVariable(xValue);
        Value yValue = yFunction.evaluate(assignment);
        chartColumnToValue.put(xValue.stringValue(), yValue.doubleValue());
      }

      if (!chartColumnToValue.isEmpty()) {
        chartRowToColumns.put(yOutputVariable.getName(), chartColumnToValue);
      }
    }

    final BarChartFactory barChartFactory = new BarChartFactory()
        .setTitle(getTitle())
        .setyAxisLabel(formatVariable(yOutputVariable, false));

    chartRowToColumns.forEach((rowKey, columnToValue) -> columnToValue
        .forEach((columnKey, value) -> barChartFactory.addValue(value, rowKey, columnKey)));

    File imageFile = createFileForImage();
    barChartFactory.generate(imageFile);

    GraphPlot graphPlot = new DefaultGraphPlot().setImageFile(imageFile);

    return graphPlot;
  }

}
