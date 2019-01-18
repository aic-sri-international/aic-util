package com.sri.ai.util.graph2d.core;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.sri.ai.util.function.api.functions.SingleInputFunctions;
import com.sri.ai.util.function.api.variables.SetOfValues;
import com.sri.ai.util.function.api.variables.Unit;
import com.sri.ai.util.function.api.variables.Variable;
import com.sri.ai.util.graph2d.api.ExternalGraphPlotter;
import com.sri.ai.util.graph2d.core.jfreechart.GraphSettings;

public abstract class AbstractExternalGraphPlotter implements ExternalGraphPlotter {
	private GraphSettings graphSettings;
	protected String title;
	Function<Variable, SetOfValues> setOfValuesForVariable;
	SingleInputFunctions singleInputFunctionsToBePlotted;
	Map<Variable, SetOfValues> fromVariableToSetOfValues;

	@Override
	public GraphSettings getGraphSettings() {
		return graphSettings;
	}

	@Override
	public void setGraphSettings(GraphSettings graphSettings) {
		this.graphSettings = graphSettings;
	}

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

	public void setSetOfValuesForVariable(
			Function<Variable, SetOfValues> setOfValuesForVariable) {

		this.setOfValuesForVariable = setOfValuesForVariable;
	}

	Variable getSingleInputFunctionsVariable() {
		List<? extends Variable> variables = singleInputFunctionsToBePlotted.getAllInputVariables().getVariables();
		Validate.isTrue(variables.size() == 1, "Expected 1, but got %d", variables.size());
		return variables.get(0);
	}

	private String filePathname = "";
	
	@Override
	public String getFilePathname() {
		return filePathname;
	}

	@Override
	public void setFilePathname(String filePathname) {
		this.filePathname = filePathname;
	}

	@Override
	public void setFromVariableToSetOfValues(Map<Variable, SetOfValues> fromVariableToSetOfValues) {
		this.fromVariableToSetOfValues = fromVariableToSetOfValues;
	}

	int getEntryCount(SetOfValues setOfValues) {
		int count = 0;
		for (Iterator iter = setOfValues.iterator(); iter.hasNext(); ++count) {
			iter.next();
		}
		return count;
	}

	String formatVariable(Variable variable, boolean useVariableName) {
		String formatted = null;

		if (variable != null) {
			String name = null;

			if (useVariableName) {
				name = variable.getName();
			} else if (!Unit.NONE.getName().equals(variable.getUnit().getName())) {
				name = variable.getUnit().getName();
			}

			if (StringUtils.trimToNull(variable.getUnit().getSymbol()) != null) {
				formatted =  name + " (" + variable.getUnit().getSymbol() + ')';
			} else {
				formatted =  name;
			}
		}
		if (formatted != null && formatted.length() == 1) {
			formatted = null;
		}
		return formatted;
	}
}
