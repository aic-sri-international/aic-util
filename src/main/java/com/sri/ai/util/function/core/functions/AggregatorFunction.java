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
 * A {@link Function} that aggregates a family of functions under a set of new variables.
 * More formally, given:
 * <ol>
 * <li> a {@link SetOfVariables} <code>X</code>
 * <li> a {@link java.util.function.Function} <code>subFunctionMaker</code> mapping assignment in <code>X</code>
 * to some {@link Function} on a set of variable <code>Others</code> with output variable <code>Output</code>,
 * </ol>
 * this is a {@link Function} on <code>X union Others</code>
 * that maps an assignment <code>{X -> x, Others -> others}</code>
 * to <code>subFunctionMaker(x).evaluate(others)</code>.
 * <p>
 * The function for each assignment of <code>X</code> is made only once, and cached.
 * 
 * @author braz
 *
 */
public class AggregatorFunction implements Function {
	
	private SetOfVariables x;
	private SetOfVariables others;
	private Variable output;
	private java.util.function.Function<Assignment, Function> subFunctionMaker;

	////////////////////////
	
	public AggregatorFunction(
			SetOfVariables x, 
			java.util.function.Function<Assignment, Function> subFunctionMaker) {
		
		this.x = x;
		this.subFunctionMaker = subFunctionMaker;
	}

	////////////////////////
	
	@Override
	public String getName() {
		makeSureOthersAndOutputAreComputed();
		return "Aggregator for " + x + " over " + others + " to " + output;
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
			inputVariables = new DefaultSetOfVariables(union(x.getVariables(), others.getVariables()));
		}
		return inputVariables;
	}

	////////////////////////
	
	private Map<Assignment, Function> cacheOfFunctions = map();
	
	@Override
	public Value evaluate(Assignment assignmentToInputVariables) {
		Assignment assignmentToX = assignmentToInputVariables.get(x);
		Function subFunctionForAssignmentToX = getCachedSubFunctionFor(assignmentToX);
		Value result = subFunctionForAssignmentToX.evaluate(assignmentToInputVariables);
		return result;
	}

	private Function getCachedSubFunctionFor(Assignment assignmentToX) {
		return getValuePossiblyCreatingIt(cacheOfFunctions, assignmentToX, this::make);
	}
	
	////////////////////////
	
	private Function make(Assignment xValues) {
		Function function = subFunctionMaker.apply(xValues);
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
			Assignment anyAssignmentToX = getAnyAssignmentToX();
			return getCachedSubFunctionFor(anyAssignmentToX);
		}
		else {
			return getFirst(cacheOfFunctions.values());
		}
	}
	
	private Assignment getAnyAssignmentToX() {
		SetOfVariables setOfVariables = x;
		return getAnyAssignment(setOfVariables);
	}

	private static Assignment getAnyAssignment(SetOfVariables setOfVariables) {
		Map<Variable, Value> anyAssignment = map();
		for (Variable inX : setOfVariables.getVariables()) {
			SetOfValues setOfValuesOrNull = inX.getSetOfValuesOrNull();
			myAssert(setOfValuesOrNull != null, () -> "Some assignment for " + inX + " was requested but it has not associated values");
			Iterator<Value> iterator = setOfValuesOrNull.iterator();
			myAssert(iterator.hasNext(), () -> "Some assignment for " + inX + " was requested but it has has an empty set of values associated with it");
			Value firstValue = iterator.next();
			anyAssignment.put(inX, firstValue);
		}
		return new DefaultAssignment(anyAssignment);
	}

	

	////////////////////////
	
	private void errorChecking() {
		myAssert(!intersect(others.getVariables(), x.getVariables()), this, () -> "requires variables X (" + x + ") not to intersect Other variables " + others);
		myAssert(!others.getVariables().contains(output), this, () -> "requires output variable (" + output + ") not to be in Other variables " + others);
		myAssert(!x.getVariables().contains(output), this, () -> "requires variable X (" + x + ") not to contain the output variable " + output);
	}

	@Override
	public SingleInputFunction project(Variable variable, Assignment assignmentToRemainingVariables) {
		// for projections, we must be sure to project subfunctions with the same assignment when we create them.
		// Originally, we were using a default 'project' implementation that simply kept the original
		// AggregatorFunction and conditioned at evaluate-time.
		// However, some Functions have efficient project methods that change the way they are evaluate,
		// so it is important to invoke their 'project' methods.

		if (x.getVariables().contains(variable)) {
			return makeProjectionOnX(variable, assignmentToRemainingVariables);
		}
		else {
			return makeProjectionInOthers(variable, assignmentToRemainingVariables);
		}
	}

	private SingleInputFunction makeProjectionOnX(Variable variable, Assignment assignmentToRemainingVariables) {
		// safe to use default approach here since the subfunctions are not being projected
		return new DefaultProjectionSingleInputFunction(this, variable, assignmentToRemainingVariables);
	}

	private SingleInputFunction makeProjectionInOthers(Variable variable, Assignment assignmentToRemainingVariables) {
		
		java.util.function.Function<Assignment, Function> newSubFunctionMaker = 
				makeNewSubFunctionMaker(variable, assignmentToRemainingVariables);
		
		SingleInputFunction singleInputProjectedAggregator = 
				makeSingleInputProjectedAggregator(newSubFunctionMaker);
		
		return singleInputProjectedAggregator;
	}

	private java.util.function.Function<Assignment, Function> makeNewSubFunctionMaker(Variable variable, Assignment assignmentToRemainingVariables) {
		return a -> makeProjectedSubFunction(a, variable, assignmentToRemainingVariables);
	}

	private Function makeProjectedSubFunction(Assignment assignmentToX, Variable variable, Assignment assignmentToRemainingVariables) {
		Function newSubFunction = subFunctionMaker.apply(assignmentToX);
		myAssert(newSubFunction.getSetOfInputVariables().getVariables().contains(variable), this, () -> " requires projection onto a variable not in X to be in Others, but " + variable + " is not in Others from just-created sub-function " + newSubFunction);
		return newSubFunction.project(variable, assignmentToRemainingVariables);
	}

	private SingleInputFunction makeSingleInputProjectedAggregator(
			java.util.function.Function<Assignment, Function> newSubFunctionMaker) {
		
		AggregatorFunction projectedAggregator = new AggregatorFunction(x, newSubFunctionMaker);
		SingleInputFunction singleInputProjectedAggregator = new FunctionToSingleInputFunctionAdapter(projectedAggregator);
		return singleInputProjectedAggregator;
	}

}