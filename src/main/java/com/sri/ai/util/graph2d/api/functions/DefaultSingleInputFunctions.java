package com.sri.ai.util.graph2d.api.functions;

import com.sri.ai.util.graph2d.api.variables.SetOfVariables;
import com.sri.ai.util.graph2d.api.variables.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultSingleInputFunctions implements SingleInputFunctions {
  private List<? extends SingleInputFunction> functions = new ArrayList<>();

  @Override
  public List<? extends SingleInputFunction> getFunctions() {
    return functions;
  }

  @Override
  public SetOfVariables getAllInputVariables() {
    List<Variable> variables = functions.stream()
        .map(SingleInputFunction::getInputVariable)
        .collect(Collectors.toList());

    SetOfVariables setOfVariables = SetOfVariables.setOfVariables(variables);
    return setOfVariables;
  }

  @Override
  public void add(SingleInputFunction singleInputFunction) {
    addSingleInputFunction(singleInputFunction);
  }

  @SuppressWarnings("unchecked")
  private void addSingleInputFunction(SingleInputFunction singleInputFunction) {
    ((List<SingleInputFunction>) functions).add(singleInputFunction);
  }
}
