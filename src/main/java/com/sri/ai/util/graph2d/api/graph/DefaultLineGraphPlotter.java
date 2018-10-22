package com.sri.ai.util.graph2d.api.graph;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sri.ai.util.graph2d.api.functions.SingleInputFunction;
import com.sri.ai.util.graph2d.api.variables.Assignment;
import com.sri.ai.util.graph2d.api.variables.SetOfValues;
import com.sri.ai.util.graph2d.api.variables.Value;
import com.sri.ai.util.graph2d.api.variables.Variable;
import com.sri.ai.util.graph2d.jfreechart.LineChartFactory;
import com.sri.ai.util.graph2d.jfreechart.LineChartFactory.SeriesEntry;

public class DefaultLineGraphPlotter extends AbstractExternalGraphPlotter {

  @Override
  public GraphPlot plot() {
    Map<String, Number[][]> nameToCoordinates = new LinkedHashMap<>();

    Variable xVariable = getSingleInputFunctionsVariable();
    SetOfValues setOfValuesForX = setOfValuesForVariable.apply(xVariable);


    Variable yOutputVariable = null;

    for (SingleInputFunction yFunction : singleInputFunctionsToBePlotted.getFunctions()) {
      Number[][] coordinates = new Number[getEntryCount(setOfValuesForX)][2];
      nameToCoordinates.put(yFunction.getName(), coordinates);

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

    final File imageFile = new File(getTitle() + ".png");
    final LineChartFactory lineChartFactory = new LineChartFactory()
        .setTitle(getTitle())
        .setxAxisLabel(formatVariable(xVariable, true))
        .setyAxisLabel(formatVariable(yOutputVariable, false));

    nameToCoordinates.forEach((name, coordinates) -> {
      lineChartFactory.addSeries(new SeriesEntry().setKey(name).setDataPoints(coordinates));
    });

    lineChartFactory.generate(imageFile);

    DefaultGraphPlot defaultGraphPlot = new DefaultGraphPlot().setImageFile(imageFile);

    return defaultGraphPlot;
  }

}
