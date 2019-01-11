package com.sri.ai.util.function.api.functions;

import java.util.ArrayList;
import java.util.List;

public class DefaultSingleInputFunctions implements SingleInputFunctions {
  private List<? extends SingleInputFunction> functions = new ArrayList<>();

  @Override
  public List<? extends SingleInputFunction> getFunctions() {
    return functions;
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
