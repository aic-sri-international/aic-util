package com.sri.ai.util.graph2d.core;

import com.sri.ai.util.Util;
import com.sri.ai.util.function.api.functions.SingleInputFunction;
import com.sri.ai.util.function.api.functions.SingleInputFunctions;
import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.api.variables.SetOfValues;
import com.sri.ai.util.function.core.values.SetOfEnumValues;
import com.sri.ai.util.graph2d.api.ExternalGeoMapPlotter;
import com.sri.ai.util.graph2d.api.GraphPlot;
import com.sri.ai.util.graph2d.api.MapRegionsUtil;
import com.sri.ai.util.graph2d.api.MapRegionsUtil.Names;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultExternalGeoMapPlotter implements ExternalGeoMapPlotter {
  private Names names;

  public DefaultExternalGeoMapPlotter(SetOfValues setOfValues) {
    List<String> other = new ArrayList<>();
    List<String> counties = new ArrayList<>();
    List<String> states = new ArrayList<>();

    if (setOfValues instanceof SetOfEnumValues) {
      for (Value value : setOfValues) {
        String sval = value.stringValue();

        if (MapRegionsUtil.isCounty(sval)) {
          counties.add(sval);
        } else if (MapRegionsUtil.isState(sval)) {
          states.add(sval);
        } else {
          other.add(sval);
        }
      }

      if ((!counties.isEmpty() || !states.isEmpty()) && !other.isEmpty()) {
        throw new IllegalArgumentException(
            String.format(
                "Unrecognized state or country: %s, States: %s Counties: %s",
                other, states, counties));
      }

      if ((!counties.isEmpty() && !states.isEmpty())) {
        throw new IllegalArgumentException(
            String.format(
                "Cannot include both states and counties: States: %s Counties: %s",
                states, counties));
      }
    }

    if (!counties.isEmpty()) {
      names = MapRegionsUtil.getCountyNames();
    } else if (!states.isEmpty()) {
      names = MapRegionsUtil.getStatesNames();
    }
  }

  public boolean isValid() {
    return names != null;
  }

  public GraphPlot plotGeoMap(SingleInputFunctions singleInputFunctionsToBePlotted) {
    GraphPlot graphPlot = new DefaultGraphPlot();

    SingleInputFunction function = singleInputFunctionsToBePlotted.getFunctions().get(0);

    Map<String, Double> fromRegionNameToValue = Util.map();

    for (Value region : Util.in(function.getInputVariable().getSetOfValuesOrNull().iterator())) {

      Value outputValue = function.evaluate(region);

      String orginalName = names.getNewNameToOriginalNames().get(region.stringValue());
      fromRegionNameToValue.put(orginalName, outputValue.doubleValue());
    }

    graphPlot.setMapRegionNameToValue(fromRegionNameToValue);

    return graphPlot;
  }
}
