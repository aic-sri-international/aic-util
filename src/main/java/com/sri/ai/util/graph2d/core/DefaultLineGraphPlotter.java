package com.sri.ai.util.graph2d.core;

import com.sri.ai.util.function.core.values.DefaultSetOfValues;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sri.ai.util.function.api.functions.SingleInputFunction;
import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.api.variables.Assignment;
import com.sri.ai.util.function.api.variables.SetOfValues;
import com.sri.ai.util.function.api.variables.Variable;
import com.sri.ai.util.graph2d.api.GraphPlot;
import com.sri.ai.util.graph2d.core.jfreechart.LineChartFactory;
import com.sri.ai.util.graph2d.core.jfreechart.LineChartFactory.SeriesEntry;
import java.util.function.Function;

public class DefaultLineGraphPlotter extends AbstractExternalGraphPlotter {

  @Override
  public GraphPlot plot() {
    Map<String, Number[][]> nameToCoordinates = new LinkedHashMap<>();

    Variable xVariable = getSingleInputFunctionsVariable();
    final SetOfValues setOfValuesForX = setOfValuesForVariable.apply(xVariable);

    Function<Value, Double> xValueToDouble = value -> (setOfValuesForX instanceof DefaultSetOfValues)
        ? (double) setOfValuesForX.getIndexOf(value) : value.doubleValue();

    Variable yOutputVariable = null;

    for (SingleInputFunction yFunction : singleInputFunctionsToBePlotted.getFunctions()) {
      Number[][] coordinates = new Number[getEntryCount(setOfValuesForX)][2];
      nameToCoordinates.put(yFunction.getOutputVariable().getName(), coordinates);

      yOutputVariable = yFunction.getOutputVariable();

      int x = 0;

      for (Value xValue : setOfValuesForX) {
        Assignment assignment = yFunction.makeAssignmentToInputVariable(xValue);
        Value yValue = yFunction.evaluate(assignment);
        coordinates[x][0] = xValueToDouble.apply(xValue);
        coordinates[x][1] = yValue.doubleValue();
        ++x;
      }
    }

    final LineChartFactory lineChartFactory = new LineChartFactory()
        .setGraphSettings(getGraphSettings())
        .setTitle(getTitle())
        .setxAxisLabel(formatVariable(xVariable, true))
        .setyAxisLabel(formatVariable(yOutputVariable, false));

    nameToCoordinates.forEach((name, coordinates) -> {
      lineChartFactory.addSeries(new SeriesEntry().setKey(name).setDataPoints(coordinates));
    });

    File imageFile = createFileForImage();
    lineChartFactory.generate(imageFile);

    GraphPlot graphPlot = new DefaultGraphPlot().setImageFile(imageFile);

    return graphPlot;
  }

}
