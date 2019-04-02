package com.sri.ai.util.function.core.functions;

import static com.sri.ai.util.Util.getFirst;
import static com.sri.ai.util.Util.getValuePossiblyCreatingIt;
import static com.sri.ai.util.Util.intersect;
import static com.sri.ai.util.Util.map;
import static com.sri.ai.util.Util.myAssert;
import static com.sri.ai.util.Util.union;

import java.util.Iterator;
import java.util.Map;

import com.sri.ai.util.function.api.functions.Function;
import com.sri.ai.util.function.api.functions.SingleInputFunction;
import com.sri.ai.util.function.api.values.Value;
import com.sri.ai.util.function.api.variables.Assignment;
import com.sri.ai.util.function.api.variables.SetOfValues;
import com.sri.ai.util.function.api.variables.SetOfVariables;
import com.sri.ai.util.function.api.variables.Variable;
import com.sri.ai.util.function.core.variables.DefaultAssignment;
import com.sri.ai.util.function.core.variables.DefaultSetOfVariables;

/**
 * A {@link Function} that aggregates a family of functions under a set of new <i>key</i> variables.
 * More formally, given:
 * <ol>
 * <li> a {@link SetOfVariables} <code>Keys</code>
 * <li> a {@link java.util.function.Function} <code>subFunctionMaker</code> mapping assignment in <code>Keys</code>
 * to some {@link Function} on a set of variables <code>Others</code> with output variable <code>Output</code>,
 * </ol>
 * this is a {@link Function} on <code>Keys union Others</code>
 * that maps an assignment <code>{Keys -> key, Others -> others}</code>
 * to <code>subFunctionMaker(key).evaluate(others)</code>.
 * <p>
 * The function for each assignment of <code>Keys</code> is made only once, and cached.
 * 
 * @author braz
 *
 */
public class AggregatorFunction extends AbstractFunction {
	
	private SetOfVariables keys;
	private SetOfVariables others;
	private Variable output;
	private java.util.function.Function<Assignment, Function> subFunctionMaker;

	////////////////////////
	
	public AggregatorFunction(
			SetOfVariables keys, 
			java.util.function.Function<Assignment, Function> subFunctionMaker) {
		
		this.keys = keys;
		this.subFunctionMaker = subFunctionMaker;
	}

	////////////////////////
	
	@Override
	public String getName() {
		makeSureOthersAndOutputAreComputed();
		return "Aggregator for " + keys + " over " + others + " to " + output;
	}

	@Override
	public Variable getOutputVariable() {
		makeSureOthersAndOutputAreComputed();
		return output;
	}

	////////////////////////
	
	private SetOfVariables inputVariables = null;
	
	@Override
	public SetOfVariables getSetOfInputVariables() {
		if (inputVariables == null) {
			makeSureOthersAndOutputAreComputed();
			inputVariables = new DefaultSetOfVariables(union(keys.getVariables(), others.getVariables()));
		}
		return inputVariables;
	}

	////////////////////////
	
	private Map<Assignment, Function> cacheOfFunctions = map();
	
	@Override
	public Value evaluate(Assignment assignmentToInputVariables) {
		Assignment assignmentToKeys = assignmentToInputVariables.get(keys);
		Function subFunctionForAssignmentToKeys = getSubFunction(assignmentToKeys);
		Value result = subFunctionForAssignmentToKeys.evaluate(assignmentToInputVariables);
		return result;
	}

	private Function getSubFunction(Assignment assignmentToKeys) {
		return getValuePossiblyCreatingIt(cacheOfFunctions, assignmentToKeys, this::makeSubFunction);
	}
	
	////////////////////////
	
	private Function makeSubFunction(Assignment keyValues) {
		Function function = subFunctionMaker.apply(keyValues);
		return function;
	}

	////////////////////////
	
	private void makeSureOthersAndOutputAreComputed() {
		if (others == null) {
			Function anySubFunction = getAnySubFunction();
			others = anySubFunction.getSetOfInputVariables();
			output = anySubFunction.getOutputVariable();
			errorChecking();
		}
	}

