package com.sri.ai.util.function.core.variables;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.api.variables.Assignment;
import com.sri.ai.util.function.api.variables.SetOfVariables;
import com.sri.ai.util.function.api.variables.Variable;

public class DefaultAssignment implements Assignment {

  private final SetOfVariables setOfVariables;
  private final Map<Variable, Value> variableToValue;

  public DefaultAssignment(SetOfVariables variables, List<? extends Value> values) {
    Validate.notNull(variables, "variables cannot be null");
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
    Value result = variableToValue.get(variable);
    return result;
  }

  @Override
  public Assignment extend(Variable variable, Value value) {
    Validate.notNull(variable, "variable cannot be null");
    Validate.notNull(value, "value cannot be null");
    Validate.isTrue(!variableToValue.containsKey(variable),
        "variable already has an assigned value");

    List<? extends Variable> variables = setOfVariables.getVariables();
    addVariable(variables, variable);

    Map<Variable, Value> variableToValue = new LinkedHashMap<>(this.variableToValue);
    variableToValue.put(variable, value);

    DefaultSetOfVariables defaultSetOfVariables = new DefaultSetOfVariables(variables);
    DefaultAssignment defaultAssignment
        = new DefaultAssignment(defaultSetOfVariables, variableToValue);

    return defaultAssignment;
  }

  @SuppressWarnings("unchecked")
  private void addVariable(List<? extends Variable> variables , Variable variable) {
    ((List<Variable>) variables).add(variable);
  }

  @Override
  public String toDisplayFormat() {
    StringBuilder sb = new StringBuilder();
    variableToValue.forEach((variable,value)-> {
      if (sb.length() > 0) {
        sb.append(", ");
      }
      sb.append(variable.getName()).append('=').append(value.stringValue());
    });
    return sb.toString();
  }

  @Override
  public String toString() {
    return "DefaultAssignment{" +
        "variableToValue=" + variableToValue +
        '}';
  }
}
