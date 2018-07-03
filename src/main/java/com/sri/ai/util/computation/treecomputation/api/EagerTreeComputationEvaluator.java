package com.sri.ai.util.computation.treecomputation.api;

import static com.sri.ai.util.Util.mapIntoList;

import java.util.ArrayList;
import java.util.List;

import com.sri.ai.util.base.NullaryFunction;

/**
 * A {@link TreeComputationEvaluator} that is eager, that is, computes all sub-computation's results first
 * and only then passes them to a method {@link #function(List<T> subsValues)} in order to determine its result.
 *  
 * @author braz
 *
 * @param <T>
 */
@FunctionalInterface
public interface EagerTreeComputationEvaluator<T> extends TreeComputationEvaluator<T> {
	
	T function(List<T> subsValues);

	@Override
	default T apply(ArrayList<? extends NullaryFunction<T>> subs) {
		List<T> subResults = mapIntoList(subs, NullaryFunction::apply);
		T result = function(subResults);
		return result;
	}
}
