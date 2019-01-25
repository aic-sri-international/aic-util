package com.sri.ai.util.distribution;

import static com.sri.ai.util.Util.mapIntoArrayList;

import java.util.ArrayList;

import com.sri.ai.util.base.Pair;
import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.api.variables.SetOfVariables;
import com.sri.ai.util.function.api.variables.Variable;
import com.sri.ai.util.function.core.values.DefaultValue;

/**
 * A class that takes list of objects, discretizes them according to a given discretizer,
 * and keeps a conditional probability normalized for one of the dimensions, indicated as the query.
 * <p>
 * The class provides a method {@link #beforeFirstUseOfUnderlyingDistribution()}
 * which is invoked when the underlying distribution is first used.
 * By default, this method does nothing, but
 * it may be useful for extending classes for generating a first batch of values to be registered
 * but only when they are to be used.
 * 
 * @author braz
 *
 */
public class DiscretizedConditionalProbabilityDistribution implements java.util.function.Function<ArrayList<Object>, Value> {

	protected void beforeFirstUseOfUnderlyingDistribution() {
	}
	
	//////////////////////////////

	protected ConditionalDiscretizer discretizer;
	protected WeightedFrequencyArrayConditionalDistribution conditionalDistribution;

	//////////////////////////////

	protected DiscretizedConditionalProbabilityDistribution(SetOfVariables setOfVariablesWithRange, int queryVariableIndex) {
		this.discretizer = new ConditionalDiscretizer(setOfVariablesWithRange, queryVariableIndex);
		this.conditionalDistribution = null; // lazy
	}

	//////////////////////////////

	public SetOfVariables getSetOfVariablesWithRange() {
		return discretizer.getSetOfVariablesWithRange();
	}

	public int getQueryVariableIndex() {
		return discretizer.getQueryVariableIndex();
	}

	public WeightedFrequencyArrayConditionalDistribution getConditionalDistribution() {
		if (conditionalDistribution == null) {
			conditionalDistribution = makeConditionalDistribution(getSetOfVariablesWithRange(), getQueryVariableIndex());
			beforeFirstUseOfUnderlyingDistribution();
		}
		return conditionalDistribution;
	}
	
	public boolean averageWeightIsZero() {
		return getConditionalDistribution().averageWeightIsZero();
	}

	public int getNumberOfSamples() {
		return getConditionalDistribution().getNumberOfSamples();
	}

	public double getTotalWeight() {
		return getConditionalDistribution().getTotalWeight();
	}

	//////////////////////////////

	public void register(ArrayList<Object> valueObjects, double weight) {
		ArrayList<Object> valueObjectsForDiscretizer = getValueObjectsForDiscretizer(valueObjects);
		Pair<Integer, ArrayList<Integer>> valueIndices = discretizer.getValueIndices(getValueObjectsForDiscretizer(valueObjectsForDiscretizer));
		Integer queryValueIndex = valueIndices.first;
		ArrayList<Integer> second = valueIndices.second;
		if (queryValueIndex != -1) { // query value is in range
			getConditionalDistribution().register(queryValueIndex, second, weight);
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
		double probability = getConditionalDistribution().getProbability(queryValueIndex, nonQueryValueIndices);
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
		return getConditionalDistribution().toString();
	}
}