	private Function getAnySubFunction() {
		if (cacheOfFunctions.isEmpty()) {
			Assignment anyAssignmentToKeys = getAnyAssignmentToKeys();
			return getSubFunction(anyAssignmentToKeys);
		}
		else {
			return getFirst(cacheOfFunctions.values());
		}
	}
	
	private Assignment getAnyAssignmentToKeys() {
		SetOfVariables setOfVariables = keys;
		return getAnyAssignment(setOfVariables);
	}

	private static Assignment getAnyAssignment(SetOfVariables setOfVariables) {
		Map<Variable, Value> anyAssignment = map();
		for (Variable inKeys : setOfVariables.getVariables()) {
			SetOfValues setOfValuesOrNull = inKeys.getSetOfValuesOrNull();
			myAssert(setOfValuesOrNull != null, () -> "Some assignment for " + inKeys + " was requested but it has not associated values");
			Iterator<Value> iterator = setOfValuesOrNull.iterator();
			myAssert(iterator.hasNext(), () -> "Some assignment for " + inKeys + " was requested but it has has an empty set of values associated with it");
			Value firstValue = iterator.next();
			anyAssignment.put(inKeys, firstValue);
		}
		return new DefaultAssignment(anyAssignment);
	}

	@Override
	protected SingleInputFunction projectIfNeeded(Variable variable, Assignment assignmentToRemainingVariables) {
		// We differentiate the cases in which the given variable is one of Keys or not.
		
		// for projections, we must be sure to project subfunctions with the same assignment when we created them with.
		// Originally, we were using a default 'project' implementation that simply kept the original
		// AggregatorFunction and conditioned at evaluate-time.
		// However, some Functions have efficient project methods that change the way they are evaluated,
		// so it is important to invoke their 'project' methods.

		if (keys.getVariables().contains(variable)) {
			return useDefaultProjectionSinceVariableIsOneOfKeys(variable, assignmentToRemainingVariables);
			// TODO: instead of using default projection, need to create a new aggregate function with some of the keys conditioned,
			// and all 'other' variables conditioning sub-functions
		}
		else {
			return projectIfNeededOnOneOfOthers(variable, assignmentToRemainingVariables);
		}
	}

	public SingleInputFunction useDefaultProjectionSinceVariableIsOneOfKeys(Variable variable, Assignment assignmentToRemainingVariables) {
		// we can use the default projection here because the default projection is only wasteful if the inner function is
		// more profitably computed taking into account the projecting assignment, but this is not the case here
		// because the sub functions do not even know about the keys.
		return new DefaultProjectionSingleInputFunction(this, variable, assignmentToRemainingVariables);
	}

	public SingleInputFunction projectIfNeededOnOneOfOthers(Variable variable, Assignment assignmentToRemainingVariables) {
		// here we do not use the default projection because the sub-functions may have a more efficient projection method.
		
		myAssert(assignmentToRemainingVariables.getSetOfVariables().getVariables().containsAll(keys.getVariables()), this, () -> " only supports projections on a key (one of " + keys + ") or, if not on a key, then given assignment must assign values to all keys, but got assignment " + assignmentToRemainingVariables + " when the keys are " + keys);

		Function subFunctionForKeys = getSubFunction(assignmentToRemainingVariables.get(keys));

		Assignment assignmentToRemainingVariablesMinusKeys = assignmentToRemainingVariables.exclude(keys);

		return subFunctionForKeys.project(variable, assignmentToRemainingVariablesMinusKeys);
	}

	////////////////////////
	
	private void errorChecking() {
		myAssert(!intersect(others.getVariables(), keys.getVariables()), this, () -> "requires variables Keys (" + keys + ") not to intersect Other variables " + others);
		myAssert(!others.getVariables().contains(output), this, () -> "requires output variable (" + output + ") not to be in Other variables " + others);
		myAssert(!keys.getVariables().contains(output), this, () -> "requires variable Keys (" + keys + ") not to contain the output variable " + output);
	}

}
