package com.sri.ai.util.graph2d.api.variables;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.Validate;

public class DefaultAssignment implements Assignment {

  private final SetOfVariables setOfVariables;
  private final Map<Variable, Value> variableToValue;

  DefaultAssignment(SetOfVariables variables, List<? extends Value> values) {
    Validate.notNull(variables, "SetOfVariables cannot be null");
    Validate.notEmpty(values, "Value list cannot be empty");
    setOfVariables = variables;
    variableToValue = new HashMap<>();

    List<? extends Variable> variableList = variables.getVariables();

    Validate.isTrue(variableList.size() == values.size(),
        "Number of variables=%d not equal to number of values=%d",
        variableList.size(), values.size());

    for (int i = 0; i < variableList.size(); ++i) {
      Variable variable = variableList.get(i);
      Value value = values.get(i);
      variableToValue.put(variable,  value);
    }
  }

  private DefaultAssignment(SetOfVariables setOfVariables, Map<Variable, Value> variableToValue) {
    this.setOfVariables = setOfVariables;
    this.variableToValue = variableToValue;
  }

  @Override
  public SetOfVariables getSetOfVariables() {
    return setOfVariables;
  }

  @Override
  public Value get(Variable variable) {
    Validate.notNull(variable, "Variable cannot be null");
    return variableToValue.get(variable);
  }

  @Override
  public Assignment extend(Variable variable, Value value) {
    Validate.notNull(variable, "Variable cannot be null");
    Validate.notNull(value, "Value cannot be null");
    Validate.isTrue(!variableToValue.containsKey(variable),
        "Variable already has an assigned value");

    List<? extends Variable> variables = setOfVariables.getVariables();
    addVariable(variables, variable);

    Map<Variable, Value> variableToValue = new HashMap<>(this.variableToValue);
    variableToValue.put(variable, value);
    return new DefaultAssignment(new DefaultSetOfVariables(variables), variableToValue);
  }

  @SuppressWarnings("unchecked")
  private void addVariable(List<? extends Variable> variables , Variable variable) {
    ((List<Variable>) variables).add(variable);
  }
}
