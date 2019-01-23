package com.sri.ai.util.distribution;

import static com.sri.ai.util.Util.mapIntoArrayList;

import java.util.ArrayList;

import com.sri.ai.util.base.Pair;
import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.api.variables.SetOfVariables;
import com.sri.ai.util.function.api.variables.Variable;
import com.sri.ai.util.function.core.values.DefaultValue;

public class DiscretizedConditionalProbabilityDistribution implements java.util.function.Function<ArrayList<Object>, Value> {

	protected ConditionalDiscretizer discretizer;
	protected WeightedFrequencyArrayConditionalDistribution conditionalDistribution;

	//////////////////////////////

	protected DiscretizedConditionalProbabilityDistribution(SetOfVariables setOfVariablesWithRange, int queryVariableIndex) {
		this.discretizer = new ConditionalDiscretizer(setOfVariablesWithRange, queryVariableIndex);
		this.conditionalDistribution = makeConditionalDistribution(setOfVariablesWithRange, queryVariableIndex);
	}

	//////////////////////////////

	public SetOfVariables getSetOfVariablesWithRange() {
		return discretizer.getSetOfVariablesWithRange();
	}

	public int getQueryVariableIndex() {
		return discretizer.getQueryVariableIndex();
	}

	public double getTotalWeight() {
		return conditionalDistribution.getTotalWeight();
	}

	//////////////////////////////

	public void register(ArrayList<Object> valueObjects, double weight) {
		ArrayList<Object> valueObjectsForDiscretizer = getValueObjectsForDiscretizer(valueObjects);
		Pair<Integer, ArrayList<Integer>> valueIndices = discretizer.getValueIndices(getValueObjectsForDiscretizer(valueObjectsForDiscretizer));
		Integer queryValueIndex = valueIndices.first;
		ArrayList<Integer> second = valueIndices.second;
		if (queryValueIndex != -1) { // query value is in range
			conditionalDistribution.register(queryValueIndex, second, weight);
		}
	}

	/**
	 * A sampling factor values can be any Java object, but the discretizer only deals
	 * with numbers and strings, so we must convert anything that is not numeric to strings here.
	 * @param valueObjects
	 * @return
	 */
	private ArrayList<Object> getValueObjectsForDiscretizer(ArrayList<Object> valueObjects) {
		ArrayList<Object> result = mapIntoArrayList(valueObjects, o -> o instanceof Number? o : o.toString());
		return result;
	}
	
	@Override
	public Value apply(ArrayList<Object> valueObjects) {
		Pair<Integer, ArrayList<Integer>> valueIndices = discretizer.getValueIndices(getValueObjectsForDiscretizer(valueObjects));
		int queryValueIndex = valueIndices.first;
		ArrayList<Integer> nonQueryValueIndices = valueIndices.second;
		double probability = conditionalDistribution.getProbability(queryValueIndex, nonQueryValueIndices);
		return new DefaultValue(probability);
	}

	//////////////////////////////

	private WeightedFrequencyArrayConditionalDistribution makeConditionalDistribution(SetOfVariables setOfVariablesWithRange, int queryVariableIndex) {
		Variable queryVariable = setOfVariablesWithRange.getVariables().get(queryVariableIndex);
		int numberOfQueryValueIndices = queryVariable.getSetOfValuesOrNull().size();
		WeightedFrequencyArrayConditionalDistribution conditionalDistribution = 
				new WeightedFrequencyArrayConditionalDistribution(numberOfQueryValueIndices);
		return conditionalDistribution;
	}

	@Override
	public String toString() {
		return conditionalDistribution.toString();
	}
}