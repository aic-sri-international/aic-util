package com.sri.ai.util.rangeoperation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import com.sri.ai.util.Util;
import com.sri.ai.util.experiment.HashMultiMap;
import com.sri.ai.util.experiment.MultiMap;

/**
 * An extension of {@link TreeMap<String, Object>} that is able to keep track
 * and manage dependencies between its entries.
 * If a variable v1 depends on v2 and v2 is changed, we want v1 to be removed
 * so it is recalculated next time its value is requested.
 * <p>
 * This is useful if we want to use the map as a store for multiple variables
 * and want to make sure that all stored values are valid and consistent.
 * <p>
 * This works by computing new values using {@link DAEFunction}s.
 * These are functions from an environment to a new value (stored under the function's String representation)
 * with the method {@link #getResultOrRecompute(DAEFunction)}.
 * This method monitors accesses made by the function to the environment
 * in order to determine which values depend on which other values.
 * This monitoring is possible due to DAEFunctions only being able to access the environment
 * through methods {@link #get(String)} and {@link #getResultOrRecompute(DAEFunction)},
 * because these methods have internal hooks that do the necessary bookkeeping automatically.
 * <p>
 * If a DAEFunction's {@link DAEFunction#isRandom()} returns <code>true</code>,
 * or depends on a value computed by a random DAEFunction, it is always recomputed
 * since its values cannot be reused.
 *
 * @author braz
 */
@SuppressWarnings("serial")
public class DependencyAwareEnvironment extends TreeMap<String, Object> {
    
	/** Map from variables to set of variables depending on it. */
	private MultiMap fromParentsToChildren = new HashMultiMap();
	
	private Set<String> randomVariables = new HashSet<String>();
	
	private Stack<String> variablesBeingComputed = new Stack<String>();

	/**
	 * Returns the result of the evaluation of a {@link DAEFunction} on the current environment,
	 * only actually evaluating it if the variables it depends on have been changed since the last evaluation
	 * (during which invocations of {@link #get(String)} are monitored in order to determine such dependencies).
	 */
	public Object getResultOrRecompute(DAEFunction function) {
		String variable = function.toString();

		if (function.isRandom()) {
			randomVariables.add(variable);
		}
		
		if ( ! randomVariables.contains(variable) && containsKey(variable)) {
			return super.get(variable);
		}
		
		startingCalculation(variable);
		Object value = function.apply(this);
		finishedCalculation(variable, value);
		
		Object result = value;
		return result;
	}

	private void startingCalculation(String variable) {
		variablesBeingComputed.push(variable);
	}

	private void registerDependencyOfVariableBeingCurrentlyComputedOn(String variable) {
		if ( ! variablesBeingComputed.isEmpty()) {
			String beingCurrentlyComputed = variablesBeingComputed.peek();
			fromParentsToChildren.add(variable, beingCurrentlyComputed);
			if (randomVariables.contains(variable)) {
				randomVariables.add(beingCurrentlyComputed);
			}
		}
	}

	private void finishedCalculation(String variable, Object value) {
		if ( ! variablesBeingComputed.isEmpty() && ! variablesBeingComputed.peek().equals(variable)) {
			Util.fatalError("DependencyAwareEnvironment.finishedCalculation called on " + variable + " when " + variablesBeingComputed.peek() + " is the currently calculated variable.");
		}
		put(variable, value);
		variablesBeingComputed.pop();
		registerDependencyOfVariableBeingCurrentlyComputedOn(variable);
	}

	public Object get(String variable) {
		registerDependencyOfVariableBeingCurrentlyComputedOn(variable);
		Object result = super.get(variable);
		return result;
	}

	@Override
	public Object put(String variable, Object value) {
		Object previousValue = super.get(variable);
		if ((value == null && previousValue == null) || value.equals(previousValue)) {
			return value;
		}
		removeChildrenOf(variable);
		Object result = super.put(variable, value);
		return result;
	}

	public void remove(String variable) {
		removeChildrenOf(variable);
		super.remove(variable);
	}

	@SuppressWarnings("unchecked")
	protected void removeChildrenOf(String variable) {
		for (String child : ((Collection<String>) fromParentsToChildren.get(variable))) {
			remove(child);
		}
		fromParentsToChildren.remove(variable);
	}

	///////////// CONVENIENCE METHODS ///////////////

	public Object getWithDefault(String variable, Object defaultValue) {
		Object result = get(variable);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	public int getInt(String variable) {
		return ((Integer) get(variable)).intValue();
	}
}
