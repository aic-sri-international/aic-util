package com.sri.ai.util.function.core.functions;

import static com.sri.ai.util.Util.myAssert;

import com.sri.ai.util.function.api.functions.Function;
import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.api.variables.Assignment;
import com.sri.ai.util.function.api.variables.Variable;
import com.sri.ai.util.function.core.values.DefaultValue;

public class MeanFunction extends AbstractStatisticFunction implements Function {

	public MeanFunction(Variable variable, Function function) {
		super("Mean", variable, function);
	}

	@Override
	public Value evaluate(Assignment assignmentToInputVariables) {
		double total = 0;
		double totalWeight = 0;
		for (Value value : getVariable().getSetOfValuesOrNull()) {
			Assignment fullAssignment = assignmentToInputVariables.extend(getVariable(), value);
			Value probability = getFunction().evaluate(fullAssignment);
			total += value.doubleValue() * probability.doubleValue();
			totalWeight += probability.doubleValue();
		}
		myAssert(totalWeight > 0.0, () -> "Mean of " + getVariable() + " cannot be computed because all provided probabilities are zero.");
		double mean = total/totalWeight;
		return new DefaultValue(mean);
	}

}
