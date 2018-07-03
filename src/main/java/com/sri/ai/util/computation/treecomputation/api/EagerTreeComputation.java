package com.sri.ai.util.computation.treecomputation.api;

import java.util.List;

/**
 * A {@link TreeComputation} whose {@link TreeComputationEvaluator} is defined as an {@link EagerTreeComputationEvaluator} 
 * based on an abstract method {@link #function(List)}.
 * 
 * @author braz
 *
 * @param <T>
 */
public interface EagerTreeComputation<T> extends TreeComputation<T> {
	
	T function(List<T> subsValues);

	@Override
	default EagerTreeComputationEvaluator<T> getEvaluator() {
		return this::function;
	}
}
