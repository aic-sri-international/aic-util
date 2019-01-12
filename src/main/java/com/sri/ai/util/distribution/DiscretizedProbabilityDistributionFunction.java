package com.sri.ai.util.distribution;

import static com.sri.ai.util.Util.mapIntoArrayList;

import java.util.ArrayList;
import java.util.List;

import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.api.variables.Assignment;
import com.sri.ai.util.function.api.variables.SetOfVariables;
import com.sri.ai.util.function.api.variables.Unit;
import com.sri.ai.util.function.api.variables.Variable;
import com.sri.ai.util.function.core.functions.AbstractFunction;
import com.sri.ai.util.function.core.variables.RealVariable;

/**
 * Adds functionality to {@link DiscretizedConditionalProbabilityDistribution} so it works as a {@link com.sri.ai.util.function.api.functions.Function}.
 * 
 * @author braz
 *
 */
public class DiscretizedProbabilityDistributionFunction extends AbstractFunction {

	protected DiscretizedConditionalProbabilityDistribution conditionalDistribution;
	
	protected int queryVariableIndex;
	protected WeightedFrequencyArrayConditionalDistribution indexDistribution;

	/////////////////////////////////

	protected DiscretizedProbabilityDistributionFunction(SetOfVariables setOfInputVariablesWithRange, int queryVariableIndex) {
		
		super(makeOutputVariable(setOfInputVariablesWithRange.get(queryVariableIndex)), setOfInputVariablesWithRange);

		conditionalDistribution = new DiscretizedConditionalProbabilityDistribution(setOfInputVariablesWithRange, queryVariableIndex);
		
		this.queryVariableIndex = queryVariableIndex;

		int numberOfQueryValueIndices = getInputVariables().getVariables().get(queryVariableIndex).getSetOfValuesOrNull().size() + 1;
		this.indexDistribution = new WeightedFrequencyArrayConditionalDistribution(numberOfQueryValueIndices);

	}
	
	public static RealVariable makeOutputVariable(Variable queryVariable) {
		return new RealVariable("P(" + queryVariable.getName() + " | ...)", Unit.NONE);
	}
	
	/////////////////////////////////

	@Override
	public Value evaluate(Assignment assignmentToInputVariables) {
		ArrayList<Object> valueObjects = getValues(assignmentToInputVariables);
		return conditionalDistribution.evaluate(valueObjects);
	}

	private ArrayList<Object> getValues(Assignment assignment) {
		ArrayList<Object> valueObjects = mapIntoArrayList(getVariables(), v -> assignment.get(v).objectValue());
		return valueObjects;
	}
	
	protected List<? extends Variable> getVariables() {
		return getInputVariables().getVariables();
	}

	@Override
	public String getName() {
		return "P(" + getInputVariables().getVariables().get(queryVariableIndex).getName() + " | ...)";
	}

	/////////////////////////////////

	public void register(ArrayList<Object> valueObjects, double weight) {
		conditionalDistribution.register(valueObjects, weight);
	}

}