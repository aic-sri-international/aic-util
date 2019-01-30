package com.sri.ai.util.function.core.variables;

import static com.sri.ai.util.Util.getByExplicitlyIterating;
import static com.sri.ai.util.Util.join;

import java.util.ArrayList;
import java.util.Collection;
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

  public DefaultSetOfVariables(LinkedHashSet<? extends Variable> variables) {
	    this.variables = Collections.unmodifiableSet(variables);
	  }

  public DefaultSetOfVariables(Collection<? extends Variable> variables) {
	    this(new LinkedHashSet<>(variables));
	  }

  @Override
  public ArrayList<? extends Variable> getVariables() {
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
    return "{" + join(variables) + "}";
  }

  @Override
  public Variable get(int i) {
	  return getByExplicitlyIterating(variables, i);
  }

@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((variables == null) ? 0 : variables.hashCode());
	return result;
}

@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	DefaultSetOfVariables other = (DefaultSetOfVariables) obj;
	if (variables == null) {
		if (other.variables != null)
			return false;
	} else if (!variables.equals(other.variables))
		return false;
	return true;
}

}
