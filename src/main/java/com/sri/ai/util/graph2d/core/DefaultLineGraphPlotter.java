package com.sri.ai.util.graph2d.core;

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

public class DefaultLineGraphPlotter extends AbstractExternalGraphPlotter {

  @Override
  public GraphPlot plot() {
    Map<String, Number[][]> nameToCoordinates = new LinkedHashMap<>();

    Variable xVariable = getSingleInputFunctionsVariable();
    SetOfValues setOfValuesForX = setOfValuesForVariable.apply(xVariable);


    Variable yOutputVariable = null;

    for (SingleInputFunction yFunction : singleInputFunctionsToBePlotted.getFunctions()) {
      Number[][] coordinates = new Number[getEntryCount(setOfValuesForX)][2];
      nameToCoordinates.put(yFunction.getOutputVariable().getName(), coordinates);

      yOutputVariable = yFunction.getOutputVariable();

      int x = 0;

      for (Value xValue : setOfValuesForX) {
        Assignment assignment = yFunction.makeAssignmentToInputVariable(xValue);
        Value yValue = yFunction.evaluate(assignment);
        coordinates[x][0] = xValue.doubleValue();
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

    DefaultGraphPlot defaultGraphPlot = new DefaultGraphPlot().setImageFile(imageFile);

    return defaultGraphPlot;
  }

}
