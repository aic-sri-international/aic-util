package com.sri.ai.util.distribution;

import static com.sri.ai.util.Util.mapIntoArrayList;

import java.util.ArrayList;
import java.util.List;

import com.sri.ai.util.function.api.functions.SingleInputFunction;
import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.api.variables.Assignment;
import com.sri.ai.util.function.api.variables.SetOfVariables;
import com.sri.ai.util.function.api.variables.Unit;
import com.sri.ai.util.function.api.variables.Variable;
import com.sri.ai.util.function.core.functions.AbstractFunction;
import com.sri.ai.util.function.core.functions.DefaultProjectionSingleInputFunction;
import com.sri.ai.util.function.core.variables.RealVariable;

/**
 * Adds functionality to {@link DiscretizedConditionalProbabilityDistribution} so it works as a {@link com.sri.ai.util.function.api.functions.Function}.
 * 
 * @author braz
 *
 */
public class DiscretizedConditionalProbabilityDistributionFunction extends AbstractFunction implements java.util.function.Function<ArrayList<Object>, Value> {

	protected DiscretizedConditionalProbabilityDistribution conditionalDistribution;
	
	/////////////////////////////////

	public DiscretizedConditionalProbabilityDistributionFunction(DiscretizedConditionalProbabilityDistribution conditionalDistribution) {
		
		super(makeOutputVariable(conditionalDistribution), conditionalDistribution.getSetOfVariablesWithRange());

		this.conditionalDistribution = conditionalDistribution;

	}

	public DiscretizedConditionalProbabilityDistributionFunction(SetOfVariables setOfInputVariablesWithRange, int queryVariableIndex) {
		this(new DiscretizedConditionalProbabilityDistribution(setOfInputVariablesWithRange, queryVariableIndex));
	}
	
	private static RealVariable makeOutputVariable(DiscretizedConditionalProbabilityDistribution conditionalDistribution) {
		return makeOutputVariable(conditionalDistribution.getSetOfVariablesWithRange().get(conditionalDistribution.getQueryVariableIndex()));
	}
	
	public static RealVariable makeOutputVariable(Variable queryVariable) {
		return new RealVariable("Probability of " + queryVariable.getName(), Unit.NONE);
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
	
	public boolean averageWeightIsZero() {
		return conditionalDistribution.averageWeightIsZero();
	}

	public int getNumberOfSamples() {
		return conditionalDistribution.getNumberOfSamples();
	}

	public double getTotalWeight() {
		return conditionalDistribution.getTotalWeight();
	}

	/////////////////////////////////

	public void register(ArrayList<Object> valueObjects, double weight) {
		conditionalDistribution.register(valueObjects, weight);
	}
	
	/////////////////////////////////

	@Override
	public Value evaluate(Assignment assignmentToInputVariables) {
		ArrayList<Object> valueObjects = getValues(assignmentToInputVariables);
		return apply(valueObjects);
	}
	
	@Override
	public SingleInputFunction project(Variable variable, Assignment assignmentToRemainingVariables) {
		return new DefaultProjectionSingleInputFunction(this, variable, assignmentToRemainingVariables);
	}

	@Override
	public Value apply(ArrayList<Object> values) {
		return conditionalDistribution.apply(values);
	}

	private ArrayList<Object> getValues(Assignment assignment) {
		ArrayList<Object> valueObjects = mapIntoArrayList(getVariables(), v -> assignment.get(v).objectValue());
		return valueObjects;
	}
	
	protected List<? extends Variable> getVariables() {
		return getSetOfInputVariables().getVariables();
	}

	private Variable getQueryVariable() {
		return getVariables().get(getQueryVariableIndex());
	}

	@Override
	public String getName() {
		return "Probability of " + getQueryVariable().getName();
	}

}