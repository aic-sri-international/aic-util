package com.sri.ai.util.graph2d.api.graph;

import com.sri.ai.util.graph2d.api.functions.SingleInputFunctions;
import com.sri.ai.util.graph2d.api.variables.SetOfValues;
import com.sri.ai.util.graph2d.api.variables.Variable;
import java.util.Map;

public abstract class AbstractExternalGraphPlotter implements ExternalGraphPlotter {
  protected String title;
  protected SingleInputFunctions singleInputFunctionsToBePlotted;
  protected Map<Variable, SetOfValues> fromVariableToSetOfValues;

  @Override
  public SingleInputFunctions getFunctions() {
    return singleInputFunctionsToBePlotted;
  }

  @Override
  public void setFunctions(SingleInputFunctions singleInputFunctionsToBePlotted) {
    this.singleInputFunctionsToBePlotted = singleInputFunctionsToBePlotted;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public void setFromVariableToSetOfValues(Map<Variable, SetOfValues> fromVariableToSetOfValues) {
    this.fromVariableToSetOfValues = fromVariableToSetOfValues;
  }
}
