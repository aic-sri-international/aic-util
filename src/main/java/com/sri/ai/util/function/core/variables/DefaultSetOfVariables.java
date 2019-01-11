package com.sri.ai.util.function.core.variables;

import static com.sri.ai.util.Util.getByExplicitlyIterating;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import com.sri.ai.util.Util;
import com.sri.ai.util.function.api.variables.SetOfVariables;
import com.sri.ai.util.function.api.variables.Variable;

public class DefaultSetOfVariables implements SetOfVariables {

  private final Set<? extends Variable> variables;

  public DefaultSetOfVariables(List<? extends Variable> variables) {
	    this(new LinkedHashSet<>(variables));
	  }

  public DefaultSetOfVariables(Variable variable) {
    Validate.notNull(variable, "Variable cannot be null");
    variables = Collections.unmodifiableSet(Collections.singleton(variable));
  }

  private DefaultSetOfVariables(LinkedHashSet<? extends Variable> variables) {
    this.variables = Collections.unmodifiableSet(variables);
  }

  @Override
  public List<? extends Variable> getVariables() {
    return new ArrayList<>(variables);
  }

  @Override
  public Variable getFirst() {
    Variable firstVariable = variables.iterator().next();
    return firstVariable;
  }

  @Override
  public SetOfVariables minus(Variable variable) {
    List<? extends Variable> variables = Util.removeNonDestructively(this.variables, variable);
    DefaultSetOfVariables defaultSetOfVariables = new DefaultSetOfVariables(variables);
    return defaultSetOfVariables;
  }

  @Override
  public int size() {
	  return variables.size();
  }

  @Override
  public String toString() {
    return "DefaultSetOfVariables{" +
        "variables=" + variables +
        '}';
  }

  @Override
  public Variable get(int i) {
	  return getByExplicitlyIterating(variables, i);
  }

}
