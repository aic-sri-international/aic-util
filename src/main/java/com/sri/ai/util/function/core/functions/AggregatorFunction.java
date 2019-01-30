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
 * <li> a {@link java.util.function.Function} <code>maker</code> mapping assignment in <code>X</code>
 * to some {@link Function} on a set of variable <code>Others</code> with output variable <code>Output</code>,
 * </ol>
 * this is a {@link Function} on <code>X union Others</code>
 * that maps an assignment <code>{X -> x, Others -> others}</code>
 * to <code>maker(x).evaluate(others)</code>.
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
	private java.util.function.Function<Assignment, Function> maker;

	////////////////////////
	
	public AggregatorFunction(
			SetOfVariables x, 
			java.util.function.Function<Assignment, Function> maker) {
		
		this.x = x;
		this.maker = maker;
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
		Function function = maker.apply(xValues);
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

	public static Assignment getAnyAssignment(SetOfVariables setOfVariables) {
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

}
