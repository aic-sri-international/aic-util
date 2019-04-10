package com.sri.ai.util.function.core.variables;

import static com.sri.ai.util.Util.join;
import static com.sri.ai.util.Util.map;
import static com.sri.ai.util.function.api.values.Value.value;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.function.Function;
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

	public DefaultAssignment(Map<Variable, Value> variableToValue) {
		this(new DefaultSetOfVariables(variableToValue.keySet()), variableToValue);
	}

	private DefaultAssignment(SetOfVariables setOfVariables, Map<Variable, Value> variableToValue) {
		this.setOfVariables = setOfVariables;
		this.variableToValue = variableToValue;
	}

	public static DefaultAssignment assignment(Object... variablesAndValues) {
		for (int i = 1; i != variablesAndValues.length + 1; i += 2) { // NOTE: unusual for loop over odd positions only
			Object object = variablesAndValues[i];
			variablesAndValues[i] = (object instanceof Value)? object : value(object);
		}
		DefaultAssignment result = new DefaultAssignment(map(variablesAndValues));
		return result;
	}

	@Override
	public SetOfVariables getSetOfVariables() {
		return setOfVariables;
	}

	@Override
	public Assignment exclude(SetOfVariables setOfVariables) {
		Map<Variable, Value> newMap = map();
		for (Map.Entry<Variable, Value> entry : variableToValue.entrySet()) {
			if ( ! setOfVariables.getVariables().contains(entry.getKey())) {
				newMap.put(entry.getKey(), entry.getValue());
			}
		}
		return new DefaultAssignment(newMap);
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

	@Override
	public Assignment remove(Variable variableToRemove) {

		Map<Variable, Value> variableToValue = new LinkedHashMap<>(this.variableToValue);
		variableToValue.remove(variableToRemove);

		return new DefaultAssignment(setOfVariables.minus(variableToRemove), variableToValue);
	}

	@SuppressWarnings("unchecked")
	private void addVariable(List<? extends Variable> variables , Variable variable) {
		((List<Variable>) variables).add(variable);
	}

	@Override
	public String toDisplayFormat(Function<Double, String> decimalFormatter) {
		StringBuilder sb = new StringBuilder();

		variableToValue.forEach((variable,value)-> {
			if (sb.length() > 0) {
				sb.append(", ");
			}

			sb.append(variable.getName()).append('=');

			if (variable instanceof RealVariable) {
				sb.append(decimalFormatter.apply(value.doubleValue()));
			} else {
				sb.append(value.stringValue());
			}
		});
		return sb.toString();
	}

	@Override
	public String toString() {
		return "{" + join(variableToValue.entrySet()) + "}";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((setOfVariables == null) ? 0 : setOfVariables.hashCode());
		result = prime * result + ((variableToValue == null) ? 0 : variableToValue.hashCode());
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
		DefaultAssignment other = (DefaultAssignment) obj;
		if (setOfVariables == null) {
			if (other.setOfVariables != null)
				return false;
		} else if (!setOfVariables.equals(other.setOfVariables))
			return false;
		if (variableToValue == null) {
			if (other.variableToValue != null)
				return false;
		} else if (!variableToValue.equals(other.variableToValue))
			return false;
		return true;
	}
}
