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
public class DiscretizedConditionalProbabilityDistributionFunction extends AbstractFunction {

	protected DiscretizedConditionalProbabilityDistribution conditionalDistribution;
	
	protected WeightedFrequencyArrayConditionalDistribution indexDistribution;

	/////////////////////////////////

	public DiscretizedConditionalProbabilityDistributionFunction(DiscretizedConditionalProbabilityDistribution conditionalDistribution) {
		
		super(makeOutputVariable(conditionalDistribution), conditionalDistribution.getSetOfVariablesWithRange());

		this.conditionalDistribution = conditionalDistribution;
		
		int numberOfQueryValueIndices = getSetOfInputVariables().getVariables().get(getQueryVariableIndex()).getSetOfValuesOrNull().size() + 1;
		this.indexDistribution = new WeightedFrequencyArrayConditionalDistribution(numberOfQueryValueIndices);

	}

	public DiscretizedConditionalProbabilityDistributionFunction(SetOfVariables setOfInputVariablesWithRange, int queryVariableIndex) {
		this(new DiscretizedConditionalProbabilityDistribution(setOfInputVariablesWithRange, queryVariableIndex));
	}
	
	private static RealVariable makeOutputVariable(DiscretizedConditionalProbabilityDistribution conditionalDistribution) {
		return makeOutputVariable(conditionalDistribution.getSetOfVariablesWithRange().get(conditionalDistribution.getQueryVariableIndex()));
	}
	
	public static RealVariable makeOutputVariable(Variable queryVariable) {
		return new RealVariable("P(" + queryVariable.getName() + " | ...)", Unit.NONE);
	}

	//////////////////////////////

	public SetOfVariables getSetOfVariablesWithRange() {
		return conditionalDistribution.getSetOfVariablesWithRange();
	}

	public int getQueryVariableIndex() {
		return conditionalDistribution.getQueryVariableIndex();
	}

	public DiscretizedConditionalProbabilityDistribution getConditionalDistribution() {
		return conditionalDistribution;
	}

	/////////////////////////////////

	public void register(ArrayList<Object> valueObjects, double weight) {
		conditionalDistribution.register(valueObjects, weight);
	}
	
	/////////////////////////////////

	@Override
	public Value evaluate(Assignment assignmentToInputVariables) {
		ArrayList<Object> valueObjects = getValues(assignmentToInputVariables);
		return conditionalDistribution.apply(valueObjects);
	}

	private ArrayList<Object> getValues(Assignment assignment) {
		ArrayList<Object> valueObjects = mapIntoArrayList(getVariables(), v -> assignment.get(v).objectValue());
		return valueObjects;
	}
	
	protected List<? extends Variable> getVariables() {
		return getSetOfInputVariables().getVariables();
	}

	@Override
	public String getName() {
		return "P(" + getSetOfInputVariables().getVariables().get(getQueryVariableIndex()).getName() + " | ...)";
	}

}